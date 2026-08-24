import { api } from '@/lib/api'

export type CategoryType = 'INCOME' | 'EXPENSE'

export interface Category {
  id: number
  name: string
  type: CategoryType
  color: string | null
  icon: string | null
}

interface CategoryResponse {
  id: number
  name: string
  type: CategoryType
  color: string | null
  icon: string | null
}

function toCategory(response: CategoryResponse): Category {
  return {
    id: response.id,
    name: response.name,
    type: response.type,
    color: response.color,
    icon: response.icon,
  }
}

export async function fetchCategories(): Promise<Category[]> {
  const { data } = await api.get<CategoryResponse[]>('/api/v1/categories')
  return (Array.isArray(data) ? data : []).map(toCategory)
}

export interface CategoryInput {
  name: string
  type: CategoryType
  color?: string | null
}

export async function createCategory(input: CategoryInput): Promise<Category> {
  const { data } = await api.post<CategoryResponse>('/api/v1/categories', {
    name: input.name,
    type: input.type,
    color: input.color ?? null,
  })
  return toCategory(data)
}

export async function updateCategory(
  id: number,
  input: CategoryInput,
): Promise<Category> {
  const { data } = await api.put<CategoryResponse>(`/api/v1/categories/${id}`, {
    name: input.name,
    type: input.type,
    color: input.color ?? null,
  })
  return toCategory(data)
}

export async function deleteCategory(id: number): Promise<void> {
  await api.delete(`/api/v1/categories/${id}`)
}
