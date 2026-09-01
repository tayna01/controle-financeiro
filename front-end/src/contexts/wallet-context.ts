import { createContext, useContext } from 'react'
import type { Wallet } from '@/services/wallets'

interface WalletContextValue {
  wallets: Wallet[]
  selectedWallet: Wallet | null
  loading: boolean
  loadError: string | null
  selectWallet: (walletId: number) => void
}

export const WalletContext = createContext<WalletContextValue | null>(null)

export function useWallet(): WalletContextValue {
  const context = useContext(WalletContext)
  if (!context) {
    throw new Error('useWallet deve ser usado dentro de <WalletProvider>')
  }
  return context
}