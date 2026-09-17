import { Route, Routes } from 'react-router-dom'
import { Navbar } from './components/Navbar'
import { ProtectedRoute } from './components/ProtectedRoute'
import { Login } from './pages/Login'
import { Register } from './pages/Register'
import { Salas } from './pages/Salas'
import { MisReservas } from './pages/MisReservas'
import { AdminSalas } from './pages/admin/AdminSalas'
import { AdminReservas } from './pages/admin/AdminReservas'

function App() {
  return (
    <div>
      <Navbar />
      <Routes>
        <Route path="/" element={<Salas />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route element={<ProtectedRoute />}>
          <Route path="/mis-reservas" element={<MisReservas />} />
        </Route>
        <Route element={<ProtectedRoute adminOnly />}>
          <Route path="/admin/salas" element={<AdminSalas />} />
          <Route path="/admin/reservas" element={<AdminReservas />} />
        </Route>
      </Routes>
    </div>
  )
}

export default App
