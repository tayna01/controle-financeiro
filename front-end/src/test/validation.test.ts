import { describe, it, expect } from 'vitest'
import { emailSchema, passwordSchema } from '@/lib/validation'

describe('validation', () => {
  it('accepts valid email', () => {
    expect(() => emailSchema.parse('user@example.com')).not.toThrow()
  })

  it('rejects invalid email', () => {
    expect(() => emailSchema.parse('invalid')).toThrow()
  })

  it('accepts strong password', () => {
    expect(() => passwordSchema.parse('Strong@123')).not.toThrow()
  })

  it('rejects weak password', () => {
    expect(() => passwordSchema.parse('weak')).toThrow()
  })
})