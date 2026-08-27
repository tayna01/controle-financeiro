import { Card } from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import type { Category } from '@/services/categories'
import type { TransactionType } from '@/services/transactions'

const SELECT_CLASS =
  'flex h-11 w-full rounded-xl border border-border bg-surface px-4 py-2 text-sm focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-primary/40'

interface TransactionFiltersProps {
  categories: Category[]
  filterType: '' | TransactionType
  filterCategory: string
  filterStartDate: string
  filterEndDate: string
  onFilterTypeChange: (value: '' | TransactionType) => void
  onFilterCategoryChange: (value: string) => void
  onFilterStartDateChange: (value: string) => void
  onFilterEndDateChange: (value: string) => void
  onResetPage: () => void
}

export function TransactionFilters({
  categories,
  filterType,
  filterCategory,
  filterStartDate,
  filterEndDate,
  onFilterTypeChange,
  onFilterCategoryChange,
  onFilterStartDateChange,
  onFilterEndDateChange,
  onResetPage,
}: TransactionFiltersProps) {
  function handleTypeChange(event: React.ChangeEvent<HTMLSelectElement>) {
    onFilterTypeChange(event.target.value as '' | TransactionType)
    onResetPage()
  }

  function handleCategoryChange(event: React.ChangeEvent<HTMLSelectElement>) {
    onFilterCategoryChange(event.target.value)
    onResetPage()
  }

  function handleStartDateChange(event: React.ChangeEvent<HTMLInputElement>) {
    onFilterStartDateChange(event.target.value)
    onResetPage()
  }

  function handleEndDateChange(event: React.ChangeEvent<HTMLInputElement>) {
    onFilterEndDateChange(event.target.value)
    onResetPage()
  }

  return (
    <Card className="grid gap-4 p-5 md:grid-cols-4">
      <div className="space-y-2">
        <Label htmlFor="filter-type">Tipo</Label>
        <select
          id="filter-type"
          className={SELECT_CLASS}
          value={filterType}
          onChange={handleTypeChange}
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
          onChange={handleCategoryChange}
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
          onChange={handleStartDateChange}
        />
      </div>

      <div className="space-y-2">
        <Label htmlFor="filter-end">Data final</Label>
        <Input
          id="filter-end"
          type="date"
          value={filterEndDate}
          onChange={handleEndDateChange}
        />
      </div>
    </Card>
  )
}
