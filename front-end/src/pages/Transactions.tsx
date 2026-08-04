import { PageHeader } from '@/components/page-header'

export function Transactions() {
  return (
    <div className="space-y-6">
      <PageHeader
        title="Transações"
        description="Gerencie suas receitas e despesas"
        breadcrumbs={[
          { label: 'Início', to: '/app/dashboard' },
          { label: 'Transações' },
        ]}
      />
    </div>
  )
}
