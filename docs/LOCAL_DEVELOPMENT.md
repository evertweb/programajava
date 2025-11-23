# Desarrollo Local - ForestechOil

Esta guía describe cómo trabajar con ForestechOil completamente en tu entorno local (WSL/Linux).

## Arquitectura del Entorno Local

```
┌─────────────────────────────────────────────────────────────────────┐
│                     DESARROLLO LOCAL (WSL/Linux)                    │
├─────────────────────────────────────────────────────────────────────┤
│  Workstation:                                                       │
│  ├── Docker Compose (microservicios)                                │
│  │   ├── Consul (8500)                                              │  
│  │   ├── MySQL (3307)                                               │
│  │   ├── Redis (6379)                                               │
│  │   └── Microservices (8080-8086)                                  │
│  ├── IDE (VS Code / IntelliJ)                                       │
│  └── Electron Dev Mode (npm run electron:dev)                       │
└─────────────────────────────────────────────────────────────────────┘
                                │
                                │ git push
                                ▼
                        ┌───────────────┐
                        │ GitHub (main) │
                        └───────┬───────┘
                                │
                                │ on tag push (v*)
                                ▼
                    ┌────────────────────────┐
                    │   GitHub Actions       │
                    │   (Build Multi-OS)     │
                    ├────────────────────────┤
                    │ - Windows (.exe)       │
                    │ - Linux (.AppImage)    │
                    │ - macOS (.dmg)         │
                    └────────────────────────┘
```

---

## Inicio Rápido

### 1. Clonar el Repositorio

```bash
git clone https://github.com/evertweb/programajava.git forestechOil
cd forestechOil
```

### 2. Iniciar Backend (Microservicios)

```bash
cd forestech-microservices

# Primera vez - construir imágenes
docker compose build

# Iniciar todos los servicios
docker compose up -d

# Verificar que estén corriendo
docker compose ps
```

**Servicios disponibles:**
- API Gateway: http://localhost:8080
- Consul UI: http://localhost:8500
- MySQL: localhost:3307

### 3. Iniciar Frontend (Desarrollo)

```bash
cd forestech-ui

# Instalar dependencias (solo primera vez)
npm install

# Modo desarrollo con Electron
npm run electron:dev
```

---

## Workflow Diario

### Iniciar el Día

```bash
# 1. Actualizar desde GitHub
git pull origin main

# 2. Iniciar servicios backend
cd forestech-microservices
docker compose up -d

# 3. Iniciar frontend en modo desarrollo
cd ../forestech-ui
npm run electron:dev
```

### Desarrollo

```bash
# Backend - Compilar un servicio tras cambios
cd forestech-microservices
docker compose up -d --build catalog-service

# Backend - Ver logs de un servicio
docker compose logs -f inventory-service

# Frontend - El Hot Reload está habilitado automáticamente
# Solo edita archivos en src/ y la app se recargará
```

### Fin del Día

```bash
# Guardar cambios
git add .
git commit -m "feat: descripción de cambios"
git push origin main

# Opcional: Detener servicios para liberar recursos
cd forestech-microservices
docker compose down
```

---

## Generar Ejecutable para Windows (.exe)

**Problema:** En WSL es muy complicado compilar ejecutables de Windows (.exe) porque requiere Wine y tiene muchas incompatibilidades.

**Solución:** Usar GitHub Actions para compilar automáticamente en Windows nativo.

### Proceso de Release

```bash
cd forestech-ui

# 1. Actualizar versión
npm version patch    # 0.0.2 → 0.0.3
# o
npm version minor    # 0.0.2 → 0.1.0

# 2. Commit y push
git add package.json package-lock.json
git commit -m "chore: bump version to v0.0.3"
git push origin main

# 3. Crear tag (esto dispara GitHub Actions)
git tag v0.0.3
git push origin v0.0.3
```

**¿Qué sucede automáticamente?**
1. GitHub Actions detecta el tag `v0.0.3`
2. Compila la aplicación en **Windows, Linux y macOS** en paralelo
3. Genera los instaladores:
   - `ForestechOil Setup 0.0.3.exe` (Windows)
   - `ForestechOil-0.0.3.AppImage` (Linux)
   - `ForestechOil-0.0.3.dmg` (macOS)
4. Los publica en: https://github.com/evertweb/programajava/releases

**Tiempo estimado:** 5-10 minutos

---

## Comandos Útiles

### Backend (Docker Compose)

```bash
cd forestech-microservices

# Ver estado de servicios
docker compose ps

# Ver logs en tiempo real
docker compose logs -f

# Ver logs de un servicio específico
docker compose logs -f catalog-service

# Rebuild completo (sin caché)
docker compose down
docker compose build --no-cache
docker compose up -d

# Reiniciar un servicio
docker compose restart fleet-service

# Acceder a MySQL
docker exec -it mysql-forestech mysql -u root -p'hp' FORESTECHOIL
```

### Frontend (Electron)

```bash
cd forestech-ui

# Desarrollo con hot reload
npm run electron:dev

# Build para navegador (solo pruebas)
npm run build

# Build para Linux (AppImage)
npm run electron:build:linux

# ⚠️ NO usar en WSL (problemas con Wine)
# npm run electron:build:win  
# Usar GitHub Actions en su lugar
```

---

## Solución de Problemas

### Los servicios no inician

```bash
# Ver qué salió mal
docker compose logs

# Reiniciar todo
docker compose down
docker compose up -d
```

### MySQL no responde

```bash
# Verificar salud del contenedor
docker exec mysql-forestech mysqladmin ping -u root -p'hp'

# Ver logs de MySQL
docker logs mysql-forestech --tail 50
```

### Electron no inicia en desarrollo

```bash
# Matar procesos huérfanos
pkill -f electron
pkill -f vite

# Limpiar e intentar de nuevo
cd forestech-ui
rm -rf node_modules package-lock.json
npm install
npm run electron:dev
```

### GitHub Actions falla al compilar

1. Ir a: https://github.com/evertweb/programajava/actions
2. Revisar los logs del workflow fallido
3. Verificar permisos: Settings → Actions → Workflow permissions → "Read and write permissions"

---

## Estructura de Directorios

```
forestechOil/
├── forestech-microservices/     # Backend
│   ├── services/                # Microservicios (8 servicios)
│   ├── infrastructure/          # Config server
│   └── docker-compose.yml       # Orquestación
│
├── forestech-ui/                # Frontend
│   ├── src/                     # Código React
│   ├── electron/                # Proceso principal Electron
│   └── package.json
│
├── .github/
│   └── workflows/
│       └── release.yml          # CI/CD para builds automáticos
│
└── docs/
    ├── LOCAL_DEVELOPMENT.md     # Esta guía
    └── ARQUITECTURA_UML.md
```

---

## Puertos de Servicios

| Puerto | Servicio | URL |
|--------|----------|-----|
| 8080 | API Gateway | http://localhost:8080 |
| 8081 | Catalog Service | http://localhost:8081 |
| 8082 | Fleet Service | http://localhost:8082 |
| 8083 | Inventory Service | http://localhost:8083 |
| 8084 | Partners Service | http://localhost:8084 |
| 8085 | Invoicing Service | http://localhost:8085 |
| 8086 | Reports Service | http://localhost:8086 |
| 8500 | Consul UI | http://localhost:8500 |
| 3307 | MySQL | localhost:3307 |
| 6379 | Redis | localhost:6379 |

---

## Preguntas Frecuentes

**P: ¿Por qué no generar el .exe localmente?**
R: En WSL compilar .exe requiere Wine y tiene muchos problemas. GitHub Actions tiene runners nativos de Windows, es más confiable y automático.

**P: ¿Puedo usar Windows nativo en vez de WSL?**
R: Sí, todos los comandos funcionan igual. Incluso podrías generar el .exe localmente con `npm run electron:build:win`.

**P: ¿Cómo actualizo las dependencias npm?**
R: `cd forestech-ui && npm update`

**P: ¿Cómo reseteo la base de datos?**
R: `docker compose down -v && docker compose up -d` (el flag `-v` elimina volúmenes)

---

**¿Necesitas ayuda?** Abre un issue en: https://github.com/evertweb/programajava/issues
