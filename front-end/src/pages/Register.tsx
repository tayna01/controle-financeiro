import { Link } from 'react-router-dom'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { PasswordInput } from '@/components/ui/password-input'
import { PasswordStrength } from '@/components/password-strength'
import { AuthAside } from '@/components/auth-aside'
import { ThemeToggle } from '@/components/theme-toggle'
import { cn } from '@/lib/utils'
import { useRegister } from '@/hooks/use-register'

export function Register() {
  const {
    step,
    loading,
    senha,
    isLastStep,
    currentStepTitle,
    errors,
    register,
    handleSubmit,
    handleNext,
    handleBack,
    onSubmit,
  } = useRegister()

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
          <p className="mt-1 text-sm text-muted">{currentStepTitle}</p>

          <div className="mt-6 flex items-center gap-2" aria-hidden="true">
            {Array.from({ length: 2 }, (_, index) => (
              <div
                key={index}
                className={cn(
                  'h-1 flex-1 rounded-full',
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
                  onClick={handleBack}
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
