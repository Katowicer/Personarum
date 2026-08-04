import { computed, ref } from 'vue'
import { defineStore } from 'pinia'

import { ApiError } from '@/api/http'
import {
  currentUser,
  login as loginRequest,
  type CurrentUser,
  type LoginCredentials,
} from '@/services/authService'

export const useAuthStore = defineStore('auth', () => {
  const user = ref<CurrentUser | null>(null)
  const loading = ref(false)
  const error = ref<string | null>(null)
  const sessionChecked = ref(false)

  const isAuthenticated = computed(() => user.value !== null)

  async function login(credentials: LoginCredentials): Promise<boolean> {
    loading.value = true
    error.value = null

    try {
      user.value = await loginRequest(credentials)
      sessionChecked.value = true

      return true
    } catch (cause) {
      user.value = null
      error.value = getErrorMessage(cause)

      return false
    } finally {
      loading.value = false
    }
  }

  async function restoreSession(): Promise<void> {
    if (sessionChecked.value) {
      return
    }

    loading.value = true
    error.value = null

    try {
      user.value = await currentUser()
    } catch (cause) {
      user.value = null

      if (!(cause instanceof ApiError && cause.status === 401)) {
        error.value = getErrorMessage(cause)
      }
    } finally {
      sessionChecked.value = true
      loading.value = false
    }
  }

  function getErrorMessage(cause: unknown): string {
    if (cause instanceof Error) {
      return cause.message
    }

    // TODO: migliorare error handling, forse separare su componente dedicata
    return 'Errore'
  }

  return {
    user,
    loading,
    error,
    sessionChecked,
    isAuthenticated,
    login,
    restoreSession,
  }
})
