import { ChevronRight } from 'lucide-react'
import { Link } from 'react-router-dom'
import { cn } from '@/lib/utils'

interface BreadcrumbItem {
  label: string
  to?: string
}

interface PageHeaderProps {
  title: string
  description?: string
  breadcrumbs?: BreadcrumbItem[]
  className?: string
}

export function PageHeader({
  title,
  description,
  breadcrumbs = [],
  className,
}: PageHeaderProps) {
  return (
    <header className={cn('space-y-4', className)}>
      <div className="flex items-center gap-2 text-sm text-muted">
        {breadcrumbs.map((item, index) => {
          const isLast = index === breadcrumbs.length - 1
          return (
            <div key={item.label} className="flex items-center gap-2">
              {item.to && !isLast ? (
                <Link
                  to={item.to}
                  className="font-medium transition-colors hover:text-foreground"
                >
                  {item.label}
                </Link>
              ) : (
                <span className={cn(!isLast && 'font-medium')}>{item.label}</span>
              )}
              {!isLast && <ChevronRight className="size-4" />}
            </div>
          )
        })}
      </div>

      <div>
        <h1 className="text-2xl font-bold">{title}</h1>
        {description && (
          <p className="mt-1 text-sm text-muted">{description}</p>
        )}
      </div>
    </header>
  )
}