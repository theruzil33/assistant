import { useState } from 'react'
import { Page } from './TrackingPage.styles'
import TrackingList from './TrackingList'
import TrackingTasksMapping from './TrackingTasksMapping'

export default function TrackingPage() {
  const [selectedTrackingId, setSelectedTrackingId] = useState<number | null>(null)

  return (
    <Page>
      <h1>Tracking</h1>
      <TrackingList selectedTrackingId={selectedTrackingId} onSelect={setSelectedTrackingId} />
      {selectedTrackingId !== null && (
        <TrackingTasksMapping trackingId={selectedTrackingId} />
      )}
    </Page>
  )
}
