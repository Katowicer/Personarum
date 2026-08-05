<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import {
  createProfile,
  findProfileById,
  updateProfile,
  type ProfilePayload,
} from '@/services/profileService'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const saving = ref(false)
const error = ref<string | null>(null)

const form = reactive({
  firstName: '',
  lastName: '',
  birthDate: '',
  birthPlace: '',
  fiscalCode: '',
  email: '',
  phone: '',
})

const editing = computed(() => route.name === 'profile-edit')

const title = computed(() => {
  return editing.value ? 'Modifica profilo' : 'Nuovo profilo'
})

const profileId = computed<number | null>(() => {
  if (!editing.value) {
    return null
  }

  const value = Number(route.params.profileId)

  if (!Number.isInteger(value) || value <= 0) {
    return null
  }

  return value
})

const cancelRoute = computed(() => {
  if (profileId.value !== null) {
    return {
      name: 'profile-detail',
      params: {
        profileId: profileId.value,
      },
    }
  }

  return {
    name: 'profiles',
  }
})

async function loadProfile(): Promise<void> {
  if (!editing.value) {
    return
  }

  if (profileId.value === null) {
    error.value = 'Identificativo del profilo non valido'
    return
  }

  loading.value = true
  error.value = null

  try {
    const profile = await findProfileById(profileId.value)

    // Todo: spostare in composable / utility
    form.firstName = profile.firstName
    form.lastName = profile.lastName
    form.birthDate = profile.birthDate ?? ''
    form.birthPlace = profile.birthPlace ?? ''
    form.fiscalCode = profile.fiscalCode ?? ''
    form.email = profile.email ?? ''
    form.phone = profile.phone ?? ''
  } catch (cause) {
    error.value = getErrorMessage(cause)
  } finally {
    loading.value = false
  }
}

async function saveProfile(): Promise<void> {
  if (form.firstName.trim() === '' || form.lastName.trim() === '') {
    error.value = 'Nome e cognome sono obbligatori'
    return
  }

  saving.value = true
  error.value = null

  try {
    const payload = buildPayload()

    const savedProfile =
      editing.value && profileId.value !== null
        ? await updateProfile(profileId.value, payload)
        : await createProfile(payload)

    await router.push({
      name: 'profile-detail',
      params: {
        profileId: savedProfile.id,
      },
    })
  } catch (cause) {
    error.value = getErrorMessage(cause)
  } finally {
    saving.value = false
  }
}

// todo: spostare in composable / utility
function buildPayload(): ProfilePayload {
  return {
    firstName: form.firstName.trim(),
    lastName: form.lastName.trim(),
    birthDate: emptyToNull(form.birthDate),
    birthPlace: emptyToNull(form.birthPlace),
    fiscalCode: emptyToNull(form.fiscalCode),
    email: emptyToNull(form.email),
    phone: emptyToNull(form.phone),
  }
}

function emptyToNull(value: string): string | null {
  const normalized = value.trim()

  return normalized === '' ? null : normalized
}

function getErrorMessage(cause: unknown): string {
  if (cause instanceof Error) {
    return cause.message
  }

  return 'Impossibile salvare il profilo'
}

onMounted(loadProfile)
</script>

<template>
  <v-container>
    <v-btn variant="text" prepend-icon="mdi-arrow-left" :to="cancelRoute" class="mb-4">
      Annulla
    </v-btn>

    <h1 class="text-h4 mb-6">
      {{ title }}
    </h1>

    <v-progress-linear v-if="loading" indeterminate class="mb-4" />

    <v-alert v-if="error" type="error" variant="tonal" class="mb-4">
      {{ error }}
    </v-alert>

    <v-card v-if="!loading">
      <v-card-text>
        <form @submit.prevent="saveProfile">
          <v-row>
            <v-col cols="12" md="6">
              <v-text-field
                v-model="form.firstName"
                label="Nome"
                maxlength="100"
                required
                :disabled="saving"
              />
            </v-col>

            <v-col cols="12" md="6">
              <v-text-field
                v-model="form.lastName"
                label="Cognome"
                maxlength="100"
                required
                :disabled="saving"
              />
            </v-col>

            <v-col cols="12" md="6">
              <v-text-field
                v-model="form.birthDate"
                label="Data di nascita"
                type="date"
                :disabled="saving"
              />
            </v-col>

            <v-col cols="12" md="6">
              <v-text-field
                v-model="form.birthPlace"
                label="Luogo di nascita"
                maxlength="120"
                :disabled="saving"
              />
            </v-col>

            <v-col cols="12" md="6">
              <v-text-field
                v-model="form.fiscalCode"
                label="Codice fiscale"
                maxlength="16"
                :disabled="saving"
              />
            </v-col>

            <v-col cols="12" md="6">
              <v-text-field
                v-model="form.email"
                label="Email"
                type="email"
                maxlength="254"
                :disabled="saving"
              />
            </v-col>

            <v-col cols="12" md="6">
              <v-text-field
                v-model="form.phone"
                label="Telefono"
                maxlength="40"
                :disabled="saving"
              />
            </v-col>
          </v-row>

          <div class="d-flex justify-end mt-4">
            <v-btn variant="text" :to="cancelRoute" class="mr-2" :disabled="saving">
              Annulla
            </v-btn>

            <v-btn
              color="primary"
              type="submit"
              :loading="saving"
              :disabled="form.firstName.trim() === '' || form.lastName.trim() === ''"
            >
              Salva
            </v-btn>
          </div>
        </form>
      </v-card-text>
    </v-card>
  </v-container>
</template>
