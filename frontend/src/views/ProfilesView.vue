<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

import { createProfile, findAllProfiles, type Profile } from '@/services/profileService'
import {
  downloadProfileJson,
  downloadProfilesJson,
  parseProfilesJson,
} from '@/services/profileJsonService'

const router = useRouter()
const authStore = useAuthStore()

const profiles = ref<Profile[]>([])
const search = ref('')

const loading = ref(false)
const importing = ref(false)

const error = ref<string | null>(null)
const success = ref<string | null>(null)

const importInput = ref<HTMLInputElement | null>(null)

const filteredProfiles = computed(() => {
  const query = search.value.trim().toLowerCase()

  if (query === '') {
    return profiles.value
  }

  return profiles.value.filter((profile) => {
    const searchableValues = [
      profile.firstName,
      profile.lastName,
      profile.fiscalCode,
      profile.email,
      profile.phone,
    ]

    return searchableValues.some((value) => {
      return value?.toLowerCase().includes(query)
    })
  })
})

async function loadProfiles(): Promise<void> {
  loading.value = true
  error.value = null

  try {
    profiles.value = await findAllProfiles()
  } catch (cause) {
    error.value = getErrorMessage(cause)
  } finally {
    loading.value = false
  }
}

function openImportFile(): void {
  error.value = null
  success.value = null
  importInput.value?.click()
}

async function importProfiles(event: Event): Promise<void> {
  const input = event.target as HTMLInputElement
  const file = input.files?.[0]

  input.value = ''

  if (!file) {
    return
  }

  importing.value = true
  error.value = null
  success.value = null

  try {
    const content = await file.text()
    const payloads = parseProfilesJson(content)

    let importedProfiles = 0

    for (const payload of payloads) {
      await createProfile(payload)
      importedProfiles += 1
    }

    await loadProfiles()

    success.value =
      importedProfiles === 1
        ? 'Profilo importato correttamente'
        : `${importedProfiles} profili importati correttamente`
  } catch (cause) {
    error.value = getErrorMessage(cause)
  } finally {
    importing.value = false
  }
}

function exportAllProfiles(): void {
  error.value = null
  success.value = null

  if (profiles.value.length === 0) {
    error.value = 'Non sono presenti profili da esportare'
    return
  }

  downloadProfilesJson(profiles.value)
}

function exportProfile(profile: Profile): void {
  error.value = null
  success.value = null
  downloadProfileJson(profile)
}

async function openProfile(profile: Profile): Promise<void> {
  await router.push({
    name: 'profile-detail',
    params: {
      profileId: profile.id,
    },
  })
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

onMounted(loadProfiles)
</script>

<template>
  <v-container>
    <div class="d-flex align-center justify-space-between flex-wrap ga-3 mb-6">
      <h1 class="text-h4">Profili</h1>

      <div class="d-flex flex-wrap ga-2">
        <input
          ref="importInput"
          type="file"
          accept="application/json,.json"
          hidden
          @change="importProfiles"
        />

        <v-btn
          variant="outlined"
          prepend-icon="mdi-file-import"
          :loading="importing"
          @click="openImportFile"
        >
          Importa JSON
        </v-btn>

        <v-btn
          variant="outlined"
          prepend-icon="mdi-file-export"
          :disabled="profiles.length === 0"
          @click="exportAllProfiles"
        >
          Esporta
        </v-btn>

        <v-btn color="primary" prepend-icon="mdi-account-plus" :to="{ name: 'profile-create' }">
          Nuovo profilo
        </v-btn>

        <v-btn
          v-if="authStore.user?.role === 'ADMIN'"
          variant="outlined"
          prepend-icon="mdi-account-cog"
          :to="{ name: 'admin-users' }"
        >
          Utenti
        </v-btn>
      </div>
    </div>

    <v-alert
      v-if="error"
      type="error"
      variant="tonal"
      closable
      class="mb-4"
      @click:close="error = null"
    >
      {{ error }}
    </v-alert>

    <v-alert
      v-if="success"
      type="success"
      variant="tonal"
      closable
      class="mb-4"
      @click:close="success = null"
    >
      {{ success }}
    </v-alert>

    <v-text-field
      v-model="search"
      label="Cerca profilo"
      placeholder="Nome, cognome, codice fiscale, email o telefono"
      prepend-inner-icon="mdi-magnify"
      clearable
      hide-details
      class="mb-4"
    />

    <v-progress-linear v-if="loading" indeterminate class="mb-4" />

    <v-alert v-if="!loading && !error && profiles.length === 0" type="info" variant="tonal">
      Non sono ancora presenti profili.
    </v-alert>

    <v-alert
      v-else-if="!loading && profiles.length > 0 && filteredProfiles.length === 0"
      type="info"
      variant="tonal"
    >
      Nessun profilo corrisponde alla ricerca.
    </v-alert>

    <template v-if="filteredProfiles.length > 0">
      <div class="text-body-2 text-medium-emphasis mb-2">
        {{ filteredProfiles.length }}
        {{ filteredProfiles.length === 1 ? 'profilo visualizzato' : 'profili visualizzati' }}
      </div>

      <v-card>
        <v-table>
          <thead>
            <tr>
              <th>Nome</th>
              <th>Codice fiscale</th>
              <th>Email</th>
              <th>Telefono</th>
              <th class="text-right">Azioni</th>
            </tr>
          </thead>

          <tbody>
            <tr v-for="profile in filteredProfiles" :key="profile.id">
              <td>
                {{ profile.firstName }}
                {{ profile.lastName }}
              </td>

              <td>
                {{ displayValue(profile.fiscalCode) }}
              </td>

              <td>
                {{ displayValue(profile.email) }}
              </td>

              <td>
                {{ displayValue(profile.phone) }}
              </td>

              <td class="text-right">
                <v-btn variant="text" size="small" @click="openProfile(profile)"> Apri </v-btn>

                <v-btn
                  variant="text"
                  size="small"
                  :to="{
                    name: 'profile-edit',
                    params: {
                      profileId: profile.id,
                    },
                  }"
                >
                  Modifica
                </v-btn>

                <v-btn
                  variant="text"
                  size="small"
                  prepend-icon="mdi-download"
                  @click="exportProfile(profile)"
                >
                  JSON
                </v-btn>
              </td>
            </tr>
          </tbody>
        </v-table>
      </v-card>
    </template>
  </v-container>
</template>
