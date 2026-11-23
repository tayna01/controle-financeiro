import { useState } from 'react'
import { Download } from 'lucide-react'
import { PageHeader } from '@/components/page-header'
import { Button } from '@/components/ui/button'
import { Card } from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { useWallet } from '@/contexts/wallet-context'
import { api, getApiErrorMessage } from '@/lib/api'
import { useToast } from '@/hooks/use-toast'

export function Reports() {
  const { selectedWallet } = useWallet()
  const { toast } = useToast()
  const [startDate, setStartDate] = useState('')
  const [endDate, setEndDate] = useState('')
  const [format, setFormat] = useState<'csv' | 'pdf'>('csv')
  const [downloading, setDownloading] = useState(false)

  async function handleDownload() {
    if (!selectedWallet) return
    setDownloading(true)
    try {
      const response = await api.get(
        `/api/v1/wallets/${selectedWallet.id}/reports/statement`,
        {
          params: {
            startDate: startDate || undefined,
            endDate: endDate || undefined,
            format,
          },
          responseType: 'blob',
        },
      )
      const url = window.URL.createObjectURL(new Blob([response.data]))
      const link = document.createElement('a')
      link.href = url
      link.download = `extrato.${format}`
      document.body.appendChild(link)
      link.click()
      link.remove()
      window.URL.revokeObjectURL(url)
      toast({ title: 'Relatório baixado com sucesso' })
    } catch (error) {
      toast({
        title: 'Erro ao baixar relatório',
        description: getApiErrorMessage(error, 'Não foi possível baixar o relatório.'),
        variant: 'destructive',
      })
    } finally {
      setDownloading(false)
    }
  }

  return (
    <div className="space-y-6">
      <PageHeader
        title="Relatórios"
        description="Gere extratos da carteira selecionada"
        breadcrumbs={[
          { label: 'Início', to: '/app/dashboard' },
          { label: 'Relatórios' },
        ]}
      />

      <Card className="space-y-4 p-6">
        <div className="grid gap-4 md:grid-cols-3">
          <div className="space-y-2">
            <Label htmlFor="start">Data inicial</Label>
            <Input
              id="start"
              type="date"
              value={startDate}
              onChange={(event) => setStartDate(event.target.value)}
            />
          </div>
          <div className="space-y-2">
            <Label htmlFor="end">Data final</Label>
            <Input
              id="end"
              type="date"
              value={endDate}
              onChange={(event) => setEndDate(event.target.value)}
            />
          </div>
          <div className="space-y-2">
            <Label>Formato</Label>
            <Select value={format} onValueChange={(value) => setFormat(value as 'csv' | 'pdf')}>
              <SelectTrigger>
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="csv">CSV</SelectItem>
                <SelectItem value="pdf">PDF</SelectItem>
              </SelectContent>
            </Select>
          </div>
        </div>

        <Button onClick={handleDownload} disabled={!selectedWallet || downloading}>
          <Download className="size-4" />
          {downloading ? 'Baixando...' : 'Baixar relatório'}
        </Button>
      </Card>
    </div>
  )
}