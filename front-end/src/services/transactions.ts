export type TransactionType = 'income' | 'expense'

export interface Transaction {
  id: string
  description: string
  amount: number
  type: TransactionType
  category: string
  date: string
}

const MOCK_TRANSACTIONS: Transaction[] = [
  { id: '1', description: 'Salário', amount: 5200, type: 'income', category: 'Salário', date: '2026-08-01' },
  { id: '2', description: 'Freelance', amount: 850, type: 'income', category: 'Freelance', date: '2026-08-02' },
  { id: '3', description: 'Rendimento', amount: 120, type: 'income', category: 'Investimentos', date: '2026-08-03' },
  { id: '4', description: 'Aluguel', amount: 1450, type: 'expense', category: 'Moradia', date: '2026-08-01' },
  { id: '5', description: 'Mercado', amount: 320.5, type: 'expense', category: 'Alimentação', date: '2026-08-01' },
  { id: '6', description: 'Combustível', amount: 185, type: 'expense', category: 'Transporte', date: '2026-08-02' },
  { id: '7', description: 'Restaurante', amount: 98, type: 'expense', category: 'Alimentação', date: '2026-08-02' },
  { id: '8', description: 'Cinema', amount: 60, type: 'expense', category: 'Lazer', date: '2026-08-02' },
  { id: '9', description: 'Academia', amount: 89.9, type: 'expense', category: 'Saúde', date: '2026-08-03' },
  { id: '10', description: 'Streaming', amount: 54.9, type: 'expense', category: 'Lazer', date: '2026-08-03' },
  { id: '11', description: 'Uber', amount: 42.3, type: 'expense', category: 'Transporte', date: '2026-08-03' },
  { id: '12', description: 'Farmácia', amount: 76.1, type: 'expense', category: 'Saúde', date: '2026-08-03' },
]

export function fetchTransactions(): Promise<Transaction[]> {
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve(MOCK_TRANSACTIONS)
    }, 500)
  })
}

export function formatCurrency(value: number): string {
  return new Intl.NumberFormat('pt-BR', {
    style: 'currency',
    currency: 'BRL',
  }).format(value)
}

export function formatDate(date: string): string {
  return new Intl.DateTimeFormat('pt-BR', {
    day: '2-digit',
    month: 'short',
  }).format(new Date(date))
}
