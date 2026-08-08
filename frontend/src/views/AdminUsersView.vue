<script setup lang="ts">
import { onMounted, ref } from 'vue'

import { findAllUsers, type UserAccount } from '@/services/adminUserService'

const users = ref<UserAccount[]>([])
const loading = ref(false)
const error = ref<string | null>(null)

async function loadUsers(): Promise<void> {
  loading.value = true
  error.value = null

  try {
    users.value = await findAllUsers()
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

  return 'Impossibile caricare gli utenti'
}

onMounted(loadUsers)
</script>

<template>
  <v-container>
    <v-btn variant="text" prepend-icon="mdi-arrow-left" :to="{ name: 'profiles' }" class="mb-4">
      Profili
    </v-btn>

    <div class="d-flex align-center justify-space-between mb-6">
      <h1 class="text-h4">Utenti</h1>

      <v-btn color="primary" prepend-icon="mdi-account-plus" :to="{ name: 'admin-user-create' }">
        Nuovo utente
      </v-btn>
    </div>

    <v-progress-linear v-if="loading" indeterminate class="mb-4" />

    <v-alert v-if="error" type="error" variant="tonal" class="mb-4">
      {{ error }}
    </v-alert>

    <v-card v-if="users.length > 0">
      <v-table>
        <thead>
          <tr>
            <th>Username</th>
            <th>Ruolo</th>
            <th>Stato</th>
            <th class="text-right">Azioni</th>
          </tr>
        </thead>

        <tbody>
          <tr v-for="user in users" :key="user.id">
            <td>{{ user.username }}</td>

            <td>{{ user.role }}</td>

            <td>
              <v-chip :color="user.enabled ? 'success' : undefined" size="small">
                {{ user.enabled ? 'Abilitato' : 'Disabilitato' }}
              </v-chip>
            </td>

            <td class="text-right">
              <v-btn
                variant="text"
                size="small"
                :to="{
                  name: 'admin-user-edit',
                  params: {
                    userId: user.id,
                  },
                }"
              >
                Modifica
              </v-btn>
            </td>
          </tr>
        </tbody>
      </v-table>
    </v-card>

    <v-alert v-else-if="!loading && !error" type="info" variant="tonal">
      Nessun utente presente.
    </v-alert>
  </v-container>
</template>
