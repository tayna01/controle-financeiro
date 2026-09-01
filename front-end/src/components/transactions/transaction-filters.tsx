import { Card } from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import {
  Select,
  SelectTrigger,
  SelectValue,
  SelectContent,
  SelectItem,
} from '@/components/ui/select'
import type { Category } from '@/services/categories'
import type { TransactionType } from '@/services/transactions'

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
  function handleTypeChange(value: string) {
    onFilterTypeChange(value as '' | TransactionType)
    onResetPage()
  }

  function handleCategoryChange(value: string) {
    onFilterCategoryChange(value)
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
      <div className="space-y-2 w-full">
        <Label htmlFor="filter-type">Tipo</Label>
        <Select value={filterType} onValueChange={handleTypeChange}>
          <SelectTrigger id="filter-type">
            <SelectValue placeholder="Todos" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="">Todos</SelectItem>
            <SelectItem value="income">Receitas</SelectItem>
            <SelectItem value="expense">Despesas</SelectItem>
          </SelectContent>
        </Select>
      </div>

      <div className="space-y-2 w-full">
        <Label htmlFor="filter-category">Categoria</Label>
        <Select value={filterCategory} onValueChange={handleCategoryChange}>
          <SelectTrigger id="filter-category">
            <SelectValue placeholder="Todas" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="">Todas</SelectItem>
            {categories.map((category) => (
              <SelectItem key={category.id} value={String(category.id)}>
                {category.name}
              </SelectItem>
            ))}
          </SelectContent>
        </Select>
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
