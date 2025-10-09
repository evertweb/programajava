# 🛠️ JAVA PROJECT SETUP - Guía de Instalación

> **Objetivo:** Verificar y configurar todo lo necesario para programar en Java en Linux.

---

## ✅ **CHECKLIST RÁPIDO**

Antes de empezar, verifica que tienes:
- [ ] JDK 17 o 21 instalado
- [ ] Maven instalado
- [ ] IntelliJ IDEA configurado (ya lo tienes ✅)
- [ ] VS Code con extensiones Java
- [ ] Git configurado
- [ ] Conexión a SQL Server verificada

---

## 📦 **PASO 1: VERIFICAR JDK**

### **¿Qué es JDK?**
- **JDK (Java Development Kit)**: Herramientas para compilar y ejecutar programas Java
- **JRE (Java Runtime Environment)**: Solo ejecuta programas, no compila
- **JVM (Java Virtual Machine)**: "Motor" que ejecuta bytecode Java

**Necesitas JDK (no solo JRE) porque vas a programar.**

### **Verificar Instalación:**
```bash
# Verificar versión de Java
java -version

# Verificar compilador Java
javac -version

# Ver dónde está instalado
which java
which javac
```

**Salida Esperada:**
```
java version "21.0.x" 2024-xx-xx LTS
Java(TM) SE Runtime Environment (build ...)
Java HotSpot(TM) 64-Bit Server VM (build ...)
```

**¿Versión recomendada?**
- ✅ Java 21 (LTS - Long Term Support) - **Más reciente**
- ✅ Java 17 (LTS) - **Muy usado en empresas**
- ⚠️ Java 11 (LTS) - **Funciona pero viejo**

### **Si NO tienes JDK instalado:**

#### **Opción 1: OpenJDK (Recomendado - Gratis y Open Source)**
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install openjdk-21-jdk

# Fedora
sudo dnf install java-21-openjdk-devel

# Arch Linux
sudo pacman -S jdk-openjdk
```

#### **Opción 2: Oracle JDK (Oficial pero requiere cuenta)**
```bash
# Descargar desde:
# https://www.oracle.com/java/technologies/downloads/#java21

# Descomprimir y configurar JAVA_HOME
tar -xvf jdk-21_linux-x64_bin.tar.gz
sudo mv jdk-21 /usr/lib/jvm/
```

### **Configurar JAVA_HOME:**
```bash
# Editar ~/.bashrc o ~/.zshrc
nano ~/.bashrc

# Agregar al final:
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH

# Recargar configuración
source ~/.bashrc

# Verificar
echo $JAVA_HOME
```

---

## 📦 **PASO 2: INSTALAR MAVEN**

### **¿Qué es Maven?**
Maven es un **gestor de dependencias y builds** para Java. Hace 3 cosas principales:
1. **Descarga librerías** automáticamente (JDBC driver, logging, etc.)
2. **Compila tu proyecto** con un solo comando: `mvn compile`
3. **Gestiona estructura** estándar de proyectos Java

**Alternativa:** Gradle (más moderno, pero Maven es estándar en empresas)

### **Verificar si está instalado:**
```bash
mvn -version
```

**Salida Esperada:**
```
Apache Maven 3.9.x
Maven home: /usr/share/maven
Java version: 21.0.x, vendor: Oracle Corporation
```

### **Si NO está instalado:**

#### **Opción 1: Desde Package Manager (Fácil)**
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install maven

# Fedora
sudo dnf install maven

# Arch Linux
sudo pacman -S maven
```

#### **Opción 2: Descarga Manual (Última versión)**
```bash
# Descargar Maven
cd /tmp
wget https://dlcdn.apache.org/maven/maven-3/3.9.9/binaries/apache-maven-3.9.9-bin.tar.gz

# Descomprimir
tar -xvf apache-maven-3.9.9-bin.tar.gz
sudo mv apache-maven-3.9.9 /opt/maven

# Configurar PATH
nano ~/.bashrc

# Agregar:
export M2_HOME=/opt/maven
export PATH=$M2_HOME/bin:$PATH

# Recargar
source ~/.bashrc

# Verificar
mvn -version
```

---

## 🔧 **PASO 3: CONFIGURAR INTELLIJ IDEA**

### **Ya tienes IntelliJ instalado ✅**

### **Verificar configuración:**

1. **Abrir IntelliJ IDEA**
2. **File → Project Structure** (Ctrl+Alt+Shift+S)
3. **Project Settings → Project:**
   - **SDK:** Debe aparecer tu JDK (Java 21 o 17)
   - Si no aparece: Click en "Add SDK" → "JDK" → Navega a `/usr/lib/jvm/java-21-openjdk-amd64`

4. **Settings → Build, Execution, Deployment → Build Tools → Maven:**
   - **Maven home path:** Debe detectar `/usr/share/maven` automáticamente
   - **User settings file:** `~/.m2/settings.xml` (se crea automáticamente)

### **Plugins Recomendados para IntelliJ:**
- ✅ **Maven Helper** - Gestión visual de dependencias
- ✅ **Database Tools** - Ver SQL Server desde IntelliJ
- ✅ **GitToolBox** - Mejor integración con Git
- ✅ **Rainbow Brackets** - Colorea paréntesis (muy útil)

**Instalar plugins:**
- File → Settings → Plugins → Marketplace → Buscar y instalar

---

## 🔧 **PASO 4: CONFIGURAR VS CODE (Opcional pero útil)**

### **¿Por qué usar VS Code también?**
- Edición rápida de archivos markdown/SQL
- Ya lo usas para el proyecto React
- Puedes tener IntelliJ para Java y VS Code para todo lo demás

### **Extensiones Necesarias:**

```bash
# Instalar Extension Pack for Java (incluye todo lo esencial)
code --install-extension vscjava.vscode-java-pack

# Otras extensiones útiles
code --install-extension vscjava.vscode-maven
code --install-extension redhat.java
code --install-extension vscjava.vscode-java-debug
code --install-extension vscjava.vscode-java-dependency
```

**O instalar manualmente:**
1. Abrir VS Code
2. Extensiones (Ctrl+Shift+X)
3. Buscar: **"Extension Pack for Java"**
4. Instalar (incluye 6 extensiones en una)

### **Configurar Java en VS Code:**

**Abrir Command Palette (Ctrl+Shift+P):**
```
Java: Configure Java Runtime
```

**Seleccionar tu JDK:**
- Debe detectar `/usr/lib/jvm/java-21-openjdk-amd64`

---

## 📦 **PASO 5: SQL SERVER JDBC DRIVER**

### **¿Qué es JDBC Driver?**
Es la "librería" que permite a Java conectarse a SQL Server. Es como un puente:

```
Java App → JDBC Driver → SQL Server
```

### **NO necesitas descargarlo manualmente**
Maven lo descargará automáticamente cuando crees el `pom.xml` con esta dependencia:

```xml
<dependency>
    <groupId>com.microsoft.sqlserver</groupId>
    <artifactId>mssql-jdbc</artifactId>
    <version>12.8.1.jre11</version>
</dependency>
```

**Maven hace la magia:**
1. Lee `pom.xml`
2. Descarga `mssql-jdbc-12.8.1.jar` desde Maven Central
3. Lo guarda en `~/.m2/repository/`
4. Lo agrega al classpath de tu proyecto

---

## 🧪 **PASO 6: VERIFICAR CONEXIÓN A SQL SERVER**

### **Probar conexión desde terminal:**

```bash
# Verificar conectividad de red
ping 24.199.89.134

# Verificar puerto SQL Server (1433)
nc -zv 24.199.89.134 1433
# O con telnet:
telnet 24.199.89.134 1433
```

**Salida Esperada:**
```
Connection to 24.199.89.134 1433 port [tcp/ms-sql-s] succeeded!
```

**Si falla:**
- ⚠️ Verifica que tu firewall permite conexiones salientes al puerto 1433
- ⚠️ Verifica que DigitalOcean permite tu IP

---

## 📁 **PASO 7: ESTRUCTURA DEL PROYECTO**

### **Directorio del Proyecto:**
```bash
cd /home/hp/Documents/forestech
mkdir forestech-cli-java
cd forestech-cli-java
```

### **Estructura Estándar Maven:**
```
forestech-cli-java/
├── pom.xml                          # Configuración Maven
├── README.md                        # Documentación del proyecto
├── src/
│   ├── main/
│   │   ├── java/                   # Código fuente Java
│   │   │   ├── Main.java           # Punto de entrada
│   │   │   ├── config/
│   │   │   │   └── DatabaseConnection.java
│   │   │   ├── models/
│   │   │   │   ├── Movement.java
│   │   │   │   ├── Vehicle.java
│   │   │   │   ├── Supplier.java
│   │   │   │   └── Product.java
│   │   │   ├── services/
│   │   │   │   ├── MovementService.java
│   │   │   │   ├── VehicleService.java
│   │   │   │   ├── SupplierService.java
│   │   │   │   └── InventoryService.java
│   │   │   ├── ui/
│   │   │   │   └── ConsoleMenu.java
│   │   │   └── exceptions/
│   │   │       └── InsufficientInventoryException.java
│   │   └── resources/              # Archivos de configuración
│   │       ├── config.properties
│   │       └── logback.xml
│   └── test/
│       └── java/                   # Tests (después)
│           └── MovementServiceTest.java
└── target/                          # Archivos compilados (Maven lo crea)
```

**Importante:**
- ✅ `src/main/java/` contiene TODO tu código
- ✅ Cada carpeta (config, models, services) es un **paquete** (package)
- ✅ `target/` lo crea Maven automáticamente al compilar (git ignore esto)

---

## 📝 **PASO 8: CREAR POM.XML INICIAL**

El archivo `pom.xml` es el "corazón" del proyecto Maven. Define:
- Información del proyecto (nombre, versión)
- Dependencias (JDBC, logging, etc.)
- Plugins (cómo compilar, empaquetar)

**Este archivo lo crearemos juntos en el siguiente paso.**

---

## ✅ **VERIFICACIÓN FINAL**

### **Ejecuta estos comandos para verificar TODO:**

```bash
# Java
java -version
javac -version
echo $JAVA_HOME

# Maven
mvn -version

# Git
git --version

# Conectividad SQL Server
nc -zv 24.199.89.134 1433

# IntelliJ IDEA (abrir desde terminal)
idea.sh &  # O el comando que uses para abrir IntelliJ
```

### **Checklist Final:**
- [ ] `java -version` muestra Java 17 o 21
- [ ] `javac -version` muestra el compilador
- [ ] `mvn -version` muestra Maven 3.8+
- [ ] IntelliJ abre correctamente
- [ ] VS Code tiene Extension Pack for Java
- [ ] Conexión a SQL Server funciona (nc o telnet)

---

## 🎯 **TROUBLESHOOTING COMÚN**

### **Problema: `java: command not found`**
**Solución:**
```bash
# Verificar si está instalado
ls /usr/lib/jvm/

# Si existe pero no está en PATH
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH

# Agregar permanentemente a ~/.bashrc
echo 'export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64' >> ~/.bashrc
echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.bashrc
source ~/.bashrc
```

---

### **Problema: `mvn: command not found`**
**Solución:**
```bash
# Instalar Maven
sudo apt install maven

# O verificar PATH
export PATH=/opt/maven/bin:$PATH
```

---

### **Problema: IntelliJ no encuentra JDK**
**Solución:**
1. File → Project Structure
2. Platform Settings → SDKs
3. Click "+" → Add JDK
4. Navega a `/usr/lib/jvm/java-21-openjdk-amd64`

---

### **Problema: No puedo conectar a SQL Server**
**Solución:**
```bash
# Verificar firewall
sudo ufw status
sudo ufw allow out 1433/tcp

# Verificar conectividad
telnet 24.199.89.134 1433

# Si usa VPN, verificar que esté activa
```

---

## 📚 **RECURSOS ADICIONALES**

### **Documentación Oficial:**
- [OpenJDK Installation Guide](https://openjdk.org/install/)
- [Maven Getting Started](https://maven.apache.org/guides/getting-started/)
- [IntelliJ IDEA Documentation](https://www.jetbrains.com/help/idea/)

### **Tutoriales de Setup:**
- [How to Install Java on Linux](https://www.digitalocean.com/community/tutorials/how-to-install-java-with-apt-on-ubuntu-22-04)
- [Maven in 5 Minutes](https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html)

---

## ✅ **PRÓXIMO PASO**

Una vez que hayas verificado TODO lo anterior:

1. ✅ Lee `JAVA_AGENTS_INSTRUCTIONS.md` para entender cómo trabajar con IAs
2. ✅ Lee `JAVA_NEXT_STEPS.md` para ver tu siguiente tarea concreta
3. ✅ Abre IntelliJ y prepárate para crear tu primer proyecto Java

---

## 🆘 **¿NECESITAS AYUDA?**

Si algo no funciona, dime:
1. **¿Qué comando ejecutaste?**
2. **¿Qué error te dio?** (copia el mensaje completo)
3. **¿Qué sistema operativo usas?** (Ubuntu, Fedora, Arch, etc.)
4. **¿Versión de Java?** (`java -version`)

**¡Estoy aquí para ayudarte! 🚀**
