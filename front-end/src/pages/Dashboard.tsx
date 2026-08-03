import { PageHeader } from '@/components/page-header'

export function Dashboard() {
  return (
    <div className="space-y-6">
      <PageHeader
        title="Dashboard"
        description="Resumo financeiro de agosto de 2026"
        breadcrumbs={[
          { label: 'Início', to: '/dashboard' },
          { label: 'Dashboard' },
        ]}
      />
    </div>
  )
}
