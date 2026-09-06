import React from 'react'
import Card from '../../../components/ui/Card'
import Button from '../../../components/ui/Button'
import PredictionConfidence from './PredictionConfidence'
import PredictionExplanation from './PredictionExplanation'
import VoiceButton from '../../../components/common/VoiceButton'
import { useLanguage } from '../../../i18n/LanguageContext'

export const YieldPredictionCard = ({
  prediction,
  loading = false,
  refreshing = false,
  onRefresh,
}) => {
  const { t } = useLanguage()

  if (loading) {
    return (
      <Card className="p-5">
        <div className="flex items-center gap-3 text-secondary text-sm">
          <div className="spinner spinner--sm" />
          <span>{t('common.loading', 'Generating AI yield prediction...')}</span>
        </div>
      </Card>
    )
  }

  if (!prediction) return null

  const isAlert = prediction.healthStatus === 'ALERT'

  const getVoicePredictionText = () => {
    const expectedLabel = t('yield.expectedHarvest', 'Expected Harvest Date')
    const yieldLabel = t('yield.estimatedYield', 'Estimated Yield')
    return `${expectedLabel}: ${prediction.daysUntilHarvest} days (${prediction.predictedHarvestDate}). ${yieldLabel}: ${prediction.minimumKg} to ${prediction.maximumKg} kg. ${prediction.explanation || ''}`
  }

  return (
    <Card className={`p-5 ${isAlert ? 'card--border-red' : 'card--border-gold'}`}>
      <div className="flex justify-between items-start mb-4">
        <div>
          <div className="flex items-center gap-2">
            <span className="text-xl">🤖</span>
            <h3 className="font-brand font-bold text-lg text-primary">
              {t('nav.yieldPrediction', 'AI Yield Prediction')}
            </h3>
          </div>
          <p className="text-xs text-muted mt-1">
            Hive {prediction.hiveCode} • Generated {new Date(prediction.generatedAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
          </p>
        </div>

        <div className="flex items-center gap-2">
          <VoiceButton text={getVoicePredictionText()} size="xs" />
          {onRefresh && (
            <Button
              variant="ghost"
              size="xs"
              onClick={onRefresh}
              loading={refreshing}
              className="text-xs"
            >
              🔄 Refresh
            </Button>
          )}
        </div>
      </div>

      {/* ALERT Banner if hive is in ALERT status */}
      {isAlert && (
        <div className="alert alert--error mb-4">
          🔴 <strong>Hive Alert:</strong> Your hive needs attention. Yield prediction is less reliable until hive conditions improve.
        </div>
      )}

      {/* Core Prediction Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 mb-4 bg-slate-900/50 p-3.5 rounded-xl border border-amber-500/15">
        <div>
          <p className="text-xs text-muted font-medium">{t('yield.expectedHarvest', 'Expected Harvest')}</p>
          <p className="font-brand font-bold text-lg text-primary">
            ~{prediction.daysUntilHarvest} days
          </p>
          <p className="text-xs text-secondary">
            {new Date(prediction.predictedHarvestDate).toLocaleDateString('en-IN', {
              day: '2-digit',
              month: 'short',
              year: 'numeric',
            })}
          </p>
        </div>

        <div>
          <p className="text-xs text-muted font-medium">{t('yield.estimatedYield', 'Estimated Yield')}</p>
          <p className="font-brand font-bold text-lg text-gold">
            {prediction.minimumKg} – {prediction.maximumKg} kg
          </p>
          <p className="text-xs text-secondary">Predicted harvest range</p>
        </div>
      </div>

      {/* Confidence Indicator */}
      <PredictionConfidence confidence={prediction.confidence} />

      {/* Collapsible Explanation */}
      <PredictionExplanation
        explanation={prediction.explanation}
        details={prediction.explanationDetails}
      />
    </Card>
  )
}

export default YieldPredictionCard
