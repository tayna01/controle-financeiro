import { useState } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { Loader2, Users } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog'
import { useAuth } from '@/contexts/auth-context'
import type { WalletRole } from '@/services/wallet-sharing'

const ROLE_OPTIONS: { value: WalletRole; label: string }[] = [
  { value: 'EDITOR', label: 'Editor' },
  { value: 'VISUALIZADOR', label: 'Visualizador' },
]

const shareSchema = z.object({
  email: z.string().email('Informe um e-mail válido.'),
})

type ShareFormData = z.infer<typeof shareSchema>

interface ShareWalletDialogProps {
  open: boolean
  onOpenChange: (open: boolean) => void
  saving: boolean
  onSubmit: (data: { email: string; role: WalletRole }) => Promise<boolean>
}

export function ShareWalletDialog({
  open,
  onOpenChange,
  saving,
  onSubmit,
}: ShareWalletDialogProps) {
  const { user } = useAuth()
  const [role, setRole] = useState<WalletRole>('VISUALIZADOR')

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<ShareFormData>({
    resolver: zodResolver(shareSchema),
  })

  async function handleFormSubmit(data: ShareFormData) {
    const ok = await onSubmit({ email: data.email, role })
    if (ok) {
      reset()
      setRole('VISUALIZADOR')
    }
  }

  function handleOpenChange(next: boolean) {
    if (!next) {
      reset()
      setRole('VISUALIZADOR')
    }
    onOpenChange(next)
  }

  return (
    <Dialog open={open} onOpenChange={handleOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Compartilhar carteira</DialogTitle>
          <DialogDescription>
            Dê acesso a outro usuário informando o e-mail cadastrado.
          </DialogDescription>
        </DialogHeader>

        <form className="space-y-5" onSubmit={handleSubmit(handleFormSubmit)} noValidate>
          <div className="space-y-2">
            <Label htmlFor="share-email">E-mail do usuário</Label>
            <Input
              id="share-email"
              type="email"
              placeholder="usuario@exemplo.com"
              autoComplete="off"
              {...register('email')}
            />
            {errors.email && (
              <p className="text-sm text-expense">{errors.email.message}</p>
            )}
            {user?.email && (
              <p className="text-xs text-muted">
                Não é possível compartilhar com o seu próprio e-mail ({user.email}).
              </p>
            )}
          </div>

          <div className="space-y-2">
            <Label>Papel do usuário</Label>
            <Select value={role} onValueChange={(value) => setRole(value as WalletRole)}>
              <SelectTrigger>
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                {ROLE_OPTIONS.map((option) => (
                  <SelectItem key={option.value} value={option.value}>
                    {option.label}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
            <p className="text-xs text-muted">
              Editor pode gerenciar as transações; Visualizador só vê.
            </p>
          </div>

          <DialogFooter>
            <Button type="submit" size="lg" className="w-full" disabled={saving}>
              {saving ? (
                <Loader2 className="size-5 animate-spin" />
              ) : (
                <Users className="size-5" />
              )}
              Compartilhar
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  )
}
