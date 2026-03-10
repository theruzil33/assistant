import { BrowserRouter, Routes, Route, Link } from 'react-router-dom'
import TasksPage from './pages/TasksPage/index'

function Home() {
  return <h1>Hello World</h1>
}

export default function App() {
  return (
    <BrowserRouter>
      <nav>
        <Link to="/">Home</Link> | <Link to="/tasks">Tasks</Link>
      </nav>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/tasks" element={<TasksPage />} />
      </Routes>
    </BrowserRouter>
  )
}
