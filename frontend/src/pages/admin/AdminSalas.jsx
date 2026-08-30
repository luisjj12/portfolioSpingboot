import { useEffect, useState } from 'react'
import { api } from '../../api/client'
import { useAuth } from '../../context/AuthContext'

const emptyForm = { nombre: '', capacidad: '', ubicacion: '' }

export function AdminSalas() {
  const { token } = useAuth()
  const [salas, setSalas] = useState([])
  const [loading, setLoading] = useState(true)
  const [form, setForm] = useState(emptyForm)
  const [editandoId, setEditandoId] = useState(null)
  const [error, setError] = useState('')

  const cargar = () => {
    setLoading(true)
    api
      .getSalas()
      .then(setSalas)
      .catch(() => setError('No se pudieron cargar las salas'))
      .finally(() => setLoading(false))
  }

  useEffect(cargar, [])

  const editar = (sala) => {
    setEditandoId(sala.id)
    setForm({ nombre: sala.nombre, capacidad: sala.capacidad, ubicacion: sala.ubicacion })
  }

  const cancelarEdicion = () => {
    setEditandoId(null)
    setForm(emptyForm)
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    const datos = { ...form, capacidad: Number(form.capacidad) }
    try {
      if (editandoId) {
        await api.updateSala(editandoId, datos, token)
      } else {
        await api.createSala(datos, token)
      }
      cancelarEdicion()
      cargar()
    } catch {
      setError('No se pudo guardar la sala')
    }
  }

  const eliminar = async (id) => {
    if (!confirm('¿Eliminar esta sala?')) return
    try {
      await api.deleteSala(id, token)
      setSalas((prev) => prev.filter((s) => s.id !== id))
    } catch {
      alert('No se pudo eliminar la sala')
    }
  }

  return (
    <div className="page">
      <h1 className="page-title">Administrar salas</h1>
      <p className="page-subtitle">Crea, edita o elimina las salas disponibles</p>

      <form onSubmit={handleSubmit} className="inline-form form-row" style={{ flexWrap: 'wrap' }}>
        <input
          type="text"
          placeholder="Nombre"
          required
          value={form.nombre}
          onChange={(e) => setForm({ ...form, nombre: e.target.value })}
          className="input"
        />
        <input
          type="number"
          min="1"
          placeholder="Capacidad"
          required
          value={form.capacidad}
          onChange={(e) => setForm({ ...form, capacidad: e.target.value })}
          className="input"
        />
        <input
          type="text"
          placeholder="Ubicación"
          required
          value={form.ubicacion}
          onChange={(e) => setForm({ ...form, ubicacion: e.target.value })}
          className="input"
        />
        {error && <p className="text-error inline-form-actions">{error}</p>}
        <div className="inline-form-actions">
          <button type="submit" className="btn btn-primary">
            {editandoId ? 'Guardar cambios' : 'Crear sala'}
          </button>
          {editandoId && (
            <button type="button" onClick={cancelarEdicion} className="btn btn-outline">
              Cancelar
            </button>
          )}
        </div>
      </form>

      {loading && <p className="text-muted">Cargando...</p>}

      <div className="list-stack">
        {salas.map((sala) => (
          <div key={sala.id} className="card card-row">
            <div>
              <p className="card-title">{sala.nombre}</p>
              <p className="text-muted text-sm">{sala.ubicacion} · capacidad {sala.capacidad}</p>
            </div>
            <div className="form-row" style={{ flex: 'none' }}>
              <button onClick={() => editar(sala)} className="btn btn-outline btn-sm">
                Editar
              </button>
              <button onClick={() => eliminar(sala.id)} className="btn btn-danger btn-sm">
                Eliminar
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}
