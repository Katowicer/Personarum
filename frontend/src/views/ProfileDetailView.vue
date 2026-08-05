<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import { deleteProfile, findProfileById, type Profile } from '@/services/profileService'

const route = useRoute()
const router = useRouter()

const profile = ref<Profile | null>(null)
const loading = ref(false)
const deleting = ref(false)
const error = ref<string | null>(null)

function readProfileId(): number | null {
  const profileId = Number(route.params.profileId)

  if (!Number.isInteger(profileId) || profileId <= 0) {
    return null
  }

  return profileId
}

async function loadProfile(): Promise<void> {
  const profileId = readProfileId()

  if (profileId === null) {
    error.value = 'Identificativo del profilo non valido'
    return
  }

  loading.value = true
  error.value = null

  try {
    profile.value = await findProfileById(profileId)
  } catch (cause) {
    error.value = getErrorMessage(cause)
  } finally {
    loading.value = false
  }
}

async function removeProfile(): Promise<void> {
  if (profile.value === null) {
    return
  }

  const confirmed = window.confirm(
    `Eliminare il profilo di ${profile.value.firstName} ${profile.value.lastName}?`,
  )

  if (!confirmed) {
    return
  }

  deleting.value = true
  error.value = null

  try {
    await deleteProfile(profile.value.id)
    await router.push({ name: 'profiles' })
  } catch (cause) {
    error.value = getErrorMessage(cause)
  } finally {
    deleting.value = false
  }
}

function getErrorMessage(cause: unknown): string {
  if (cause instanceof Error) {
    return cause.message
  }

  return 'Si è verificato un errore'
}

function displayValue(value: string | null): string {
  return value || '—'
}

function displayDate(value: string | null): string {
  if (value === null) {
    return '—'
  }

  return new Date(`${value}T00:00:00`).toLocaleDateString('it-IT')
}

onMounted(loadProfile)
</script>

<template>
  <v-container>
    <v-btn
      variant="text"
      prepend-icon="mdi-arrow-left"
      :to="{ name: 'profiles' }"
      class="mb-4"
    ></v-btn>

    <v-progress-linear v-if="loading" indeterminate class="mb-4" />

    <v-alert v-if="error" type="error" variant="tonal" class="mb-4">
      {{ error }}
    </v-alert>

    <template v-if="profile">
      <div class="d-flex align-center justify-space-between mb-6">
        <h1 class="text-h4">{{ profile.firstName }} {{ profile.lastName }}</h1>

        <div>
          <v-btn
            color="primary"
            variant="outlined"
            class="mr-2"
            :to="{
              name: 'profile-edit',
              params: { profileId: profile.id },
            }"
          >
            Modifica
          </v-btn>

          <v-btn color="error" variant="outlined" :loading="deleting" @click="removeProfile">
            Elimina
          </v-btn>
        </div>
      </div>

      <v-card>
        <v-card-title> Dati personali </v-card-title>

        <v-card-text>
          <v-row>
            <v-col cols="12" md="6">
              <strong>Nome</strong>
              <div>{{ profile.firstName }}</div>
            </v-col>

            <v-col cols="12" md="6">
              <strong>Cognome</strong>
              <div>{{ profile.lastName }}</div>
            </v-col>

            <v-col cols="12" md="6">
              <strong>Data di nascita</strong>
              <div>{{ displayDate(profile.birthDate) }}</div>
            </v-col>

            <v-col cols="12" md="6">
              <strong>Luogo di nascita</strong>
              <div>{{ displayValue(profile.birthPlace) }}</div>
            </v-col>

            <v-col cols="12" md="6">
              <strong>Codice fiscale</strong>
              <div>{{ displayValue(profile.fiscalCode) }}</div>
            </v-col>

            <v-col cols="12" md="6">
              <strong>Email</strong>
              <div>{{ displayValue(profile.email) }}</div>
            </v-col>

            <v-col cols="12" md="6">
              <strong>Telefono</strong>
              <div>{{ displayValue(profile.phone) }}</div>
            </v-col>
          </v-row>
        </v-card-text>
      </v-card>
    </template>
  </v-container>
</template>
