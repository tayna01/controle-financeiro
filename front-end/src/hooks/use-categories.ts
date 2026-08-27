import { useCallback, useEffect, useState } from 'react'
import {
  createCategory,
  deleteCategory,
  fetchCategories,
  updateCategory,
  type Category,
  type CategoryType,
} from '@/services/categories'

export function useCategories() {
  const [categories, setCategories] = useState<Category[]>([])
  const [loading, setLoading] = useState(true)
  const [loadError, setLoadError] = useState<string | null>(null)

  const [dialogOpen, setDialogOpen] = useState(false)
  const [editing, setEditing] = useState<Category | null>(null)
  const [saving, setSaving] = useState(false)
  const [formError, setFormError] = useState<string | null>(null)
  const [deletingId, setDeletingId] = useState<string | null>(null)

  const load = useCallback(async () => {
    try {
      const data = await fetchCategories()
      setCategories(data)
      setLoadError(null)
    } catch {
      setLoadError('Não foi possível carregar as categorias.')
    }
    setLoading(false)
  }, [])

  useEffect(() => {
    let active = true
    fetchCategories()
      .then((data) => {
        if (active) {
          setCategories(data)
          setLoading(false)
        }
      })
      .catch(() => {
        if (active) {
          setLoadError('Não foi possível carregar as categorias.')
          setLoading(false)
        }
      })
    return () => {
      active = false
    }
  }, [])

  function openCreateDialog() {
    setEditing(null)
    setFormError(null)
    setDialogOpen(true)
  }

  function openEditDialog(category: Category) {
    setEditing(category)
    setFormError(null)
    setDialogOpen(true)
  }

  async function handleSave(data: {
    name: string
    type: CategoryType
    color: string
    onFieldError?: (field: string, message: string) => void
  }) {
    setSaving(true)
    setFormError(null)
    try {
      if (editing) {
        await updateCategory(editing.id, {
          name: data.name,
          type: data.type,
          color: data.color,
        })
      } else {
        await createCategory({
          name: data.name,
          type: data.type,
          color: data.color,
        })
      }
      setDialogOpen(false)
      await load()
    } catch (error) {
      const message =
        error instanceof Error ? error.message : 'Falha ao salvar categoria'
      if (
        typeof message === 'string' &&
        message.toLowerCase().includes('já')
      ) {
        data.onFieldError?.('name', message)
      } else {
        setFormError(message)
      }
    } finally {
      setSaving(false)
    }
  }

  async function handleDelete(category: Category) {
    const confirmed = window.confirm(
      `Excluir a categoria "${category.name}"? As transações vinculadas não serão apagadas.`,
    )
    if (!confirmed) return

    setDeletingId(String(category.id))
    try {
      await deleteCategory(category.id)
      await load()
    } catch {
      window.alert('Não foi possível excluir a categoria.')
    } finally {
      setDeletingId(null)
    }
  }

  return {
    categories,
    loading,
    loadError,
    dialogOpen,
    editing,
    saving,
    formError,
    deletingId,
    setDialogOpen,
    openCreateDialog,
    openEditDialog,
    handleSave,
    handleDelete,
  }
}
