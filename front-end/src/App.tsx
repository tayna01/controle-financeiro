import { Navigate, Route, Routes } from 'react-router-dom'
import { ForgotPassword } from '@/pages/ForgotPassword'
import { Login } from '@/pages/Login'
import { Register } from '@/pages/Register'

function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/esqueci-senha" element={<ForgotPassword />} />
      <Route path="/cadastro" element={<Register />} />
      <Route path="*" element={<Navigate to="/login" replace />} />
    </Routes>
  )
}

export default App
