<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import {
  changeUserPassword,
  createUser,
  findUserById,
  updateUser,
} from '@/services/adminUserService'
import type { Role } from '@/services/authService'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const saving = ref(false)
const error = ref<string | null>(null)

const form = reactive({
  username: '',
  password: '',
  role: 'OPERATOR' as Role,
  enabled: true,
})

const roles: Role[] = ['ADMIN', 'OPERATOR']

const editing = computed(() => {
  return route.name === 'admin-user-edit'
})

const userId = computed<number | null>(() => {
  if (!editing.value) {
    return null
  }

  const value = Number(route.params.userId)

  if (!Number.isInteger(value) || value <= 0) {
    return null
  }

  return value
})

async function loadUser(): Promise<void> {
  if (!editing.value) {
    return
  }

  if (userId.value === null) {
    error.value = 'Identificativo utente non valido'
    return
  }

  loading.value = true
  error.value = null

  try {
    const user = await findUserById(userId.value)

    form.username = user.username
    form.role = user.role
    form.enabled = user.enabled
  } catch (cause) {
    error.value = getErrorMessage(cause)
  } finally {
    loading.value = false
  }
}

async function saveUser(): Promise<void> {
  error.value = null

  if (!editing.value) {
    if (form.username.trim() === '') {
      error.value = 'Username obbligatorio'
      return
    }

    if (form.password.length < 8) {
      error.value = 'La password deve contenere almeno 8 caratteri'
      return
    }
  }

  saving.value = true

  try {
    if (editing.value) {
      if (userId.value === null) {
        error.value = 'Identificativo utente non valido'
        return
      }

      await updateUser(userId.value, {
        role: form.role,
        enabled: form.enabled,
      })

      if (form.password !== '') {
        if (form.password.length < 8) {
          error.value = 'La nuova password deve contenere almeno 8 caratteri'
          return
        }

        await changeUserPassword(userId.value, form.password)
      }
    } else {
      await createUser({
        username: form.username.trim(),
        password: form.password,
        role: form.role,
      })
    }

    await router.push({
      name: 'admin-users',
    })
  } catch (cause) {
    error.value = getErrorMessage(cause)
  } finally {
    saving.value = false
  }
}

function getErrorMessage(cause: unknown): string {
  if (cause instanceof Error) {
    return cause.message
  }

  return 'Impossibile salvare l’utente'
}

onMounted(loadUser)
</script>

<template>
  <v-container>
    <v-btn variant="text" prepend-icon="mdi-arrow-left" :to="{ name: 'admin-users' }" class="mb-4">
      Utenti
    </v-btn>

    <h1 class="text-h4 mb-6">
      {{ editing ? 'Modifica utente' : 'Nuovo utente' }}
    </h1>

    <v-progress-linear v-if="loading" indeterminate class="mb-4" />

    <v-alert v-if="error" type="error" variant="tonal" class="mb-4">
      {{ error }}
    </v-alert>

    <v-card v-if="!loading">
      <v-card-text>
        <form @submit.prevent="saveUser">
          <v-text-field
            v-model="form.username"
            label="Username"
            maxlength="80"
            :disabled="editing || saving"
            :required="!editing"
          />

          <v-select v-model="form.role" label="Ruolo" :items="roles" :disabled="saving" />

          <v-switch
            v-if="editing"
            v-model="form.enabled"
            label="Utente abilitato"
            color="primary"
            :disabled="saving"
          />

          <v-text-field
            v-model="form.password"
            type="password"
            :label="editing ? 'Nuova password' : 'Password'"
            :hint="editing ? 'Lascia vuoto per non modificarla' : 'Minimo 8 caratteri'"
            persistent-hint
            autocomplete="new-password"
            :required="!editing"
            :disabled="saving"
          />

          <div class="d-flex justify-end mt-6">
            <v-btn variant="text" :to="{ name: 'admin-users' }" :disabled="saving" class="mr-2">
              Annulla
            </v-btn>

            <v-btn type="submit" color="primary" :loading="saving"> Salva </v-btn>
          </div>
        </form>
      </v-card-text>
    </v-card>
  </v-container>
</template>
