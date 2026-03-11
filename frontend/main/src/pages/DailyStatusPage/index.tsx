import { useEffect, useState } from 'react'
import { Page, Table, Button } from '../../components/ui'
import styled from 'styled-components'

interface Tracking {
  id: number
  title: string
}

interface Task {
  id: number
  title: string
  active: boolean
}

interface TaskRow extends Task {
  statusId: number | null
  checked: boolean
}

const today = new Date().toISOString().slice(0, 10)

const todayLabel = new Date().toLocaleDateString('ru-RU', {
  day: 'numeric',
  month: 'long',
  year: 'numeric',
})

const Select = styled.select`
  padding: 6px 10px;
  font-size: 14px;
`

export default function DailyStatusPage() {
  const [trackings, setTrackings] = useState<Tracking[]>([])
  const [selectedId, setSelectedId] = useState<number | null>(null)
  const [rows, setRows] = useState<TaskRow[]>([])
  const [loadingTasks, setLoadingTasks] = useState(false)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    fetch('/api/trackings')
      .then((res) => {
        if (!res.ok) throw new Error(`Error: ${res.status}`)
        return res.json() as Promise<Tracking[]>
      })
      .then(setTrackings)
      .catch((err) => setError(err.message))
  }, [])

  useEffect(() => {
    if (selectedId === null) {
      setRows([])
      return
    }

    setLoadingTasks(true)
    setError(null)

    fetch(`/api/trackings/${selectedId}/tasks`)
      .then((res) => {
        if (!res.ok) throw new Error(`Error: ${res.status}`)
        return res.json() as Promise<Task[]>
      })
      .then((tasks) =>
        Promise.all(
          tasks.map((task) =>
            fetch(`/api/task-statuses?trackingId=${selectedId}&taskId=${task.id}`)
              .then((res) => {
                if (!res.ok) throw new Error(`Error: ${res.status}`)
                return res.json() as Promise<{ id: number; date: string }[]>
              })
              .then((statuses) => {
                const todayStatus = statuses.find((s) => s.date === today) ?? null
                return {
                  ...task,
                  statusId: todayStatus?.id ?? null,
                  checked: todayStatus !== null,
                } satisfies TaskRow
              })
          )
        )
      )
      .then((data) => setRows(data.sort((a, b) => a.id - b.id)))
      .catch((err) => setError(err.message))
      .finally(() => setLoadingTasks(false))
  }, [selectedId])

  function handleCheck(taskId: number, checked: boolean) {
    setRows((prev) =>
      prev.map((r) => (r.id === taskId ? { ...r, checked } : r))
    )
  }

  function handleSave() {
    if (selectedId === null) return
    setSaving(true)
    setError(null)

    const toCreate = rows.filter((r) => r.checked && r.statusId === null)
    const toDelete = rows.filter((r) => !r.checked && r.statusId !== null)

    const creates = toCreate.map((r) =>
      fetch('/api/task-statuses', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ trackingId: selectedId, taskId: r.id, status: 'DONE', date: today }),
      })
        .then((res) => {
          if (!res.ok) throw new Error(`Error: ${res.status}`)
          return res.json() as Promise<{ id: number }>
        })
        .then((created) => ({ taskId: r.id, statusId: created.id }))
    )

    const deletes = toDelete.map((r) =>
      fetch(`/api/task-statuses/${r.statusId}`, { method: 'DELETE' }).then((res) => {
        if (!res.ok) throw new Error(`Error: ${res.status}`)
        return { taskId: r.id }
      })
    )

    Promise.all([...creates, ...deletes])
      .then((results) => {
        setRows((prev) =>
          prev.map((r) => {
            const created = (results as { taskId: number; statusId?: number }[]).find(
              (res) => res.taskId === r.id && res.statusId !== undefined
            )
            if (created) return { ...r, statusId: created.statusId! }

            const deleted = results.find((res) => res.taskId === r.id && !('statusId' in res))
            if (deleted) return { ...r, statusId: null }

            return r
          })
        )
      })
      .catch((err) => setError(err.message))
      .finally(() => setSaving(false))
  }

  const hasChanges = rows.some(
    (r) => (r.checked && r.statusId === null) || (!r.checked && r.statusId !== null)
  )

  return (
    <Page>
      <h1>Daily Status</h1>

      <Select
        value={selectedId ?? ''}
        onChange={(e) => setSelectedId(e.target.value ? Number(e.target.value) : null)}
      >
        <option value="">— Выберите tracking —</option>
        {trackings.map((t) => (
          <option key={t.id} value={t.id}>
            {t.title}
          </option>
        ))}
      </Select>

      {error && <p style={{ color: 'red' }}>{error}</p>}

      {loadingTasks && <p>Loading...</p>}

      {!loadingTasks && selectedId !== null && (
        <>
          <Button onClick={handleSave} disabled={saving || !hasChanges}>
            {saving ? 'Сохранение...' : 'Обновить'}
          </Button>

          <Table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Task</th>
                <th>{todayLabel}</th>
              </tr>
            </thead>
            <tbody>
              {rows.length === 0 ? (
                <tr>
                  <td colSpan={3}>Нет задач</td>
                </tr>
              ) : (
                rows.map((row) => (
                  <tr key={row.id}>
                    <td>{row.id}</td>
                    <td>{row.title}</td>
                    <td style={{ textAlign: 'center' }}>
                      <input
                        type="checkbox"
                        checked={row.checked}
                        onChange={(e) => handleCheck(row.id, e.target.checked)}
                      />
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </Table>
        </>
      )}
    </Page>
  )
}
