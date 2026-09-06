import fs from 'fs'
import path from 'path'
import { fileURLToPath } from 'url'

const __filename = fileURLToPath(import.meta.url)
const __dirname = path.dirname(__filename)
const srcDir = path.join(__dirname, '../src')

// Approved Hex Colors (Case-insensitive)
const APPROVED_HEX = new Set([
  '#2563EB', '#1D4ED8', '#3B82F6', '#DBEAFE', '#EFF6FF',
  '#F59E0B', '#D97706', '#FEF3C7', '#FFFBEB',
  '#F8FAFC', '#FFFFFF', '#1E293B', '#64748B', '#E2E8F0', '#94A3B8',
  '#000000', '#000', '#FFF', '#fff',
  // Approved Status Feedback Hexes (Success Green & Danger Red)
  '#16A34A', '#15803D', '#86EFAC', '#DCFCE7', '#F0FDF4',
  '#DC2626', '#FEE2E2', '#FCA5A5', '#FEF2F2'
].map(c => c.toUpperCase()))

// Unauthorized Tailwind color patterns to flag
const DISALLOWED_TAILWIND_PATTERNS = [
  /text-(red|green|emerald|teal|cyan|purple|pink|violet|indigo|rose|lime|yellow)-[0-9]+/i,
  /bg-(red|green|emerald|teal|cyan|purple|pink|violet|indigo|rose|lime|yellow)-[0-9]+/i,
  /border-(red|green|emerald|teal|cyan|purple|pink|violet|indigo|rose|lime|yellow)-[0-9]+/i,
  /from-(red|green|emerald|teal|cyan|purple|pink|violet|indigo|rose|lime|yellow)-[0-9]+/i,
  /to-(red|green|emerald|teal|cyan|purple|pink|violet|indigo|rose|lime|yellow)-[0-9]+/i,
]

function getAllFiles(dir, fileList = []) {
  const files = fs.readdirSync(dir)
  files.forEach((file) => {
    const filePath = path.join(dir, file)
    if (fs.statSync(filePath).isDirectory()) {
      getAllFiles(filePath, fileList)
    } else if (/\.(js|jsx|css)$/.test(file)) {
      fileList.push(filePath)
    }
  })
  return fileList
}

console.log('🎨 Running HoneyChain Blue + Honey Color Audit on src/...\n')

const files = getAllFiles(srcDir)
let errorCount = 0

files.forEach((file) => {
  const relPath = path.relative(srcDir, file)
  const content = fs.readFileSync(file, 'utf-8')
  const lines = content.split('\n')

  lines.forEach((line, idx) => {
    // 1. Hex Color Regex
    const hexMatches = line.match(/#[0-9A-Fa-f]{3,8}\b/g)
    if (hexMatches) {
      hexMatches.forEach((hex) => {
        const upperHex = hex.toUpperCase()
        if (!APPROVED_HEX.has(upperHex)) {
          console.error(`❌ UNAUTHORIZED HEX [${relPath}:${idx + 1}]: ${hex} on line: "${line.trim()}"`)
          errorCount++
        }
      })
    }

    // 2. Disallowed Tailwind Class Regex
    DISALLOWED_TAILWIND_PATTERNS.forEach((pattern) => {
      if (pattern.test(line)) {
        const match = line.match(pattern)[0]
        console.error(`❌ DISALLOWED TAILWIND CLASS [${relPath}:${idx + 1}]: ${match} on line: "${line.trim()}"`)
        errorCount++
      }
    })
  })
})

console.log('\n----------------------------------------')
if (errorCount === 0) {
  console.log('🎉 SUCCESS: 0 unauthorized colors found! 100% compliant with Blue + Honey design system.')
  process.exit(0)
} else {
  console.error(`🚨 FAIL: Found ${errorCount} unauthorized color usage(s).`)
  process.exit(1)
}
