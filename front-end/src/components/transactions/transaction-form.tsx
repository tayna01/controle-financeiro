import { useEffect } from 'react'
import { useForm, useWatch } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { Loader2 } from 'lucide-react'
import { Button } from '@/components/ui/button'
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
import type { Category } from '@/services/categories'
import type { Transaction, TransactionInput } from '@/services/transactions'

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

interface TransactionFormProps {
  open: boolean
  onOpenChange: (open: boolean) => void
  editingTransaction: Transaction | null
  categories: Category[]
  saving: boolean
  formError: string | null
  onSubmit: (input: TransactionInput) => Promise<void>
}

export function TransactionForm({
  open,
  onOpenChange,
  editingTransaction,
  categories,
  saving,
  formError,
  onSubmit,
}: TransactionFormProps) {
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

  useEffect(() => {
    if (!open) return
    if (editingTransaction) {
      reset({
        type: editingTransaction.type,
        amount: String(editingTransaction.amount),
        description: editingTransaction.description,
        date: editingTransaction.date,
        categoryId: '',
      })
    } else {
      reset({
        type: 'expense',
        amount: '',
        description: '',
        date: new Date().toISOString().slice(0, 10),
        categoryId: '',
      })
    }
  }, [open, editingTransaction, reset])

  function handleFormSubmit(data: TransactionFormData) {
    onSubmit({
      type: data.type,
      amount: Number(data.amount),
      description: data.description.trim(),
      date: data.date,
      categoryId: data.categoryId ? Number(data.categoryId) : null,
    })
  }

  const categoriesForForm = categories.filter(
    (category) =>
      !formType ||
      category.type === (formType === 'income' ? 'INCOME' : 'EXPENSE'),
  )

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>
            {editingTransaction ? 'Editar transação' : 'Nova transação'}
          </DialogTitle>
          <DialogDescription>
            Preencha os dados do lançamento.
          </DialogDescription>
        </DialogHeader>

        <form className="space-y-5" onSubmit={handleSubmit(handleFormSubmit)} noValidate>
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
              {editingTransaction ? 'Salvar alterações' : 'Adicionar transação'}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  )
}
