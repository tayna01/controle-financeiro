import { PageHeader } from '@/components/page-header'

export function Profile() {
  return (
    <div className="space-y-6">
      <PageHeader
        title="Perfil"
        description="Gerencie seus dados e preferências da conta"
        breadcrumbs={[
          { label: 'Início', to: '/dashboard' },
          { label: 'Perfil' },
        ]}
      />
    </div>
  )
}
