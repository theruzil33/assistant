import { BrowserRouter, Routes, Route, Link } from 'react-router-dom'
import TasksPage from './pages/TasksPage/index'
import TrackingPage from './pages/TrackingPage/index'
import DailyStatusPage from './pages/DailyStatusPage/index'
import StatsPage from './pages/StatsPage/index'

function Home() {
  return <h1>Hello World</h1>
}

export default function App() {
  return (
    <BrowserRouter>
      <nav>
        <Link to="/">Home</Link> | <Link to="/tasks">Tasks</Link> | <Link to="/tracking">Tracking</Link> | <Link to="/daily">Daily Status</Link> | <Link to="/stats">Статистика</Link>
      </nav>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/tasks" element={<TasksPage />} />
        <Route path="/tracking" element={<TrackingPage />} />
        <Route path="/daily" element={<DailyStatusPage />} />
        <Route path="/stats" element={<StatsPage />} />
      </Routes>
    </BrowserRouter>
  )
}
