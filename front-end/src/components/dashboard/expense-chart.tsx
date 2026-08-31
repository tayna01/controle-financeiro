import {
  Cell,
  Legend,
  Pie,
  PieChart,
  ResponsiveContainer,
  Tooltip,
} from 'recharts'
import { Card } from '@/components/ui/card'
import { type Transaction } from '@/services/transactions'
import { formatCurrency } from '@/lib/utils'

const COLORS = [
  '#7c3aed',
  '#d946ef',
  '#16a34a',
  '#e11d48',
  '#f59e0b',
  '#3b82f6',
  '#06b6d4',
]

interface ExpenseChartProps {
  transactions: Transaction[]
}

export function ExpenseChart({ transactions }: ExpenseChartProps) {
  const expensesByCategory = transactions
    .filter((transaction) => transaction.type === 'expense')
    .reduce<Record<string, number>>((acc, transaction) => {
      acc[transaction.category] =
        (acc[transaction.category] ?? 0) + transaction.amount
      return acc
    }, {})

  const data = Object.entries(expensesByCategory).map(([name, value]) => ({
    name,
    value,
  }))

  return (
    <Card className="p-6">
      <h3 className="font-semibold">Despesas por categoria</h3>
      <p className="text-sm text-muted">Distribuição das despesas do mês</p>

      {data.length === 0 ? (
        <p className="mt-16 text-center text-sm text-muted">
          Nenhuma despesa neste mês
        </p>
      ) : (
        <div className="mt-4 h-72">
          <ResponsiveContainer width="100%" height="100%">
            <PieChart>
              <Pie
                data={data}
                dataKey="value"
                nameKey="name"
                innerRadius={60}
                outerRadius={100}
                paddingAngle={2}
              >
                {data.map((entry, index) => (
                  <Cell key={entry.name} fill={COLORS[index % COLORS.length]} />
                ))}
              </Pie>
              <Tooltip
                formatter={(value) => formatCurrency(Number(value))}
                contentStyle={{
                  backgroundColor: 'var(--surface)',
                  border: '1px solid var(--border)',
                  borderRadius: 12,
                }}
              />
              <Legend verticalAlign="bottom" />
            </PieChart>
          </ResponsiveContainer>
        </div>
      )}
    </Card>
  )
}
