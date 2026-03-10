import { useEffect, useState } from 'react'
import {
  Actions,
  Button,
  CheckboxLabel,
  Form,
  Input,
  Page,
  Table,
  TableWrapper,
} from './TasksPage.styles'

interface Task {
  id: number
  title: string
  active: boolean
  createdAt: string
}

export default function TasksPage() {
  const [tasks, setTasks] = useState<Task[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [title, setTitle] = useState('')
  const [active, setActive] = useState(true)
  const [submitting, setSubmitting] = useState(false)
  const [selected, setSelected] = useState<Set<number>>(new Set())
  const [toggling, setToggling] = useState(false)
  const [deleting, setDeleting] = useState(false)

  useEffect(() => {
    fetch('/api/tasks')
      .then((res) => {
        if (!res.ok) throw new Error(`Error: ${res.status}`)
        return res.json()
      })
      .then(setTasks)
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false))
  }, [])

  function handleSubmit(e: React.SyntheticEvent<HTMLFormElement>) {
    e.preventDefault()
    setSubmitting(true)
    fetch('/api/tasks', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ title, active }),
    })
      .then((res) => {
        if (!res.ok) throw new Error(`Error: ${res.status}`)
        return res.json()
      })
      .then((created: Task) => {
        setTasks((prev) => [...prev, created])
        setTitle('')
        setActive(true)
      })
      .catch((err) => setError(err.message))
      .finally(() => setSubmitting(false))
  }

  function toggleSelect(id: number) {
    setSelected((prev) => {
      const next = new Set(prev)
      next.has(id) ? next.delete(id) : next.add(id)
      return next
    })
  }

  function handleDelete() {
    if (selected.size === 0) return
    setDeleting(true)
    const deletes = [...selected].map((id) =>
      fetch(`/api/tasks/${id}`, { method: 'DELETE' }).then((res) => {
        if (!res.ok) throw new Error(`Error: ${res.status}`)
        return id
      })
    )
    Promise.all(deletes)
      .then((ids) => {
        setTasks((prev) => prev.filter((t) => !ids.includes(t.id)))
        setSelected(new Set())
      })
      .catch((err) => setError(err.message))
      .finally(() => setDeleting(false))
  }

  function handleToggleActive() {
    if (selected.size === 0) return
    setToggling(true)
    const updates = tasks
      .filter((t) => selected.has(t.id))
      .map((t) =>
        fetch(`/api/tasks/${t.id}`, {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ ...t, active: !t.active }),
        }).then((res) => {
          if (!res.ok) throw new Error(`Error: ${res.status}`)
          return res.json() as Promise<Task>
        })
      )
    Promise.all(updates)
      .then((updated) => {
        setTasks((prev) =>
          prev.map((t) => updated.find((u) => u.id === t.id) ?? t)
        )
        setSelected(new Set())
      })
      .catch((err) => setError(err.message))
      .finally(() => setToggling(false))
  }

  if (loading) return <p>Loading...</p>
  if (error) return <p>Error: {error}</p>

  return (
    <Page>
      <h1>Tasks</h1>

      <Form onSubmit={handleSubmit}>
        <Input
          type="text"
          placeholder="Title"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          required
        />
        <CheckboxLabel>
          <input
            type="checkbox"
            checked={active}
            onChange={(e) => setActive(e.target.checked)}
          />
          Active
        </CheckboxLabel>
        <Button type="submit" disabled={submitting}>
          {submitting ? 'Creating...' : 'Create'}
        </Button>
      </Form>

      <TableWrapper>
        <Table>
          <thead>
            <tr>
              <th></th>
              <th>ID</th>
              <th>Title</th>
              <th>Active</th>
              <th>Created At</th>
            </tr>
          </thead>
          <tbody>
            {tasks.map((task) => (
              <tr key={task.id}>
                <td>
                  <input
                    type="checkbox"
                    checked={selected.has(task.id)}
                    onChange={() => toggleSelect(task.id)}
                  />
                </td>
                <td>{task.id}</td>
                <td>{task.title}</td>
                <td>{task.active ? 'Yes' : 'No'}</td>
                <td>{new Date(task.createdAt).toLocaleString()}</td>
              </tr>
            ))}
          </tbody>
        </Table>

        <Actions>
          <Button
            onClick={handleToggleActive}
            disabled={selected.size === 0 || toggling}
          >
            {toggling ? 'Updating...' : 'Изменить активацию'}
          </Button>
          <Button
            onClick={handleDelete}
            disabled={selected.size === 0 || deleting}
          >
            {deleting ? 'Deleting...' : 'Удалить'}
          </Button>
        </Actions>
      </TableWrapper>
    </Page>
  )
}
