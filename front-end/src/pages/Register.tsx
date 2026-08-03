import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { Wallet } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { PasswordInput } from '@/components/ui/password-input'
import { AuthAside } from '@/components/auth-aside'
import { ThemeToggle } from '@/components/theme-toggle'

const registerSchema = z
  .object({
    nome: z.string().min(1, 'Informe seu nome'),
    email: z.email('Informe um e-mail válido'),
    senha: z.string().min(6, 'A senha deve ter pelo menos 6 caracteres'),
    confirmarSenha: z.string().min(6, 'Confirme sua senha'),
  })
  .refine((data) => data.senha === data.confirmarSenha, {
    message: 'As senhas não conferem',
    path: ['confirmarSenha'],
  })

type RegisterData = z.infer<typeof registerSchema>

export function Register() {
  const [loading, setLoading] = useState(false)
  const navigate = useNavigate()
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<RegisterData>({ resolver: zodResolver(registerSchema) })

  function onSubmit() {
    setLoading(true)
    setTimeout(() => {
      setLoading(false)
      navigate('/login')
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
            <Wallet className="size-6 text-primary" />
            Controle Financeiro
          </div>

          <h2 className="text-2xl font-bold">Crie sua conta</h2>
          <p className="mt-1 text-sm text-muted">
            Preencha os dados abaixo para começar a usar o Controle Financeiro
          </p>

          <form
            className="mt-8 space-y-5"
            onSubmit={handleSubmit(onSubmit)}
            noValidate
          >
            <div className="space-y-2">
              <Label htmlFor="nome">Nome</Label>
              <Input
                id="nome"
                type="text"
                placeholder="Seu nome completo"
                autoComplete="name"
                {...register('nome')}
              />
              {errors.nome && (
                <p className="text-sm text-expense">{errors.nome.message}</p>
              )}
            </div>

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
              <Label htmlFor="senha">Senha</Label>
              <PasswordInput
                id="senha"
                placeholder="••••••••"
                autoComplete="new-password"
                {...register('senha')}
              />
              {errors.senha && (
                <p className="text-sm text-expense">{errors.senha.message}</p>
              )}
            </div>

            <div className="space-y-2">
              <Label htmlFor="confirmarSenha">Confirmar senha</Label>
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

            <Button type="submit" size="lg" className="w-full" disabled={loading}>
              {loading ? 'Cadastrando...' : 'Criar conta'}
            </Button>
          </form>

          <p className="mt-8 text-center text-sm text-muted">
            Já tem uma conta?{' '}
            <Link
              to="/login"
              className="font-semibold text-primary hover:underline"
            >
              Entrar
            </Link>
          </p>
        </div>
      </section>
    </main>
  )
}