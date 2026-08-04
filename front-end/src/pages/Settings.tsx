import { PageHeader } from '@/components/page-header'

export function Settings() {
  return (
    <div className="space-y-6">
      <PageHeader
        title="Configurações"
        description="Personalize as preferências da sua conta"
        breadcrumbs={[
          { label: 'Início', to: '/app/dashboard' },
          { label: 'Configurações' },
        ]}
      />
    </div>
  )
}
