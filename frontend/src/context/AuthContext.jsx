import { createContext, useCallback, useContext, useState } from 'react'
import { api } from '../api/client'

const AuthContext = createContext(null)
const STORAGE_KEY = 'portfolioapp_auth'

function loadStored() {
  try {
    const raw = localStorage.getItem(STORAGE_KEY)
    return raw ? JSON.parse(raw) : null
  } catch {
    return null
  }
}

export function AuthProvider({ children }) {
  const [auth, setAuth] = useState(loadStored)

  const persist = (data) => {
    setAuth(data)
    if (data) localStorage.setItem(STORAGE_KEY, JSON.stringify(data))
    else localStorage.removeItem(STORAGE_KEY)
  }

  const login = useCallback(async (email, password) => {
    const data = await api.login({ email, password })
    persist(data)
    return data
  }, [])

  const register = useCallback(async (nombre, email, password) => {
    const data = await api.register({ nombre, email, password })
    persist(data)
    return data
  }, [])

  const logout = useCallback(() => persist(null), [])

  const value = {
    user: auth,
    token: auth?.token ?? null,
    isAuthenticated: !!auth,
    isAdmin: auth?.role === 'ADMIN',
    login,
    register,
    logout,
  }

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth debe usarse dentro de AuthProvider')
  return ctx
}
