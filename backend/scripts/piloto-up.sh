#!/usr/bin/env bash
# Levanta el entorno completo del piloto desde cero.
# Uso: ./scripts/piloto-up.sh

set -euo pipefail
cd "$(dirname "$0")/.."

COMPOSE_FILE="docker-compose.piloto.yml"

# Copiar .env si no existe
if [ ! -f .env ]; then
  cp .env.piloto.example .env
  echo "Creado .env desde .env.piloto.example"
fi

# Abortar si quedan marcadores sin reemplazar
if grep -q '<CAMBIAR>' .env; then
  echo "ERROR: .env contiene <CAMBIAR>. Defina POSTGRES_PASSWORD, JWT_SECRET y SEED_PASSWORD." >&2
  exit 1
fi

# Cargar variables
set -a; source .env; set +a

echo "Construyendo imagenes..."
docker compose -f "$COMPOSE_FILE" build

echo "Levantando servicios..."
docker compose -f "$COMPOSE_FILE" up -d

echo ""
echo "=== Entorno del piloto levantado ==="
echo "  Frontend:  http://localhost:${FRONTEND_PORT:-3000}"
echo "  Backend:   http://localhost:${BACKEND_PORT:-8081}"
echo "  Postgres:  localhost:${DB_PORT:-5433}"
echo ""
echo "Esperando a que el backend este listo..."

for i in $(seq 1 60); do
  HTTP_CODE=$(curl -sf -o /dev/null -w "%{http_code}" \
    "http://localhost:${BACKEND_PORT:-8081}/api/v1/auth/login" -X POST \
    -H "Content-Type: application/json" \
    -d '{"email":"test@test","password":"x"}' 2>/dev/null || echo "000")

  if [ "$HTTP_CODE" = "401" ] || [ "$HTTP_CODE" = "400" ]; then
    echo "Backend listo (HTTP $HTTP_CODE)."
    break
  fi
  sleep 2
done

echo ""
echo "=== Cuentas de prueba (perfil dev) ==="
echo "  Contrasena comun: la definida en SEED_PASSWORD (.env)"
echo ""
echo "  DIR-01:  dir01@prueba.test"
echo "  STF-01:  stf01@prueba.test   ... STF-03"
echo "  COO-01:  coo01@prueba.test   ... COO-25"
echo "  TEC-01:  tec01@prueba.test   ... TEC-10"
echo "  Ensayo:  ensayo-dir@prueba.test, ensayo-coo@prueba.test"
echo "  Dev:     director@example.com, staff@example.com,"
echo "           coordinador@example.com, tecnico@example.com"
echo ""
echo "Para guardar snapshot:   ./scripts/snapshot-save.sh nombre"
echo "Para restaurar snapshot: ./scripts/snapshot-restore.sh snapshots/nombre.dump"
