import { Loader2, Pencil, Trash2 } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Card } from '@/components/ui/card'
import { cn } from '@/lib/utils'
import type { Category, CategoryType } from '@/services/categories'

const TYPE_LABELS: Record<CategoryType, string> = {
  INCOME: 'Receita',
  EXPENSE: 'Despesa',
}

interface CategoryCardProps {
  category: Category
  isDeleting: boolean
  onEdit: (category: Category) => void
  onDelete: (category: Category) => void
}

export function CategoryCard({
  category,
  isDeleting,
  onEdit,
  onDelete,
}: CategoryCardProps) {
  return (
    <Card className="p-5">
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
            onClick={() => onEdit(category)}
          >
            <Pencil className="size-4" />
          </Button>
          <Button
            variant="ghost"
            size="icon"
            className="text-expense hover:text-expense"
            aria-label={`Excluir ${category.name}`}
            disabled={isDeleting}
            onClick={() => onDelete(category)}
          >
            {isDeleting ? (
              <Loader2 className="size-4 animate-spin" />
            ) : (
              <Trash2 className="size-4" />
            )}
          </Button>
        </div>
      </div>
    </Card>
  )
}
