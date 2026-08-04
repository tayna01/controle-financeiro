import { Link } from 'react-router-dom'
import { ShieldCheck } from 'lucide-react'
import { PageHeader } from '@/components/page-header'
import { Card } from '@/components/ui/card'

export function Profile() {
  return (
    <div className="space-y-6">
      <PageHeader
        title="Perfil"
        description="Gerencie seus dados e preferências da conta"
        breadcrumbs={[
          { label: 'Início', to: '/app/dashboard' },
          { label: 'Perfil' },
        ]}
      />

      <div className="grid gap-6 md:grid-cols-2">
        <Card className="transition-colors hover:border-primary/40">
          <Link
            to="/app/perfil/senha"
            className="flex items-center justify-between gap-4 p-6"
          >
            <div className="flex items-center gap-4">
              <div className="flex size-12 shrink-0 items-center justify-center rounded-xl bg-primary/10 text-primary">
                <ShieldCheck className="size-6" />
              </div>
              <div>
                <h3 className="font-semibold">Segurança</h3>
                <p className="text-sm text-muted">Altere a senha da sua conta</p>
              </div>
            </div>
            <span className="text-sm font-semibold text-primary">Alterar</span>
          </Link>
        </Card>
      </div>
    </div>
  )
}
