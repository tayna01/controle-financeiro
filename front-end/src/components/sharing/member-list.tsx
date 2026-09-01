import { Loader2, ShieldCheck, Trash2, UserRound } from 'lucide-react'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { Card } from '@/components/ui/card'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { cn } from '@/lib/utils'
import type { WalletMember, WalletRole } from '@/services/wallet-sharing'

const ROLE_LABELS: Record<WalletRole, string> = {
  DONO: 'Dono',
  EDITOR: 'Editor',
  VISUALIZADOR: 'Visualizador',
}

const ROLE_OPTIONS: WalletRole[] = ['EDITOR', 'VISUALIZADOR']

interface MemberListProps {
  members: WalletMember[]
  isOwner: boolean
  removingId: number | null
  changingRoleId: number | null
  onRemove: (member: WalletMember) => void
  onChangeRole: (member: WalletMember, role: WalletRole) => void
}

export function MemberList({
  members,
  isOwner,
  removingId,
  changingRoleId,
  onRemove,
  onChangeRole,
}: MemberListProps) {
  if (members.length === 0) {
    return (
      <Card className="flex flex-col items-center gap-2 p-10 text-center">
        <UserRound className="size-8 text-muted" />
        <p className="font-semibold">Nenhum membro compartilhado</p>
        <p className="text-sm text-muted">
          Compartilhe esta carteira com outros usuários para acessarem juntos.
        </p>
      </Card>
    )
  }

  return (
    <Card className="divide-y divide-border">
      {members.map((member) => {
        const isRoleChanging = changingRoleId === member.userId
        const isRemoving = removingId === member.userId
        const isDonor = member.role === 'DONO'

        return (
          <div key={member.userId} className="flex items-center justify-between gap-3 p-4">
            <div className="flex min-w-0 items-center gap-3">
              <span className="flex size-9 shrink-0 items-center justify-center rounded-full bg-primary/10 text-primary">
                <UserRound className="size-4" />
              </span>
              <div className="min-w-0">
                <p className="truncate font-semibold">{member.name}</p>
                <p className="truncate text-sm text-muted">{member.email}</p>
              </div>
            </div>

            <div className="flex shrink-0 items-center gap-2">
              {isDonor ? (
                <Badge className="gap-1 bg-primary/10 text-primary">
                  <ShieldCheck />
                  {ROLE_LABELS[member.role]}
                </Badge>
              ) : isOwner ? (
                <div className="relative">
                  <Select
                    value={member.role}
                    disabled={isRoleChanging || isRemoving}
                    onValueChange={(value) =>
                      onChangeRole(member, value as WalletRole)
                    }
                  >
                    <SelectTrigger size="sm" className="w-32">
                      {isRoleChanging ? (
                        <Loader2 className="size-4 animate-spin" />
                      ) : (
                        <SelectValue />
                      )}
                    </SelectTrigger>
                    <SelectContent>
                      {ROLE_OPTIONS.map((option) => (
                        <SelectItem key={option} value={option}>
                          {ROLE_LABELS[option]}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>
              ) : (
                <Badge
                  className={cn(
                    member.role === 'EDITOR'
                      ? 'bg-income/10 text-income'
                      : 'bg-muted text-muted',
                  )}
                >
                  {ROLE_LABELS[member.role]}
                </Badge>
              )}

              {!isDonor && isOwner && (
                <Button
                  variant="ghost"
                  size="icon"
                  className="text-expense hover:text-expense"
                  aria-label={`Remover acesso de ${member.name}`}
                  disabled={isRemoving || isRoleChanging}
                  onClick={() => onRemove(member)}
                >
                  {isRemoving ? (
                    <Loader2 className="size-4 animate-spin" />
                  ) : (
                    <Trash2 className="size-4" />
                  )}
                </Button>
              )}
            </div>
          </div>
        )
      })}
    </Card>
  )
}
