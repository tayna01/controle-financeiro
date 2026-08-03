import { useState } from 'react'
import { Link } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { AuthAside } from '@/components/auth-aside'
import { ThemeToggle } from '@/components/theme-toggle'

const forgotPasswordSchema = z.object({
  email: z.email('Informe um e-mail válido'),
})

type ForgotPasswordData = z.infer<typeof forgotPasswordSchema>

export function ForgotPassword() {
  const [loading, setLoading] = useState(false)
  const [sent, setSent] = useState(false)
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<ForgotPasswordData>({ resolver: zodResolver(forgotPasswordSchema) })

  function onSubmit() {
    setLoading(true)
    setTimeout(() => {
      setLoading(false)
      setSent(true)
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

          {sent ? (
            <>
              <h2 className="text-2xl font-bold">Verifique seu e-mail</h2>
              <p className="mt-1 text-sm text-muted">
                Se o e-mail informado estiver cadastrado, enviaremos um link de
                redefinição para você.
              </p>

              <div className="mt-8 rounded-xl bg-primary/10 px-4 py-3 text-sm text-primary">
                Link simulado (apenas para teste):
                <br />
                <Link
                  to="/redefinir-senha/token-demo"
                  className="font-semibold underline"
                >
                  /redefinir-senha/token-demo
                </Link>
              </div>

              <Button
                type="button"
                size="lg"
                className="mt-4 w-full"
                onClick={() => setSent(false)}
              >
                Enviar novamente
              </Button>
            </>
          ) : (
            <>
              <h2 className="text-2xl font-bold">Esqueceu sua senha?</h2>
              <p className="mt-1 text-sm text-muted">
                Informe seu e-mail cadastrado e enviaremos um link para
                redefinir sua senha
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
                    <p className="text-sm text-expense">
                      {errors.email.message}
                    </p>
                  )}
                </div>

                <Button
                  type="submit"
                  size="lg"
                  className="w-full"
                  disabled={loading}
                >
                  {loading ? 'Enviando...' : 'Enviar link'}
                </Button>
              </form>
            </>
          )}

          <p className="mt-8 text-center text-sm text-muted">
            Lembrou a senha?{' '}
            <Link
              to="/login"
              className="font-semibold text-primary hover:underline"
            >
              Voltar para o login
            </Link>
          </p>
        </div>
      </section>
    </main>
  )
}
