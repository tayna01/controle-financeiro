import { cn } from '@/lib/utils'

interface PasswordStrengthProps {
  password: string
}

const STRENGTH_RULES = [
  { label: '8+ caracteres', test: (p: string) => p.length >= 8 },
  { label: 'Letra maiúscula', test: (p: string) => /[A-Z]/.test(p) },
  { label: 'Letra minúscula', test: (p: string) => /[a-z]/.test(p) },
  { label: 'Número', test: (p: string) => /\d/.test(p) },
  { label: 'Símbolo', test: (p: string) => /[^A-Za-z0-9]/.test(p) },
]

const TOTAL_SEGMENTS = 4

export function PasswordStrength({ password }: PasswordStrengthProps) {
  if (!password) return null

  const metCount = STRENGTH_RULES.filter((rule) => rule.test(password)).length
  const filledSegments = Math.min(
    TOTAL_SEGMENTS,
    Math.max(1, Math.round((metCount / STRENGTH_RULES.length) * TOTAL_SEGMENTS)),
  )

  const level =
    metCount <= 2 ? { label: 'Senha fraca', bar: 'bg-expense', text: 'text-expense' }
    : metCount <= 4 ? { label: 'Senha média', bar: 'bg-amber-500', text: 'text-amber-500' }
    : { label: 'Senha forte', bar: 'bg-income', text: 'text-income' }

  return (
    <div className="space-y-2">
      <div className="flex gap-1">
        {Array.from({ length: TOTAL_SEGMENTS }, (_, index) => (
          <div
            key={index}
            className={cn(
              'h-1 flex-1 rounded-full bg-border transition-colors',
              index < filledSegments && level.bar,
            )}
          />
        ))}
      </div>
      <div className="flex items-center justify-between gap-2">
        <p className={cn('text-xs font-medium', level.text)}>{level.label}</p>
        <p className="text-xs text-muted">
          {metCount} de {STRENGTH_RULES.length} critérios atendidos
        </p>
      </div>
    </div>
  )
}