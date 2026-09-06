import React from 'react'
import { Link } from 'react-router-dom'
import Card from '../../../components/ui/Card'
import Button from '../../../components/ui/Button'
import HealthStatusBadge from './HealthStatusBadge'
import VoiceButton from '../../../components/common/VoiceButton'
import { useLanguage } from '../../../i18n/LanguageContext'

export const HiveHealthCard = ({ health }) => {
  const { t } = useLanguage()

  if (!health) return null

  const { hiveId, hiveCode, clusterName, status, message, checkedAt } = health

  const getBorderTheme = () => {
    if (status === 'ALERT') return 'border-blue-300 bg-blue-50/40 text-blue-950 shadow-sm'
    if (status === 'WATCH') return 'border-amber-300 bg-amber-50/40 text-amber-950 shadow-sm'
    return 'border-slate-200 bg-white text-slate-900 shadow-sm'
  }

  // Voice text based on hive status
  const getVoiceAlertText = () => {
    let alertMsg = ''
    if (status === 'HEALTHY') alertMsg = t('iot.voiceAlertHealthy', 'Hive is healthy. Temperature and colony activity are normal.')
    else if (status === 'WATCH') alertMsg = t('iot.voiceAlertWatch', 'Hive requires monitoring. Environmental indicators require attention.')
    else if (status === 'ALERT') alertMsg = t('iot.voiceAlertAlert', 'Hive alert triggered! Immediate apiary inspection recommended.')
    return `${hiveCode || `Hive #${hiveId}`}. ${alertMsg} ${message || ''}`
  }

  return (
    <Card className={`p-6 space-y-4 border ${getBorderTheme()} transition-all hover:scale-[1.01]`}>
      {/* Header */}
      <div className="flex items-start justify-between gap-2">
        <div className="flex items-center gap-3">
          <div className="w-12 h-12 rounded-xl bg-amber-100 border border-amber-200 flex items-center justify-center text-2xl">
            🐝
          </div>
          <div>
            <h3 className="text-slate-900 font-bold text-lg font-['Outfit'] flex items-center gap-2">
              <span>{hiveCode || `Hive #${hiveId}`}</span>
            </h3>
            <p className="text-xs text-slate-500">
              {clusterName ? `Apiary: ${clusterName}` : 'Registered Hive'}
            </p>
          </div>
        </div>
        <div className="flex items-center gap-2">
          <HealthStatusBadge status={status} size="sm" />
          <VoiceButton text={getVoiceAlertText()} size="xs" />
        </div>
      </div>

      {/* Primary Plain-Language UX Message */}
      <div className="p-3.5 rounded-xl bg-slate-50 border border-slate-200 space-y-1">
        <p className="text-xs text-slate-500 font-medium uppercase tracking-wider">Health Assessment</p>
        <p className="text-sm font-medium text-slate-800 leading-relaxed">
          {message}
        </p>
      </div>

      {/* Footer Info & Action */}
      <div className="flex items-center justify-between pt-1 text-xs text-slate-500">
        <span className="font-mono text-[11px]">
          {checkedAt
            ? `Updated ${new Date(checkedAt).toLocaleTimeString('en-IN', { hour: '2-digit', minute: '2-digit' })}`
            : 'Live monitoring'}
        </span>
        <Link to={`/hives/${hiveId}/health`}>
          <Button variant="secondary" size="sm" className="text-xs">
            {t('common.viewDetails', 'View Sensor Details')} →
          </Button>
        </Link>
      </div>
    </Card>
  )
}

export default HiveHealthCard
