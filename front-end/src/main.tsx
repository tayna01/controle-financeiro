import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom'
import './index.css'
import App from './App.tsx'
import { AuthProvider } from '@/contexts/auth-provider'
import { Toaster } from '@/components/ui/toaster'

const savedTheme = localStorage.getItem('theme')
document.documentElement.classList.toggle('dark', savedTheme ? savedTheme === 'dark' : true)

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <BrowserRouter>
      <AuthProvider>
        <App />
        <Toaster />
      </AuthProvider>
    </BrowserRouter>
  </StrictMode>,
)
