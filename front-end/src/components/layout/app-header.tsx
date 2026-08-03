import { ThemeToggle } from '@/components/theme-toggle'

export function AppHeader() {
  return (
    <header className="sticky top-0 z-30 flex h-16 items-center justify-end border-b border-border bg-background/80 px-4 backdrop-blur lg:px-8">
      <ThemeToggle />
    </header>
  )
}
