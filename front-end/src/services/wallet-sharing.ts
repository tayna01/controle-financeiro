import { api, getApiErrorMessage } from '@/lib/api'
import { getOrCreateDefaultWallet } from '@/services/wallets'

export type WalletRole = 'DONO' | 'EDITOR' | 'VISUALIZADOR'

export interface WalletMember {
  userId: number
  name: string
  email: string
  role: WalletRole
  joinedAt: string
}

interface MemberResponse {
  userId: number
  name: string
  email: string
  role: WalletRole
  joinedAt: string
}

function toWalletMember(response: MemberResponse): WalletMember {
  return {
    userId: response.userId,
    name: response.name,
    email: response.email,
    role: response.role,
    joinedAt: response.joinedAt,
  }
}

function getUserFriendlyMessage(error: unknown, fallback: string): string {
  const message = getApiErrorMessage(error, '')
  if (!message) {
    return fallback
  }

  const text = message.toLowerCase()

  if (text.includes('não encontrado com o e-mail')) {
    return 'Nenhum usuário encontrado com este e-mail.'
  }

  if (text.includes('já é membro') || text.includes('já e membro')) {
    return 'Este usuário já é membro desta carteira.'
  }

  if (text.includes('somente o dono')) {
    return 'Somente o dono da carteira pode executar esta ação.'
  }

  if (text.includes('alterar o papel do dono')) {
    return 'Não é possível alterar o papel do dono da carteira.'
  }

  if (text.includes('remover o dono')) {
    return 'Não é possível remover o dono da carteira.'
  }

  if (text.includes('membro não encontrado')) {
    return 'Membro não encontrado.'
  }

  return message
}

async function resolveWalletId(): Promise<number> {
  return getOrCreateDefaultWallet()
}

export async function fetchWalletMembers(): Promise<WalletMember[]> {
  const walletId = await resolveWalletId()
  const { data } = await api.get<MemberResponse[]>(
    `/api/v1/wallets/${walletId}/members`,
  )
  return (Array.isArray(data) ? data : []).map(toWalletMember)
}

export interface AddMemberInput {
  email: string
  role: WalletRole
}

export async function addWalletMember(input: AddMemberInput): Promise<void> {
  const walletId = await resolveWalletId()
  try {
    await api.post(`/api/v1/wallets/${walletId}/members`, {
      email: input.email,
      role: input.role,
    })
  } catch (error) {
    throw new Error(
      getUserFriendlyMessage(error, 'Não foi possível compartilhar a carteira.'),
      { cause: error },
    )
  }
}

export async function updateWalletMemberRole(
  userId: number,
  role: WalletRole,
): Promise<void> {
  const walletId = await resolveWalletId()
  try {
    await api.patch(`/api/v1/wallets/${walletId}/members/${userId}`, { role })
  } catch (error) {
    throw new Error(
      getUserFriendlyMessage(error, 'Não foi possível alterar o papel do membro.'),
      { cause: error },
    )
  }
}

export async function removeWalletMember(userId: number): Promise<void> {
  const walletId = await resolveWalletId()
  try {
    await api.delete(`/api/v1/wallets/${walletId}/members/${userId}`)
  } catch (error) {
    throw new Error(
      getUserFriendlyMessage(error, 'Não foi possível remover o compartilhamento.'),
      { cause: error },
    )
  }
}
