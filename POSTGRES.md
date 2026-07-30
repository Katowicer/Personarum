# Comandi ambiente Devbox

## Inizializzare datbase

```sh
if [ ! -f "$PGDATA/PG_VERSION" ]; then initdb -D "$PGDATA" --encoding=UTF8 --locale=C; fi
```

## Avviare servizio

```sh
devbox services start postgresql
```

## Verificare servizio

```sh
devbox services ls
pg_isready -h "$PGHOST" -p "$PGPORT"
```

## Credenziali registrate

User: personarum
Password: personarum
Database: personarum
Porta: 5432

## Verifica connessione

```sh
PGPASSWORD=personarum psql -h 127.0.0.1 -p "$PGPORT" -U personarum -d personarum -c "SELECT current_database(), current_user;"
```

## Tabelle

```sql
devbox run -- psql -U personarum -d personarum -c '\d profiles'
```

```sql
devbox run -- psql -U personarum -d personarum \
  -c 'SELECT installed_rank, version, description, success FROM flyway_schema_history ORDER BY installed_rank;'
```

    ```sql
devbox run -- psql -U personarum -d personarum \
  -c 'TRUNCATE TABLE profiles RESTART IDENTITY;'
```
