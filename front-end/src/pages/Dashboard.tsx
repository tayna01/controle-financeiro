import { useEffect, useState } from 'react'
import { PageHeader } from '@/components/page-header'
import { ExpenseChart } from '@/components/dashboard/expense-chart'
import { RecentTransactions } from '@/components/dashboard/recent-transactions'
import { SummaryCards } from '@/components/dashboard/summary-cards'
import { Skeleton } from '@/components/ui/skeleton'
import { fetchTransactions, type Transaction } from '@/services/transactions'
import { useWallet } from '@/contexts/wallet-context'

export function Dashboard() {
  const { selectedWallet } = useWallet()
  const walletId = selectedWallet?.id
  const [transactions, setTransactions] = useState<Transaction[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    if (!walletId) return
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
  }, [walletId])

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
              <Skeleton key={index} className="h-28 rounded-2xl" />
            ))}
          </div>
          <div className="grid gap-6 lg:grid-cols-2">
            <Skeleton className="h-72 rounded-2xl" />
            <Skeleton className="h-72 rounded-2xl" />
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
