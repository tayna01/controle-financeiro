import {
  ArrowDownLeft,
  ArrowUpRight,
  Loader2,
  Pencil,
  Receipt,
  Trash2,
} from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Card } from '@/components/ui/card'
import {
  Pagination,
  PaginationContent,
  PaginationItem,
  PaginationNext,
  PaginationPrevious,
} from '@/components/ui/pagination'
import { Skeleton } from '@/components/ui/skeleton'
import { cn, formatCurrency, formatDate } from '@/lib/utils'
import { type Transaction, type TransactionPage } from '@/services/transactions'

interface TransactionListProps {
  data: TransactionPage | null
  loading: boolean
  loadError: string | null
  deletingId: string | null
  page: number
  onEdit: (id: string) => void
  onDelete: (id: string) => void
  onPageChange: (page: number) => void
}

export function TransactionList({
  data,
  loading,
  loadError,
  deletingId,
  page,
  onEdit,
  onDelete,
  onPageChange,
}: TransactionListProps) {
  return (
    <Card className="p-6">
      {loading ? (
        <div className="space-y-3">
          {Array.from({ length: 5 }, (_, index) => (
            <Skeleton key={index} className="h-12 rounded-xl" />
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
            {data.items.map((transaction) => (
              <TransactionRow
                key={transaction.id}
                transaction={transaction}
                isDeleting={deletingId === transaction.id}
                onEdit={onEdit}
                onDelete={onDelete}
              />
            ))}
          </ul>

          <div className="mt-4 flex flex-col items-center justify-between gap-3 border-t border-border pt-4 sm:flex-row">
            <span className="text-sm text-muted">
              {data.totalElements} transação(ões) · página {data.page + 1} de{' '}
              {Math.max(data.totalPages, 1)}
            </span>
            <Pagination>
              <PaginationContent>
                <PaginationItem>
                  <PaginationPrevious
                    href="#"
                    onClick={(event) => {
                      event.preventDefault()
                      if (data.page > 0 && !loading) onPageChange(page - 1)
                    }}
                    aria-disabled={data.page === 0 || loading}
                    className={
                      data.page === 0 || loading
                        ? 'pointer-events-none opacity-50'
                        : undefined
                    }
                  />
                </PaginationItem>
                <PaginationItem>
                  <PaginationNext
                    href="#"
                    onClick={(event) => {
                      event.preventDefault()
                      if (data.page < data.totalPages - 1 && !loading)
                        onPageChange(page + 1)
                    }}
                    aria-disabled={data.page >= data.totalPages - 1 || loading}
                    className={
                      data.page >= data.totalPages - 1 || loading
                        ? 'pointer-events-none opacity-50'
                        : undefined
                    }
                  />
                </PaginationItem>
              </PaginationContent>
            </Pagination>
          </div>
        </>
      )}
    </Card>
  )
}

function TransactionRow({
  transaction,
  isDeleting,
  onEdit,
  onDelete,
}: {
  transaction: Transaction
  isDeleting: boolean
  onEdit: (id: string) => void
  onDelete: (id: string) => void
}) {
  const isIncome = transaction.type === 'income'

  return (
    <li className="group flex items-center justify-between gap-4 py-3">
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
            onClick={() => onEdit(transaction.id)}
          >
            <Pencil className="size-4" />
          </Button>
          <Button
            variant="ghost"
            size="icon"
            className="text-expense hover:text-expense"
            aria-label={`Excluir ${transaction.description}`}
            disabled={isDeleting}
            onClick={() => onDelete(transaction.id)}
          >
            {isDeleting ? (
              <Loader2 className="size-4 animate-spin" />
            ) : (
              <Trash2 className="size-4" />
            )}
          </Button>
        </div>
      </div>
    </li>
  )
}
