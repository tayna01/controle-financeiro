import { MobileNav } from '@/components/layout/sidebar'
import { ThemeToggle } from '@/components/theme-toggle'

export function AppHeader() {
  return (
    <header className="flex h-16 items-center justify-between gap-4 border-b border-border bg-background px-4 lg:justify-end lg:px-8">
      <MobileNav />
      <ThemeToggle />
    </header>
  )
}
