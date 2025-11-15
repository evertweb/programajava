# 🚀 Forestech - Guía Rápida

## ✨ TODO EN 4 PASOS

### 1️⃣ Escribe tu código
```bash
# Edita tus archivos Java en VS Code
```

### 2️⃣ Ejecuta EL SCRIPT MAESTRO
```bash
cd /home/hp/forestechOil/forestech-cli-java/build-scripts
./build.sh
```

### 3️⃣ En Windows: Launch4j
```
1. Abre Launch4j
2. File → Load Config → C:\forestech-build\forestech.xml
3. Click en ⚙️ Build wrapper
```

### 4️⃣ Ejecuta tu aplicación
```
Doble click en: C:\forestech-build\ForestechApp.exe
```

---

## 📋 Archivos Importantes

- **`build.sh`** ← EL ÚNICO SCRIPT QUE NECESITAS
- **`launch4j-config.xml`** ← Configuración para Launch4j

---

## 🎯 Flujo Completo

```
┌─────────────────────────────────────────┐
│ 1. Editas código en WSL                 │
│    ↓                                    │
│ 2. ./build.sh                           │
│    ├─ Compila con Maven                 │
│    └─ Copia a C:\forestech-build\       │
│    ↓                                    │
│ 3. Launch4j → Build wrapper             │
│    ↓                                    │
│ 4. Ejecutas ForestechApp.exe            │
└─────────────────────────────────────────┘
```

---

## 🆘 Ayuda

### Error: "Permission denied"
```bash
chmod +x build.sh
```

### Error: "Maven not found"
```bash
sudo apt install maven
```

### Error: Launch4j no encuentra el JAR
```
Verifica que exista: C:\forestech-build\forestech-app.jar
```

---

## 🎓 Primera vez usando Launch4j

1. **Descarga Launch4j:** https://launch4j.sourceforge.net/
2. **Instala** en Windows
3. **Ejecuta `./build.sh`** (copia el XML automáticamente)
4. **Abre Launch4j** y carga `C:\forestech-build\forestech.xml`
5. **Build wrapper**
6. ¡Listo!

Las siguientes veces solo necesitas **pasos 4-5** (10 segundos) ⚡

---

**¡Eso es todo! Simple y directo.** 🎉
