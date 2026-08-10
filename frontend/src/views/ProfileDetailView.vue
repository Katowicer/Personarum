<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import { deleteProfile, findProfileById, type Profile } from '@/services/profileService'
import {
  archiveProfileDocument,
  deleteProfileDocument,
  deleteProfileDocumentFile,
  documentTypeLabel,
  downloadProfileDocumentFile,
  findAllProfileDocuments,
  restoreProfileDocument,
  uploadProfileDocumentFile,
  type ProfileDocument,
} from '@/services/profileDocumentService'

const route = useRoute()
const router = useRouter()

const profile = ref<Profile | null>(null)
const documents = ref<ProfileDocument[]>([])

const loading = ref(false)
const deletingProfile = ref(false)
const busyDocumentId = ref<number | null>(null)
const error = ref<string | null>(null)

const selectedFiles = ref<Record<number, File | null>>({})

function readProfileId(): number | null {
  const value = Number(route.params.profileId)

  if (!Number.isInteger(value) || value <= 0) {
    return null
  }

  return value
}

async function loadPage(): Promise<void> {
  const profileId = readProfileId()

  if (profileId === null) {
    error.value = 'Identificativo del profilo non valido'
    return
  }

  loading.value = true
  error.value = null

  try {
    const [loadedProfile, loadedDocuments] = await Promise.all([
      findProfileById(profileId),
      findAllProfileDocuments(profileId),
    ])

    profile.value = loadedProfile
    documents.value = loadedDocuments
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

  deletingProfile.value = true
  error.value = null

  try {
    await deleteProfile(profile.value.id)
    await router.push({ name: 'profiles' })
  } catch (cause) {
    error.value = getErrorMessage(cause)
  } finally {
    deletingProfile.value = false
  }
}

async function removeDocument(document: ProfileDocument): Promise<void> {
  const profileId = readProfileId()

  if (profileId === null) {
    return
  }

  const confirmed = window.confirm(`Eliminare il documento ${documentTypeLabel(document.type)}?`)

  if (!confirmed) {
    return
  }

  busyDocumentId.value = document.id
  error.value = null

  try {
    await deleteProfileDocument(profileId, document.id)

    documents.value = documents.value.filter((item) => item.id !== document.id)
  } catch (cause) {
    error.value = getErrorMessage(cause)
  } finally {
    busyDocumentId.value = null
  }
}

function selectFile(documentId: number, event: Event): void {
  const input = event.target as HTMLInputElement
  selectedFiles.value[documentId] = input.files?.[0] ?? null
}

async function uploadFile(document: ProfileDocument): Promise<void> {
  const profileId = readProfileId()
  const file = selectedFiles.value[document.id]

  if (profileId === null || !file) {
    error.value = 'Seleziona un file da caricare'
    return
  }

  const allowedTypes = ['application/pdf', 'image/jpeg', 'image/png']

  if (!allowedTypes.includes(file.type)) {
    error.value = 'Sono ammessi soltanto file PDF, JPEG e PNG'
    return
  }

  if (file.size > 5 * 1024 * 1024) {
    error.value = 'Il file non può superare 5 MiB'
    return
  }

  busyDocumentId.value = document.id
  error.value = null

  try {
    await uploadProfileDocumentFile(profileId, document.id, file)

    selectedFiles.value[document.id] = null
  } catch (cause) {
    error.value = getErrorMessage(cause)
  } finally {
    busyDocumentId.value = null
  }
}

async function downloadFile(document: ProfileDocument): Promise<void> {
  const profileId = readProfileId()

  if (profileId === null) {
    return
  }

  busyDocumentId.value = document.id
  error.value = null

  try {
    const downloadedFile = await downloadProfileDocumentFile(profileId, document.id)

    const url = URL.createObjectURL(downloadedFile.blob)
    const link = window.document.createElement('a')

    link.href = url
    link.download = downloadedFile.fileName
    link.click()

    URL.revokeObjectURL(url)
  } catch (cause) {
    error.value = getErrorMessage(cause)
  } finally {
    busyDocumentId.value = null
  }
}

async function removeFile(document: ProfileDocument): Promise<void> {
  const profileId = readProfileId()

  if (profileId === null) {
    return
  }

  const confirmed = window.confirm('Eliminare il file allegato al documento?')

  if (!confirmed) {
    return
  }

  busyDocumentId.value = document.id
  error.value = null

  try {
    await deleteProfileDocumentFile(profileId, document.id)
  } catch (cause) {
    error.value = getErrorMessage(cause)
  } finally {
    busyDocumentId.value = null
  }
}

async function archiveDocument(document: ProfileDocument): Promise<void> {
  const profileId = readProfileId()

  if (profileId === null) {
    return
  }

  const confirmed = window.confirm(
    'Archiviare il documento? Non potrà essere modificato finché non verrà ripristinato.',
  )

  if (!confirmed) {
    return
  }

  busyDocumentId.value = document.id
  error.value = null

  try {
    const updated = await archiveProfileDocument(profileId, document.id)

    Object.assign(document, updated)
  } catch (cause) {
    error.value = getErrorMessage(cause)
  } finally {
    busyDocumentId.value = null
  }
}

async function restoreDocument(document: ProfileDocument): Promise<void> {
  const profileId = readProfileId()

  if (profileId === null) {
    return
  }

  busyDocumentId.value = document.id
  error.value = null

  try {
    const updated = await restoreProfileDocument(profileId, document.id)

    Object.assign(document, updated)
  } catch (cause) {
    error.value = getErrorMessage(cause)
  } finally {
    busyDocumentId.value = null
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

onMounted(loadPage)
</script>

<template>
  <v-container>
    <v-btn variant="text" prepend-icon="mdi-arrow-left" :to="{ name: 'profiles' }" class="mb-0">
      Profili
    </v-btn>

    <v-progress-linear v-if="loading" indeterminate class="mb-4" />

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

          <v-btn color="error" variant="outlined" :loading="deletingProfile" @click="removeProfile">
            Elimina
          </v-btn>
        </div>
      </div>

      <v-card class="mb-8">
        <v-card-title>Dati personali</v-card-title>

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

      <!-- todo: creare componente separata -->
      <div class="d-flex align-center justify-space-between mb-4">
        <h2 class="text-h5">Documenti associati</h2>

        <v-btn
          color="primary"
          prepend-icon="mdi-file-plus"
          :to="{
            name: 'profile-document-create',
            params: { profileId: profile.id },
          }"
        >
          Nuovo documento
        </v-btn>

        <div class="d-flex ga-2">
          <v-btn
            color="primary"
            variant="outlined"
            prepend-icon="mdi-file-document-plus-outline"
            :to="{
              name: 'profile-generated-documents',
              params: {
                profileId: profile.id,
              },
            }"
          >
            Documenti generati
          </v-btn>

          <v-btn
            color="primary"
            :to="{
              name: 'profile-document-create',
              params: {
                profileId: profile.id,
              },
            }"
          >
            Aggiungi documento
          </v-btn>
        </div>
      </div>

      <v-alert v-if="documents.length === 0" type="info" variant="tonal">
        Non sono ancora presenti documenti.
      </v-alert>

      <v-card v-for="document in documents" :key="document.id" class="mb-4">
        <v-card-title class="d-flex align-center justify-space-between">
          <div class="d-flex align-center ga-2">
            <span>
              {{ documentTypeLabel(document.type) }}
            </span>

            <v-chip size="small" :color="document.status === 'ACTIVE' ? 'success' : undefined">
              {{ document.status === 'ACTIVE' ? 'Attivo' : 'Archiviato' }}
            </v-chip>
          </div>

          <div>
            <v-btn
              variant="text"
              size="small"
              :to="{
                name: 'profile-document-edit',
                params: {
                  profileId: profile.id,
                  documentId: document.id,
                },
              }"
            >
              Modifica
            </v-btn>

            <v-btn
              v-if="document.status === 'ACTIVE'"
              variant="text"
              size="small"
              :loading="busyDocumentId === document.id"
              @click="archiveDocument(document)"
            >
              Archivia
            </v-btn>

            <v-btn
              v-else
              variant="text"
              size="small"
              :loading="busyDocumentId === document.id"
              @click="restoreDocument(document)"
            >
              Ripristina
            </v-btn>

            <v-btn
              color="error"
              variant="text"
              size="small"
              :loading="busyDocumentId === document.id"
              @click="removeDocument(document)"
              :disabled="document.status === 'ARCHIVED'"
            >
              Elimina
            </v-btn>
          </div>
        </v-card-title>

        <v-card-text>
          <v-row>
            <v-col cols="12" md="6">
              <strong>Numero</strong>
              <div>
                {{ displayValue(document.documentNumber) }}
              </div>
            </v-col>

            <v-col cols="12" md="6">
              <strong>Autorità di rilascio</strong>
              <div>
                {{ displayValue(document.issuingAuthority) }}
              </div>
            </v-col>

            <v-col cols="12" md="6">
              <strong>Data di rilascio</strong>
              <div>
                {{ displayDate(document.issueDate) }}
              </div>
            </v-col>

            <v-col cols="12" md="6">
              <strong>Data di scadenza</strong>
              <div>
                {{ displayDate(document.expirationDate) }}
              </div>
            </v-col>

            <v-col cols="12">
              <strong>Note</strong>
              <div>{{ displayValue(document.notes) }}</div>
            </v-col>
          </v-row>

          <v-divider class="my-4" />

          <strong>File allegato</strong>

          <div class="file-actions mt-2">
            <input
              type="file"
              accept="application/pdf,image/jpeg,image/png"
              :disabled="document.status === 'ARCHIVED'"
              @change="selectFile(document.id, $event)"
            />

            <v-btn
              color="primary"
              variant="outlined"
              size="small"
              :loading="busyDocumentId === document.id"
              :disabled="document.status === 'ARCHIVED' || !selectedFiles[document.id]"
              @click="uploadFile(document)"
            >
              Carica o sostituisci
            </v-btn>

            <v-btn
              variant="outlined"
              size="small"
              :loading="busyDocumentId === document.id"
              @click="downloadFile(document)"
            >
              Scarica
            </v-btn>

            <v-btn
              color="error"
              variant="outlined"
              size="small"
              :loading="busyDocumentId === document.id"
              @click="removeFile(document)"
              :disabled="document.status === 'ARCHIVED'"
            >
              Elimina file
            </v-btn>
          </div>
        </v-card-text>
      </v-card>
    </template>
  </v-container>
</template>

<style scoped>
.file-actions {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
}
</style>
