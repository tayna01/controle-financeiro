import { Plus, Users } from 'lucide-react'
import { PageHeader } from '@/components/page-header'
import { Button } from '@/components/ui/button'
import { Card } from '@/components/ui/card'
import { Skeleton } from '@/components/ui/skeleton'
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from '@/components/ui/alert-dialog'
import { useWalletSharing } from '@/hooks/use-wallet-sharing'
import { MemberList } from '@/components/sharing/member-list'
import { ShareWalletDialog } from '@/components/sharing/share-wallet-dialog'

export function Sharing() {
  const {
    members,
    loading,
    loadError,
    dialogOpen,
    saving,
    removingId,
    changingRoleId,
    pendingRemove,
    isOwner,
    walletName,
    setDialogOpen,
    openShareDialog,
    handleShare,
    handleRemove,
    handleChangeRole,
    setPendingRemove,
  } = useWalletSharing()

  return (
    <div className="space-y-6">
      <PageHeader
        title="Compartilhamento"
        description={
          walletName
            ? `Compartilhe a carteira "${walletName}" com outros usuários`
            : 'Compartilhe sua carteira com outros usuários'
        }
        breadcrumbs={[
          { label: 'Início', to: '/app/dashboard' },
          { label: 'Compartilhamento' },
        ]}
        actions={
          isOwner && (
            <Button onClick={openShareDialog}>
              <Plus className="size-4" />
              Compartilhar carteira
            </Button>
          )
        }
      />

      {loading ? (
        <div className="space-y-3">
          {Array.from({ length: 3 }, (_, index) => (
            <Skeleton key={index} className="h-16 rounded-2xl" />
          ))}
        </div>
      ) : loadError ? (
        <Card className="p-6 text-sm text-expense">{loadError}</Card>
      ) : !isOwner && members.length > 0 ? (
        <div className="mb-4">
          <Card className="flex items-center gap-3 p-4 text-sm text-muted">
            <Users className="size-5 shrink-0" />
            Você tem acesso como membro desta carteira. Apenas o dono pode gerenciar
            o compartilhamento.
          </Card>
        </div>
      ) : (
        <MemberList
          members={members}
          isOwner={isOwner}
          removingId={removingId}
          changingRoleId={changingRoleId}
          onRemove={setPendingRemove}
          onChangeRole={handleChangeRole}
        />
      )}

      <ShareWalletDialog
        open={dialogOpen}
        onOpenChange={setDialogOpen}
        saving={saving}
        onSubmit={handleShare}
      />

      <AlertDialog
        open={pendingRemove !== null}
        onOpenChange={(open) => {
          if (!open) setPendingRemove(null)
        }}
      >
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Remover acesso?</AlertDialogTitle>
            <AlertDialogDescription>
              Deseja remover o acesso de &quot;{pendingRemove?.name}&quot; a
              esta carteira? Esta ação não pode ser desfeita.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel
              disabled={removingId !== null}
              onClick={() => setPendingRemove(null)}
            >
              Cancelar
            </AlertDialogCancel>
            <AlertDialogAction
              className="bg-destructive text-white hover:bg-destructive/90"
              disabled={removingId !== null}
              onClick={() => pendingRemove && handleRemove(pendingRemove)}
            >
              {removingId !== null ? 'Removendo...' : 'Remover'}
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  )
}
