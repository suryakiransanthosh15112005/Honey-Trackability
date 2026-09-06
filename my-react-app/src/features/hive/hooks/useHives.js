import { useSelector, useDispatch } from 'react-redux'
import { useEffect } from 'react'
import {
  fetchHives,
  fetchHive,
  createHive,
  updateHive,
  updateHiveStatus,
  clearHiveError,
  clearSelectedHive,
} from '../hiveSlice'

export const useHives = (autoFetch = false) => {
  const hive = useSelector((state) => state.hive)
  const dispatch = useDispatch()

  useEffect(() => {
    if (autoFetch && hive.hives.length === 0 && !hive.loading) {
      dispatch(fetchHives())
    }
  }, [autoFetch, dispatch, hive.hives.length, hive.loading])

  return {
    ...hive,
    fetchHives: () => dispatch(fetchHives()),
    fetchHive: (id) => dispatch(fetchHive(id)),
    createHive: (data) => dispatch(createHive(data)),
    updateHive: (id, data) => dispatch(updateHive({ id, data })),
    updateHiveStatus: (id, status) => dispatch(updateHiveStatus({ id, status })),
    clearError: () => dispatch(clearHiveError()),
    clearSelectedHive: () => dispatch(clearSelectedHive()),
  }
}

export default useHives
