const API_URL = import.meta.env.VITE_API_URL ?? 'http://localhost:8080/api'

async function request(path, { method = 'GET', body, token } = {}) {
  const headers = {}
  if (body !== undefined) headers['Content-Type'] = 'application/json'
  if (token) headers['Authorization'] = `Bearer ${token}`

  let res
  try {
    res = await fetch(`${API_URL}${path}`, {
      method,
      headers,
      body: body !== undefined ? JSON.stringify(body) : undefined,
    })
  } catch {
    throw new Error('No se pudo conectar con el servidor. Verifica que el backend esté corriendo.')
  }

  if (!res.ok) {
    const text = await res.text().catch(() => '')
    throw new Error(text || `Error ${res.status}`)
  }

  if (res.status === 204) return null
  const contentType = res.headers.get('content-type') ?? ''
  return contentType.includes('application/json') ? res.json() : null
}

export const api = {
  login: (data) => request('/auth/login', { method: 'POST', body: data }),
  register: (data) => request('/auth/register', { method: 'POST', body: data }),

  getSalas: () => request('/salas'),
  getSala: (id) => request(`/salas/${id}`),
  createSala: (data, token) => request('/salas', { method: 'POST', body: data, token }),
  updateSala: (id, data, token) => request(`/salas/${id}`, { method: 'PUT', body: data, token }),
  deleteSala: (id, token) => request(`/salas/${id}`, { method: 'DELETE', token }),

  crearReserva: (salaId, data, token) =>
    request(`/reservas?salaId=${salaId}`, { method: 'POST', body: data, token }),
  misReservas: (token) => request('/reservas/mias', { token }),
  todasReservas: (token) => request('/reservas', { token }),
  cancelarReserva: (id, token) => request(`/reservas/${id}`, { method: 'DELETE', token }),
}
