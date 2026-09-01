import { MobileNav } from '@/components/layout/sidebar'
import { UserMenu } from '@/components/layout/user-menu'
import { WalletSelector } from '@/components/layout/wallet-selector'

export function AppHeader() {
  return (
    <header className="flex h-16 items-center justify-between gap-4 border-b border-border bg-background px-4 lg:justify-end lg:px-8">
      <MobileNav />
      <div className="flex items-center gap-2">
        <WalletSelector />
        <UserMenu />
      </div>
    </header>
  )
}
