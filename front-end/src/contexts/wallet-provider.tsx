import { useCallback, useEffect, useState, type ReactNode } from 'react'
import { WalletContext } from '@/contexts/wallet-context'
import { useAuth } from '@/contexts/auth-context'
import {
  ensureOwnedDefaultWallet,
  getCachedWalletId,
  isWalletOwnedBy,
  listWallets,
  setCachedWalletId,
  type Wallet,
} from '@/services/wallets'

function resolveSelection(
  wallets: Wallet[],
  userName: string | null | undefined,
): Wallet | null {
  const cachedId = getCachedWalletId()
  return (
    wallets.find((wallet) => wallet.id === cachedId) ??
    wallets.find((wallet) => isWalletOwnedBy(wallet, userName)) ??
    wallets[0] ??
    null
  )
}

export function WalletProvider({ children }: { children: ReactNode }) {
  const { user } = useAuth()
  const [wallets, setWallets] = useState<Wallet[]>([])
  const [selectedWallet, setSelectedWallet] = useState<Wallet | null>(null)
  const [loading, setLoading] = useState(true)
  const [loadError, setLoadError] = useState<string | null>(null)

  const refresh = useCallback(async () => {
    try {
      let data = await listWallets()
      if (!data.some((wallet) => isWalletOwnedBy(wallet, user?.nome))) {
        const created = await ensureOwnedDefaultWallet(user?.nome ?? null)
        if (created) {
          setCachedWalletId(created.id)
          data = await listWallets()
        }
      }
      data = [...data].sort((a, b) => {
        const aOwned = isWalletOwnedBy(a, user?.nome)
        const bOwned = isWalletOwnedBy(b, user?.nome)
        if (aOwned !== bOwned) {
          return aOwned ? -1 : 1
        }
        return 0
      })
      const selection = resolveSelection(data, user?.nome)
      setWallets(data)
      setSelectedWallet(selection)
      setLoadError(null)
      if (selection) {
        setCachedWalletId(selection.id)
      }
    } catch {
      setLoadError('Não foi possível carregar as carteiras.')
    }
  }, [user])

  useEffect(() => {
    let active = true
    async function load() {
      await refresh()
      if (active) {
        setLoading(false)
      }
    }
    load()
    return () => {
      active = false
    }
  }, [refresh])

  useEffect(() => {
    function handleFocusChange() {
      void refresh()
    }
    function handleVisibilityChange() {
      if (document.visibilityState === 'visible') {
        void refresh()
      }
    }
    window.addEventListener('focus', handleFocusChange)
    document.addEventListener('visibilitychange', handleVisibilityChange)
    return () => {
      window.removeEventListener('focus', handleFocusChange)
      document.removeEventListener('visibilitychange', handleVisibilityChange)
    }
  }, [refresh])

  function selectWallet(walletId: number) {
    const wallet = wallets.find((item) => item.id === walletId)
    if (!wallet) return
    setCachedWalletId(walletId)
    setSelectedWallet(wallet)
  }

  const canEdit =
    selectedWallet !== null && isWalletOwnedBy(selectedWallet, user?.nome)

  return (
    <WalletContext.Provider
      value={{
        wallets,
        selectedWallet,
        canEdit,
        loading,
        loadError,
        selectWallet,
      }}
    >
      {children}
    </WalletContext.Provider>
  )
}