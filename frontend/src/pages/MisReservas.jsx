import { useEffect, useState } from 'react'
import { api } from '../api/client'
import { useAuth } from '../context/AuthContext'

export function MisReservas() {
  const { token } = useAuth()
  const [reservas, setReservas] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const cargar = () => {
    setLoading(true)
    api
      .misReservas(token)
      .then(setReservas)
      .catch(() => setError('No se pudieron cargar tus reservas'))
      .finally(() => setLoading(false))
  }

  useEffect(cargar, [token])

  const cancelar = async (id) => {
    if (!confirm('¿Cancelar esta reserva?')) return
    try {
      await api.cancelarReserva(id, token)
      setReservas((prev) => prev.filter((r) => r.id !== id))
    } catch (err) {
      alert(err.message || 'No se pudo cancelar la reserva')
    }
  }

  return (
    <div className="page">
      <h1 className="page-title">Mis reservas</h1>
      <p className="page-subtitle">Estas son las reservas que has hecho</p>

      {loading && <p className="text-muted">Cargando...</p>}
      {!loading && error && <p className="text-error">{error}</p>}
      {!loading && !error && reservas.length === 0 && (
        <p className="text-muted">Todavía no tienes reservas.</p>
      )}

      <div className="list-stack">
        {reservas.map((r) => (
          <div key={r.id} className="card card-row">
            <div>
              <p className="card-title">{r.sala?.nombre}</p>
              <p className="text-muted text-sm">
                {r.fecha} · {r.horaInicio} - {r.horaFin}
              </p>
            </div>
            <button onClick={() => cancelar(r.id)} className="btn btn-danger btn-sm">
              Cancelar
            </button>
          </div>
        ))}
      </div>
    </div>
  )
}
