import { useCallback, useEffect, useState } from 'react'
import {
  createCategory,
  deleteCategory,
  fetchCategories,
  updateCategory,
  type Category,
  type CategoryType,
} from '@/services/categories'
import { toast } from '@/hooks/use-toast'

export function useCategories() {
  const [categories, setCategories] = useState<Category[]>([])
  const [loading, setLoading] = useState(true)
  const [loadError, setLoadError] = useState<string | null>(null)

  const [dialogOpen, setDialogOpen] = useState(false)
  const [editing, setEditing] = useState<Category | null>(null)
  const [saving, setSaving] = useState(false)
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
    setDialogOpen(true)
  }

  function openEditDialog(category: Category) {
    setEditing(category)
    setDialogOpen(true)
  }

  async function handleSave(data: {
    name: string
    type: CategoryType
    color: string
    onFieldError?: (field: string, message: string) => void
  }) {
    setSaving(true)
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
      toast({
        title: editing ? 'Categoria atualizada' : 'Categoria criada',
      })
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
        toast({
          title: 'Erro ao salvar categoria',
          description: message,
          variant: 'destructive',
        })
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
      toast({ title: 'Categoria excluída' })
      await load()
    } catch {
      toast({
        title: 'Erro ao excluir categoria',
        description: 'Não foi possível excluir a categoria.',
        variant: 'destructive',
      })
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
    deletingId,
    setDialogOpen,
    openCreateDialog,
    openEditDialog,
    handleSave,
    handleDelete,
  }
}
