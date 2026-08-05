import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { LogOut, Moon, ShieldCheck, Sun } from 'lucide-react'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import { useAuth } from '@/contexts/auth-context'
import { AccountSettingsDialog } from '@/components/layout/account-settings'
import fotoPerfil from '@/assets/foto_perfil.jpeg'

export function UserMenu() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  const [settingsOpen, setSettingsOpen] = useState(false)
  const [dark, setDark] = useState(() =>
    document.documentElement.classList.contains('dark'),
  )

  function toggleTheme() {
    const next = !dark
    document.documentElement.classList.toggle('dark', next)
    setDark(next)
  }

  function handleLogout() {
    logout()
    navigate('/login', { replace: true })
  }

  if (!user) return null

  return (
    <>
      <DropdownMenu>
        <DropdownMenuTrigger asChild>
          <button
            type="button"
            title="Minha conta"
            aria-label="Abrir menu da conta"
            className="size-9 shrink-0 cursor-pointer overflow-hidden rounded-full border-2 border-primary/40 transition-all hover:ring-2 hover:ring-primary/40 focus-visible:ring-2 focus-visible:ring-primary focus:outline-none"
          >
            <img
              src={fotoPerfil}
              alt="Foto do perfil"
              className="size-full object-cover"
            />
          </button>
        </DropdownMenuTrigger>

        <DropdownMenuContent align="end" className="w-64">
          <DropdownMenuLabel>
            <div className="flex items-center gap-3">
              <img
                src={fotoPerfil}
                alt="Foto do perfil"
                className="size-10 shrink-0 rounded-full object-cover"
              />
              <div className="min-w-0">
                <p className="truncate text-sm font-semibold">{user.nome}</p>
                <p className="truncate text-xs text-muted">{user.email}</p>
              </div>
            </div>
          </DropdownMenuLabel>

          <DropdownMenuSeparator />

          <DropdownMenuItem onClick={() => setSettingsOpen(true)}>
            <ShieldCheck className="size-4" />
            Alterar senha
          </DropdownMenuItem>

          <DropdownMenuItem onClick={toggleTheme}>
            {dark ? <Sun className="size-4" /> : <Moon className="size-4" />}
            Tema
            <span className="ml-auto text-xs text-muted">
              {dark ? 'Claro' : 'Escuro'}
            </span>
          </DropdownMenuItem>

          <DropdownMenuSeparator />

          <DropdownMenuItem variant="destructive" onClick={handleLogout}>
            <LogOut className="size-4" />
            Sair
          </DropdownMenuItem>
        </DropdownMenuContent>
      </DropdownMenu>

      <AccountSettingsDialog
        open={settingsOpen}
        onOpenChange={setSettingsOpen}
      />
    </>
  )
}
