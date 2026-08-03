import { Outlet } from 'react-router-dom'
import { AppHeader } from '@/components/layout/app-header'
import { Sidebar } from '@/components/layout/sidebar'

export function AppLayout() {
  return (
    <div className="flex min-h-screen bg-background text-foreground">
      <Sidebar open={false} onClose={() => {}} />

      <div className="flex min-w-0 flex-1 flex-col">
        <AppHeader />
        <main className="flex-1 p-4 lg:p-8">
          <Outlet />
        </main>
      </div>
    </div>
  )
}
