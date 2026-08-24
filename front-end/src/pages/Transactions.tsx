import { useEffect, useState } from 'react'
import { useForm, useWatch } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import {
  ArrowDownLeft,
  ArrowUpRight,
  ChevronLeft,
  ChevronRight,
  Loader2,
  Pencil,
  Plus,
  Receipt,
  Trash2,
} from 'lucide-react'
import { PageHeader } from '@/components/page-header'
import { Button } from '@/components/ui/button'
import { Card } from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog'
import { cn } from '@/lib/utils'
import {
  createTransaction,
  deleteTransaction,
  formatCurrency,
  formatDate,
  listTransactions,
  updateTransaction,
  type TransactionFilters,
  type TransactionInput,
  type TransactionPage,
  type TransactionType,
} from '@/services/transactions'
import {
  fetchCategories,
  type Category,
} from '@/services/categories'

const SELECT_CLASS =
  'flex h-11 w-full rounded-xl border border-border bg-surface px-4 py-2 text-sm focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-primary/40'

const transactionSchema = z.object({
  type: z.enum(['income', 'expense']),
  amount: z
    .string()
    .min(1, 'Campo obrigatório')
    .refine((value) => !Number.isNaN(Number(value)) && Number(value) > 0, {
      message: 'Informe um valor maior que zero',
    }),
  description: z.string().max(255, 'Máximo de 255 caracteres'),
  date: z.string().min(1, 'Campo obrigatório'),
  categoryId: z.string(),
})

type TransactionFormData = z.infer<typeof transactionSchema>

export function Transactions() {
  const [data, setData] = useState<TransactionPage | null>(null)
  const [categories, setCategories] = useState<Category[]>([])
  const [loading, setLoading] = useState(true)
  const [loadError, setLoadError] = useState<string | null>(null)

  const [filterType, setFilterType] = useState<'' | TransactionType>('')
  const [filterCategory, setFilterCategory] = useState('')
  const [filterStartDate, setFilterStartDate] = useState('')
  const [filterEndDate, setFilterEndDate] = useState('')
  const [page, setPage] = useState(0)

  const [dialogOpen, setDialogOpen] = useState(false)
  const [editingId, setEditingId] = useState<string | null>(null)
  const [saving, setSaving] = useState(false)
  const [formError, setFormError] = useState<string | null>(null)
  const [deletingId, setDeletingId] = useState<string | null>(null)

  const {
    register,
    handleSubmit,
    reset,
    control,
    setValue,
    formState: { errors },
  } = useForm<TransactionFormData>({
    resolver: zodResolver(transactionSchema),
    defaultValues: {
      type: 'expense',
      amount: '',
      description: '',
      date: '',
      categoryId: '',
    },
  })

  const formType = useWatch({ control, name: 'type', defaultValue: 'expense' })

  async function reload() {
    setLoading(true)
    try {
      const filters: TransactionFilters = {
        type: filterType === '' ? null : filterType,
        categoryId: filterCategory ? Number(filterCategory) : null,
        startDate: filterStartDate || null,
        endDate: filterEndDate || null,
      }
      const result = await listTransactions(filters, page, 10)
      setData(result)
    } catch {
      setLoadError('Não foi possível carregar as transações.')
    }
    setLoading(false)
  }

  useEffect(() => {
    let active = true
    const filters: TransactionFilters = {
      type: filterType === '' ? null : filterType,
      categoryId: filterCategory ? Number(filterCategory) : null,
      startDate: filterStartDate || null,
      endDate: filterEndDate || null,
    }
    listTransactions(filters, page, 10)
      .then((result) => {
        if (active) {
          setData(result)
          setLoadError(null)
          setLoading(false)
        }
      })
      .catch(() => {
        if (active) {
          setLoadError('Não foi possível carregar as transações.')
          setLoading(false)
        }
      })
    return () => {
      active = false
    }
  }, [filterType, filterCategory, filterStartDate, filterEndDate, page])

  useEffect(() => {
    fetchCategories().then(setCategories).catch(() => setCategories([]))
  }, [])

  function openCreateDialog() {
    setEditingId(null)
    reset({
      type: 'expense',
      amount: '',
      description: '',
      date: new Date().toISOString().slice(0, 10),
      categoryId: '',
    })
    setFormError(null)
    setDialogOpen(true)
  }

  function openEditDialog(transactionId: string) {
    const transaction = data?.items.find((item) => item.id === transactionId)
    if (!transaction) return
    setEditingId(transaction.id)
    reset({
      type: transaction.type,
      amount: String(transaction.amount),
      description: transaction.description,
      date: transaction.date,
      categoryId: '',
    })
    setFormError(null)
    setDialogOpen(true)
  }

  async function onSubmit(formData: TransactionFormData) {
    setSaving(true)
    setFormError(null)
    const input: TransactionInput = {
      type: formData.type,
      amount: Number(formData.amount),
      description: formData.description.trim(),
      date: formData.date,
      categoryId: formData.categoryId ? Number(formData.categoryId) : null,
    }
    try {
      if (editingId) {
        await updateTransaction(editingId, input)
      } else {
        await createTransaction(input)
      }
      setDialogOpen(false)
      await reload()
    } catch (error) {
      setFormError(
        error instanceof Error ? error.message : 'Falha ao salvar a transação',
      )
    } finally {
      setSaving(false)
    }
  }

  async function handleDelete(transactionId: string) {
    const confirmed = window.confirm('Excluir esta transação?')
    if (!confirmed) return

    setDeletingId(transactionId)
    try {
      await deleteTransaction(transactionId)
      await reload()
    } catch {
      window.alert('Não foi possível excluir a transação.')
    } finally {
      setDeletingId(null)
    }
  }

  const categoriesForForm = categories.filter(
    (category) =>
      !formType ||
      category.type === (formType === 'income' ? 'INCOME' : 'EXPENSE'),
  )

  return (
    <div className="space-y-6">
      <PageHeader
        title="Transações"
        description="Gerencie suas receitas e despesas"
        breadcrumbs={[
          { label: 'Início', to: '/app/dashboard' },
          { label: 'Transações' },
        ]}
        actions={
          <Button onClick={openCreateDialog}>
            <Plus className="size-4" />
            Nova transação
          </Button>
        }
      />

      <Card className="grid gap-4 p-5 md:grid-cols-4">
        <div className="space-y-2">
          <Label htmlFor="filter-type">Tipo</Label>
          <select
            id="filter-type"
            className={SELECT_CLASS}
            value={filterType}
            onChange={(event) => {
              setFilterType(event.target.value as '' | TransactionType)
              setPage(0)
            }}
          >
            <option value="">Todos</option>
            <option value="income">Receitas</option>
            <option value="expense">Despesas</option>
          </select>
        </div>

        <div className="space-y-2">
          <Label htmlFor="filter-category">Categoria</Label>
          <select
            id="filter-category"
            className={SELECT_CLASS}
            value={filterCategory}
            onChange={(event) => {
              setFilterCategory(event.target.value)
              setPage(0)
            }}
          >
            <option value="">Todas</option>
            {categories.map((category) => (
              <option key={category.id} value={category.id}>
                {category.name}
              </option>
            ))}
          </select>
        </div>

        <div className="space-y-2">
          <Label htmlFor="filter-start">Data inicial</Label>
          <Input
            id="filter-start"
            type="date"
            value={filterStartDate}
            onChange={(event) => {
              setFilterStartDate(event.target.value)
              setPage(0)
            }}
          />
        </div>

        <div className="space-y-2">
          <Label htmlFor="filter-end">Data final</Label>
          <Input
            id="filter-end"
            type="date"
            value={filterEndDate}
            onChange={(event) => {
              setFilterEndDate(event.target.value)
              setPage(0)
            }}
          />
        </div>
      </Card>

      <Card className="p-6">
        {loading ? (
          <div className="space-y-3">
            {Array.from({ length: 5 }, (_, index) => (
              <div
                key={index}
                className="h-12 animate-pulse rounded-xl bg-muted/20"
              />
            ))}
          </div>
        ) : loadError ? (
          <p className="text-sm text-expense">{loadError}</p>
        ) : !data || data.items.length === 0 ? (
          <div className="flex flex-col items-center gap-2 py-8 text-center">
            <Receipt className="size-8 text-muted" />
            <p className="font-semibold">Nenhuma transação encontrada</p>
            <p className="text-sm text-muted">
              Ajuste os filtros ou registre um novo lançamento.
            </p>
          </div>
        ) : (
          <>
            <ul className="divide-y divide-border">
              {data.items.map((transaction) => {
                const isIncome = transaction.type === 'income'
                return (
                  <li
                    key={transaction.id}
                    className="group flex items-center justify-between gap-4 py-3"
                  >
                    <div className="flex min-w-0 items-center gap-3">
                      <div
                        className={cn(
                          'flex size-9 shrink-0 items-center justify-center rounded-full',
                          isIncome
                            ? 'bg-income/10 text-income'
                            : 'bg-expense/10 text-expense',
                        )}
                      >
                        {isIncome ? (
                          <ArrowUpRight className="size-4" />
                        ) : (
                          <ArrowDownLeft className="size-4" />
                        )}
                      </div>
                      <div className="min-w-0">
                        <p className="truncate text-sm font-semibold">
                          {transaction.description || 'Sem descrição'}
                        </p>
                        <p className="text-xs text-muted">
                          {transaction.category || 'Sem categoria'} ·{' '}
                          {formatDate(transaction.date)}
                        </p>
                      </div>
                    </div>
                    <div className="flex shrink-0 items-center gap-1">
                      <p
                        className={cn(
                          'text-sm font-semibold',
                          isIncome ? 'text-income' : 'text-expense',
                        )}
                      >
                        {isIncome ? '+' : '-'}
                        {formatCurrency(transaction.amount)}
                      </p>
                      <div className="ml-2 flex opacity-0 transition-opacity group-hover:opacity-100">
                        <Button
                          variant="ghost"
                          size="icon"
                          aria-label={`Editar ${transaction.description}`}
                          onClick={() => openEditDialog(transaction.id)}
                        >
                          <Pencil className="size-4" />
                        </Button>
                        <Button
                          variant="ghost"
                          size="icon"
                          className="text-expense hover:text-expense"
                          aria-label={`Excluir ${transaction.description}`}
                          disabled={deletingId === transaction.id}
                          onClick={() => handleDelete(transaction.id)}
                        >
                          {deletingId === transaction.id ? (
                            <Loader2 className="size-4 animate-spin" />
                          ) : (
                            <Trash2 className="size-4" />
                          )}
                        </Button>
                      </div>
                    </div>
                  </li>
                )
              })}
            </ul>

            <div className="mt-4 flex items-center justify-between border-t border-border pt-4">
              <span className="text-sm text-muted">
                {data.totalElements} transação(ões) · página{' '}
                {data.page + 1} de {Math.max(data.totalPages, 1)}
              </span>
              <div className="flex gap-2">
                <Button
                  variant="outline"
                  size="sm"
                  disabled={data.page === 0 || loading}
                  onClick={() => setPage((current) => current - 1)}
                >
                  <ChevronLeft className="size-4" />
                  Anterior
                </Button>
                <Button
                  variant="outline"
                  size="sm"
                  disabled={data.page >= data.totalPages - 1 || loading}
                  onClick={() => setPage((current) => current + 1)}
                >
                  Próxima
                  <ChevronRight className="size-4" />
                </Button>
              </div>
            </div>
          </>
        )}
      </Card>

      <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>
              {editingId ? 'Editar transação' : 'Nova transação'}
            </DialogTitle>
            <DialogDescription>
              Preencha os dados do lançamento.
            </DialogDescription>
          </DialogHeader>

          <form className="space-y-5" onSubmit={handleSubmit(onSubmit)} noValidate>
            <input type="hidden" {...register('type')} />
            <div className="grid grid-cols-2 gap-3">
              <button
                type="button"
                onClick={() => setValue('type', 'income')}
                className={cn(
                  'h-11 rounded-xl border px-4 py-2 text-sm font-medium transition-colors',
                  formType === 'income'
                    ? 'border-income bg-income/10 text-income'
                    : 'border-border bg-surface text-muted',
                )}
              >
                Receita
              </button>
              <button
                type="button"
                onClick={() => setValue('type', 'expense')}
                className={cn(
                  'h-11 rounded-xl border px-4 py-2 text-sm font-medium transition-colors',
                  formType === 'expense'
                    ? 'border-expense bg-expense/10 text-expense'
                    : 'border-border bg-surface text-muted',
                )}
              >
                Despesa
              </button>
            </div>

            <div className="space-y-2">
              <Label htmlFor="transaction-amount">Valor</Label>
              <Input
                id="transaction-amount"
                type="number"
                step="0.01"
                min="0.01"
                placeholder="0,00"
                {...register('amount')}
              />
              {errors.amount && (
                <p className="text-sm text-expense">{errors.amount.message}</p>
              )}
            </div>

            <div className="space-y-2">
              <Label htmlFor="transaction-description">Descrição</Label>
              <Input
                id="transaction-description"
                placeholder="Ex.: Mercado"
                {...register('description')}
              />
              {errors.description && (
                <p className="text-sm text-expense">
                  {errors.description.message}
                </p>
              )}
            </div>

            <div className="grid gap-4 sm:grid-cols-2">
              <div className="space-y-2">
                <Label htmlFor="transaction-date">Data</Label>
                <Input
                  id="transaction-date"
                  type="date"
                  {...register('date')}
                />
                {errors.date && (
                  <p className="text-sm text-expense">{errors.date.message}</p>
                )}
              </div>

              <div className="space-y-2">
                <Label htmlFor="transaction-category">Categoria</Label>
                <select
                  id="transaction-category"
                  className={SELECT_CLASS}
                  {...register('categoryId')}
                >
                  <option value="">Sem categoria</option>
                  {categoriesForForm.map((category) => (
                    <option key={category.id} value={category.id}>
                      {category.name}
                    </option>
                  ))}
                </select>
              </div>
            </div>

            {formError && (
              <p className="rounded-xl bg-expense/10 px-4 py-3 text-sm text-expense">
                {formError}
              </p>
            )}

            <DialogFooter>
              <Button type="submit" size="lg" className="w-full" disabled={saving}>
                {saving && <Loader2 className="size-5 animate-spin" />}
                {editingId ? 'Salvar alterações' : 'Adicionar transação'}
              </Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>
    </div>
  )
}
