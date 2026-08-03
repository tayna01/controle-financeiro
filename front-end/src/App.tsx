import { Navigate, Route, Routes } from 'react-router-dom'
import { AppLayout } from '@/components/layout/app-layout'
import { Categories } from '@/pages/Categories'
import { Dashboard } from '@/pages/Dashboard'
import { ForgotPassword } from '@/pages/ForgotPassword'
import { Login } from '@/pages/Login'
import { Profile } from '@/pages/Profile'
import { Register } from '@/pages/Register'
import { ResetPassword } from '@/pages/ResetPassword'
import { Transactions } from '@/pages/Transactions'

function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/recuperar-senha" element={<ForgotPassword />} />
      <Route path="/redefinir-senha/:token" element={<ResetPassword />} />
      <Route path="/cadastro" element={<Register />} />

      <Route element={<AppLayout />}>
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/transacoes" element={<Transactions />} />
        <Route path="/categorias" element={<Categories />} />
        <Route path="/perfil" element={<Profile />} />
      </Route>

      <Route path="*" element={<Navigate to="/login" replace />} />
    </Routes>
  )
}

export default App
