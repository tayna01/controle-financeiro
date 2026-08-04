import { TrendingDown, TrendingUp, Wallet } from 'lucide-react'
import { Card } from '@/components/ui/card'
import { formatCurrency } from '@/services/transactions'
import { cn } from '@/lib/utils'

interface SummaryCardsProps {
  balance: number
  income: number
  expense: number
}

export function SummaryCards({ balance, income, expense }: SummaryCardsProps) {
  const cards = [
    {
      label: 'Saldo atual',
      value: balance,
      icon: Wallet,
      valueClass: 'text-foreground',
    },
    {
      label: 'Receitas do mês',
      value: income,
      icon: TrendingUp,
      valueClass: 'text-income',
    },
    {
      label: 'Despesas do mês',
      value: expense,
      icon: TrendingDown,
      valueClass: 'text-expense',
    },
  ]

  return (
    <div className="grid gap-4 md:grid-cols-3">
      {cards.map((card) => (
        <Card key={card.label} className="flex items-center gap-4 p-5">
          <div className="flex size-11 shrink-0 items-center justify-center rounded-xl bg-primary/10 text-primary">
            <card.icon className="size-5" />
          </div>
          <div className="min-w-0">
            <p className="text-sm text-muted">{card.label}</p>
            <p className={cn('truncate text-2xl font-bold', card.valueClass)}>
              {formatCurrency(card.value)}
            </p>
          </div>
        </Card>
      ))}
    </div>
  )
}
