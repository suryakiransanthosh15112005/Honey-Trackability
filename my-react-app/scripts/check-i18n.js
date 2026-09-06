import fs from 'fs'
import path from 'path'
import { fileURLToPath } from 'url'

const __filename = fileURLToPath(import.meta.url)
const __dirname = path.dirname(__filename)

const localesDir = path.join(__dirname, '../src/i18n/locales')

function loadJson(filePath) {
  const content = fs.readFileSync(filePath, 'utf8')
  return JSON.parse(content)
}

function getNestedKeys(obj, prefix = '') {
  let keys = []
  for (const k in obj) {
    if (Object.prototype.hasOwnProperty.call(obj, k)) {
      const fullKey = prefix ? `${prefix}.${k}` : k
      if (typeof obj[k] === 'object' && obj[k] !== null && !Array.isArray(obj[k])) {
        keys = keys.concat(getNestedKeys(obj[k], fullKey))
      } else {
        keys.push(fullKey)
      }
    }
  }
  return keys
}

function checkCompleteness() {
  console.log('🔍 Checking i18n Translation Completeness across en, hi, ta...\n')

  const enPath = path.join(localesDir, 'en/translation.json')
  const hiPath = path.join(localesDir, 'hi/translation.json')
  const taPath = path.join(localesDir, 'ta/translation.json')

  const enObj = loadJson(enPath)
  const hiObj = loadJson(hiPath)
  const taObj = loadJson(taPath)

  const enKeys = getNestedKeys(enObj)
  const hiKeys = new Set(getNestedKeys(hiObj))
  const taKeys = new Set(getNestedKeys(taObj))

  let missingInHi = []
  let missingInTa = []

  enKeys.forEach((key) => {
    if (!hiKeys.has(key)) missingInHi.push(key)
    if (!taKeys.has(key)) missingInTa.push(key)
  })

  console.log(`📊 Total English Keys: ${enKeys.length}`)
  console.log(`✓ English: 100% keys (${enKeys.length}/${enKeys.length})`)

  if (missingInHi.length === 0) {
    console.log(`✓ Hindi: 100% keys (${hiKeys.size}/${enKeys.length})`)
  } else {
    console.error(`❌ Hindi missing ${missingInHi.length} keys:`, missingInHi)
  }

  if (missingInTa.length === 0) {
    console.log(`✓ Tamil: 100% keys (${taKeys.size}/${enKeys.length})`)
  } else {
    console.error(`❌ Tamil missing ${missingInTa.length} keys:`, missingInTa)
  }

  if (missingInHi.length > 0 || missingInTa.length > 0) {
    process.exit(1)
  } else {
    console.log('\n🎉 SUCCESS: All 3 translation dictionaries are 100% complete and synchronized!')
  }
}

checkCompleteness()
