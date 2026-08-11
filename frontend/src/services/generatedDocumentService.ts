import { apiDownload, apiRequest, type DownloadedFile } from '@/api/http'

export type DocumentGenerationType = 'STANDARD' | 'PROFILE_SUMMARY'

export interface GeneratedDocument {
  id: number
  profileId: number
  templateId: number
  templateName: string
  generationType: DocumentGenerationType
  content: string
  createdAt: string
}

export interface GenerateDocumentPayload {
  templateId: number
  generationType: DocumentGenerationType
}

function generatedDocumentsPath(profileId: number): string {
  return `/api/profiles/${profileId}/generated-documents`
}

export function findGeneratedDocuments(profileId: number): Promise<GeneratedDocument[]> {
  return apiRequest<GeneratedDocument[]>(generatedDocumentsPath(profileId))
}

export function findGeneratedDocumentById(
  profileId: number,
  documentId: number,
): Promise<GeneratedDocument> {
  return apiRequest<GeneratedDocument>(`${generatedDocumentsPath(profileId)}/${documentId}`)
}

export function generateDocument(
  profileId: number,
  payload: GenerateDocumentPayload,
): Promise<GeneratedDocument> {
  return apiRequest<GeneratedDocument>(generatedDocumentsPath(profileId), {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(payload),
  })
}

export function downloadGeneratedDocumentPdf(
  profileId: number,
  documentId: number,
): Promise<DownloadedFile> {
  return apiDownload(`${generatedDocumentsPath(profileId)}/${documentId}/pdf`)
}
