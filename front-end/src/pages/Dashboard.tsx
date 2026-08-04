import { useEffect, useState } from 'react'
import { PageHeader } from '@/components/page-header'
import { ExpenseChart } from '@/components/dashboard/expense-chart'
import { RecentTransactions } from '@/components/dashboard/recent-transactions'
import { SummaryCards } from '@/components/dashboard/summary-cards'
import { fetchTransactions, type Transaction } from '@/services/transactions'

export function Dashboard() {
  const [transactions, setTransactions] = useState<Transaction[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    let active = true
    fetchTransactions().then((data) => {
      if (active) {
        setTransactions(data)
        setLoading(false)
      }
    })
    return () => {
      active = false
    }
  }, [])

  const income = transactions
    .filter((transaction) => transaction.type === 'income')
    .reduce((sum, transaction) => sum + transaction.amount, 0)

  const expense = transactions
    .filter((transaction) => transaction.type === 'expense')
    .reduce((sum, transaction) => sum + transaction.amount, 0)

  const balance = income - expense

  return (
    <div className="space-y-6">
      <PageHeader
        title="Dashboard"
        description="Resumo financeiro de agosto de 2026"
        breadcrumbs={[
          { label: 'Início', to: '/app/dashboard' },
          { label: 'Dashboard' },
        ]}
      />

      {loading ? (
        <div className="space-y-6" aria-label="Carregando dashboard">
          <div className="grid gap-4 md:grid-cols-3">
            {Array.from({ length: 3 }, (_, index) => (
              <div
                key={index}
                className="h-28 animate-pulse rounded-2xl border border-border bg-surface"
              />
            ))}
          </div>
          <div className="grid gap-6 lg:grid-cols-2">
            <div className="h-72 animate-pulse rounded-2xl border border-border bg-surface" />
            <div className="h-72 animate-pulse rounded-2xl border border-border bg-surface" />
          </div>
        </div>
      ) : (
        <>
          <SummaryCards balance={balance} income={income} expense={expense} />
          <div className="grid gap-6 lg:grid-cols-2">
            <ExpenseChart transactions={transactions} />
            <RecentTransactions transactions={transactions} />
          </div>
        </>
      )}
    </div>
  )
}
