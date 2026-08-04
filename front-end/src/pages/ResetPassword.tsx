import { useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { Button } from '@/components/ui/button'
import { Label } from '@/components/ui/label'
import { PasswordInput } from '@/components/ui/password-input'
import { AuthAside } from '@/components/auth-aside'
import { ThemeToggle } from '@/components/theme-toggle'
import { passwordSchema, requiredString } from '@/lib/validation'

const resetPasswordSchema = z
  .object({
    senha: passwordSchema,
    confirmarSenha: requiredString,
  })
  .refine((data) => data.senha === data.confirmarSenha, {
    message: 'As senhas não conferem',
    path: ['confirmarSenha'],
  })

type ResetPasswordData = z.infer<typeof resetPasswordSchema>

export function ResetPassword() {
  const { token } = useParams<{ token: string }>()
  const navigate = useNavigate()
  const [loading, setLoading] = useState(false)
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<ResetPasswordData>({ resolver: zodResolver(resetPasswordSchema) })

  const tokenInvalido = !token

  function onSubmit() {
    setLoading(true)
    setTimeout(() => {
      setLoading(false)
      navigate('/login', {
        state: { successMessage: 'Senha redefinida com sucesso. Faça login.' },
      })
    }, 1200)
  }

  return (
    <main className="flex min-h-screen bg-background text-foreground">
      <AuthAside />

      <section className="relative flex flex-1 flex-col items-center justify-center p-6">
        <div className="absolute right-6 top-6">
          <ThemeToggle />
        </div>

        <div className="w-full max-w-sm">
          <div className="mb-8 flex items-center gap-2 text-xl font-bold lg:hidden">
            <img src="/favicon.svg" alt="" className="size-7" />
            Controle Financeiro
          </div>

          {tokenInvalido ? (
            <>
              <h2 className="text-2xl font-bold">Link inválido</h2>
              <p className="mt-1 text-sm text-muted">
                O link de redefinição de senha é inválido, ausente ou já
                expirou. Solicite um novo link para continuar.
              </p>

              <Button
                type="button"
                size="lg"
                className="mt-8 w-full"
                onClick={() => navigate('/recuperar-senha')}
              >
                Solicitar novo link
              </Button>

              <p className="mt-8 text-center text-sm text-muted">
                Lembrou a senha?{' '}
                <Link
                  to="/login"
                  className="font-semibold text-primary hover:underline"
                >
                  Voltar para o login
                </Link>
              </p>
            </>
          ) : (
            <>
              <h2 className="text-2xl font-bold">Defina uma nova senha</h2>
              <p className="mt-1 text-sm text-muted">
                Crie uma senha forte para proteger sua conta
              </p>

              <form
                className="mt-8 space-y-5"
                onSubmit={handleSubmit(onSubmit)}
                noValidate
              >
                <div className="space-y-2">
                  <Label htmlFor="senha">Nova senha</Label>
                  <PasswordInput
                    id="senha"
                    placeholder="••••••••"
                    autoComplete="new-password"
                    {...register('senha')}
                  />
                  {errors.senha && (
                    <p className="text-sm text-expense">
                      {errors.senha.message}
                    </p>
                  )}
                </div>

                <div className="space-y-2">
                  <Label htmlFor="confirmarSenha">Confirmar nova senha</Label>
                  <PasswordInput
                    id="confirmarSenha"
                    placeholder="••••••••"
                    autoComplete="new-password"
                    {...register('confirmarSenha')}
                  />
                  {errors.confirmarSenha && (
                    <p className="text-sm text-expense">
                      {errors.confirmarSenha.message}
                    </p>
                  )}
                </div>

                <Button
                  type="submit"
                  size="lg"
                  className="w-full"
                  disabled={loading}
                >
                  {loading ? 'Salvando...' : 'Redefinir senha'}
                </Button>
              </form>
            </>
          )}
        </div>
      </section>
    </main>
  )
}
