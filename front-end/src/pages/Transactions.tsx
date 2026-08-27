import { Plus } from 'lucide-react'
import { PageHeader } from '@/components/page-header'
import { Button } from '@/components/ui/button'
import { useTransactions } from '@/hooks/use-transactions'
import { TransactionFilters } from '@/components/transactions/transaction-filters'
import { TransactionList } from '@/components/transactions/transaction-list'
import { TransactionForm } from '@/components/transactions/transaction-form'

export function Transactions() {
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
    formError,
    deletingId,
    setFilterType,
    setFilterCategory,
    setFilterStartDate,
    setPage,
    setDialogOpen,
    openCreateDialog,
    openEditDialog,
    handleSave,
    handleDelete,
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
          <Button onClick={openCreateDialog}>
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
        onEdit={openEditDialog}
        onDelete={handleDelete}
        onPageChange={setPage}
      />

      <TransactionForm
        open={dialogOpen}
        onOpenChange={setDialogOpen}
        editingTransaction={editingTransaction}
        categories={categories}
        saving={saving}
        formError={formError}
        onSubmit={handleSave}
      />
    </div>
  )
}
