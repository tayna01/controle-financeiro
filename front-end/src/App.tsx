import { Navigate, Route, Routes } from 'react-router-dom'
import { AppLayout } from '@/components/layout/app-layout'
import { RequireAuth } from '@/components/require-auth'
import { WalletProvider } from '@/contexts/wallet-provider'
import { Categories } from '@/pages/Categories'
import { ChangePassword } from '@/pages/ChangePassword'
import { Dashboard } from '@/pages/Dashboard'
import { ForgotPassword } from '@/pages/ForgotPassword'
import { Login } from '@/pages/Login'
import { Profile } from '@/pages/Profile'
import { Register } from '@/pages/Register'
import { ResetPassword } from '@/pages/ResetPassword'
import { Sharing } from '@/pages/Sharing'
import { Transactions } from '@/pages/Transactions'

function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/recuperar-senha" element={<ForgotPassword />} />
      <Route path="/redefinir-senha/:token" element={<ResetPassword />} />
      <Route path="/cadastro" element={<Register />} />

      <Route element={<RequireAuth />}>
        <Route
          element={
            <WalletProvider>
              <AppLayout />
            </WalletProvider>
          }
        >
          <Route path="/app/dashboard" element={<Dashboard />} />
          <Route path="/app/transacoes" element={<Transactions />} />
          <Route path="/app/categorias" element={<Categories />} />
          <Route path="/app/compartilhamento" element={<Sharing />} />
          <Route path="/app/perfil" element={<Profile />} />
          <Route path="/app/perfil/senha" element={<ChangePassword />} />
        </Route>
      </Route>

      <Route path="*" element={<Navigate to="/login" replace />} />
    </Routes>
  )
}

export default App
