import { api } from '@/lib/api'
import { getOrCreateDefaultWallet } from '@/services/wallets'

export type TransactionType = 'income' | 'expense'

export interface Transaction {
  id: string
  description: string
  amount: number
  type: TransactionType
  category: string
  date: string
}

export interface TransactionPage {
  items: Transaction[]
  totalElements: number
  totalPages: number
  page: number
}

export interface TransactionInput {
  type: TransactionType
  amount: number
  description: string
  date: string
  categoryId?: number | null
}

export interface TransactionFilters {
  type?: TransactionType | null
  categoryId?: number | null
  startDate?: string | null
  endDate?: string | null
}

interface TransactionResponse {
  id: number
  type: 'INCOME' | 'EXPENSE'
  amount: number
  description: string | null
  date: string
  categoryId: number | null
  categoryName: string | null
}

interface PageResponse {
  content: TransactionResponse[]
  totalElements: number
  totalPages: number
  number: number
}

function toTransaction(response: TransactionResponse): Transaction {
  return {
    id: String(response.id),
    description: response.description ?? '',
    amount: Number(response.amount),
    type: response.type === 'INCOME' ? 'income' : 'expense',
    category: response.categoryName ?? '',
    date: response.date,
  }
}

async function resolveWalletId(): Promise<number> {
  return getOrCreateDefaultWallet()
}

export async function fetchTransactions(): Promise<Transaction[]> {
  const walletId = await resolveWalletId()
  const { data } = await api.get<PageResponse>(
    `/api/v1/wallets/${walletId}/transactions`,
    { params: { size: 100, sort: 'date,desc' } },
  )
  return data.content.map(toTransaction)
}

export async function listTransactions(
  filters: TransactionFilters,
  page = 0,
  size = 10,
): Promise<TransactionPage> {
  const walletId = await resolveWalletId()
  const { data } = await api.get<PageResponse>(
    `/api/v1/wallets/${walletId}/transactions`,
    {
      params: {
        page,
        size,
        sort: 'date,desc',
        type: filters.type ? filters.type.toUpperCase() : undefined,
        categoryId: filters.categoryId ?? undefined,
        startDate: filters.startDate || undefined,
        endDate: filters.endDate || undefined,
      },
    },
  )
  return {
    items: data.content.map(toTransaction),
    totalElements: data.totalElements,
    totalPages: data.totalPages,
    page: data.number,
  }
}

export async function fetchSummary(
  startDate?: string,
  endDate?: string,
): Promise<{
  totalIncome: number
  totalExpense: number
  balance: number
  transactionCount: number
}> {
  const walletId = await resolveWalletId()
  const { data } = await api.get<{
    totalIncome: number
    totalExpense: number
    balance: number
    transactionCount: number
  }>(`/api/v1/wallets/${walletId}/summary`, {
    params: { startDate, endDate },
  })
  return {
    totalIncome: Number(data.totalIncome),
    totalExpense: Number(data.totalExpense),
    balance: Number(data.balance),
    transactionCount: data.transactionCount,
  }
}

export async function createTransaction(
  input: TransactionInput,
): Promise<void> {
  const walletId = await resolveWalletId()
  await api.post(
    `/api/v1/wallets/${walletId}/transactions`,
    toRequestPayload(input),
  )
}

export async function updateTransaction(
  id: string,
  input: TransactionInput,
): Promise<void> {
  const walletId = await resolveWalletId()
  await api.put(
    `/api/v1/wallets/${walletId}/transactions/${id}`,
    toRequestPayload(input),
  )
}

function toRequestPayload(input: TransactionInput) {
  return {
    type: input.type.toUpperCase(),
    amount: input.amount,
    description: input.description || null,
    date: input.date,
    categoryId: input.categoryId ?? null,
  }
}

export async function deleteTransaction(id: string): Promise<void> {
  const walletId = await resolveWalletId()
  await api.delete(`/api/v1/wallets/${walletId}/transactions/${id}`)
}
