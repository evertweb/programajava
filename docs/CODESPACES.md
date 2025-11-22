# GitHub Codespaces - Guía de Desarrollo

Esta guía describe cómo usar GitHub Codespaces como entorno principal de desarrollo para ForestechOil.

## Arquitectura del Entorno

```
┌─────────────────────────────────────────────────────────────────────┐
│                        GITHUB CODESPACES                            │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  Dev Container (Ubuntu 22.04)                                │    │
│  │  ├── Java 17 + Maven                                         │    │
│  │  ├── Node.js 20 + npm                                        │    │
│  │  └── Docker-in-Docker                                        │    │
│  │       └── docker-compose                                     │    │
│  │            ├── Consul (8500)                                 │    │
│  │            ├── MySQL (3307)                                  │    │
│  │            ├── Redis (6379)                                  │    │
│  │            └── Microservices (8080-8086)                     │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                              │                                       │
│                     Port Forwarding                                  │
│                              │                                       │
└──────────────────────────────┼──────────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────────────┐
│                         PC LOCAL (WSL)                              │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  JetBrains Gateway → IntelliJ IDEA (UI remota)              │    │
│  ├─────────────────────────────────────────────────────────────┤    │
│  │  Frontend Testing:                                           │    │
│  │  └── forestech-ui (npm run dev → localhost:5173)            │    │
│  │       └── Conecta a backend LOCAL (docker-compose)          │    │
│  └─────────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────────┘
```

## Requisitos del Codespace

| Recurso | Mínimo | Recomendado |
|---------|--------|-------------|
| CPU | 4 cores | 4 cores |
| RAM | 16 GB | 16 GB |
| Storage | 32 GB | 64 GB |

**Costo estimado:** ~$0.36/hora (4-core) | ~$58-115/mes (uso moderado)

---

## Inicio Rápido

### 1. Crear un Codespace

```
GitHub → Repo → Code → Codespaces → Create codespace on main
```

O desde CLI:
```bash
gh codespace create -r evertweb/programajava -b main --machine standardLinux32gb
```

### 2. Esperar la Inicialización

El Codespace ejecutará automáticamente:
1. `post-create.sh` - Descarga dependencias Maven/npm
2. `post-start.sh` - Inicia Consul, MySQL, Redis

**Tiempo estimado:** 2-5 minutos (con prebuild) | 10-15 minutos (sin prebuild)

### 3. Verificar Servicios

```bash
# Ver contenedores activos
docker compose ps

# Verificar salud
health

# Ver logs
logs
```

---

## Configuración de JetBrains Gateway

### Instalación

1. Descargar JetBrains Gateway: https://www.jetbrains.com/remote-development/gateway/
2. Instalar y abrir

### Conexión a Codespaces

1. **Abrir Gateway** → "New Connection"
2. Seleccionar **"GitHub Codespaces"**
3. **Autenticarse** con tu cuenta de GitHub
4. Seleccionar el Codespace activo
5. Elegir **"IntelliJ IDEA"** como IDE backend
6. Esperar descarga del IDE remoto (~2 min primera vez)

### Configuración Recomendada

Una vez conectado a IntelliJ:

```
Settings → Build, Execution, Deployment → Build Tools → Maven
  → Maven home: /usr/share/maven
  → User settings file: /home/vscode/.m2/settings.xml

Settings → Build, Execution, Deployment → Compiler → Java Compiler
  → Project bytecode version: 17
```

### Troubleshooting Gateway

| Problema | Solución |
|----------|----------|
| Conexión lenta | Verificar que el Codespace tenga 4+ cores |
| IDE no responde | Reiniciar Codespace: `gh codespace restart` |
| No encuentra Maven | Ejecutar `source ~/.bashrc` en terminal |

---

## Workflow Diario

### Inicio del Día

```bash
# 1. Abrir Codespace (GitHub web o CLI)
gh codespace list
gh codespace code -c <codespace-name>  # Abre en VS Code
# O conectar vía JetBrains Gateway

# 2. Verificar que los servicios estén activos
docker compose ps
health

# 3. Si los servicios no están corriendo:
start-all
```

### Desarrollo

```bash
# Compilar un servicio específico
rebuild-service inventory-service --skip-tests

# Compilar todos los servicios
rebuild-all --skip-tests

# Ver logs de un servicio
docker compose logs -f inventory-service

# Acceder a MySQL
docker exec -it mysql-forestech mysql -u root -p'forestech_root_2024' FORESTECHOIL
```

### Sincronización con Local

```bash
# En Codespaces: Guardar cambios
git add .
git commit -m "feat: nueva funcionalidad"
git push origin main

# En PC local (WSL): Sincronizar
forestech-sync
# O manualmente:
cd ~/forestechOil
git pull origin main
cd forestech-microservices
docker compose down && docker compose up -d --build
```

### Fin del Día

```bash
# Guardar todo
git add . && git commit -m "wip: progreso del día" && git push

# El Codespace se suspende automáticamente después de 30 min de inactividad
# Para suspender manualmente:
gh codespace stop
```

---

## Comandos Útiles (Aliases)

Estos aliases se configuran automáticamente en el Codespace:

| Alias | Comando | Descripción |
|-------|---------|-------------|
| `start-all` | `docker compose up -d` | Iniciar todos los servicios |
| `stop-all` | `docker compose down` | Detener todos los servicios |
| `rebuild-all` | Script personalizado | Recompilar todos los microservicios |
| `rebuild-service` | Script personalizado | Recompilar un servicio específico |
| `health` | Script health-check | Verificar salud de servicios |
| `logs` | `docker compose logs -f` | Ver logs de todos los servicios |
| `ui-dev` | `npm run dev` | Iniciar frontend en modo desarrollo |
| `cdm` | `cd forestech-microservices` | Ir a directorio de microservicios |
| `cdu` | `cd forestech-ui` | Ir a directorio de frontend |

---

## Puertos Reenviados

| Puerto | Servicio | Acceso desde PC |
|--------|----------|-----------------|
| 8080 | API Gateway | http://localhost:8080 |
| 8500 | Consul UI | http://localhost:8500 |
| 5173 | Vite Dev Server | http://localhost:5173 |
| 3307 | MySQL | localhost:3307 |
| 6379 | Redis | localhost:6379 |

**Nota:** En Codespaces, los puertos se reenvían automáticamente. Puedes acceder desde tu navegador local usando las URLs de Codespaces (ej: `https://tu-codespace-8080.app.github.dev`).

---

## Pruebas del Frontend en PC Local

El frontend no puede conectarse directamente al backend de Codespaces por limitaciones de red. Por eso, usamos el PC local como servidor de pruebas:

### Workflow de Pruebas

```bash
# 1. En Codespaces: Hacer push
git push origin main

# 2. En PC local: Sincronizar
cd ~/forestechOil
bash scripts/local-sync.sh

# 3. En PC local: Iniciar frontend
cd forestech-ui
npm run dev

# 4. Abrir navegador: http://localhost:5173
```

### Configuración del Frontend

El frontend usa la variable de entorno `VITE_API_URL`:

```bash
# Crear archivo .env.local en forestech-ui/
VITE_API_URL=http://localhost:8080/api
```

---

## Prebuilds

Los prebuilds reducen el tiempo de inicio del Codespace pre-descargando dependencias.

### Activar Prebuilds

1. GitHub → Settings → Codespaces
2. "Set up prebuild" → Branch: `main`
3. Triggers: "On push" + "On configuration change"

### Verificar Estado

```bash
# Ver workflows de prebuild
gh run list --workflow=codespaces-prebuild.yml
```

---

## Buenas Prácticas

### Do's ✅

- **Commits frecuentes:** Hacer push cada 1-2 horas
- **Usar prebuilds:** Ahorra 5-10 min en cada inicio
- **Suspender cuando no uses:** Ahorra costos
- **Sincronizar antes de editar en local:** Evita conflictos

### Don'ts ❌

- **No editar en local y Codespaces simultáneamente**
- **No hacer push con servicios rotos**
- **No dejar Codespaces activos toda la noche**
- **No ignorar el prebuild workflow**

---

## Solución de Problemas

### El Codespace tarda mucho en iniciar

```bash
# Verificar si hay prebuild
gh codespace list

# Forzar rebuild
gh codespace rebuild
```

### Los servicios no inician

```bash
# Verificar Docker
docker info

# Reiniciar servicios
stop-all
start-all

# Ver logs de error
docker compose logs mysql-forestech
```

### JetBrains Gateway no conecta

1. Verificar que el Codespace esté activo
2. Cerrar Gateway y reconectar
3. Reiniciar el Codespace: `gh codespace restart`

### MySQL no responde

```bash
# Verificar estado
docker exec mysql-forestech mysqladmin ping -u root -p'forestech_root_2024'

# Reiniciar MySQL
docker compose restart mysql-forestech

# Ver logs
docker logs mysql-forestech --tail 50
```

---

## Costos y Optimización

### Estimación de Costos (4-core / 16GB)

| Uso | Horas/mes | Costo aprox. |
|-----|-----------|--------------|
| Light (4h/día, 5 días) | 80h | ~$29 |
| Moderate (8h/día, 5 días) | 160h | ~$58 |
| Heavy (8h/día, 7 días) | 224h | ~$81 |

### Tips para Reducir Costos

1. **Usar prebuilds:** Reduce tiempo activo
2. **Auto-suspend:** Configurar a 30 min
3. **Detener manualmente:** Cuando no uses
4. **Usar 2-core para tareas ligeras:** Solo cuando sea posible

---

## Referencias

- [GitHub Codespaces Documentation](https://docs.github.com/en/codespaces)
- [JetBrains Gateway Documentation](https://www.jetbrains.com/help/idea/remote-development-starting-page.html)
- [Dev Containers Specification](https://containers.dev/)
