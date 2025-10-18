# 🌿 GIT LEARNING ROADMAP - Aprende Git desde Cero

> **Objetivo:** Dominar Git y GitHub para versionar tu código como un profesional.

> **Integración:** Aprenderás Git EN PARALELO con Java, aplicando control de versiones desde el primer día.

---

## 🎯 **¿POR QUÉ APRENDER GIT?**

### **Razones Críticas:**
1. **Historial de Cambios** - Puedes ver qué cambiaste y cuándo
2. **Deshacer Errores** - Si rompes algo, vuelves atrás
3. **Experimentar sin Miedo** - Creas ramas para probar cosas nuevas
4. **Colaboración** - Trabajar en equipo sin pisar código ajeno
5. **Portfolio** - Tu GitHub es tu currículum como desarrollador
6. **Estándar de la Industria** - TODAS las empresas usan Git

### **Tu Situación:**
> "Todo el proyecto Forestech lo crearon IAs y no sé nada de los sistemas"

**ESTO CAMBIA AHORA.** Aprenderás Git desde cero, comandos manuales, entendiendo QUÉ hace cada cosa.

---

## 📚 **FILOSOFÍA DE APRENDIZAJE GIT**

### **Aprender Git CON Java (No Separado):**

```
Día 1: Aprendes variables en Java
       → git add Main.java
       → git commit -m "Aprendí variables en Java"

Día 2: Aprendes clases en Java
       → git add Movement.java
       → git commit -m "Creé mi primera clase: Movement"

Día 3: Conectas a SQL Server
       → git add DatabaseConnection.java
       → git commit -m "Logré conectar a SQL Server con JDBC"
```

**Cada concepto de Java = Un commit de Git**

---

## 🗺️ **ROADMAP GIT - INTEGRADO CON JAVA**

### **FASE GIT-0: CONFIGURACIÓN INICIAL (Día 1)**

**Objetivo:** Instalar y configurar Git por primera vez

#### **Checkpoint Git-0.1: Verificar Instalación**
```bash
# Verificar si Git está instalado
git --version

# Si no está instalado (Ubuntu/Debian)
sudo apt update
sudo apt install git

# Verificar de nuevo
git --version
```

**Salida esperada:**
```
git version 2.43.0 (o superior)
```

---

#### **Checkpoint Git-0.2: Configuración Global**

**¿Qué es la configuración global?**
Git necesita saber QUIÉN hace los cambios (tu nombre y email).

```bash
# Configurar tu nombre (aparecerá en cada commit)
git config --global user.name "Tu Nombre"

# Configurar tu email
git config --global user.email "tu@email.com"

# Configurar editor por defecto (nano es más fácil para principiantes)
git config --global core.editor "nano"

# Verificar configuración
git config --list
```

**Conceptos:**
- `--global` = Configuración para TODOS tus proyectos
- `user.name` = Tu nombre (puede ser cualquiera, no necesita ser real)
- `user.email` = Tu email (puede ser falso si no tienes GitHub aún)

---

#### **Checkpoint Git-0.3: Inicializar Primer Repositorio**

**¿Qué es un repositorio?**
Es una carpeta que Git "vigila" para rastrear cambios.

```bash
# Navegar a tu carpeta de Java
cd ~/Documents/programajava

# Inicializar repositorio Git
git init

# Ver estado actual
git status
```

**Qué sucedió:**
```
Initialized empty Git repository in /home/hp/Documents/programajava/.git/
```

Se creó una carpeta oculta `.git/` donde Git guarda TODO el historial.

**Conceptos:**
- `git init` = Crear repositorio nuevo
- `git status` = Ver qué archivos cambiaron
- `.git/` = Carpeta oculta con toda la "magia" de Git

---

### **FASE GIT-1: COMANDOS BÁSICOS (Semana 1)**

**Objetivo:** Dominar los 5 comandos esenciales de Git

#### **Checkpoint Git-1.1: git status (Ver Estado)**

**El comando MÁS IMPORTANTE de Git.**

```bash
git status
```

**¿Qué muestra?**
- Archivos modificados (en rojo)
- Archivos preparados para commit (en verde)
- Rama actual
- Archivos sin seguimiento (untracked)

**Ejercicio:**
```bash
cd ~/Documents/programajava
git status

# Verás algo como:
# Untracked files:
#   INICIO.md
#   JAVA_LEARNING_ROADMAP.md
#   ...
```

**Significado:**
- **Untracked** = Git ve el archivo pero no lo está vigilando aún
- **Modified** = Archivo ya vigilado que cambió
- **Staged** = Archivo preparado para commit (agregado con `git add`)

---

#### **Checkpoint Git-1.2: git add (Preparar Cambios)**

**¿Qué hace `git add`?**
"Prepara" archivos para el commit (como poner cosas en una caja antes de cerrarla).

```bash
# Agregar UN archivo específico
git add INICIO.md

# Agregar TODOS los archivos
git add .

# Agregar archivos por patrón
git add *.md       # Todos los Markdown
git add src/*.java # Todos los Java en src/

# Ver qué agregaste
git status
```

**Analogía:**
```
Trabajando:        Staging Area:      Repositorio:
(tu código)        (git add)          (git commit)
   📝    ────────→    📦    ────────→    💾
   ↓                  ↓                  ↓
Editando           Preparando         Guardado
```

**Práctica:**
```bash
# Agregar archivo de roadmap
git add JAVA_LEARNING_ROADMAP.md

# Ver estado (estará en verde)
git status

# Agregar todos los demás
git add .

# Ver estado de nuevo
git status
```

---

#### **Checkpoint Git-1.3: git commit (Guardar Cambios)**

**¿Qué es un commit?**
Una "foto" de tu código en ese momento. Puedes volver a esa foto después.

```bash
# Commit con mensaje corto
git commit -m "Inicio del proyecto de aprendizaje Java"

# Commit con mensaje detallado
git commit -m "Agregada documentación inicial" -m "Incluye roadmap de Java y Git"

# Ver historial de commits
git log
```

**Anatomía de un buen mensaje de commit:**
```bash
# ❌ MAL
git commit -m "cambios"
git commit -m "fix"
git commit -m "asdfasdf"

# ✅ BIEN
git commit -m "Agregado archivo INICIO.md con guía de inicio"
git commit -m "Creada clase Movement con atributos básicos"
git commit -m "Configurada conexión a SQL Server con JDBC"
git commit -m "Implementado método createMovement en MovementService"
```

**Convención (aprenderás después):**
```bash
# Tipo: descripción
git commit -m "feat: Agregada funcionalidad de ENTRADA"
git commit -m "fix: Corregido error en cálculo de inventario"
git commit -m "docs: Actualizado README con instrucciones"
git commit -m "refactor: Mejorada clase Movement con encapsulamiento"
```

**Práctica TU PRIMER COMMIT:**
```bash
cd ~/Documents/programajava

# Agregar todos los archivos
git add .

# Ver qué vas a commitear
git status

# Hacer el commit
git commit -m "🎓 Inicio del proyecto de aprendizaje Java y Git"

# Ver el historial
git log
```

---

#### **Checkpoint Git-1.4: git log (Ver Historial)**

**¿Qué hace `git log`?**
Muestra todos los commits (fotos) que has hecho.

```bash
# Log completo
git log

# Log resumido (una línea por commit)
git log --oneline

# Log con gráfico de ramas
git log --oneline --graph --all

# Log de los últimos 5 commits
git log -5

# Log de UN archivo específico
git log INICIO.md
```

**Salida de ejemplo:**
```
commit a1b2c3d4e5f6 (HEAD -> main)
Author: Tu Nombre <tu@email.com>
Date:   Mon Oct 7 18:00:00 2025 -0500

    🎓 Inicio del proyecto de aprendizaje Java y Git
```

**Conceptos:**
- `commit a1b2c3d4e5f6` = ID único del commit (hash)
- `HEAD -> main` = Estás en la rama `main`, en el commit más reciente
- `Author` = Quién hizo el commit
- `Date` = Cuándo se hizo
- Mensaje = Descripción de qué cambió

---

#### **Checkpoint Git-1.5: git diff (Ver Diferencias)**

**¿Qué hace `git diff`?**
Muestra QUÉ cambió exactamente en los archivos.

```bash
# Ver cambios NO agregados (working directory vs staging)
git diff

# Ver cambios agregados (staging vs último commit)
git diff --staged

# Ver cambios de un archivo específico
git diff INICIO.md

# Ver cambios entre dos commits
git diff a1b2c3d HEAD
```

**Lectura de la salida:**
```diff
diff --git a/Main.java b/Main.java
index abc123..def456 100644
--- a/Main.java     (versión vieja)
+++ b/Main.java     (versión nueva)
@@ -1,3 +1,4 @@
 public class Main {
     public static void main(String[] args) {
-        System.out.println("Hello");
+        System.out.println("Hello World");
+        System.out.println("Learning Java!");
     }
 }
```

**Símbolos:**
- `-` = Línea eliminada (roja)
- `+` = Línea agregada (verde)
- Sin símbolo = Línea sin cambios

---

### **FASE GIT-2: DESHACER CAMBIOS (Semana 2)**

**Objetivo:** Aprender a revertir errores sin perder trabajo

#### **Checkpoint Git-2.1: git restore (Descartar Cambios)**

**Situación:** Editaste un archivo pero no te gustó. Quieres volver a cómo estaba.

```bash
# Descartar cambios de UN archivo (volver a último commit)
git restore INICIO.md

# Descartar TODOS los cambios
git restore .

# Quitar archivo del staging area (deshacer git add)
git restore --staged INICIO.md
```

**Ejemplo práctico:**
```bash
# 1. Editas Main.java y lo rompes
nano Main.java
# [Editas mal]

# 2. Ves que no funciona
# 3. Quieres volver a la versión anterior
git restore Main.java

# 4. Main.java vuelve a como estaba en el último commit
```

**⚠️ CUIDADO:**
`git restore` BORRA tus cambios permanentemente. Úsalo solo si estás seguro.

---

#### **Checkpoint Git-2.2: git reset (Deshacer Commits)**

**Situación:** Hiciste un commit pero era un error.

```bash
# Deshacer el último commit (pero mantener cambios)
git reset --soft HEAD~1

# Deshacer el último commit (descartar cambios)
git reset --hard HEAD~1

# Deshacer los últimos 3 commits
git reset --hard HEAD~3
```

**Tipos de reset:**
```
--soft  → Deshace commit, MANTIENE cambios en staging
--mixed → Deshace commit, mantiene cambios sin staging (default)
--hard  → Deshace commit, BORRA cambios (peligroso)
```

**Práctica segura:**
```bash
# Ver historial
git log --oneline

# Deshacer último commit (mantener cambios)
git reset --soft HEAD~1

# Ver estado
git status  # Los cambios siguen ahí

# Volver a commitear (corregido)
git commit -m "Mensaje corregido"
```

---

#### **Checkpoint Git-2.3: git revert (Revertir Commit Antiguo)**

**Situación:** Necesitas deshacer un commit viejo sin borrar el historial.

```bash
# Revertir commit específico
git revert a1b2c3d

# Revertir sin hacer commit automático
git revert --no-commit a1b2c3d
```

**Diferencia reset vs revert:**
```
git reset  → Borra commits del historial (reescribe historia)
git revert → Crea NUEVO commit que deshace cambios (mantiene historia)
```

**Cuándo usar cada uno:**
- `reset` = Solo si NO has publicado (pushed) el commit
- `revert` = Si ya publicaste el commit (más seguro)

---

### **FASE GIT-3: RAMAS (BRANCHES) (Semana 3)**

**Objetivo:** Trabajar en features sin romper código estable

#### **Checkpoint Git-3.1: ¿Qué es una Rama?**

**Analogía:**
```
main (rama principal - código estable)
  │
  ├─── feature-movement (rama para crear Movement)
  │
  └─── feature-database (rama para conectar BD)
```

Es como tener copias paralelas del proyecto donde experimentas sin afectar el original.

---

#### **Checkpoint Git-3.2: Crear y Cambiar Ramas**

```bash
# Ver ramas existentes
git branch

# Crear rama nueva
git branch feature-crear-clase-movement

# Cambiar a esa rama
git checkout feature-crear-clase-movement

# Crear Y cambiar en un solo comando (recomendado)
git checkout -b feature-crear-clase-movement

# Ver en qué rama estás
git branch
git status
```

**Flujo de trabajo con ramas:**
```bash
# 1. Estás en main
git checkout main

# 2. Creas rama para nueva feature
git checkout -b feature-entrada-movement

# 3. Trabajas en esa rama
# Editas archivos...
git add .
git commit -m "feat: Implementado movimiento ENTRADA"

# 4. Vuelves a main
git checkout main

# 5. Fusionas la feature
git merge feature-entrada-movement

# 6. Borras la rama (ya no la necesitas)
git branch -d feature-entrada-movement
```

---

#### **Checkpoint Git-3.3: Fusionar Ramas (git merge)**

```bash
# 1. Ir a la rama destino (usualmente main)
git checkout main

# 2. Fusionar rama de feature
git merge feature-entrada-movement

# 3. Si hay conflictos, resolverlos manualmente
# (Lo aprenderás después)
```

---

### **FASE GIT-4: GITHUB (Semana 4)**

**Objetivo:** Subir tu código a la nube y aprender colaboración

#### **Checkpoint Git-4.1: Crear Cuenta en GitHub**

1. Ve a https://github.com
2. Sign Up (Registrarse)
3. Verifica email
4. Configura perfil

---

#### **Checkpoint Git-4.2: Conectar Git Local con GitHub**

```bash
# Generar llave SSH (para autenticación)
ssh-keygen -t ed25519 -C "tu@email.com"

# Copiar llave pública
cat ~/.ssh/id_ed25519.pub

# Agregar a GitHub:
# Settings → SSH and GPG keys → New SSH key → Pegar
```

---

#### **Checkpoint Git-4.3: Crear Repositorio en GitHub**

1. GitHub → New Repository
2. Nombre: `aprendizaje-java`
3. Descripción: "Proyecto de aprendizaje Java desde cero"
4. Public o Private (tu elección)
5. NO inicializar con README (ya tienes uno)
6. Create Repository

---

#### **Checkpoint Git-4.4: git remote (Conectar Repositorios)**

```bash
# Ver remotos configurados
git remote -v

# Agregar remoto de GitHub
git remote add origin git@github.com:TU_USUARIO/aprendizaje-java.git

# Verificar
git remote -v
```

**Conceptos:**
- `origin` = Nombre convencional del repositorio remoto principal
- `git@github.com:...` = URL SSH de tu repo en GitHub

---

#### **Checkpoint Git-4.5: git push (Subir a GitHub)**

```bash
# Subir rama main por primera vez
git push -u origin main

# Subir después (más simple)
git push

# Subir rama específica
git push origin feature-movement

# Subir todas las ramas
git push --all
```

**Qué sucede:**
```
Local (tu PC)  →  GitHub (nube)
     📁      push      ☁️
```

---

#### **Checkpoint Git-4.6: git pull (Descargar de GitHub)**

```bash
# Descargar cambios desde GitHub
git pull

# Descargar de rama específica
git pull origin main
```

**Cuándo usar:**
- Si trabajas desde múltiples computadoras
- Si colaboras con otros
- Para sincronizar cambios

---

### **FASE GIT-5: GITIGNORE (Semana 5)**

**Objetivo:** Evitar subir archivos innecesarios o sensibles

#### **Checkpoint Git-5.1: Crear .gitignore**

**¿Qué es `.gitignore`?**
Archivo que le dice a Git QUÉ archivos ignorar (no versionar).

```bash
# Crear .gitignore
nano .gitignore
```

**Contenido para proyecto Java:**

```gitignore
# Archivos compilados
*.class
target/

# IDEs
../.idea/
.vscode/
*.iml

# Sistema operativo
.DS_Store
Thumbs.db

# Configuración sensible
config.properties
*.env
```

**Agregar y commitear:**
```bash
git add .gitignore
git commit -m "Agregado .gitignore para Java"
```

---

### **FASE GIT-6: COLABORACIÓN (Semana 6+)**

**Objetivo:** Trabajar en equipo con Git

#### **Checkpoint Git-6.1: Fork y Pull Request**

**Fork:** Copiar repositorio de alguien más a tu cuenta  
**Pull Request:** Proponer tus cambios al repositorio original

**Flujo:**
```
1. Fork del repo → Copia a tu GitHub
2. Clone a tu PC → git clone
3. Crear rama → git checkout -b mi-feature
4. Hacer cambios → git commit
5. Push a tu fork → git push origin mi-feature
6. Pull Request en GitHub → Proponer cambios
7. Review y Merge → Dueño acepta tus cambios
```

---

## 🎯 **INTEGRACIÓN GIT + JAVA: TU FLUJO DIARIO**

### **Día Típico de Aprendizaje:**

```bash
# 1. MAÑANA: Empiezas sesión de aprendizaje
cd ~/Documents/programajava
git status

# 2. Creas rama para lo que aprenderás hoy
git checkout -b dia-3-clases-y-objetos

# 3. APRENDES: Clase Movement en Java
# Creas src/main/java/models/Movement.java

# 4. COMMIT #1: Primera versión de la clase
git add src/main/java/models/Movement.java
git commit -m "Creada clase Movement con atributos básicos"

# 5. APRENDES: Agregar constructor
# Editas Movement.java

# 6. COMMIT #2: Agregaste constructor
git add src/main/java/models/Movement.java
git commit -m "Agregado constructor a Movement"

# 7. APRENDES: Getters y setters
# Editas Movement.java

# 8. COMMIT #3: Completaste la clase
git add src/main/java/models/Movement.java
git commit -m "Agregados getters y setters a Movement"

# 9. FIN DEL DÍA: Fusionas a main
git checkout main
git merge dia-3-clases-y-objetos

# 10. Actualizas log de aprendizaje
nano JAVA_LEARNING_LOG.md
git add JAVA_LEARNING_LOG.md
git commit -m "Actualizado log: Día 3 - Clases y Objetos"

# 11. Subes a GitHub
git push origin main

# 12. Borras rama del día (ya no la necesitas)
git branch -d dia-3-clases-y-objetos
```

---

## 📊 **PROGRESO GIT SUGERIDO**

| Semana Java | Fase Git | Habilidad Git |
|-------------|----------|---------------|
| Semana 1 | Git-0, Git-1 | init, add, commit, status, log |
| Semana 2 | Git-2 | restore, reset, revert |
| Semana 3 | Git-3 | branch, checkout, merge |
| Semana 4 | Git-4 | GitHub, remote, push, pull |
| Semana 5 | Git-5 | .gitignore, buenas prácticas |
| Semana 6+ | Git-6 | Colaboración, PR, fork |

---

## 🎓 **EJERCICIOS PRÁCTICOS GIT**

### **Ejercicio 1: Tu Primer Commit**
```bash
cd ~/Documents/programajava
git init
git add .
git commit -m "🎓 Inicio del proyecto de aprendizaje Java y Git"
git log
```

### **Ejercicio 2: Simular Error y Revertir**
```bash
# Edita un archivo mal a propósito
echo "BASURA" >> INICIO.md

# Ve el cambio
git diff

# Reviértelo
git restore INICIO.md

# Verifica que volvió a la normalidad
cat INICIO.md
```

### **Ejercicio 3: Trabajar con Ramas**
```bash
# Crear rama
git checkout -b practica-ramas

# Hacer cambio
echo "Practicando ramas" > practica.txt
git add practica.txt
git commit -m "Agregado archivo de práctica"

# Volver a main
git checkout main

# Fusionar
git merge practica-ramas

# Ver historial
git log --oneline --graph
```

---

## 📚 **RECURSOS DE GIT**

### **Documentación Oficial:**
- [Pro Git Book (Español)](https://git-scm.com/book/es/v2)
- [GitHub Docs](https://docs.github.com/es)

### **Tutoriales Interactivos:**
- [Learn Git Branching](https://learngitbranching.js.org/?locale=es_ES)
- [GitHub Skills](https://skills.github.com/)

### **Cheat Sheets:**
```bash
# Comandos esenciales
git init           # Iniciar repo
git status         # Ver estado
git add .          # Agregar todo
git commit -m ""   # Guardar cambios
git log            # Ver historial
git branch         # Ver ramas
git checkout -b    # Crear rama
git merge          # Fusionar ramas
git push           # Subir a GitHub
git pull           # Descargar de GitHub
```

---

## ✅ **CHECKLIST DE DOMINIO GIT**

### **Nivel Principiante:**
- [ ] Entiendo qué es Git y por qué usarlo
- [ ] Puedo hacer `git init`, `add`, `commit`
- [ ] Sé ver el historial con `git log`
- [ ] Puedo ver cambios con `git status` y `git diff`
- [ ] Entiendo la diferencia entre working directory, staging, y repositorio

### **Nivel Intermedio:**
- [ ] Puedo deshacer cambios con `restore` y `reset`
- [ ] Sé crear y cambiar entre ramas
- [ ] Puedo fusionar ramas con `merge`
- [ ] Entiendo qué son los conflictos (y cómo resolverlos)
- [ ] Uso `.gitignore` correctamente

### **Nivel Avanzado:**
- [ ] Tengo cuenta en GitHub
- [ ] Puedo conectar repo local con GitHub
- [ ] Sé hacer `push` y `pull`
- [ ] Puedo hacer Pull Requests
- [ ] Escribo buenos mensajes de commit
- [ ] Uso ramas para organizar mi trabajo

---

## 🎯 **TU PRÓXIMO PASO CON GIT**

**Ahora mismo:**
```bash
cd ~/Documents/programajava

# Inicializar Git
git init

# Ver estado
git status

# Agregar todos los archivos
git add .

# Ver qué agregaste
git status

# Hacer tu primer commit
git commit -m "🎓 Inicio del proyecto de aprendizaje Java y Git - Día 1"

# Ver el historial
git log

# Ver el commit en detalle
git show
```

**Después de esto:**
Lee `JAVA_NEXT_STEPS.md` para tu siguiente tarea de Java.

---

## 💡 **CONSEJOS DE ORO**

1. **Commitea Frecuentemente** - Cada concepto nuevo = 1 commit
2. **Mensajes Claros** - Tu yo del futuro te agradecerá
3. **Ramas Para Experimentar** - Nunca tengas miedo de romper algo
4. **`git status` es tu Mejor Amigo** - Úsalo constantemente
5. **Lee los Mensajes de Git** - Te dice qué hacer cuando algo falla

---

**¡Ahora dominarás Git Y Java al mismo tiempo! 🚀**

**Última actualización:** 7 de octubre de 2025
