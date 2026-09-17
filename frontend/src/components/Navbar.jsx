import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export function Navbar() {
  const { user, isAuthenticated, isAdmin, logout } = useAuth()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  return (
    <nav className="navbar">
      <div className="navbar-inner">
        <Link to="/" className="navbar-brand">
          Portfolio<span>App</span>
        </Link>
        <div className="navbar-links">
          <Link to="/">Salas</Link>
          {isAuthenticated && <Link to="/mis-reservas">Mis reservas</Link>}
          {isAdmin && <Link to="/admin/salas">Admin salas</Link>}
          {isAdmin && <Link to="/admin/reservas">Admin reservas</Link>}
          {isAuthenticated ? (
            <>
              <span className="navbar-user">{user.nombre}</span>
              <button onClick={handleLogout} className="btn btn-dark btn-sm">
                Salir
              </button>
            </>
          ) : (
            <>
              <Link to="/login">Entrar</Link>
              <Link to="/register" className="btn btn-primary btn-sm">
                Crear cuenta
              </Link>
            </>
          )}
        </div>
      </div>
    </nav>
  )
}
