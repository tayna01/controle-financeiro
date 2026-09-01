import { Loader2, Wallet } from 'lucide-react'
import {
  Select,
  SelectContent,
  SelectGroup,
  SelectItem,
  SelectLabel,
  SelectTrigger,
} from '@/components/ui/select'
import { useWallet } from '@/contexts/wallet-context'
import { useAuth } from '@/contexts/auth-context'
import { cn } from '@/lib/utils'
import { isWalletOwnedBy } from '@/services/wallets'

export function WalletSelector() {
  const { wallets, selectedWallet, loading, loadError, selectWallet } =
    useWallet()
  const { user } = useAuth()

  const isDisabled = loading || wallets.length === 0
  const ownedWallets = wallets.filter((wallet) =>
    isWalletOwnedBy(wallet, user?.nome),
  )
  const sharedWallets = wallets.filter((wallet) => !ownedWallets.includes(wallet))
  const selectedIsShared =
    selectedWallet !== null && sharedWallets.includes(selectedWallet)

  return (
    <div className="flex items-center gap-2">
      <Select
        value={selectedWallet ? String(selectedWallet.id) : undefined}
        onValueChange={(value) => selectWallet(Number(value))}
        disabled={isDisabled}
      >
        <SelectTrigger
          size="sm"
          className={cn(
            'w-auto max-w-52 gap-2 lg:max-w-64',
            !isDisabled && 'cursor-pointer',
          )}
          aria-label="Selecionar carteira"
        >
          {loading ? (
            <Loader2 className="size-4 shrink-0 animate-spin" />
          ) : (
            <Wallet className="size-4 shrink-0" />
          )}
          <span className="truncate font-medium">
            {loadError
              ? 'Carteiras indisponíveis'
              : selectedWallet
                ? selectedWallet.name +
                  (selectedIsShared && selectedWallet.ownerName
                    ? ` · ${selectedWallet.ownerName}`
                    : '')
                : 'Nenhuma carteira'}
          </span>
        </SelectTrigger>
        <SelectContent align="end">
          <SelectGroup>
            <SelectLabel>Suas carteiras</SelectLabel>
            {ownedWallets.map((wallet) => (
              <SelectItem key={wallet.id} value={String(wallet.id)}>
                <span className="truncate">{wallet.name}</span>
              </SelectItem>
            ))}
          </SelectGroup>
          {sharedWallets.length > 0 && (
            <SelectGroup>
              <SelectLabel>Compartilhadas com você</SelectLabel>
              {sharedWallets.map((wallet) => (
                <SelectItem key={wallet.id} value={String(wallet.id)}>
                  <span className="flex items-center gap-1">
                    <span className="truncate">{wallet.name}</span>
                    {wallet.ownerName && (
                      <span className="truncate text-muted">
                        {'·'} {wallet.ownerName}
                      </span>
                    )}
                  </span>
                </SelectItem>
              ))}
            </SelectGroup>
          )}
        </SelectContent>
      </Select>
    </div>
  )
}