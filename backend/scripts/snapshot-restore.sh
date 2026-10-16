#!/usr/bin/env bash
# Restaura un snapshot de la base de datos del piloto.
# Uso: ./scripts/snapshot-restore.sh snapshots/snapshot_corte1.dump
#
# ATENCION: esto borra todos los datos actuales de la base.

set -euo pipefail
cd "$(dirname "$0")/.."

COMPOSE_FILE="docker-compose.piloto.yml"
DUMP_FILE="${1:?Uso: $0 <archivo.dump>}"

if [ ! -f "$DUMP_FILE" ]; then
  echo "Error: no existe el archivo $DUMP_FILE"
  exit 1
fi

DB_NAME="${POSTGRES_DB:-sisepp_piloto}"
DB_USER="${POSTGRES_USER:-sisepp_user}"

echo "Restaurando $DUMP_FILE en la base $DB_NAME..."

# Detener backend para cerrar conexiones
docker compose -f "$COMPOSE_FILE" stop backend 2>/dev/null || true

# Restaurar
cat "$DUMP_FILE" | docker compose -f "$COMPOSE_FILE" exec -T db pg_restore \
  --clean --if-exists \
  -U "$DB_USER" \
  -d "$DB_NAME"

# Reiniciar backend
docker compose -f "$COMPOSE_FILE" start backend

echo "Restauracion completada. Backend reiniciado."
