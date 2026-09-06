import { useEffect, useCallback } from 'react'
import { useSelector, useDispatch } from 'react-redux'
import {
  fetchBeekeeperProfile,
  fetchProfileStatus,
  createProfile,
  updateProfile,
  clearBeekeeperError,
} from '../beekeeperSlice'

export const useBeekeeperProfile = (autoFetch = false) => {
  const beekeeper = useSelector((state) => state.beekeeper)
  const dispatch = useDispatch()

  useEffect(() => {
    if (autoFetch && !beekeeper.fetched && !beekeeper.loading) {
      dispatch(fetchBeekeeperProfile())
      dispatch(fetchProfileStatus())
    }
  }, [autoFetch, dispatch, beekeeper.fetched, beekeeper.loading])

  const doFetchProfile = useCallback(() => dispatch(fetchBeekeeperProfile()), [dispatch])
  const doFetchStatus = useCallback(() => dispatch(fetchProfileStatus()), [dispatch])
  const doCreateProfile = useCallback((data) => dispatch(createProfile(data)), [dispatch])
  const doUpdateProfile = useCallback((data) => dispatch(updateProfile(data)), [dispatch])
  const doClearError = useCallback(() => dispatch(clearBeekeeperError()), [dispatch])

  return {
    ...beekeeper,
    fetchProfile: doFetchProfile,
    fetchStatus: doFetchStatus,
    createProfile: doCreateProfile,
    updateProfile: doUpdateProfile,
    clearError: doClearError,
  }
}

export default useBeekeeperProfile
