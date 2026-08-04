import { ArrowDownLeft, ArrowUpRight } from 'lucide-react'
import { Card } from '@/components/ui/card'
import {
  formatCurrency,
  formatDate,
  type Transaction,
} from '@/services/transactions'
import { cn } from '@/lib/utils'

interface RecentTransactionsProps {
  transactions: Transaction[]
}

export function RecentTransactions({ transactions }: RecentTransactionsProps) {
  const sorted = [...transactions]
    .sort((a, b) => b.date.localeCompare(a.date))
    .slice(0, 8)

  return (
    <Card className="p-6">
      <div className="flex items-center justify-between">
        <h3 className="font-semibold">Lançamentos recentes</h3>
        <span className="text-sm text-muted">{transactions.length} no mês</span>
      </div>

      <ul className="mt-2 divide-y divide-border">
        {sorted.map((transaction) => {
          const isIncome = transaction.type === 'income'
          return (
            <li
              key={transaction.id}
              className="flex items-center justify-between gap-4 py-3"
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
                    {transaction.description}
                  </p>
                  <p className="text-xs text-muted">
                    {transaction.category} · {formatDate(transaction.date)}
                  </p>
                </div>
              </div>
              <p
                className={cn(
                  'shrink-0 text-sm font-semibold',
                  isIncome ? 'text-income' : 'text-expense',
                )}
              >
                {isIncome ? '+' : '-'}
                {formatCurrency(transaction.amount)}
              </p>
            </li>
          )
        })}
      </ul>
    </Card>
  )
}
