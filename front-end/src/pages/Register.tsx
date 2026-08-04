import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useForm, useWatch } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { PasswordInput } from '@/components/ui/password-input'
import { PasswordStrength } from '@/components/password-strength'
import { AuthAside } from '@/components/auth-aside'
import { ThemeToggle } from '@/components/theme-toggle'
import { cn } from '@/lib/utils'
import { emailSchema, passwordSchema, requiredString } from '@/lib/validation'

const registerSchema = z
  .object({
    nome: requiredString,
    email: emailSchema,
    senha: passwordSchema,
    confirmarSenha: requiredString,
  })
  .refine((data) => data.senha === data.confirmarSenha, {
    message: 'As senhas não conferem',
    path: ['confirmarSenha'],
  })

type RegisterData = z.infer<typeof registerSchema>

const STEPS: { title: string; fields: (keyof RegisterData)[] }[] = [
  {
    title: 'Preencha seus dados para criar sua conta',
    fields: ['nome', 'email'],
  },
  {
    title: 'Defina uma senha segura para proteger sua conta',
    fields: ['senha', 'confirmarSenha'],
  },
]

export function Register() {
  const [step, setStep] = useState(0)
  const [loading, setLoading] = useState(false)
  const navigate = useNavigate()
  const {
    register,
    handleSubmit,
    control,
    trigger,
    setError,
    formState: { errors },
  } = useForm<RegisterData>({
    resolver: zodResolver(registerSchema),
    mode: 'onTouched',
  })

  const senha = useWatch({ control, name: 'senha', defaultValue: '' })

  const MOCK_EMAILS_CADASTRADOS = ['usuario@exemplo.com', 'teste@exemplo.com']

  const isLastStep = step === STEPS.length - 1

  async function handleNext() {
    const valid = await trigger(STEPS[step].fields)
    if (valid) setStep((current) => current + 1)
  }

  function onSubmit(data: RegisterData) {
    setLoading(true)
    setTimeout(() => {
      setLoading(false)
      if (MOCK_EMAILS_CADASTRADOS.includes(data.email.toLowerCase())) {
        setStep(0)
        setError('email', {
          type: 'manual',
          message: 'Este e-mail já está cadastrado',
        })
        return
      }
      navigate('/login', {
        state: { successMessage: 'Conta criada com sucesso. Faça login.' },
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

          <h2 className="text-2xl font-bold">Crie sua conta</h2>
          <p className="mt-1 text-sm text-muted">{STEPS[step].title}</p>

          <div className="mt-6 flex items-center gap-2" aria-hidden="true">
            {STEPS.map((currentStep, index) => (
              <div
                key={currentStep.title}
                className={cn(
                  'h-1 flex-1 rounded-full transition-colors',
                  index <= step ? 'bg-primary' : 'bg-border',
                )}
              />
            ))}
          </div>

          <form
            className="mt-8 space-y-5"
            onSubmit={handleSubmit(onSubmit)}
            noValidate
          >
            <div key={step} className="space-y-5 animate-step">
              {step === 0 && (
                <>
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
                      <p className="text-sm text-expense">
                        {errors.nome.message}
                      </p>
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
                      <p className="text-sm text-expense">
                        {errors.email.message}
                      </p>
                    )}
                  </div>
                </>
              )}

              {step === 1 && (
                <>
                  <div className="space-y-2">
                    <Label htmlFor="senha">Senha</Label>
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
                    <PasswordStrength password={senha} />
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
                </>
              )}
            </div>

            <div className="flex gap-3">
              {step > 0 && (
                <Button
                  type="button"
                  variant="outline"
                  size="lg"
                  onClick={() => setStep((current) => current - 1)}
                >
                  Voltar
                </Button>
              )}

              {!isLastStep ? (
                <Button
                  type="button"
                  size="lg"
                  className="w-full"
                  onClick={handleNext}
                >
                  Continuar
                </Button>
              ) : (
                <Button
                  type="submit"
                  size="lg"
                  className="w-full"
                  disabled={loading}
                >
                  {loading ? 'Cadastrando...' : 'Criar conta'}
                </Button>
              )}
            </div>
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
