import { api } from '@/lib/api'

export interface Wallet {
  id: number
  name: string
  description: string | null
  createdAt: string
}

const WALLET_KEY = 'walletId'

interface WalletResponse {
  id: number
  name: string
}

async function listWallets(): Promise<WalletResponse[]> {
  const { data } = await api.get<WalletResponse[]>('/api/v1/wallets')
  return Array.isArray(data) ? data : []
}

export async function getOrCreateDefaultWallet(): Promise<number> {
  const cached = localStorage.getItem(WALLET_KEY)
  if (cached) {
    return Number(cached)
  }

  const wallets = await listWallets()
  let wallet = wallets[0]

  if (!wallet) {
    const { data } = await api.post<WalletResponse>('/api/v1/wallets', {
      name: 'Carteira Pessoal',
    })
    wallet = data
  }

  localStorage.setItem(WALLET_KEY, String(wallet.id))
  return wallet.id
}

export function clearCachedWallet(): void {
  localStorage.removeItem(WALLET_KEY)
}
