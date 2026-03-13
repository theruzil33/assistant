import { createContext, useContext, useEffect, useState } from 'react'

interface AuthUser {
  id: number
  username: string
}

interface AuthContextValue {
  user: AuthUser | null
  loading: boolean
  login: (username: string, password: string) => Promise<void>
  logout: () => void
}

const AuthContext = createContext<AuthContextValue | null>(null)

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<AuthUser | null>(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    fetch('/auth/check')
      .then((res) => {
        if (res.ok) {
          const stored = localStorage.getItem('auth_user')
          if (stored) setUser(JSON.parse(stored))
        } else {
          localStorage.removeItem('auth_user')
        }
      })
      .catch(() => localStorage.removeItem('auth_user'))
      .finally(() => setLoading(false))
  }, [])

  async function login(username: string, password: string) {
    const res = await fetch('/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password }),
    })
    if (!res.ok) throw new Error('Неверный логин или пароль')
    const data: AuthUser = await res.json()
    setUser(data)
    localStorage.setItem('auth_user', JSON.stringify(data))
  }

  function logout() {
    setUser(null)
    localStorage.removeItem('auth_user')
  }

  return <AuthContext.Provider value={{ user, loading, login, logout }}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used within AuthProvider')
  return ctx
}
