import { useState } from 'react'
import { NavLink, useNavigate } from 'react-router-dom'
import {
  ArrowLeftRight,
  ChevronsLeft,
  ChevronsRight,
  LayoutDashboard,
  LogOut,
  Menu,
  Settings,
  Tags,
  UserRound,
} from 'lucide-react'
import { cn } from '@/lib/utils'
import { useAuth } from '@/contexts/auth-context'
import { Button } from '@/components/ui/button'
import { Separator } from '@/components/ui/separator'
import {
  Sheet,
  SheetContent,
  SheetTitle,
  SheetTrigger,
} from '@/components/ui/sheet'

const NAV_ITEMS = [
  {
    to: '/app/dashboard',
    label: 'Dashboard',
    icon: LayoutDashboard,
    end: true,
  },
  { to: '/app/transacoes', label: 'Transações', icon: ArrowLeftRight },
  { to: '/app/categorias', label: 'Categorias', icon: Tags },
  { to: '/app/configuracoes', label: 'Configurações', icon: Settings },
  { to: '/app/perfil', label: 'Perfil', icon: UserRound },
]

interface SidebarContentProps {
  collapsed?: boolean
  onNavigate?: () => void
}

function SidebarContent({ collapsed = false, onNavigate }: SidebarContentProps) {
  const navigate = useNavigate()
  const { logout } = useAuth()

  function handleLogout() {
    logout()
    navigate('/login', { replace: true })
  }

  return (
    <>
      <div
        className={cn(
          'flex h-16 items-center gap-2 border-b border-border text-lg font-bold',
          collapsed ? 'justify-center px-2' : 'px-6',
        )}
      >
        <img src="/favicon.svg" alt="" className="size-7 shrink-0" />
        {!collapsed && <span className="truncate">Controle Financeiro</span>}
      </div>

      <nav
        className={cn(
          'flex-1 space-y-1 overflow-y-auto p-4',
          collapsed && 'px-3',
        )}
      >
        {NAV_ITEMS.map((item) => (
          <NavLink
            key={item.to}
            to={item.to}
            end={item.end}
            onClick={onNavigate}
            title={item.label}
            className={({ isActive }) =>
              cn(
                'flex items-center gap-3 rounded-xl px-4 py-2.5 text-sm font-semibold transition-colors',
                collapsed && 'justify-center px-2',
                isActive
                  ? 'bg-primary/10 text-primary'
                  : 'text-muted hover:bg-muted/10 hover:text-foreground',
              )
            }
          >
            <item.icon className="size-5 shrink-0" />
            {!collapsed && item.label}
          </NavLink>
        ))}
      </nav>

      <Separator />

      <div className={cn('py-4', collapsed ? 'flex justify-center px-2' : 'px-6')}>
        <button
          className="flex items-center gap-1.5 text-xs font-semibold text-muted transition-colors hover:text-expense"
          onClick={handleLogout}
          title="Sair"
        >
          <LogOut className="size-4 shrink-0" />
          {!collapsed && <span>Sair</span>}
        </button>
      </div>
    </>
  )
}

interface SidebarProps {
  collapsed: boolean
  onToggleCollapse: () => void
}

export function Sidebar({ collapsed, onToggleCollapse }: SidebarProps) {
  return (
    <aside
      className={cn(
        'relative hidden h-full shrink-0 flex-col border-r border-border bg-surface transition-[width] duration-200 lg:flex',
        collapsed ? 'w-20' : 'w-64',
      )}
    >
      <SidebarContent collapsed={collapsed} />

      <button
        onClick={onToggleCollapse}
        title={collapsed ? 'Expandir menu' : 'Recolher menu'}
        className="absolute right-0 top-1/2 z-10 flex size-6 -translate-y-1/2 translate-x-1/2 items-center justify-center rounded-full border border-border bg-surface text-muted shadow-sm transition-[color] hover:text-primary"
      >
        {collapsed ? (
          <ChevronsRight className="size-4" />
        ) : (
          <ChevronsLeft className="size-4" />
        )}
        <span className="sr-only">
          {collapsed ? 'Expandir menu' : 'Recolher menu'}
        </span>
      </button>
    </aside>
  )
}

export function MobileNav() {
  const [open, setOpen] = useState(false)

  return (
    <Sheet open={open} onOpenChange={setOpen}>
      <SheetTrigger asChild>
        <Button variant="ghost" size="icon" className="lg:hidden">
          <Menu className="size-5" />
          <span className="sr-only">Abrir menu</span>
        </Button>
      </SheetTrigger>
      <SheetContent side="left" className="flex w-72 flex-col gap-0 p-0">
        <SheetTitle className="sr-only">Menu de navegação</SheetTitle>
        <SidebarContent onNavigate={() => setOpen(false)} />
      </SheetContent>
    </Sheet>
  )
}
