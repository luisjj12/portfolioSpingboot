import { useState } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export function Login() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      await login(email, password)
      navigate(location.state?.from ?? '/')
    } catch (err) {
      setError(
        err.message?.startsWith('No se pudo conectar')
          ? err.message
          : 'Email o contraseña incorrectos',
      )
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="page-narrow">
      <h1 className="page-title">Iniciar sesión</h1>
      <p className="page-subtitle">Entra con tu cuenta de PortfolioApp</p>
      <form onSubmit={handleSubmit} className="form">
        <div className="field">
          <label>Email</label>
          <input
            type="email"
            required
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            className="input"
          />
        </div>
        <div className="field">
          <label>Contraseña</label>
          <input
            type="password"
            required
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className="input"
          />
        </div>
        {error && <p className="text-error">{error}</p>}
        <button type="submit" disabled={loading} className="btn btn-primary btn-block">
          {loading ? 'Entrando...' : 'Entrar'}
        </button>
      </form>
      <p className="text-sm text-muted mt-4">
        ¿No tienes cuenta? <Link to="/register" className="link">Regístrate</Link>
      </p>
    </div>
  )
}
