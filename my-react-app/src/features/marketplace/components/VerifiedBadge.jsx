import React from 'react'

/**
 * VerifiedBadge — indicates blockchain verification status.
 * Props:
 *   verified: boolean
 *   batchId: string
 */
const VerifiedBadge = ({ verified, batchId }) => {
  if (!verified) {
    return (
      <span className="badge badge--warning" title="Blockchain verification pending">
        <span className="badge__dot"></span>
        Unverified
      </span>
    )
  }

  return (
    <span
      className="badge badge--success"
      title={`Cryptographically verified on Blockchain (Batch: ${batchId || 'Verified'})`}
    >
      <span className="badge__dot badge__dot--pulse"></span>
      ✅ Verified Honey
    </span>
  )
}

export default VerifiedBadge
