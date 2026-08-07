import type { Profile, ProfilePayload } from '@/services/profileService'

type JsonObject = Record<string, unknown>

export function downloadProfileJson(profile: Profile): void {
  const payload = profileToPayload(profile)
  const fileName = buildProfileFileName(profile)

  downloadJson(payload, fileName)
}

export function downloadProfilesJson(profiles: Profile[]): void {
  const payloads = profiles.map(profileToPayload)

  downloadJson(payloads, 'personarum-profiles.json')
}

export function parseProfilesJson(content: string): ProfilePayload[] {
  let parsed: unknown

  try {
    parsed = JSON.parse(content)
  } catch {
    throw new Error('Il file non contiene un JSON valido')
  }

  const values = Array.isArray(parsed) ? parsed : [parsed]

  if (values.length === 0) {
    throw new Error('Il file JSON non contiene profili')
  }

  return values.map((value, index) => {
    return parseProfile(value, index)
  })
}

function parseProfile(value: unknown, index: number): ProfilePayload {
  if (!isJsonObject(value)) {
    throw new Error(`Il profilo in posizione ${index + 1} non è valido`)
  }

  return {
    firstName: readRequiredString(value, 'firstName', 100, index),
    lastName: readRequiredString(value, 'lastName', 100, index),
    birthDate: readOptionalString(value, 'birthDate', 10, index),
    birthPlace: readOptionalString(value, 'birthPlace', 120, index),
    fiscalCode: readOptionalString(value, 'fiscalCode', 16, index),
    email: readOptionalString(value, 'email', 254, index),
    phone: readOptionalString(value, 'phone', 40, index),
  }
}

function readRequiredString(
  object: JsonObject,
  field: string,
  maxLength: number,
  index: number,
): string {
  const value = object[field]

  if (typeof value !== 'string' || value.trim() === '') {
    throw new Error(`Il campo ${field} è obbligatorio nel profilo ${index + 1}`)
  }

  const normalized = value.trim()

  validateLength(normalized, field, maxLength, index)

  return normalized
}

function readOptionalString(
  object: JsonObject,
  field: string,
  maxLength: number,
  index: number,
): string | null {
  const value = object[field]

  if (value === undefined || value === null || value === '') {
    return null
  }

  if (typeof value !== 'string') {
    throw new Error(`Il campo ${field} non è valido nel profilo ${index + 1}`)
  }

  const normalized = value.trim()

  if (normalized === '') {
    return null
  }

  validateLength(normalized, field, maxLength, index)

  return normalized
}

function validateLength(value: string, field: string, maxLength: number, index: number): void {
  if (value.length > maxLength) {
    throw new Error(`Il campo ${field} supera ${maxLength} caratteri nel profilo ${index + 1}`)
  }
}

function profileToPayload(profile: Profile): ProfilePayload {
  return {
    firstName: profile.firstName,
    lastName: profile.lastName,
    birthDate: profile.birthDate,
    birthPlace: profile.birthPlace,
    fiscalCode: profile.fiscalCode,
    email: profile.email,
    phone: profile.phone,
  }
}

function downloadJson(value: unknown, fileName: string): void {
  const content = JSON.stringify(value, null, 2)
  const blob = new Blob([content], {
    type: 'application/json',
  })

  const url = URL.createObjectURL(blob)
  const link = window.document.createElement('a')

  link.href = url
  link.download = fileName
  link.click()

  URL.revokeObjectURL(url)
}

function buildProfileFileName(profile: Profile): string {
  const name = `${profile.firstName}-${profile.lastName}`
    .trim()
    .toLowerCase()
    .replace(/[^a-z0-9]+/g, '-')
    .replace(/^-|-$/g, '')

  return `${name || 'profile'}.json`
}

function isJsonObject(value: unknown): value is JsonObject {
  return typeof value === 'object' && value !== null && !Array.isArray(value)
}
