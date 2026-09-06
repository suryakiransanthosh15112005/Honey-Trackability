import React from 'react'
import { Link } from 'react-router-dom'
import HiveHealthCard from './HiveHealthCard'
import EmptyState from '../../../components/ui/EmptyState'
import Button from '../../../components/ui/Button'

export const HiveHealthGrid = ({ hivesHealth }) => {
  if (!hivesHealth || hivesHealth.length === 0) {
    return (
      <EmptyState
        icon="🐝"
        title="No Active Hives Found"
        description="Register your apiary hive to activate real-time IoT telemetry monitoring, sensor health tracking, and automated AI yield predictions."
        action={
          <Link to="/beekeeper/hives">
            <Button variant="primary" size="sm">
              + Register Your First Hive
            </Button>
          </Link>
        }
      />
    )
  }

  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-5">
      {hivesHealth.map((health) => (
        <HiveHealthCard key={health.hiveId} health={health} />
      ))}
    </div>
  )
}

export default HiveHealthGrid
