import { Plus, Tag } from 'lucide-react'
import { PageHeader } from '@/components/page-header'
import { Button } from '@/components/ui/button'
import { Card } from '@/components/ui/card'
import { useCategories } from '@/hooks/use-categories'
import { CategoryCard } from '@/components/categories/category-card'
import { CategoryForm } from '@/components/categories/category-form'

export function Categories() {
  const {
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
  } = useCategories()

  return (
    <div className="space-y-6">
      <PageHeader
        title="Categorias"
        description="Organize as categorias dos seus lançamentos"
        breadcrumbs={[
          { label: 'Início', to: '/app/dashboard' },
          { label: 'Categorias' },
        ]}
        actions={
          <Button onClick={openCreateDialog}>
            <Plus className="size-4" />
            Nova categoria
          </Button>
        }
      />

      {loading ? (
        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {Array.from({ length: 6 }, (_, index) => (
            <div
              key={index}
              className="h-24 animate-pulse rounded-2xl border border-border bg-surface"
            />
          ))}
        </div>
      ) : loadError ? (
        <Card className="p-6 text-sm text-expense">{loadError}</Card>
      ) : categories.length === 0 ? (
        <Card className="flex flex-col items-center gap-2 p-10 text-center">
          <Tag className="size-8 text-muted" />
          <p className="font-semibold">Nenhuma categoria cadastrada</p>
          <p className="text-sm text-muted">
            Crie categorias para organizar suas receitas e despesas.
          </p>
        </Card>
      ) : (
        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {categories.map((category) => (
            <CategoryCard
              key={category.id}
              category={category}
              isDeleting={deletingId === String(category.id)}
              onEdit={openEditDialog}
              onDelete={handleDelete}
            />
          ))}
        </div>
      )}

      <CategoryForm
        open={dialogOpen}
        onOpenChange={setDialogOpen}
        editing={editing}
        saving={saving}
        formError={formError}
        onSubmit={handleSave}
      />
    </div>
  )
}
