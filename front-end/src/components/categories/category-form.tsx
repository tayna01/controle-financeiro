import { useState } from 'react'
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
import { ToggleGroup, ToggleGroupItem } from '@/components/ui/toggle-group'
import type { Category, CategoryType } from '@/services/categories'

const categorySchema = z.object({
  name: z.string().min(1, 'Campo obrigatório').max(80, 'Máximo de 80 caracteres'),
  type: z.enum(['INCOME', 'EXPENSE']),
})

type CategoryFormData = z.infer<typeof categorySchema>

interface CategoryFormProps {
  open: boolean
  onOpenChange: (open: boolean) => void
  editing: Category | null
  saving: boolean
  onSubmit: (data: {
    name: string
    type: CategoryType
    color: string
    onFieldError?: (field: string, message: string) => void
  }) => Promise<void>
}

function getDefaultColor(type: CategoryType): string {
  return type === 'INCOME' ? '#16a34a' : '#dc2626'
}

function CategoryFormFields({
  editing,
  saving,
  onSubmit,
}: {
  editing: Category | null
  saving: boolean
  onSubmit: CategoryFormProps['onSubmit']
}) {
  const [color, setColor] = useState(
    () => editing?.color ?? getDefaultColor(editing?.type ?? 'EXPENSE'),
  )

  const { register, handleSubmit, control, setValue, setError, formState: { errors } } =
    useForm<CategoryFormData>({
      resolver: zodResolver(categorySchema),
      defaultValues: {
        name: editing?.name ?? '',
        type: editing?.type ?? 'EXPENSE',
      },
    })

  const type = useWatch({ control, name: 'type', defaultValue: editing?.type ?? 'EXPENSE' })

  function handleFormSubmit(data: CategoryFormData) {
    onSubmit({
      name: data.name,
      type: data.type,
      color,
      onFieldError: (field, message) =>
        setError(field as 'name', { type: 'manual', message }),
    })
  }

  return (
    <form className="space-y-5" onSubmit={handleSubmit(handleFormSubmit)} noValidate>
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
        <ToggleGroup
          type="single"
          variant="outline"
          value={type}
          onValueChange={(value) => {
            if (value) setValue('type', value as 'INCOME' | 'EXPENSE')
          }}
          className="w-full"
        >
          <ToggleGroupItem
            type="button"
            value="INCOME"
            className="flex-1 text-income data-[state=on]:bg-income data-[state=on]:text-white"
          >
            Receita
          </ToggleGroupItem>
          <ToggleGroupItem
            type="button"
            value="EXPENSE"
            className="flex-1 text-expense data-[state=on]:bg-expense data-[state=on]:text-white"
          >
            Despesa
          </ToggleGroupItem>
        </ToggleGroup>
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

      <DialogFooter>
        <Button type="submit" size="lg" className="w-full" disabled={saving}>
          {saving && <Loader2 className="size-5 animate-spin" />}
          {editing ? 'Salvar alterações' : 'Criar categoria'}
        </Button>
      </DialogFooter>
    </form>
  )
}

export function CategoryForm({
  open,
  onOpenChange,
  editing,
  saving,
  onSubmit,
}: CategoryFormProps) {
  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
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

        {open && (
          <CategoryFormFields
            key={editing?.id ?? 'new'}
            editing={editing}
            saving={saving}
            onSubmit={onSubmit}
          />
        )}
      </DialogContent>
    </Dialog>
  )
}
