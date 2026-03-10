import { BrowserRouter, Routes, Route, Link } from 'react-router-dom'
import TasksPage from './pages/TasksPage/index'
import TrackingPage from './pages/TrackingPage/index'

function Home() {
  return <h1>Hello World</h1>
}

export default function App() {
  return (
    <BrowserRouter>
      <nav>
        <Link to="/">Home</Link> | <Link to="/tasks">Tasks</Link> | <Link to="/tracking">Tracking</Link>
      </nav>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/tasks" element={<TasksPage />} />
        <Route path="/tracking" element={<TrackingPage />} />
      </Routes>
    </BrowserRouter>
  )
}
