import { api } from '@/lib/api'
import { getCurrentUser } from '@/services/auth'

const WALLET_KEY = 'walletId'

export interface Wallet {
  id: number
  name: string
  description: string | null
  ownerName: string | null
  memberCount: number
  createdAt: string
}

interface MemberResponse {
  userId: number
  name: string
  email: string
  role: string
  joinedAt: string
}

interface WalletResponse {
  id: number
  name: string
  description: string | null
  owner: MemberResponse | null
  members: MemberResponse[] | null
  createdAt: string
}

function toWallet(response: WalletResponse): Wallet {
  return {
    id: response.id,
    name: response.name,
    description: response.description,
    ownerName: response.owner?.name ?? null,
    memberCount: Array.isArray(response.members) ? response.members.length : 0,
    createdAt: response.createdAt,
  }
}

export function isWalletOwnedBy(
  wallet: Wallet,
  userName: string | null | undefined,
): boolean {
  return Boolean(
    userName &&
      wallet.ownerName &&
      wallet.ownerName.toLowerCase() === userName.toLowerCase(),
  )
}

export async function listWallets(): Promise<Wallet[]> {
  const { data } = await api.get<WalletResponse[]>('/api/v1/wallets')
  return (Array.isArray(data) ? data : []).map(toWallet)
}

export function getCachedWalletId(): number | null {
  const cached = localStorage.getItem(WALLET_KEY)
  return cached ? Number(cached) : null
}

export function setCachedWalletId(walletId: number): void {
  localStorage.setItem(WALLET_KEY, String(walletId))
}

export function clearCachedWallet(): void {
  localStorage.removeItem(WALLET_KEY)
}

let ensuringDefault: Promise<Wallet | null> | null = null

export async function ensureOwnedDefaultWallet(
  userName: string | null,
): Promise<Wallet | null> {
  if (ensuringDefault) {
    return ensuringDefault
  }

  ensuringDefault = (async () => {
    try {
      const wallets = await listWallets()
      const owned = wallets.find((wallet) => isWalletOwnedBy(wallet, userName))
      if (owned) {
        return owned
      }

      const { data } = await api.post<WalletResponse>('/api/v1/wallets', {
        name: 'Carteira Pessoal',
      })
      return toWallet(data)
    } finally {
      ensuringDefault = null
    }
  })()

  return ensuringDefault
}

export async function getOrCreateDefaultWallet(): Promise<number> {
  const cached = getCachedWalletId()
  if (cached) {
    return cached
  }

  const user = getCurrentUser()
  const wallet = await ensureOwnedDefaultWallet(user?.nome ?? null)
  if (!wallet) {
    throw new Error('Não foi possível criar uma carteira padrão.')
  }

  setCachedWalletId(wallet.id)
  return wallet.id
}