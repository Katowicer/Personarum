# Specifiche configurazione ambiente frontend

## Verifica codice

```sh
npm run format
npm run lint
npm run type-check
npm run build
```

## Verifica dipendenze

```
npm ls --depth=0 \
  vue \
  vue-router \
  pinia \
  vite \
  typescript \
  vue-tsc \
  vuetify \
  vite-plugin-vuetify
```
