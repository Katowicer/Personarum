<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import {
  createProfileDocument,
  documentTypeOptions,
  findProfileDocumentById,
  updateProfileDocument,
  type DocumentType,
  type ProfileDocumentPayload,
} from '@/services/profileDocumentService'

interface DocumentForm {
  type: DocumentType
  documentNumber: string
  issuingAuthority: string
  issueDate: string
  expirationDate: string
  notes: string
}

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const saving = ref(false)
const error = ref<string | null>(null)

const form = reactive<DocumentForm>({
  type: 'IDENTITY_CARD',
  documentNumber: '',
  issuingAuthority: '',
  issueDate: '',
  expirationDate: '',
  notes: '',
})

const editing = computed(() => {
  return route.name === 'profile-document-edit'
})

const profileId = computed(() => {
  return readPositiveNumber(route.params.profileId)
})

const documentId = computed(() => {
  if (!editing.value) {
    return null
  }

  return readPositiveNumber(route.params.documentId)
})

const cancelRoute = computed(() => ({
  name: 'profile-detail',
  params: {
    profileId: profileId.value,
  },
}))

const today = new Date().toISOString().slice(0, 10)

async function loadDocument(): Promise<void> {
  if (!editing.value) {
    return
  }

  if (profileId.value === null || documentId.value === null) {
    error.value = 'Identificativo non valido'
    return
  }

  loading.value = true
  error.value = null

  try {
    const document = await findProfileDocumentById(profileId.value, documentId.value)

    form.type = document.type
    form.documentNumber = document.documentNumber ?? ''
    form.issuingAuthority = document.issuingAuthority ?? ''
    form.issueDate = document.issueDate ?? ''
    form.expirationDate = document.expirationDate ?? ''
    form.notes = document.notes ?? ''
  } catch (cause) {
    error.value = getErrorMessage(cause)
  } finally {
    loading.value = false
  }
}

async function saveDocument(): Promise<void> {
  if (profileId.value === null) {
    error.value = 'Identificativo del profilo non valido'
    return
  }

  if (form.issueDate !== '' && form.expirationDate !== '' && form.expirationDate < form.issueDate) {
    error.value = 'La data di scadenza non può precedere la data di rilascio'
    return
  }

  saving.value = true
  error.value = null

  try {
    const payload = buildPayload()

    if (editing.value) {
      if (documentId.value === null) {
        error.value = 'Identificativo del documento non valido'
        return
      }

      await updateProfileDocument(profileId.value, documentId.value, payload)
    } else {
      await createProfileDocument(profileId.value, payload)
    }

    await router.push({
      name: 'profile-detail',
      params: {
        profileId: profileId.value,
      },
    })
  } catch (cause) {
    error.value = getErrorMessage(cause)
  } finally {
    saving.value = false
  }
}

function buildPayload(): ProfileDocumentPayload {
  return {
    type: form.type,
    documentNumber: emptyToNull(form.documentNumber),
    issuingAuthority: emptyToNull(form.issuingAuthority),
    issueDate: emptyToNull(form.issueDate),
    expirationDate: emptyToNull(form.expirationDate),
    notes: emptyToNull(form.notes),
  }
}

function emptyToNull(value: string): string | null {
  const normalized = value.trim()

  return normalized === '' ? null : normalized
}

function readPositiveNumber(value: unknown): number | null {
  const number = Number(value)

  if (!Number.isInteger(number) || number <= 0) {
    return null
  }

  return number
}

function getErrorMessage(cause: unknown): string {
  if (cause instanceof Error) {
    return cause.message
  }

  return 'Impossibile salvare il documento'
}

onMounted(loadDocument)
</script>

<template>
  <v-container>
    <v-btn variant="text" prepend-icon="mdi-arrow-left" :to="cancelRoute" class="mb-4">
      Annulla
    </v-btn>

    <h1 class="text-h4 mb-6">
      {{ editing ? 'Modifica documento' : 'Nuovo documento' }}
    </h1>

    <v-progress-linear v-if="loading" indeterminate class="mb-4" />

    <v-alert v-if="error" type="error" variant="tonal" class="mb-4">
      {{ error }}
    </v-alert>

    <v-card v-if="!loading">
      <v-card-text>
        <form @submit.prevent="saveDocument">
          <v-row>
            <v-col cols="12">
              <v-select
                v-model="form.type"
                :items="documentTypeOptions"
                item-title="title"
                item-value="value"
                label="Tipo documento"
                :disabled="saving"
                required
              />
            </v-col>

            <v-col cols="12" md="6">
              <v-text-field
                v-model="form.documentNumber"
                label="Numero documento"
                maxlength="100"
                :disabled="saving"
              />
            </v-col>

            <v-col cols="12" md="6">
              <v-text-field
                v-model="form.issuingAuthority"
                label="Autorità di rilascio"
                maxlength="150"
                :disabled="saving"
              />
            </v-col>

            <v-col cols="12" md="6">
              <v-text-field
                v-model="form.issueDate"
                label="Data di rilascio"
                type="date"
                :max="today"
                :disabled="saving"
              />
            </v-col>

            <v-col cols="12" md="6">
              <v-text-field
                v-model="form.expirationDate"
                label="Data di scadenza"
                type="date"
                :min="form.issueDate || undefined"
                :disabled="saving"
              />
            </v-col>

            <v-col cols="12">
              <v-textarea
                v-model="form.notes"
                label="Note"
                maxlength="1000"
                rows="3"
                :disabled="saving"
              />
            </v-col>
          </v-row>

          <div class="d-flex justify-end mt-4">
            <v-btn variant="text" :to="cancelRoute" class="mr-2" :disabled="saving">
              Annulla
            </v-btn>

            <v-btn color="primary" type="submit" :loading="saving"> Salva </v-btn>
          </div>
        </form>
      </v-card-text>
    </v-card>
  </v-container>
</template>
