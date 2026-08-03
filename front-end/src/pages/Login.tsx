import { useState } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { Wallet } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { ThemeToggle } from '@/components/theme-toggle'

const loginSchema = z.object({
  email: z.email('Informe um e-mail válido'),
  senha: z.string().min(6, 'A senha deve ter pelo menos 6 caracteres'),
})

type LoginData = z.infer<typeof loginSchema>

export function Login() {
  const [loading, setLoading] = useState(false)
  const [mockMessage, setMockMessage] = useState<string | null>(null)
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginData>({ resolver: zodResolver(loginSchema) })

  function onSubmit(data: LoginData) {
    setLoading(true)
    setMockMessage(null)
    setTimeout(() => {
      setLoading(false)
      setMockMessage(
        `Login simulado: ${data.email} (backend ainda não integrado)`,
      )
    }, 1200)
  }

  return (
    <main className="flex min-h-screen bg-background text-foreground">
      <aside className="hidden flex-col justify-between bg-gradient-to-br from-[#5b21b6] to-[#a21caf] p-12 text-white lg:flex lg:w-1/2">
        <div className="flex items-center gap-3 text-xl font-bold">
          Controle Financeiro
        </div>

        <div>
          <h1 className="text-4xl font-bold leading-tight">
            Organize suas finanças
            <br />
            em um só lugar
          </h1>
          <p className="mt-4 max-w-md text-white/80">
            Acompanhe receitas e despesas, veja gráficos do seu mês e mantenha
            o controle do seu dinheiro.
          </p>
        </div>

        <p className="text-sm text-white/60">versão 1.0</p>
      </aside>

      <section className="relative flex flex-1 flex-col items-center justify-center p-6">
        <div className="absolute right-6 top-6">
          <ThemeToggle />
        </div>

        <div className="w-full max-w-sm">
          <div className="mb-8 flex items-center gap-2 text-xl font-bold lg:hidden">
            <Wallet className="size-6 text-primary" />
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
                <a
                  href="#"
                  className="text-sm font-medium text-primary hover:underline"
                >
                  Esqueceu a senha?
                </a>
              </div>
              <Input
                id="senha"
                type="password"
                placeholder="••••••••"
                autoComplete="current-password"
                {...register('senha')}
              />
              {errors.senha && (
                <p className="text-sm text-expense">{errors.senha.message}</p>
              )}
            </div>

            <Button type="submit" size="lg" className="w-full" disabled={loading}>
              {loading ? 'Entrando...' : 'Entrar'}
            </Button>
          </form>

          {mockMessage && (
            <p className="mt-4 rounded-xl bg-primary/10 px-4 py-3 text-sm text-primary">
              {mockMessage}
            </p>
          )}

          <p className="mt-8 text-center text-sm text-muted">
            Não tem uma conta?{' '}
            <a href="#" className="font-semibold text-primary hover:underline">
              Cadastre-se
            </a>
          </p>
        </div>
      </section>
    </main>
  )
}
