import { useEffect, useState } from 'react'
import { PageHeader } from '@/components/page-header'
import { ExpenseChart } from '@/components/dashboard/expense-chart'
import { RecentTransactions } from '@/components/dashboard/recent-transactions'
import { SummaryCards } from '@/components/dashboard/summary-cards'
import { Card } from '@/components/ui/card'
import { Skeleton } from '@/components/ui/skeleton'
import {
  fetchSummary,
  fetchTransactions,
  type Summary,
  type Transaction,
} from '@/services/transactions'
import { useWallet } from '@/contexts/wallet-context'
import { getApiErrorMessage } from '@/lib/api'

function DashboardSkeleton() {
  return (
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
  )
}

interface Failure {
  walletId: number
  message: string
}

export function Dashboard() {
  const {
    selectedWallet,
    loading: walletLoading,
    loadError: walletError,
  } = useWallet()
  const walletId = selectedWallet?.id
  const [summary, setSummary] = useState<Summary | null>(null)
  const [transactions, setTransactions] = useState<Transaction[]>([])
  const [loadedWalletId, setLoadedWalletId] = useState<number | null>(null)
  const [failure, setFailure] = useState<Failure | null>(null)

  useEffect(() => {
    if (!walletId) return
    let active = true
    Promise.all([fetchSummary(walletId), fetchTransactions(walletId)])
      .then(([summaryData, transactionData]) => {
        if (active) {
          setSummary(summaryData)
          setTransactions(transactionData)
          setLoadedWalletId(walletId)
          setFailure(null)
        }
      })
      .catch((error) => {
        if (active) {
          setFailure({
            walletId,
            message: getApiErrorMessage(
              error,
              'Não foi possível carregar o resumo da carteira.',
            ),
          })
        }
      })
    return () => {
      active = false
    }
  }, [walletId])

  const walletReady = walletId !== null && loadedWalletId === walletId
  const walletFailure =
    walletId !== null && failure !== null && failure.walletId === walletId

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

      {!walletId ? (
        walletLoading ? (
          <DashboardSkeleton />
        ) : (
          <Card className="p-6 text-sm text-muted" role="alert">
            {walletError ??
              'Nenhuma carteira selecionada. Escolha uma carteira no topo para visualizar o resumo.'}
          </Card>
        )
      ) : walletFailure ? (
        <Card className="p-6 text-sm text-muted" role="alert">
          {failure?.message}
        </Card>
      ) : walletReady && summary ? (
        <>
          <SummaryCards
            balance={summary.balance}
            income={summary.totalIncome}
            expense={summary.totalExpense}
          />
          <div className="grid gap-6 lg:grid-cols-2">
            <ExpenseChart transactions={transactions} />
            <RecentTransactions transactions={transactions} />
          </div>
        </>
      ) : (
        <DashboardSkeleton />
      )}
    </div>
  )
}
