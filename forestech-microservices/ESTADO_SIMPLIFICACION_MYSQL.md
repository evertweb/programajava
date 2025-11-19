# Estado Actual - Simplificación de Arquitectura MySQL

## Problema Actual

Los servicios `catalog-service` y `fleet-service` están crasheando al intentar conectarse al contenedor MySQL compartido.

### Diagnóstico
- ✅ MySQL contenedor: **healthy** y funcionando (puerto 3307)
- ✅ Consul: **UP**
- ✅ Config Server: **UP**
- ❌ catalog-service: **Exited (1)** - Connection refused
- ❌ fleet-service: **Exited (1)** - Connection refused

### Log Error
```
Caused by: java.net.ConnectException: Connection refused
Caused by: com.mysql.cj.exceptions.CJCommunicationsException: Communications link failure
```

## Causa Raíz

**Timing Issue**: Los servicios se inician inmediatamente después de que MySQL container está "healthy", pero MySQL aún no está aceptando conexiones en el puerto 3306 interno.

El healthcheck de MySQL verifica solo que `mysqladmin ping` funcione, pero no garantiza que el puerto esté listo para conexiones externas desde otros contenedores.

## Opciones de Solución

### Opción A: Agregar Retry Logic (RECOMENDADO)
**Qué hacer:**
- Agregar configuración Spring Boot para reintentar conexión MySQL
- `spring.datasource.hikari.connection-timeout=60000`
- `spring.datasource.hikari.initialization-fail-timeout=60000`

**Pros:**
- Solución rápida (5 min)
- Arquitectura simplificada mantenida (1 BD)
- Patrón robusto para microservicios

**Contras:**
- Servicios tardarán más en iniciar primera vez

### Opción B: Volver a 5 MySQL Containers
**Qué hacer:**
- Revertir a arquitectura original que funcionaba
- 5 contenedores MySQL independientes

**Pros:**
- Funcionaba correctamente antes
- Cada servicio BD independiente

**Contras:**
- \u003e1GB RAM de overhead
- Complejidad operacional alta
- NO es lo que el usuario quiere

### Opción C: Wait Script en Services
**Qué hacer:**
- Agregar `wait-for-it.sh` script en Dockerfile
- Servicios esperan hasta que MySQL:3306 responda

**Pros:**
- Control preciso de timing
- Arquitectura simplificada mantenida

**Contras:**
- Requiere rebuild de imágenes
- Script adicional a mantener

## Recomendación

**Opción A** es la mejor: agregar retry logic en Spring Boot.

Es la solución más robusta y profesional para microservicios en producción.

## Próximos Pasos

Esperar decisión del usuario sobre qué opción implementar.
