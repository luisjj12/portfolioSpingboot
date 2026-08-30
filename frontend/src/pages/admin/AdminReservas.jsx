import { useEffect, useState } from 'react'
import { api } from '../../api/client'
import { useAuth } from '../../context/AuthContext'

export function AdminReservas() {
  const { token } = useAuth()
  const [reservas, setReservas] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const cargar = () => {
    setLoading(true)
    api
      .todasReservas(token)
      .then(setReservas)
      .catch(() => setError('No se pudieron cargar las reservas'))
      .finally(() => setLoading(false))
  }

  useEffect(cargar, [token])

  const cancelar = async (id) => {
    if (!confirm('¿Cancelar esta reserva?')) return
    try {
      await api.cancelarReserva(id, token)
      setReservas((prev) => prev.filter((r) => r.id !== id))
    } catch {
      alert('No se pudo cancelar la reserva')
    }
  }

  return (
    <div className="page">
      <h1 className="page-title">Todas las reservas</h1>
      <p className="page-subtitle">Vista de administrador de todas las reservas activas</p>

      {loading && <p className="text-muted">Cargando...</p>}
      {!loading && error && <p className="text-error">{error}</p>}
      {!loading && !error && reservas.length === 0 && (
        <p className="text-muted">No hay reservas registradas.</p>
      )}

      <div className="table-wrap">
        <table>
          <thead>
            <tr>
              <th>Sala</th>
              <th>Usuario</th>
              <th>Fecha</th>
              <th>Horario</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {reservas.map((r) => (
              <tr key={r.id}>
                <td>{r.sala?.nombre}</td>
                <td>{r.usuario?.nombre} ({r.usuario?.email})</td>
                <td>{r.fecha}</td>
                <td>{r.horaInicio} - {r.horaFin}</td>
                <td className="table-actions">
                  <button onClick={() => cancelar(r.id)} className="btn btn-danger btn-sm">
                    Cancelar
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}
