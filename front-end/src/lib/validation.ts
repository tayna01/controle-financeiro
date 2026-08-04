import { z } from 'zod'

export const requiredString = z.string().min(1, 'Campo obrigatório')

export const emailSchema = requiredString.email('Informe um e-mail válido')

export const passwordSchema = z
  .string()
  .min(8, 'A senha deve ter pelo menos 8 caracteres')
  .regex(/[A-Z]/, 'A senha deve ter pelo menos uma letra maiúscula')
  .regex(/[a-z]/, 'A senha deve ter pelo menos uma letra minúscula')
  .regex(/\d/, 'A senha deve ter pelo menos um número')
  .regex(/[^A-Za-z0-9]/, 'A senha deve ter pelo menos um símbolo')
