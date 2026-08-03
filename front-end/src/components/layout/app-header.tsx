import { MobileNav } from '@/components/layout/sidebar'
import { ThemeToggle } from '@/components/theme-toggle'
import { useAuth } from '@/contexts/auth-context'

export function AppHeader() {
  const { user } = useAuth()

  return (
    <header className="flex h-16 items-center justify-between gap-4 border-b border-border bg-background px-4 lg:justify-end lg:px-8">
      <MobileNav />

      <div className="flex items-center gap-4">
        {user && (
          <div className="flex items-center gap-2.5">
            <div className="flex size-9 items-center justify-center rounded-full bg-primary/10 text-sm font-bold text-primary">
              {user.nome.charAt(0).toUpperCase()}
            </div>
            <span className="hidden text-sm font-semibold sm:block">
              {user.nome}
            </span>
          </div>
        )}
        <ThemeToggle />
      </div>
    </header>
  )
}
