<script setup lang="ts">
import { onMounted, ref } from 'vue'

import { findAllProfiles, type Profile } from '@/services/profileService'

const profiles = ref<Profile[]>([])
const loading = ref(false)
const error = ref<string | null>(null)

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

function getErrorMessage(cause: unknown): string {
  if (cause instanceof Error) {
    return cause.message
  }

  return 'Impossibile caricare i profili'
}

function displayValue(value: string | null): string {
  return value || '—'
}

onMounted(loadProfiles)
</script>

<template>
  <v-container>
    <div class="d-flex align-center justify-space-between mb-6">
      <h1 class="text-h4">Profili</h1>

      <v-btn color="primary" prepend-icon="mdi-account-plus" :to="{ name: 'profile-create' }">
        Nuovo profilo
      </v-btn>
    </div>

    <v-progress-linear v-if="loading" indeterminate class="mb-4" />

    <v-alert v-if="error" type="error" variant="tonal" class="mb-4">
      {{ error }}
    </v-alert>

    <v-alert v-if="!loading && !error && profiles.length === 0" type="info" variant="tonal">
      Non sono ancora presenti profili.
    </v-alert>

    <v-card v-if="profiles.length > 0">
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
          <tr v-for="profile in profiles" :key="profile.id">
            <td>{{ profile.firstName }} {{ profile.lastName }}</td>

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
              <v-btn
                variant="text"
                size="small"
                :to="{
                  name: 'profile-detail',
                  params: { profileId: profile.id },
                }"
              >
                Apri
              </v-btn>

              <v-btn
                variant="text"
                size="small"
                :to="{
                  name: 'profile-edit',
                  params: { profileId: profile.id },
                }"
              >
                Modifica
              </v-btn>
            </td>
          </tr>
        </tbody>
      </v-table>
    </v-card>
  </v-container>
</template>
