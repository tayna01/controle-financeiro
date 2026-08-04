import { PageHeader } from '@/components/page-header'

export function Categories() {
  return (
    <div className="space-y-6">
      <PageHeader
        title="Categorias"
        description="Organize as categorias dos seus lançamentos"
        breadcrumbs={[
          { label: 'Início', to: '/app/dashboard' },
          { label: 'Categorias' },
        ]}
      />
    </div>
  )
}
