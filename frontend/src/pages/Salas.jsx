import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api } from '../api/client'
import { useAuth } from '../context/AuthContext'

const emptyForm = { fecha: '', horaInicio: '', horaFin: '' }

export function Salas() {
  const { token, isAuthenticated } = useAuth()
  const [salas, setSalas] = useState([])
  const [loading, setLoading] = useState(true)
  const [salaAbierta, setSalaAbierta] = useState(null)
  const [form, setForm] = useState(emptyForm)
  const [error, setError] = useState('')
  const [ok, setOk] = useState('')
  const [enviando, setEnviando] = useState(false)

  useEffect(() => {
    api
      .getSalas()
      .then(setSalas)
      .catch(() => setError('No se pudo conectar con el servidor'))
      .finally(() => setLoading(false))
  }, [])

  const abrirFormulario = (salaId) => {
    setSalaAbierta(salaAbierta === salaId ? null : salaId)
    setForm(emptyForm)
    setError('')
    setOk('')
  }

  const handleReservar = async (e, salaId) => {
    e.preventDefault()
    if (form.horaInicio >= form.horaFin) {
      setError('La hora de inicio debe ser anterior a la hora de fin')
      return
    }
    setError('')
    setOk('')
    setEnviando(true)
    try {
      await api.crearReserva(salaId, form, token)
      setOk('¡Reserva creada!')
      setForm(emptyForm)
    } catch (err) {
      setError(err.message || 'No se pudo crear la reserva')
    } finally {
      setEnviando(false)
    }
  }

  return (
    <div className="page">
      <h1 className="page-title">Salas disponibles</h1>
      <p className="page-subtitle">Elige una sala y reserva el horario que necesites</p>

      {loading && <p className="text-muted">Cargando salas...</p>}
      {!loading && error && salas.length === 0 && <p className="text-error">{error}</p>}
      {!loading && !error && salas.length === 0 && (
        <p className="text-muted">Todavía no hay salas registradas.</p>
      )}

      <div className="grid">
        {salas.map((sala) => (
          <div key={sala.id} className="card">
            <h2 className="card-title">{sala.nombre}</h2>
            <p className="text-muted text-sm">{sala.ubicacion}</p>
            <p className="text-muted text-sm">Capacidad: {sala.capacidad} personas</p>

            {isAuthenticated ? (
              <button onClick={() => abrirFormulario(sala.id)} className="btn btn-primary btn-block mt-4">
                {salaAbierta === sala.id ? 'Cancelar' : 'Reservar'}
              </button>
            ) : (
              <Link to="/login" className="btn btn-outline btn-block mt-4">
                Inicia sesión para reservar
              </Link>
            )}

            {salaAbierta === sala.id && (
              <form onSubmit={(e) => handleReservar(e, sala.id)} className="reserve-form">
                <div className="field">
                  <label>Fecha</label>
                  <input
                    type="date"
                    required
                    value={form.fecha}
                    onChange={(e) => setForm({ ...form, fecha: e.target.value })}
                    className="input"
                  />
                </div>
                <div className="form-row">
                  <div className="field">
                    <label>Desde</label>
                    <input
                      type="time"
                      required
                      value={form.horaInicio}
                      onChange={(e) => setForm({ ...form, horaInicio: e.target.value })}
                      className="input"
                    />
                  </div>
                  <div className="field">
                    <label>Hasta</label>
                    <input
                      type="time"
                      required
                      value={form.horaFin}
                      onChange={(e) => setForm({ ...form, horaFin: e.target.value })}
                      className="input"
                    />
                  </div>
                </div>
                {error && <p className="text-error">{error}</p>}
                {ok && <p className="text-success">{ok}</p>}
                <button type="submit" disabled={enviando} className="btn btn-dark btn-block">
                  {enviando ? 'Reservando...' : 'Confirmar reserva'}
                </button>
              </form>
            )}
          </div>
        ))}
      </div>
    </div>
  )
}
