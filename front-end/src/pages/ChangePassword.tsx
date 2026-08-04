import { useState } from 'react'
import { useForm, useWatch } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { Button } from '@/components/ui/button'
import { Card } from '@/components/ui/card'
import { Label } from '@/components/ui/label'
import { PasswordInput } from '@/components/ui/password-input'
import { PasswordStrength } from '@/components/password-strength'
import { PageHeader } from '@/components/page-header'
import { changePassword } from '@/services/auth'
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

export function ChangePassword() {
  const [loading, setLoading] = useState(false)
  const [successMessage, setSuccessMessage] = useState<string | null>(null)
  const {
    register,
    handleSubmit,
    control,
    reset,
    setError,
    formState: { errors },
  } = useForm<ChangePasswordData>({
    resolver: zodResolver(changePasswordSchema),
  })

  const novaSenha = useWatch({ control, name: 'novaSenha', defaultValue: '' })

  async function onSubmit(data: ChangePasswordData) {
    setLoading(true)
    setSuccessMessage(null)
    try {
      await changePassword(data.senhaAtual, data.novaSenha)
      setSuccessMessage('Senha alterada com sucesso')
      reset()
    } catch (error) {
      setError('senhaAtual', {
        type: 'manual',
        message: error instanceof Error ? error.message : 'Falha ao alterar senha',
      })
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="space-y-6">
      <PageHeader
        title="Alterar senha"
        description="Atualize a senha da sua conta"
        breadcrumbs={[
          { label: 'Início', to: '/app/dashboard' },
          { label: 'Perfil', to: '/app/perfil' },
          { label: 'Alterar senha' },
        ]}
      />

      <Card className="w-full max-w-md p-6">
        <form
          className="space-y-5"
          onSubmit={handleSubmit(onSubmit)}
          noValidate
        >
          <div className="space-y-2">
            <Label htmlFor="senhaAtual">Senha atual</Label>
            <PasswordInput
              id="senhaAtual"
              placeholder="••••••••"
              autoComplete="current-password"
              {...register('senhaAtual')}
            />
            {errors.senhaAtual && (
              <p className="text-sm text-expense">{errors.senhaAtual.message}</p>
            )}
          </div>

          <div className="space-y-2">
            <Label htmlFor="novaSenha">Nova senha</Label>
            <PasswordInput
              id="novaSenha"
              placeholder="••••••••"
              autoComplete="new-password"
              {...register('novaSenha')}
            />
            {errors.novaSenha && (
              <p className="text-sm text-expense">{errors.novaSenha.message}</p>
            )}
            <PasswordStrength password={novaSenha} />
          </div>

          <div className="space-y-2">
            <Label htmlFor="confirmarNovaSenha">Confirmar nova senha</Label>
            <PasswordInput
              id="confirmarNovaSenha"
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

          <Button
            type="submit"
            size="lg"
            className="w-full"
            disabled={loading}
          >
            Alterar senha
          </Button>
        </form>

        {successMessage && (
          <p className="mt-4 rounded-xl bg-income/10 px-4 py-3 text-sm text-income">
            {successMessage}
          </p>
        )}
      </Card>
    </div>
  )
}
