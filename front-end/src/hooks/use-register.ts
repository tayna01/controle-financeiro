import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useForm, useWatch } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { isAxiosError } from 'axios'
import { z } from 'zod'
import { register as registerAccount } from '@/services/auth'
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

export function useRegister() {
  const [step, setStep] = useState(0)
  const [loading, setLoading] = useState(false)
  const [formError, setFormError] = useState<string | null>(null)
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

  const isLastStep = step === STEPS.length - 1
  const currentStepTitle = STEPS[step].title

  async function handleNext() {
    const valid = await trigger(STEPS[step].fields)
    if (valid) {
      setFormError(null)
      setStep((current) => current + 1)
    }
  }

  function handleBack() {
    setStep((current) => current - 1)
  }

  async function onSubmit(data: RegisterData) {
    setLoading(true)
    setFormError(null)
    try {
      await registerAccount(data.nome, data.email, data.senha)
      navigate('/login', {
        state: { successMessage: 'Conta criada com sucesso. Faça login.' },
      })
    } catch (error) {
      setLoading(false)
      if (isAxiosError(error) && error.response?.status === 409) {
        setStep(0)
        setError('email', {
          type: 'manual',
          message: 'Este e-mail já está cadastrado',
        })
        return
      }
      setFormError('Falha ao criar conta. Tente novamente.')
    }
  }

  return {
    step,
    loading,
    formError,
    senha,
    isLastStep,
    currentStepTitle,
    errors,
    register,
    handleSubmit,
    handleNext,
    handleBack,
    onSubmit,
  }
}
