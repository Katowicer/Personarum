import { apiDownload, apiRequest, type DownloadedFile } from '@/api/http'

export type DocumentType =
  'IDENTITY_CARD' | 'DRIVING_LICENSE' | 'PASSPORT' | 'FISCAL_CARD' | 'OTHER'

export type ProfileDocumentStatus = 'ACTIVE' | 'ARCHIVED'

export interface ProfileDocument {
  id: number
  profileId: number
  type: DocumentType
  status: ProfileDocumentStatus
  documentNumber: string | null
  issuingAuthority: string | null
  issueDate: string | null
  expirationDate: string | null
  notes: string | null
}

export interface ProfileDocumentPayload {
  type: DocumentType
  documentNumber: string | null
  issuingAuthority: string | null
  issueDate: string | null
  expirationDate: string | null
  notes: string | null
}

export const documentTypeOptions: {
  title: string
  value: DocumentType
}[] = [
  {
    title: "Carta d'identità",
    value: 'IDENTITY_CARD',
  },
  {
    title: 'Patente',
    value: 'DRIVING_LICENSE',
  },
  {
    title: 'Passaporto',
    value: 'PASSPORT',
  },
  {
    title: 'Tessera sanitaria',
    value: 'FISCAL_CARD',
  },
  {
    title: 'Altro',
    value: 'OTHER',
  },
]

function documentsPath(profileId: number): string {
  return `/api/profiles/${profileId}/documents`
}

export function findAllProfileDocuments(profileId: number): Promise<ProfileDocument[]> {
  return apiRequest<ProfileDocument[]>(documentsPath(profileId))
}

export function findProfileDocumentById(
  profileId: number,
  documentId: number,
): Promise<ProfileDocument> {
  return apiRequest<ProfileDocument>(`${documentsPath(profileId)}/${documentId}`)
}

export function createProfileDocument(
  profileId: number,
  payload: ProfileDocumentPayload,
): Promise<ProfileDocument> {
  return apiRequest<ProfileDocument>(documentsPath(profileId), {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(payload),
  })
}

export function updateProfileDocument(
  profileId: number,
  documentId: number,
  payload: ProfileDocumentPayload,
): Promise<ProfileDocument> {
  return apiRequest<ProfileDocument>(`${documentsPath(profileId)}/${documentId}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(payload),
  })
}

export function deleteProfileDocument(profileId: number, documentId: number): Promise<void> {
  return apiRequest<void>(`${documentsPath(profileId)}/${documentId}`, {
    method: 'DELETE',
  })
}

export function uploadProfileDocumentFile(
  profileId: number,
  documentId: number,
  file: File,
): Promise<void> {
  const formData = new FormData()
  formData.append('file', file)

  return apiRequest<void>(`${documentsPath(profileId)}/${documentId}/file`, {
    method: 'PUT',
    body: formData,
  })
}

export function downloadProfileDocumentFile(
  profileId: number,
  documentId: number,
): Promise<DownloadedFile> {
  return apiDownload(`${documentsPath(profileId)}/${documentId}/file`)
}

export function deleteProfileDocumentFile(profileId: number, documentId: number): Promise<void> {
  return apiRequest<void>(`${documentsPath(profileId)}/${documentId}/file`, {
    method: 'DELETE',
  })
}

export function archiveProfileDocument(
  profileId: number,
  documentId: number,
): Promise<ProfileDocument> {
  return apiRequest<ProfileDocument>(`${documentsPath(profileId)}/${documentId}/archive`, {
    method: 'PUT',
  })
}

export function restoreProfileDocument(
  profileId: number,
  documentId: number,
): Promise<ProfileDocument> {
  return apiRequest<ProfileDocument>(`${documentsPath(profileId)}/${documentId}/restore`, {
    method: 'PUT',
  })
}

export function documentTypeLabel(type: DocumentType): string {
  const option = documentTypeOptions.find((item) => item.value === type)

  return option?.title ?? type
}
