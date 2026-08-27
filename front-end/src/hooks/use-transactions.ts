import { useCallback, useEffect, useState } from 'react'
import {
  createTransaction,
  deleteTransaction,
  listTransactions,
  updateTransaction,
  type TransactionFilters,
  type TransactionInput,
  type TransactionPage,
  type TransactionType,
} from '@/services/transactions'
import {
  fetchCategories,
  type Category,
} from '@/services/categories'

export function useTransactions() {
  const [data, setData] = useState<TransactionPage | null>(null)
  const [categories, setCategories] = useState<Category[]>([])
  const [loading, setLoading] = useState(true)
  const [loadError, setLoadError] = useState<string | null>(null)

  const [filterType, setFilterType] = useState<'' | TransactionType>('')
  const [filterCategory, setFilterCategory] = useState('')
  const [filterStartDate, setFilterStartDate] = useState('')
  const [filterEndDate] = useState('')
  const [page, setPage] = useState(0)

  const [dialogOpen, setDialogOpen] = useState(false)
  const [editingId, setEditingId] = useState<string | null>(null)
  const [saving, setSaving] = useState(false)
  const [formError, setFormError] = useState<string | null>(null)
  const [deletingId, setDeletingId] = useState<string | null>(null)

  const buildFilters = useCallback((): TransactionFilters => ({
    type: filterType === '' ? null : filterType,
    categoryId: filterCategory ? Number(filterCategory) : null,
    startDate: filterStartDate || null,
    endDate: filterEndDate || null,
  }), [filterType, filterCategory, filterStartDate, filterEndDate])

  useEffect(() => {
    let active = true
    listTransactions(buildFilters(), page, 10)
      .then((result) => {
        if (active) {
          setData(result)
          setLoadError(null)
          setLoading(false)
        }
      })
      .catch(() => {
        if (active) {
          setLoadError('Não foi possível carregar as transações.')
          setLoading(false)
        }
      })
    return () => {
      active = false
    }
  }, [buildFilters, page])

  useEffect(() => {
    fetchCategories().then(setCategories).catch(() => setCategories([]))
  }, [])

  function openCreateDialog() {
    setEditingId(null)
    setFormError(null)
    setDialogOpen(true)
  }

  function openEditDialog(transactionId: string) {
    setEditingId(transactionId)
    setFormError(null)
    setDialogOpen(true)
  }

  async function handleSave(input: TransactionInput) {
    setSaving(true)
    setFormError(null)
    try {
      if (editingId) {
        await updateTransaction(editingId, input)
      } else {
        await createTransaction(input)
      }
      setDialogOpen(false)
      await reload()
    } catch (error) {
      setFormError(
        error instanceof Error ? error.message : 'Falha ao salvar a transação',
      )
    } finally {
      setSaving(false)
    }
  }

  async function reload() {
    setLoading(true)
    try {
      const result = await listTransactions(buildFilters(), page, 10)
      setData(result)
      setLoadError(null)
    } catch {
      setLoadError('Não foi possível carregar as transações.')
    }
    setLoading(false)
  }

  async function handleDelete(transactionId: string) {
    const confirmed = window.confirm('Excluir esta transação?')
    if (!confirmed) return

    setDeletingId(transactionId)
    try {
      await deleteTransaction(transactionId)
      await reload()
    } catch {
      window.alert('Não foi possível excluir a transação.')
    } finally {
      setDeletingId(null)
    }
  }

  const editingTransaction = editingId
    ? data?.items.find((item) => item.id === editingId) ?? null
    : null

  return {
    data,
    categories,
    loading,
    loadError,
    filterType,
    filterCategory,
    filterStartDate,
    filterEndDate,
    page,
    dialogOpen,
    editingTransaction,
    saving,
    formError,
    deletingId,
    setFilterType,
    setFilterCategory,
    setFilterStartDate,
    setPage,
    setDialogOpen,
    openCreateDialog,
    openEditDialog,
    handleSave,
    handleDelete,
  }
}
