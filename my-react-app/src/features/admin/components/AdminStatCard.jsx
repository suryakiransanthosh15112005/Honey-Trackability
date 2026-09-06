import React from 'react'

export const AdminStatCard = ({ icon, label, value, subtext, color = 'gold' }) => {
  return (
    <div className={`kpi-card kpi-card--${color}`}>
      <div className="kpi-card__icon">{icon}</div>
      <div className="kpi-card__content">
        <p className="kpi-card__label">{label}</p>
        <h3 className="kpi-card__value">{value}</h3>
        {subtext && <p className="kpi-card__subtext text-secondary text-xs mt-1">{subtext}</p>}
      </div>
    </div>
  )
}

export default AdminStatCard
