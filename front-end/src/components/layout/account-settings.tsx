import { useForm, useWatch } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog'
import { Button } from '@/components/ui/button'
import { Label } from '@/components/ui/label'
import { PasswordInput } from '@/components/ui/password-input'
import { PasswordStrength } from '@/components/password-strength'
import { useAuth } from '@/contexts/auth-context'
import { changePassword } from '@/services/auth'
import { useToast } from '@/hooks/use-toast'
import { passwordSchema, requiredString } from '@/lib/validation'

const changePasswordSchema = z
  .object({
    senhaAtual: requiredString,
    novaSenha: passwordSchema,
    confirmarNovaSenha: requiredString,
  })
  .refine((data) => data.novaSenha === data.confirmarNovaSenha, {
    message: 'As senhas não conferem',
    path: ['confirmarNovaSenha'],
  })

type ChangePasswordData = z.infer<typeof changePasswordSchema>

interface AccountSettingsDialogProps {
  open: boolean
  onOpenChange: (open: boolean) => void
}

export function AccountSettingsDialog({
  open,
  onOpenChange,
}: AccountSettingsDialogProps) {
  const { user } = useAuth()
  const { toast } = useToast()

  const {
    register,
    handleSubmit,
    control,
    reset,
    setError,
    formState: { errors, isSubmitting },
  } = useForm<ChangePasswordData>({
    resolver: zodResolver(changePasswordSchema),
  })

  const novaSenha = useWatch({ control, name: 'novaSenha', defaultValue: '' })

  function handleOpenChange(nextOpen: boolean) {
    onOpenChange(nextOpen)
    if (nextOpen) {
      reset()
    }
  }

  async function onSubmit(data: ChangePasswordData) {
    try {
      await changePassword(data.senhaAtual, data.novaSenha)
      toast({ title: 'Senha alterada com sucesso' })
      reset()
      onOpenChange(false)
    } catch (error) {
      setError('senhaAtual', {
        type: 'manual',
        message:
          error instanceof Error ? error.message : 'Falha ao alterar senha',
      })
    }
  }

  if (!user) return null

  return (
    <Dialog open={open} onOpenChange={handleOpenChange}>
      <DialogContent className="max-h-[85vh] overflow-y-auto sm:max-w-lg">
        <DialogHeader>
          <DialogTitle>Alterar senha</DialogTitle>
          <DialogDescription>
            Atualize a senha da sua conta
          </DialogDescription>
        </DialogHeader>

        <form
          className="space-y-4"
          onSubmit={handleSubmit(onSubmit)}
          noValidate
        >
          <div className="space-y-2">
            <Label htmlFor="senha-atual">Senha atual</Label>
            <PasswordInput
              id="senha-atual"
              placeholder="••••••••"
              autoComplete="current-password"
              {...register('senhaAtual')}
            />
            {errors.senhaAtual && (
              <p className="text-sm text-expense">
                {errors.senhaAtual.message}
              </p>
            )}
          </div>

          <div className="space-y-2">
            <Label htmlFor="nova-senha">Nova senha</Label>
            <PasswordInput
              id="nova-senha"
              placeholder="••••••••"
              autoComplete="new-password"
              {...register('novaSenha')}
            />
            {errors.novaSenha && (
              <p className="text-sm text-expense">
                {errors.novaSenha.message}
              </p>
            )}
            <PasswordStrength password={novaSenha} />
          </div>

          <div className="space-y-2">
            <Label htmlFor="confirmar-nova-senha">Confirmar nova senha</Label>
            <PasswordInput
              id="confirmar-nova-senha"
              placeholder="••••••••"
              autoComplete="new-password"
              {...register('confirmarNovaSenha')}
            />
            {errors.confirmarNovaSenha && (
              <p className="text-sm text-expense">
                {errors.confirmarNovaSenha.message}
              </p>
            )}
          </div>

          <Button type="submit" size="lg" className="w-full" disabled={isSubmitting}>
            Alterar senha
          </Button>
        </form>
      </DialogContent>
    </Dialog>
  )
}
