#!/usr/bin/env bash
# Guarda un snapshot de la base de datos del piloto.
# Uso: ./scripts/snapshot-save.sh [nombre_archivo]
#
# Ejemplo:
#   ./scripts/snapshot-save.sh snapshot_corte1
#   -> genera snapshots/snapshot_corte1.dump

set -euo pipefail
cd "$(dirname "$0")/.."

COMPOSE_FILE="docker-compose.piloto.yml"
DUMP_NAME="${1:-snapshot_$(date +%Y%m%d_%H%M%S)}"
DUMP_DIR="snapshots"

mkdir -p "$DUMP_DIR"

docker compose -f "$COMPOSE_FILE" exec -T db pg_dump \
  -U "${POSTGRES_USER:-sisepp_user}" \
  -Fc \
  "${POSTGRES_DB:-sisepp_piloto}" \
  > "${DUMP_DIR}/${DUMP_NAME}.dump"

echo "Snapshot guardado: ${DUMP_DIR}/${DUMP_NAME}.dump"
