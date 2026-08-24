import { useEffect, useState } from 'react'
import { useForm, useWatch } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { Loader2, Pencil, Plus, Tag, Trash2 } from 'lucide-react'
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
  createCategory,
  deleteCategory,
  fetchCategories,
  updateCategory,
  type Category,
  type CategoryType,
} from '@/services/categories'

const categorySchema = z.object({
  name: z.string().min(1, 'Campo obrigatório').max(80, 'Máximo de 80 caracteres'),
  type: z.enum(['INCOME', 'EXPENSE']),
})

type CategoryFormData = {
  name: string
  type: CategoryType
}

const TYPE_LABELS: Record<CategoryType, string> = {
  INCOME: 'Receita',
  EXPENSE: 'Despesa',
}

export function Categories() {
  const [categories, setCategories] = useState<Category[]>([])
  const [loading, setLoading] = useState(true)
  const [loadError, setLoadError] = useState<string | null>(null)

  const [dialogOpen, setDialogOpen] = useState(false)
  const [editing, setEditing] = useState<Category | null>(null)
  const [saving, setSaving] = useState(false)
  const [formError, setFormError] = useState<string | null>(null)
  const [deletingId, setDeletingId] = useState<string | null>(null)
  const [color, setColor] = useState('#dc2626')

  const { register, handleSubmit, reset, control, setValue, setError, formState: { errors } } =
    useForm<CategoryFormData>({
      resolver: zodResolver(categorySchema),
      defaultValues: { name: '', type: 'EXPENSE' },
    })

  const type = useWatch({ control, name: 'type', defaultValue: 'EXPENSE' })

  async function reload() {
    try {
      const data = await fetchCategories()
      setCategories(data)
    } catch {
      setLoadError('Não foi possível carregar as categorias.')
    }
    setLoading(false)
  }

  useEffect(() => {
    let active = true
    fetchCategories()
      .then((data) => {
        if (active) {
          setCategories(data)
          setLoading(false)
        }
      })
      .catch(() => {
        if (active) {
          setLoadError('Não foi possível carregar as categorias.')
          setLoading(false)
        }
      })
    return () => {
      active = false
    }
  }, [])

  function openCreateDialog() {
    setEditing(null)
    reset({ name: '', type: 'EXPENSE' })
    setColor('#dc2626')
    setFormError(null)
    setDialogOpen(true)
  }

  function openEditDialog(category: Category) {
    setEditing(category)
    reset({ name: category.name, type: category.type })
    setColor(category.color ?? (category.type === 'INCOME' ? '#16a34a' : '#dc2626'))
    setFormError(null)
    setDialogOpen(true)
  }

  async function onSubmit(data: CategoryFormData) {
    setSaving(true)
    setFormError(null)
    try {
      if (editing) {
        await updateCategory(editing.id, {
          name: data.name,
          type: data.type,
          color,
        })
      } else {
        await createCategory({ name: data.name, type: data.type, color })
      }
      setDialogOpen(false)
      await reload()
    } catch (error) {
      const message =
        error instanceof Error ? error.message : 'Falha ao salvar categoria'
      if (
        typeof message === 'string' &&
        message.toLowerCase().includes('já')
      ) {
        setError('name', { type: 'manual', message })
      } else {
        setFormError(message)
      }
    } finally {
      setSaving(false)
    }
  }

  async function handleDelete(category: Category) {
    const confirmed = window.confirm(
      `Excluir a categoria "${category.name}"? As transações vinculadas não serão apagadas.`,
    )
    if (!confirmed) return

    setDeletingId(String(category.id))
    try {
      await deleteCategory(category.id)
      await reload()
    } catch {
      window.alert('Não foi possível excluir a categoria.')
    } finally {
      setDeletingId(null)
    }
  }

  return (
    <div className="space-y-6">
      <PageHeader
        title="Categorias"
        description="Organize as categorias dos seus lançamentos"
        breadcrumbs={[
          { label: 'Início', to: '/app/dashboard' },
          { label: 'Categorias' },
        ]}
        actions={
          <Button onClick={openCreateDialog}>
            <Plus className="size-4" />
            Nova categoria
          </Button>
        }
      />

      {loading ? (
        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {Array.from({ length: 6 }, (_, index) => (
            <div
              key={index}
              className="h-24 animate-pulse rounded-2xl border border-border bg-surface"
            />
          ))}
        </div>
      ) : loadError ? (
        <Card className="p-6 text-sm text-expense">{loadError}</Card>
      ) : categories.length === 0 ? (
        <Card className="flex flex-col items-center gap-2 p-10 text-center">
          <Tag className="size-8 text-muted" />
          <p className="font-semibold">Nenhuma categoria cadastrada</p>
          <p className="text-sm text-muted">
            Crie categorias para organizar suas receitas e despesas.
          </p>
        </Card>
      ) : (
        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {categories.map((category) => (
            <Card key={category.id} className="p-5">
              <div className="flex items-start justify-between gap-3">
                <div className="flex min-w-0 items-center gap-3">
                  <span
                    className="size-3 shrink-0 rounded-full"
                    style={{
                      backgroundColor: category.color ?? '#94a3b8',
                    }}
                  />
                  <div className="min-w-0">
                    <p className="truncate font-semibold">{category.name}</p>
                    <span
                      className={cn(
                        'mt-1 inline-block rounded-full px-2 py-0.5 text-xs font-medium',
                        category.type === 'INCOME'
                          ? 'bg-income/10 text-income'
                          : 'bg-expense/10 text-expense',
                      )}
                    >
                      {TYPE_LABELS[category.type]}
                    </span>
                  </div>
                </div>
                <div className="flex shrink-0 gap-1">
                  <Button
                    variant="ghost"
                    size="icon"
                    aria-label={`Editar ${category.name}`}
                    onClick={() => openEditDialog(category)}
                  >
                    <Pencil className="size-4" />
                  </Button>
                  <Button
                    variant="ghost"
                    size="icon"
                    className="text-expense hover:text-expense"
                    aria-label={`Excluir ${category.name}`}
                    disabled={deletingId === String(category.id)}
                    onClick={() => handleDelete(category)}
                  >
                    {deletingId === String(category.id) ? (
                      <Loader2 className="size-4 animate-spin" />
                    ) : (
                      <Trash2 className="size-4" />
                    )}
                  </Button>
                </div>
              </div>
            </Card>
          ))}
        </div>
      )}

      <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>
              {editing ? 'Editar categoria' : 'Nova categoria'}
            </DialogTitle>
            <DialogDescription>
              {editing
                ? 'Atualize os dados da categoria.'
                : 'Crie uma categoria para classificar seus lançamentos.'}
            </DialogDescription>
          </DialogHeader>

          <form className="space-y-5" onSubmit={handleSubmit(onSubmit)} noValidate>
            <div className="space-y-2">
              <Label htmlFor="category-name">Nome</Label>
              <Input
                id="category-name"
                placeholder="Ex.: Mercado"
                {...register('name')}
              />
              {errors.name && (
                <p className="text-sm text-expense">{errors.name.message}</p>
              )}
            </div>

            <div className="space-y-2">
              <Label>Tipo</Label>
              <input type="hidden" {...register('type')} />
              <div className="grid grid-cols-2 gap-3">
                <button
                  type="button"
                  onClick={() => setValue('type', 'INCOME')}
                  className={cn(
                    'h-11 rounded-xl border px-4 py-2 text-sm font-medium transition-colors',
                    type === 'INCOME'
                      ? 'border-income bg-income/10 text-income'
                      : 'border-border bg-surface text-muted',
                  )}
                >
                  Receita
                </button>
                <button
                  type="button"
                  onClick={() => setValue('type', 'EXPENSE')}
                  className={cn(
                    'h-11 rounded-xl border px-4 py-2 text-sm font-medium transition-colors',
                    type === 'EXPENSE'
                      ? 'border-expense bg-expense/10 text-expense'
                      : 'border-border bg-surface text-muted',
                  )}
                >
                  Despesa
                </button>
              </div>
            </div>

            <div className="space-y-2">
              <Label htmlFor="category-color">Cor</Label>
              <div className="flex items-center gap-3">
                <input
                  id="category-color"
                  type="color"
                  value={color}
                  onChange={(event) => setColor(event.target.value)}
                  className="h-11 w-16 cursor-pointer rounded-xl border border-border bg-surface p-1"
                />
                <span className="text-xs text-muted">
                  Usada para identificar a categoria nos gráficos.
                </span>
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
                {editing ? 'Salvar alterações' : 'Criar categoria'}
              </Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>
    </div>
  )
}
