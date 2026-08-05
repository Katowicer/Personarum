import { apiRequest } from '@/api/http'

export interface Profile {
  id: number
  firstName: string
  lastName: string
  birthDate: string | null
  birthPlace: string | null
  fiscalCode: string | null
  email: string | null
  phone: string | null
}

export interface ProfilePayload {
  firstName: string
  lastName: string
  birthDate: string | null
  birthPlace: string | null
  fiscalCode: string | null
  email: string | null
  phone: string | null
}

export function findAllProfiles(): Promise<Profile[]> {
  return apiRequest<Profile[]>('/api/profiles')
}

export function findProfileById(profileId: number): Promise<Profile> {
  return apiRequest<Profile>(`/api/profiles/${profileId}`)
}

export function createProfile(payload: ProfilePayload): Promise<Profile> {
  return apiRequest<Profile>('/api/profiles', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(payload),
  })
}

export function updateProfile(profileId: number, payload: ProfilePayload): Promise<Profile> {
  return apiRequest<Profile>(`/api/profiles/${profileId}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(payload),
  })
}

export function deleteProfile(profileId: number): Promise<void> {
  return apiRequest<void>(`/api/profiles/${profileId}`, {
    method: 'DELETE',
  })
}
