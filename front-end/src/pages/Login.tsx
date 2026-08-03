import { useState } from 'react'
import { Link, Navigate, useLocation, useNavigate } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { Loader2 } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { PasswordInput } from '@/components/ui/password-input'
import { AuthAside } from '@/components/auth-aside'
import { ThemeToggle } from '@/components/theme-toggle'
import { useAuth } from '@/contexts/auth-context'

const loginSchema = z.object({
  email: z.email('Informe um e-mail válido'),
  senha: z.string().min(6, 'A senha deve ter pelo menos 6 caracteres'),
})

type LoginData = z.infer<typeof loginSchema>

export function Login() {
  const location = useLocation()
  const navigate = useNavigate()
  const { isAuthenticated, login } = useAuth()
  const [loading, setLoading] = useState(false)
  const [authError, setAuthError] = useState<string | null>(null)
  const [successMessage, setSuccessMessage] = useState<string | null>(
    () =>
      (location.state as { successMessage?: string } | null)?.successMessage ??
      null,
  )
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginData>({ resolver: zodResolver(loginSchema) })

  if (isAuthenticated) {
    return <Navigate to="/dashboard" replace />
  }

  async function onSubmit(data: LoginData) {
    setLoading(true)
    setAuthError(null)
    setSuccessMessage(null)
    try {
      await login(data.email, data.senha)
      navigate('/dashboard', { replace: true })
    } catch (error) {
      setAuthError(error instanceof Error ? error.message : 'Falha ao entrar')
      setLoading(false)
    }
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

          <h2 className="text-2xl font-bold">Bem-vindo de volta</h2>
          <p className="mt-1 text-sm text-muted">
            Entre com suas credenciais para acessar sua conta
          </p>

          <form
            className="mt-8 space-y-5"
            onSubmit={handleSubmit(onSubmit)}
            noValidate
          >
            <div className="space-y-2">
              <Label htmlFor="email">E-mail</Label>
              <Input
                id="email"
                type="email"
                placeholder="email@exemplo.com"
                autoComplete="email"
                {...register('email')}
              />
              {errors.email && (
                <p className="text-sm text-expense">{errors.email.message}</p>
              )}
            </div>

            <div className="space-y-2">
              <div className="flex items-center justify-between">
                <Label htmlFor="senha">Senha</Label>
                <Link
                  to="/recuperar-senha"
                  className="text-sm font-medium text-primary hover:underline"
                >
                  Esqueceu a senha?
                </Link>
              </div>
              <PasswordInput
                id="senha"
                placeholder="••••••••"
                autoComplete="current-password"
                {...register('senha')}
              />
              {errors.senha && (
                <p className="text-sm text-expense">{errors.senha.message}</p>
              )}
            </div>

            <Button type="submit" size="lg" className="w-full" disabled={loading}>
              {loading && <Loader2 className="size-5 animate-spin" />}
              Entrar
            </Button>
          </form>

          {authError && (
            <p className="mt-4 rounded-xl bg-expense/10 px-4 py-3 text-sm text-expense">
              {authError}
            </p>
          )}

          {successMessage && (
            <p className="mt-4 rounded-xl bg-income/10 px-4 py-3 text-sm text-income">
              {successMessage}
            </p>
          )}

          <p className="mt-8 text-center text-sm text-muted">
            Não tem uma conta?{' '}
            <Link
              to="/cadastro"
              className="font-semibold text-primary hover:underline"
            >
              Cadastre-se
            </Link>
          </p>
        </div>
      </section>
    </main>
  )
}
