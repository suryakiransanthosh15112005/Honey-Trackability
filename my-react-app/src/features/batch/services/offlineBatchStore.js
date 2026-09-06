/**
 * offlineBatchStore.js
 * Raw IndexedDB wrapper for offline batch creation drafts.
 *
 * Database: HoneyChainOfflineDB  (version 1)
 * Object Stores:
 *   pendingBatches — keyPath: localId, index on syncStatus
 *   cachedHives    — keyPath: hiveId (minimal data for offline hive selection)
 *
 * SECURITY NOTE:
 *  - JWT / passwords are never stored here.
 *  - Only the minimum batch draft data needed to replay the creation API call
 *    is persisted.
 *  - Photo blobs are stored as-is (binary); base64 encoding is avoided to
 *    keep storage efficient.
 *
 * MAX photo blob size: 5 MB. If exceeded, the photo is discarded and the
 * caller is informed so the user can decide how to proceed.
 */

const DB_NAME = 'HoneyChainOfflineDB'
const DB_VERSION = 1
const STORE_BATCHES = 'pendingBatches'
const STORE_HIVES = 'cachedHives'
const MAX_PHOTO_BYTES = 5 * 1024 * 1024 // 5 MB

/** @type {IDBDatabase|null} */
let db = null

/** Open (or reuse) the IndexedDB connection. */
function openDB() {
  if (db) return Promise.resolve(db)

  return new Promise((resolve, reject) => {
    const req = indexedDB.open(DB_NAME, DB_VERSION)

    req.onupgradeneeded = (event) => {
      const database = event.target.result

      if (!database.objectStoreNames.contains(STORE_BATCHES)) {
        const store = database.createObjectStore(STORE_BATCHES, { keyPath: 'localId' })
        store.createIndex('syncStatus', 'syncStatus', { unique: false })
        store.createIndex('createdAt', 'createdAt', { unique: false })
      }

      if (!database.objectStoreNames.contains(STORE_HIVES)) {
        database.createObjectStore(STORE_HIVES, { keyPath: 'hiveId' })
      }
    }

    req.onsuccess = (event) => {
      db = event.target.result
      resolve(db)
    }

    req.onerror = (event) => {
      reject(new Error('IndexedDB open failed: ' + event.target.error?.message))
    }
  })
}

/** Wrap an IDBRequest in a Promise. */
function idbRequest(request) {
  return new Promise((resolve, reject) => {
    request.onsuccess = () => resolve(request.result)
    request.onerror = () => reject(request.error)
  })
}

// ── Sync Status enum ─────────────────────────────────────────────────────────
export const SyncStatus = Object.freeze({
  PENDING: 'PENDING',
  SYNCING: 'SYNCING',
  SYNCED: 'SYNCED',
  FAILED: 'FAILED',
  AUTH_REQUIRED: 'AUTH_REQUIRED',
})

// ── Pending Batch CRUD ────────────────────────────────────────────────────────

/**
 * Save a new pending batch draft.
 *
 * @param {object} draft - Batch fields from the form.
 * @param {string} draft.harvestDate
 * @param {number} draft.hiveId
 * @param {string} draft.hiveCode
 * @param {string} draft.clusterName
 * @param {string|number} draft.quantityKg
 * @param {Blob|null} draft.photoBlob
 * @returns {Promise<string>} localId
 */
export async function savePendingBatch(draft) {
  const database = await openDB()

  // Generate a LOCAL- prefixed id using a UUID-style random hex string
  const localId =
    'LOCAL-' +
    Array.from(crypto.getRandomValues(new Uint8Array(12)))
      .map((b) => b.toString(16).padStart(2, '0'))
      .join('')
      .toUpperCase()

  // Enforce photo size limit
  let photoBlob = draft.photoBlob || null
  if (photoBlob && photoBlob.size > MAX_PHOTO_BYTES) {
    throw new Error(
      `Photo size (${(photoBlob.size / 1024 / 1024).toFixed(1)} MB) exceeds the 5 MB offline limit. ` +
        'Remove the photo or reconnect to continue.'
    )
  }

  const record = {
    localId,
    harvestDate: draft.harvestDate,
    hiveId: draft.hiveId,
    hiveCode: draft.hiveCode || null,
    clusterName: draft.clusterName || null,
    quantityKg: String(draft.quantityKg),
    photoBlob,
    syncStatus: SyncStatus.PENDING,
    retryCount: 0,
    lastError: null,
    officialBatchId: null,
    createdAt: new Date().toISOString(),
  }

  const tx = database.transaction(STORE_BATCHES, 'readwrite')
  await idbRequest(tx.objectStore(STORE_BATCHES).add(record))

  console.info(`[OfflineBatchStore] Saved draft ${localId}`)
  return localId
}

/**
 * Return all pending (PENDING or FAILED or AUTH_REQUIRED) drafts in creation order.
 * @returns {Promise<object[]>}
 */
export async function getPendingBatches() {
  const database = await openDB()
  const tx = database.transaction(STORE_BATCHES, 'readonly')
  const all = await idbRequest(tx.objectStore(STORE_BATCHES).index('createdAt').getAll())
  return all.filter((b) =>
    [SyncStatus.PENDING, SyncStatus.FAILED, SyncStatus.AUTH_REQUIRED].includes(b.syncStatus)
  )
}

/**
 * Return all local drafts regardless of status (for BatchListPage display).
 * @returns {Promise<object[]>}
 */
export async function getAllLocalBatches() {
  const database = await openDB()
  const tx = database.transaction(STORE_BATCHES, 'readonly')
  return idbRequest(tx.objectStore(STORE_BATCHES).getAll())
}

/**
 * Get number of batches in PENDING or SYNCING state.
 * @returns {Promise<number>}
 */
export async function getPendingCount() {
  const database = await openDB()
  const tx = database.transaction(STORE_BATCHES, 'readonly')
  const all = await idbRequest(tx.objectStore(STORE_BATCHES).getAll())
  return all.filter((b) =>
    [SyncStatus.PENDING, SyncStatus.SYNCING].includes(b.syncStatus)
  ).length
}

/**
 * Update fields of a local draft by localId.
 * @param {string} localId
 * @param {Partial<object>} updates
 */
export async function updateBatchRecord(localId, updates) {
  const database = await openDB()
  const tx = database.transaction(STORE_BATCHES, 'readwrite')
  const store = tx.objectStore(STORE_BATCHES)
  const existing = await idbRequest(store.get(localId))
  if (!existing) return
  const updated = { ...existing, ...updates }
  await idbRequest(store.put(updated))
}

/**
 * Permanently remove a local draft (call after SYNCED and UI has updated).
 * @param {string} localId
 */
export async function deleteBatch(localId) {
  const database = await openDB()
  const tx = database.transaction(STORE_BATCHES, 'readwrite')
  await idbRequest(tx.objectStore(STORE_BATCHES).delete(localId))
  console.info(`[OfflineBatchStore] Deleted draft ${localId}`)
}

// ── Hive Cache (for offline hive selector) ────────────────────────────────────

/**
 * Cache minimal hive data for offline hive selection.
 * Only stores: hiveId, hiveCode, clusterName, status.
 * @param {object[]} hives
 */
export async function cacheHives(hives) {
  const database = await openDB()
  const tx = database.transaction(STORE_HIVES, 'readwrite')
  const store = tx.objectStore(STORE_HIVES)
  for (const hive of hives) {
    await idbRequest(
      store.put({
        hiveId: hive.id,
        hiveCode: hive.hiveCode,
        clusterName: hive.clusterName,
        status: hive.status,
      })
    )
  }
}

/**
 * Return all cached hives.
 * @returns {Promise<object[]>}
 */
export async function getCachedHives() {
  const database = await openDB()
  const tx = database.transaction(STORE_HIVES, 'readonly')
  return idbRequest(tx.objectStore(STORE_HIVES).getAll())
}
