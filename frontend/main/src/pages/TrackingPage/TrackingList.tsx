import { useEffect, useState } from 'react'
import {
  Actions,
  Button,
  ClickableId,
  Form,
  Input,
  Table,
  TableWrapper,
} from './TrackingPage.styles'

interface Tracking {
  id: number
  title: string
  startDate: string | null
  endDate: string | null
  createdAt: string
}

interface Props {
  selectedTrackingId: number | null
  onSelect: (id: number | null) => void
}

export default function TrackingList({ selectedTrackingId, onSelect }: Props) {
  const [trackings, setTrackings] = useState<Tracking[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [title, setTitle] = useState('')
  const [startDate, setStartDate] = useState('')
  const [endDate, setEndDate] = useState('')
  const [submitting, setSubmitting] = useState(false)
  const [selected, setSelected] = useState<Set<number>>(new Set())
  const [deleting, setDeleting] = useState(false)

  useEffect(() => {
    fetch('/api/trackings')
      .then((res) => {
        if (!res.ok) throw new Error(`Error: ${res.status}`)
        return res.json()
      })
      .then(setTrackings)
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false))
  }, [])

  function handleSubmit(e: React.SyntheticEvent<HTMLFormElement>) {
    e.preventDefault()
    setSubmitting(true)
    fetch('/api/trackings', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        title,
        startDate: startDate || null,
        endDate: endDate || null,
      }),
    })
      .then((res) => {
        if (!res.ok) throw new Error(`Error: ${res.status}`)
        return res.json()
      })
      .then((created: Tracking) => {
        setTrackings((prev) => [...prev, created])
        setTitle('')
        setStartDate('')
        setEndDate('')
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
      fetch(`/api/trackings/${id}`, { method: 'DELETE' }).then((res) => {
        if (!res.ok) throw new Error(`Error: ${res.status}`)
        return id
      })
    )
    Promise.all(deletes)
      .then((ids) => {
        setTrackings((prev) => prev.filter((t) => !ids.includes(t.id)))
        setSelected(new Set())
        if (selectedTrackingId !== null && ids.includes(selectedTrackingId)) {
          onSelect(null)
        }
      })
      .catch((err) => setError(err.message))
      .finally(() => setDeleting(false))
  }

  function formatDate(date: string | null) {
    if (!date) return '—'
    return new Date(date).toLocaleDateString()
  }

  function handleClickId(id: number) {
    onSelect(selectedTrackingId === id ? null : id)
  }

  if (loading) return <p>Loading...</p>
  if (error) return <p>Error: {error}</p>

  return (
    <>
      <Form onSubmit={handleSubmit}>
        <Input
          type="text"
          placeholder="Title"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          required
        />
        <Input
          type="date"
          value={startDate}
          onChange={(e) => setStartDate(e.target.value)}
        />
        <Input
          type="date"
          value={endDate}
          onChange={(e) => setEndDate(e.target.value)}
        />
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
              <th>Start Date</th>
              <th>End Date</th>
              <th>Created At</th>
            </tr>
          </thead>
          <tbody>
            {trackings.map((tracking) => (
              <tr key={tracking.id}>
                <td>
                  <input
                    type="checkbox"
                    checked={selected.has(tracking.id)}
                    onChange={() => toggleSelect(tracking.id)}
                  />
                </td>
                <td>
                  <ClickableId
                    onClick={() => handleClickId(tracking.id)}
                    style={selectedTrackingId === tracking.id ? { fontWeight: 'bold' } : undefined}
                  >
                    {tracking.id}
                  </ClickableId>
                </td>
                <td>{tracking.title}</td>
                <td>{formatDate(tracking.startDate)}</td>
                <td>{formatDate(tracking.endDate)}</td>
                <td>{new Date(tracking.createdAt).toLocaleString()}</td>
              </tr>
            ))}
          </tbody>
        </Table>

        <Actions>
          <Button onClick={handleDelete} disabled={selected.size === 0 || deleting}>
            {deleting ? 'Deleting...' : 'Удалить'}
          </Button>
        </Actions>
      </TableWrapper>
    </>
  )
}
