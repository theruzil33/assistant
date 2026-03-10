import { useEffect, useState } from 'react'
import { Button, Form, Section, SectionTitle, Table } from './TrackingPage.styles'

interface Task {
  id: number
  title: string
  active: boolean
  createdAt: string
}

interface Tracking {
  tasks: Task[]
}

interface Props {
  trackingId: number
}

export default function TrackingTasksMapping({ trackingId }: Props) {
  const [trackingTasks, setTrackingTasks] = useState<Task[]>([])
  const [allTasks, setAllTasks] = useState<Task[]>([])
  const [addTaskId, setAddTaskId] = useState<string>('')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    setLoading(true)
    setError(null)
    Promise.all([
      fetch(`/api/trackings/${trackingId}/tasks`).then((res) => {
        if (!res.ok) throw new Error(`Error: ${res.status}`)
        return res.json()
      }),
      fetch('/api/tasks').then((res) => {
        if (!res.ok) throw new Error(`Error: ${res.status}`)
        return res.json()
      }),
    ])
      .then(([tasks, all]) => {
        setTrackingTasks(tasks)
        setAllTasks(all)
        setAddTaskId('')
      })
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false))
  }, [trackingId])

  function handleAddTask() {
    if (!addTaskId) return
    fetch(`/api/trackings/${trackingId}/tasks/${addTaskId}`, { method: 'POST' })
      .then((res) => {
        if (!res.ok) throw new Error(`Error: ${res.status}`)
        return res.json()
      })
      .then((updated: Tracking) => {
        setTrackingTasks(updated.tasks)
        setAddTaskId('')
      })
      .catch((err) => setError(err.message))
  }

  function handleRemoveTask(taskId: number) {
    fetch(`/api/trackings/${trackingId}/tasks/${taskId}`, { method: 'DELETE' })
      .then((res) => {
        if (!res.ok) throw new Error(`Error: ${res.status}`)
        setTrackingTasks((prev) => prev.filter((t) => t.id !== taskId))
      })
      .catch((err) => setError(err.message))
  }

  const linkedTaskIds = new Set(trackingTasks.map((t) => t.id))
  const availableTasks = allTasks.filter((t) => !linkedTaskIds.has(t.id))

  return (
    <Section>
      <SectionTitle>Tasks → Tracking #{trackingId}</SectionTitle>

      {error && <p>Error: {error}</p>}
      {loading ? (
        <p>Loading...</p>
      ) : (
        <>
          <Table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Title</th>
                <th>Active</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {trackingTasks.length === 0 ? (
                <tr>
                  <td colSpan={4}>No tasks linked</td>
                </tr>
              ) : (
                trackingTasks.map((task) => (
                  <tr key={task.id}>
                    <td>{task.id}</td>
                    <td>{task.title}</td>
                    <td>{task.active ? 'Yes' : 'No'}</td>
                    <td>
                      <Button onClick={() => handleRemoveTask(task.id)}>Удалить</Button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </Table>

          {availableTasks.length > 0 && (
            <Form
              onSubmit={(e) => {
                e.preventDefault()
                handleAddTask()
              }}
            >
              <select
                value={addTaskId}
                onChange={(e) => setAddTaskId(e.target.value)}
                required
              >
                <option value="">— выбрать task —</option>
                {availableTasks.map((t) => (
                  <option key={t.id} value={t.id}>
                    #{t.id} {t.title}
                  </option>
                ))}
              </select>
              <Button type="submit" disabled={!addTaskId}>
                Добавить
              </Button>
            </Form>
          )}
        </>
      )}
    </Section>
  )
}
