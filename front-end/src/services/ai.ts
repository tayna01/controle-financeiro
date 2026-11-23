import { api } from '@/lib/api'

export interface CategorizationResult {
  categoryId: number | null
  categoryName: string | null
}

export async function categorizeTransaction(
  description: string,
): Promise<CategorizationResult | null> {
  try {
    const { data } = await api.post<CategorizationResult>('/api/v1/ai/categorize', {
      description,
    })
    return data
  } catch {
    return null
  }
}

export async function fetchInsights(
  walletId: number,
  startDate?: string,
  endDate?: string,
): Promise<string | null> {
  try {
    const { data } = await api.get<{ insight: string }> (
      `/api/v1/wallets/${walletId}/insights`,
      { params: { startDate, endDate } },
    )
    return data.insight
  } catch {
    return null
  }
}