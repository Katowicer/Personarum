<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'

import { findEnabledTemplates, type DocumentTemplate } from '@/services/documentTemplateService'

import {
  downloadGeneratedDocumentPdf,
  findGeneratedDocuments,
  generateDocument,
  type DocumentGenerationType,
  type GeneratedDocument,
} from '@/services/generatedDocumentService'

const route = useRoute()

const templates = ref<DocumentTemplate[]>([])
const documents = ref<GeneratedDocument[]>([])

const selectedTemplateId = ref<number | null>(null)
const selectedGenerationType = ref<DocumentGenerationType>('STANDARD')

const generatedDocument = ref<GeneratedDocument | null>(null)

const loading = ref(false)
const generating = ref(false)
const error = ref<string | null>(null)
const downloadingId = ref<number | null>(null)

const profileId = computed<number | null>(() => {
  const value = route.params.profileId

  if (typeof value !== 'string') {
    return null
  }

  const parsed = Number(value)

  return Number.isInteger(parsed) && parsed > 0 ? parsed : null
})

const generationTypes = [
  {
    title: 'Standard',
    value: 'STANDARD',
  },
  {
    title: 'Riepilogo profilo',
    value: 'PROFILE_SUMMARY',
  },
]

async function load(): Promise<void> {
  if (profileId.value === null) {
    error.value = 'Profilo non valido'
    return
  }

  loading.value = true
  error.value = null

  try {
    const [loadedTemplates, loadedDocuments] = await Promise.all([
      findEnabledTemplates(),
      findGeneratedDocuments(profileId.value),
    ])

    templates.value = loadedTemplates
    documents.value = loadedDocuments
  } catch (cause) {
    error.value = getErrorMessage(cause)
  } finally {
    loading.value = false
  }
}

async function generate(): Promise<void> {
  if (profileId.value === null) {
    return
  }

  if (selectedTemplateId.value === null) {
    error.value = 'Selezionare un template'
    return
  }

  generating.value = true
  error.value = null

  try {
    const document = await generateDocument(profileId.value, {
      templateId: selectedTemplateId.value,
      generationType: selectedGenerationType.value,
    })

    generatedDocument.value = document

    documents.value = [document, ...documents.value.filter((item) => item.id !== document.id)]
  } catch (cause) {
    error.value = getErrorMessage(cause)
  } finally {
    generating.value = false
  }
}

function showDocument(document: GeneratedDocument): void {
  generatedDocument.value = document
}

function generationTypeLabel(type: DocumentGenerationType): string {
  switch (type) {
    case 'STANDARD':
      return 'Standard'
    case 'PROFILE_SUMMARY':
      return 'Riepilogo profilo'
  }
}

function formatDate(value: string): string {
  return new Intl.DateTimeFormat('it-IT', {
    dateStyle: 'short',
    timeStyle: 'short',
  }).format(new Date(value))
}

function getErrorMessage(cause: unknown): string {
  if (cause instanceof Error) {
    return cause.message
  }

  return 'Si è verificato un errore'
}

async function downloadPdf(document: GeneratedDocument): Promise<void> {
  if (profileId.value === null) {
    return
  }

  downloadingId.value = document.id
  error.value = null

  try {
    const downloadedFile = await downloadGeneratedDocumentPdf(profileId.value, document.id)

    const url = URL.createObjectURL(downloadedFile.blob)

    const link = window.document.createElement('a')

    link.href = url
    link.download = downloadedFile.fileName
    link.click()

    URL.revokeObjectURL(url)
  } catch (cause) {
    error.value = getErrorMessage(cause)
  } finally {
    downloadingId.value = null
  }
}

onMounted(load)
</script>

<template>
  <v-container>
    <v-btn
      variant="text"
      prepend-icon="mdi-arrow-left"
      :to="{
        name: 'profile-detail',
        params: { profileId },
      }"
      class="mb-4"
    >
      Profilo
    </v-btn>

    <h1 class="text-h4 mb-6">Documenti generati</h1>

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

    <v-progress-linear v-if="loading" indeterminate class="mb-4" />

    <template v-if="!loading">
      <v-card class="mb-6">
        <v-card-title> Genera documento </v-card-title>

        <v-card-text>
          <v-select
            v-model="selectedTemplateId"
            :items="templates"
            item-title="name"
            item-value="id"
            label="Template"
            :disabled="generating"
            class="mb-2"
          />

          <v-select
            v-model="selectedGenerationType"
            :items="generationTypes"
            label="Modalità di generazione"
            :disabled="generating"
            class="mb-2"
          />

          <v-alert
            v-if="selectedGenerationType === 'PROFILE_SUMMARY'"
            type="info"
            variant="tonal"
            class="mb-4"
          >
            La modalità Riepilogo profilo aggiunge automaticamente i principali dati anagrafici dopo
            il contenuto del template.
          </v-alert>

          <v-btn
            color="primary"
            prepend-icon="mdi-file-document-plus-outline"
            :loading="generating"
            :disabled="selectedTemplateId === null || templates.length === 0"
            @click="generate"
          >
            Genera
          </v-btn>
        </v-card-text>
      </v-card>

      <v-alert v-if="templates.length === 0" type="warning" variant="tonal" class="mb-6">
        Non sono disponibili template abilitati. Un amministratore deve crearne o abilitarne almeno
        uno.
      </v-alert>

      <v-card v-if="generatedDocument" class="mb-6">
        <v-card-title> Anteprima </v-card-title>

        <v-card-subtitle>
          {{ generatedDocument.templateName }}
          ·
          {{ generationTypeLabel(generatedDocument.generationType) }}
        </v-card-subtitle>

        <v-card-text>
          <pre class="generated-content">{{ generatedDocument.content }}</pre>
        </v-card-text>
        <v-card-actions>
          <v-btn
            color="primary"
            variant="outlined"
            prepend-icon="mdi-file-pdf-box"
            :loading="downloadingId === generatedDocument.id"
            @click="downloadPdf(generatedDocument)"
          >
            Scarica PDF
          </v-btn>
        </v-card-actions>
      </v-card>

      <v-card>
        <v-card-title> Storico documenti </v-card-title>

        <v-card-text v-if="documents.length === 0">
          Nessun documento generato per questo profilo.
        </v-card-text>

        <v-table v-else>
          <thead>
            <tr>
              <th>Template</th>
              <th>Modalità</th>
              <th>Data</th>
              <th class="text-right">Azioni</th>
            </tr>
          </thead>

          <tbody>
            <tr v-for="document in documents" :key="document.id">
              <td>
                {{ document.templateName }}
              </td>

              <td>
                {{ generationTypeLabel(document.generationType) }}
              </td>

              <td>
                {{ formatDate(document.createdAt) }}
              </td>

              <td class="text-right">
                <v-btn variant="text" size="small" @click="showDocument(document)">
                  Visualizza
                </v-btn>

                <v-btn
                  variant="text"
                  size="small"
                  prepend-icon="mdi-file-pdf-box"
                  :loading="downloadingId === document.id"
                  @click="downloadPdf(document)"
                >
                  PDF
                </v-btn>
              </td>
            </tr>
          </tbody>
        </v-table>
      </v-card>
    </template>
  </v-container>
</template>

<style scoped>
.generated-content {
  white-space: pre-wrap;
  font-family: inherit;
  line-height: 1.6;
}
</style>
