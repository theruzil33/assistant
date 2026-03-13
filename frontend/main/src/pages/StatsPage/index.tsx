import { useEffect, useState } from 'react'
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from 'recharts'
import { Page, Input, Button } from '../../components/ui'
import styled from 'styled-components'

interface Tracking {
  id: number
  title: string
}

interface Task {
  id: number
  title: string
}

interface TaskStatus {
  id: number
  taskId: number
  date: string
}

interface ChartPoint {
  date: string
  [taskTitle: string]: string | number
}

const COLORS = [
  '#4e79a7', '#f28e2b', '#e15759', '#76b7b2',
  '#59a14f', '#edc948', '#b07aa1', '#ff9da7',
  '#9c755f', '#bab0ac',
]

const Controls = styled.div`
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
`

const Select = styled.select`
  padding: 6px 10px;
  font-size: 14px;
`

const Label = styled.label`
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
`

function formatDate(date: Date) {
  return date.toISOString().slice(0, 10)
}

function getDatesInRange(from: string, to: string): string[] {
  const dates: string[] = []
  const cur = new Date(from)
  const end = new Date(to)
  while (cur <= end) {
    dates.push(formatDate(cur))
    cur.setDate(cur.getDate() + 1)
  }
  return dates
}

export default function StatsPage() {
  const today = formatDate(new Date())
  const defaultFrom = formatDate(new Date(Date.now() - 182 * 24 * 60 * 60 * 1000))

  const [trackings, setTrackings] = useState<Tracking[]>([])
  const [selectedId, setSelectedId] = useState<number | null>(null)
  const [fromDate, setFromDate] = useState(defaultFrom)
  const [toDate, setToDate] = useState(today)
  const [chartData, setChartData] = useState<ChartPoint[]>([])
  const [tasks, setTasks] = useState<Task[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    fetch('/life-tracking/api/trackings')
      .then((res) => {
        if (!res.ok) throw new Error(`Error: ${res.status}`)
        return res.json() as Promise<Tracking[]>
      })
      .then(setTrackings)
      .catch((err) => setError(err.message))
  }, [])

  function loadStats() {
    if (selectedId === null) return
    setLoading(true)
    setError(null)

    fetch(`/life-tracking/api/trackings/${selectedId}/tasks`)
      .then((res) => {
        if (!res.ok) throw new Error(`Error: ${res.status}`)
        return res.json() as Promise<Task[]>
      })
      .then((loadedTasks) => {
        setTasks(loadedTasks)
        return Promise.all(
          loadedTasks.map((task) =>
            fetch(`/life-tracking/api/task-statuses?trackingId=${selectedId}&taskId=${task.id}`)
              .then((res) => {
                if (!res.ok) throw new Error(`Error: ${res.status}`)
                return res.json() as Promise<TaskStatus[]>
              })
              .then((statuses) => ({ task, statuses }))
          )
        )
      })
      .then((results) => {
        const dates = getDatesInRange(fromDate, toDate)

        const data: ChartPoint[] = dates.map((date) => {
          const point: ChartPoint = { date: date.slice(5) }
          for (const { task, statuses } of results) {
            point[task.title] = statuses.some((s) => s.date === date) ? 1 : 0
          }
          return point
        })

        setChartData(data)
      })
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false))
  }

  return (
    <Page>
      <h1>Статистика</h1>

      <Controls>
        <Label>
          С
          <Input
            type="date"
            value={fromDate}
            onChange={(e) => setFromDate(e.target.value)}
          />
        </Label>
        <Label>
          По
          <Input
            type="date"
            value={toDate}
            onChange={(e) => setToDate(e.target.value)}
          />
        </Label>
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
        <Button onClick={loadStats} disabled={selectedId === null || loading}>
          {loading ? 'Загрузка...' : 'Показать'}
        </Button>
      </Controls>

      {error && <p style={{ color: 'red' }}>{error}</p>}

      {chartData.length > 0 && (
        <ResponsiveContainer width="100%" height={400}>
          <LineChart data={chartData} margin={{ top: 8, right: 16, left: 0, bottom: 60 }}>
            <XAxis
              dataKey="date"
              angle={-60}
              textAnchor="end"
              interval={Math.floor(chartData.length / 20)}
              tick={{ fontSize: 11 }}
            />
            <YAxis hide />
            <Tooltip
              formatter={(value, name) => [value === 1 ? 'Выполнено' : 'Не выполнено', name]}
            />
            <Legend verticalAlign="top" />
            {tasks.map((task, i) => (
              <Line
                key={task.id}
                dataKey={task.title}
                stroke={COLORS[i % COLORS.length]}
                strokeWidth={2}
                dot={{ r: 3 }}
                activeDot={{ r: 5 }}
                isAnimationActive={false}
              />
            ))}
          </LineChart>
        </ResponsiveContainer>
      )}
    </Page>
  )
}
