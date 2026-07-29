# Sessione applicativo

## Rimuovere cookie sessione precedente

```sh
rm -f /tmp/personarum.cookies
```

## Richiedere nuovo cookie

```sh
CSRF=$(curl -sS \
  -c /tmp/personarum.cookies \
  http://localhost:8080/api/auth/csrf \
  | python3 -c 'import sys,json; print(json.load(sys.stdin)["token"])')
```

## Effettuare login

```sh
curl -i \
  -b /tmp/personarum.cookies \
  -c /tmp/personarum.cookies \
  -H 'Content-Type: application/json' \
  -H "X-CSRF-TOKEN: $CSRF" \
  -d '{"username":"admin","password":"ADMIN_PASSWORD_007"}' \
  http://localhost:8080/api/auth/login
```

Todo, aggiungere come comando / script devbox
