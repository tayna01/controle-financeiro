import { Plus } from 'lucide-react'
import { PageHeader } from '@/components/page-header'
import { Button } from '@/components/ui/button'
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from '@/components/ui/alert-dialog'
import { useWallet } from '@/contexts/wallet-context'
import { useTransactions } from '@/hooks/use-transactions'
import { TransactionFilters } from '@/components/transactions/transaction-filters'
import { TransactionList } from '@/components/transactions/transaction-list'
import { TransactionForm } from '@/components/transactions/transaction-form'

export function Transactions() {
  const { canEdit } = useWallet()
  const {
    data,
    categories,
    loading,
    loadError,
    filterType,
    filterCategory,
    filterStartDate,
    filterEndDate,
    page,
    dialogOpen,
    editingTransaction,
    saving,
    deletingId,
    pendingDelete,
    setFilterType,
    setFilterCategory,
    setFilterStartDate,
    setPage,
    setDialogOpen,
    openCreateDialog,
    openEditDialog,
    handleSave,
    handleDelete,
    setPendingDelete,
  } = useTransactions()

  return (
    <div className="space-y-6">
      <PageHeader
        title="Transações"
        description="Gerencie suas receitas e despesas"
        breadcrumbs={[
          { label: 'Início', to: '/app/dashboard' },
          { label: 'Transações' },
        ]}
        actions={
          <Button onClick={openCreateDialog} disabled={!canEdit}>
            <Plus className="size-4" />
            Nova transação
          </Button>
        }
      />

      <TransactionFilters
        categories={categories}
        filterType={filterType}
        filterCategory={filterCategory}
        filterStartDate={filterStartDate}
        filterEndDate={filterEndDate}
        onFilterTypeChange={setFilterType}
        onFilterCategoryChange={setFilterCategory}
        onFilterStartDateChange={setFilterStartDate}
        onFilterEndDateChange={() => {}}
        onResetPage={() => setPage(0)}
      />

      <TransactionList
        data={data}
        loading={loading}
        loadError={loadError}
        deletingId={deletingId}
        page={page}
        canEdit={canEdit}
        onEdit={openEditDialog}
        onDelete={(id) =>
          setPendingDelete(data?.items.find((item) => item.id === id) ?? null)
        }
        onPageChange={setPage}
      />

      <TransactionForm
        open={dialogOpen}
        onOpenChange={setDialogOpen}
        editingTransaction={editingTransaction}
        categories={categories}
        saving={saving}
        onSubmit={handleSave}
      />

      <AlertDialog
        open={pendingDelete !== null}
        onOpenChange={(open) => {
          if (!open) setPendingDelete(null)
        }}
      >
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Excluir transação?</AlertDialogTitle>
            <AlertDialogDescription>
              Deseja excluir a transação &quot;
              {pendingDelete?.description || 'Sem descrição'}&quot;? Esta ação
              não pode ser desfeita.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel
              disabled={deletingId !== null}
              onClick={() => setPendingDelete(null)}
            >
              Cancelar
            </AlertDialogCancel>
            <AlertDialogAction
              className="bg-destructive text-white hover:bg-destructive/90"
              disabled={deletingId !== null}
              onClick={() => pendingDelete && handleDelete(pendingDelete.id)}
            >
              {deletingId !== null ? 'Excluindo...' : 'Excluir'}
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  )
}
