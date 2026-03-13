import { useEffect, useState } from 'react'
import { BrowserRouter, Routes, Route, Link, Navigate } from 'react-router-dom'
import { AuthProvider, useAuth } from './context/AuthContext'
import ProtectedRoute from './components/ProtectedRoute'
import LoginPage from './pages/LoginPage/index'
import TasksPage from './pages/TasksPage/index'
import TrackingPage from './pages/TrackingPage/index'
import DailyStatusPage from './pages/DailyStatusPage/index'
import StatsPage from './pages/StatsPage/index'

function Home() {
  const [message, setMessage] = useState<string | null>(null)

  useEffect(() => {
    fetch('/auth/check')
      .then((res) => res.text())
      .then(setMessage)
  }, [])

  return (
    <>
      <h1>Hello World</h1>
      {message && <p>{message}</p>}
    </>
  )
}

function Layout() {
  const { user, logout } = useAuth()

  return (
    <>
      {user && (
        <nav>
          <Link to="/">Home</Link> | <Link to="/tasks">Tasks</Link> | <Link to="/tracking">Tracking</Link> | <Link to="/daily">Daily Status</Link> | <Link to="/stats">Статистика</Link>
          {' '}| <span>{user.username}</span> <button onClick={logout}>Выйти</button>
        </nav>
      )}
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/" element={<ProtectedRoute><Home /></ProtectedRoute>} />
        <Route path="/tasks" element={<ProtectedRoute><TasksPage /></ProtectedRoute>} />
        <Route path="/tracking" element={<ProtectedRoute><TrackingPage /></ProtectedRoute>} />
        <Route path="/daily" element={<ProtectedRoute><DailyStatusPage /></ProtectedRoute>} />
        <Route path="/stats" element={<ProtectedRoute><StatsPage /></ProtectedRoute>} />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </>
  )
}

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Layout />
      </AuthProvider>
    </BrowserRouter>
  )
}
