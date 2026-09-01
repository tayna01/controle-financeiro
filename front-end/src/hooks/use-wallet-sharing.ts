import { useCallback, useEffect, useState } from 'react'
import {
  addWalletMember,
  fetchWalletMembers,
  removeWalletMember,
  updateWalletMemberRole,
  type WalletMember,
  type WalletRole,
} from '@/services/wallet-sharing'
import { useWallet } from '@/contexts/wallet-context'
import { toast } from '@/hooks/use-toast'

export function useWalletSharing() {
  const { selectedWallet } = useWallet()
  const walletId = selectedWallet?.id
  const [members, setMembers] = useState<WalletMember[]>([])
  const [loading, setLoading] = useState(true)
  const [loadError, setLoadError] = useState<string | null>(null)

  const [dialogOpen, setDialogOpen] = useState(false)
  const [saving, setSaving] = useState(false)

  const [removingId, setRemovingId] = useState<number | null>(null)
  const [changingRoleId, setChangingRoleId] = useState<number | null>(null)

  const isOwner = members.some((member) => member.role === 'DONO')
  const walletName = selectedWallet?.name ?? ''

  const load = useCallback(async () => {
    try {
      const data = await fetchWalletMembers()
      setMembers(data)
      setLoadError(null)
    } catch {
      setLoadError('Não foi possível carregar os compartilhamentos.')
    }
    setLoading(false)
  }, [])

  useEffect(() => {
    if (!walletId) return
    let active = true
    fetchWalletMembers()
      .then((data) => {
        if (active) {
          setMembers(data)
          setLoading(false)
        }
      })
      .catch(() => {
        if (active) {
          setLoadError('Não foi possível carregar os compartilhamentos.')
          setLoading(false)
        }
      })
    return () => {
      active = false
    }
  }, [walletId])

  function openShareDialog() {
    setDialogOpen(true)
  }

  async function handleShare(data: { email: string; role: WalletRole }) {
    setSaving(true)
    try {
      await addWalletMember(data)
      toast({ title: 'Compartilhamento realizado' })
      setDialogOpen(false)
      await load()
      return true
    } catch (error) {
      toast({
        title: 'Erro ao compartilhar',
        description:
          error instanceof Error
            ? error.message
            : 'Não foi possível compartilhar a carteira.',
        variant: 'destructive',
      })
      return false
    } finally {
      setSaving(false)
    }
  }

  async function handleRemove(member: WalletMember) {
    const confirmed = window.confirm(
      `Remover o acesso de "${member.name}" a esta carteira?`,
    )
    if (!confirmed) return

    setRemovingId(member.userId)
    try {
      await removeWalletMember(member.userId)
      await load()
      toast({ title: 'Acesso removido' })
    } catch (error) {
      toast({
        title: 'Erro ao remover acesso',
        description:
          error instanceof Error
            ? error.message
            : 'Não foi possível remover o acesso.',
        variant: 'destructive',
      })
    } finally {
      setRemovingId(null)
    }
  }

  async function handleChangeRole(member: WalletMember, role: WalletRole) {
    if (role === member.role) return
    setChangingRoleId(member.userId)
    try {
      await updateWalletMemberRole(member.userId, role)
      await load()
      toast({ title: 'Papel atualizado' })
    } catch (error) {
      toast({
        title: 'Erro ao alterar papel',
        description:
          error instanceof Error
            ? error.message
            : 'Não foi possível alterar o papel.',
        variant: 'destructive',
      })
    } finally {
      setChangingRoleId(null)
    }
  }

  return {
    members,
    loading,
    loadError,
    dialogOpen,
    saving,
    removingId,
    changingRoleId,
    isOwner,
    walletName,
    setDialogOpen,
    openShareDialog,
    handleShare,
    handleRemove,
    handleChangeRole,
  }
}
