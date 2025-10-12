# ▶️ JAVA NEXT STEPS - Siguiente Paso Específico

> **Actualizado:** 7 de octubre de 2025  
> **Fase Actual:** Fase 0 - Preparación  
> **Checkpoint Actual:** 0.1 - Verificar Entorno

---

## 🎯 **TU PRÓXIMA TAREA**

### **✅ PASO 1: Verificar Instalación de JDK y Maven**

**Objetivo:** Asegurarte de que tienes todas las herramientas necesarias instaladas y configuradas correctamente en tu sistema Linux.

---

## 📋 **INSTRUCCIONES DETALLADAS**

### **1. Abre una Terminal**

Puedes usar:
- Terminal de tu sistema (Ctrl+Alt+T en Ubuntu)
- Terminal integrada en VS Code (Ctrl+Shift+`)
- Terminal en IntelliJ IDEA (Alt+F12)

---

### **2. Verifica Java (JDK)**

Ejecuta estos comandos uno por uno:

```bash
# Verificar versión de Java
java -version

# Verificar compilador Java
javac -version

# Ver ruta de instalación
which java
which javac

# Verificar variable JAVA_HOME
echo $JAVA_HOME
```

---

### **3. Interpreta los Resultados**

#### **✅ Resultado Esperado (TODO BIEN):**

```
$ java -version
openjdk version "21.0.1" 2023-10-17 LTS
OpenJDK Runtime Environment (build 21.0.1+12-LTS)
OpenJDK 64-Bit Server VM (build 21.0.1+12-LTS, mixed mode)

$ javac -version
javac 21.0.1

$ echo $JAVA_HOME
/usr/lib/jvm/java-21-openjdk-amd64
```

**Si ves esto → ¡PERFECTO! Pasa al Paso 4.**

---

#### **❌ Resultado Problemático (NECESITA ARREGLO):**

**Caso A: `java: command not found`**
```
$ java -version
bash: java: command not found
```

**Solución:**
```bash
# Instalar OpenJDK 21
sudo apt update
sudo apt install openjdk-21-jdk -y

# Verificar de nuevo
java -version
```

---

**Caso B: Versión muy antigua (Java 8 o 11)**
```
$ java -version
openjdk version "11.0.x"
```

**Solución:**
```bash
# Instalar Java 21
sudo apt install openjdk-21-jdk -y

# Cambiar versión por defecto
sudo update-alternatives --config java
# Selecciona la opción con "java-21"

# Verificar cambio
java -version
```

---

**Caso C: JAVA_HOME no está configurado**
```
$ echo $JAVA_HOME
[vacío]
```

**Solución:**
```bash
# Editar archivo de configuración de bash
nano ~/.bashrc

# Agregar al FINAL del archivo:
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH

# Guardar: Ctrl+O, Enter, Ctrl+X

# Recargar configuración
source ~/.bashrc

# Verificar
echo $JAVA_HOME
```

---

### **4. Verifica Maven**

Ejecuta:

```bash
# Verificar versión de Maven
mvn -version
```

---

#### **✅ Resultado Esperado:**

```
$ mvn -version
Apache Maven 3.9.6
Maven home: /usr/share/maven
Java version: 21.0.1, vendor: Oracle Corporation
Java home: /usr/lib/jvm/java-21-openjdk-amd64
Default locale: en_US, platform encoding: UTF-8
OS name: "linux", version: "6.5.0-14-generic", arch: "amd64", family: "unix"
```

**Si ves esto → ¡EXCELENTE! Pasa al Paso 5.**

---

#### **❌ Resultado Problemático:**

**Caso A: `mvn: command not found`**

**Solución:**
```bash
# Instalar Maven
sudo apt update
sudo apt install maven -y

# Verificar instalación
mvn -version
```

---

### **5. Verifica Git**

```bash
# Verificar versión de Git
git --version

# Verificar configuración global
git config --global user.name
git config --global user.email
```

---

#### **✅ Resultado Esperado:**

```
$ git --version
git version 2.43.0

$ git config --global user.name
Tu Nombre

$ git config --global user.email
tu@email.com
```

**Si falta configuración:**
```bash
git config --global user.name "Tu Nombre"
git config --global user.email "tu@email.com"
```

---

### **6. Verifica Conexión a SQL Server**

```bash
# Verificar conectividad de red al servidor SQL
nc -zv 24.199.89.134 1433
```

#### **✅ Resultado Esperado:**

```
Connection to 24.199.89.134 1433 port [tcp/ms-sql-s] succeeded!
```

#### **❌ Si falla:**

Intenta con telnet:
```bash
# Si nc no está instalado, usa telnet
telnet 24.199.89.134 1433
```

O instala netcat:
```bash
sudo apt install netcat -y
nc -zv 24.199.89.134 1433
```

**Si aún falla:**
- ⚠️ Puede ser firewall local
- ⚠️ Puede ser restricción de red
- ⚠️ Puede necesitar VPN

**→ Comunica este resultado para que podamos ayudarte.**

---

## 📝 **REPORTE TUS RESULTADOS**

Una vez hayas ejecutado todos los comandos, responde:

### **Checklist de Verificación:**

```
[x] java -version muestra Java 17 o 21
[x] javac -version funciona
[x] echo $JAVA_HOME muestra una ruta
[x] mvn -version muestra Maven 3.8+
[x] git --version funciona
[x] Conexión a SQL Server (nc -zv) exitosa
[x] IntelliJ IDEA ya instalado (mencionaste que sí)
```

---

## 🎯 **DESPUÉS DE COMPLETAR ESTE PASO**

### **Si TODO está ✅:**

**Tu próximo paso será:**
- Crear la estructura del proyecto Java
- Inicializar proyecto Maven
- Configurar `pom.xml`

**→ Lee `JAVA_NEXT_STEPS.md` de nuevo (lo actualizaré con el siguiente paso)**

---

### **Si algo falla ❌:**

**Repórtame:**
1. ¿Qué comando ejecutaste?
2. ¿Qué salida te dio? (copia completa)
3. ¿Qué distribución de Linux usas? (Ubuntu/Fedora/Arch/Debian/etc.)

**→ Te ayudaré a solucionarlo específicamente para tu sistema**

---

## 💡 **ENTENDIENDO LO QUE ESTÁS VERIFICANDO**

### **¿Por qué necesito JDK?**
- **JDK** (Java Development Kit) incluye el compilador `javac` que convierte tu código `.java` en bytecode `.class`
- También incluye **JRE** (Java Runtime Environment) para ejecutar programas
- Sin JDK, no puedes compilar código Java (solo ejecutar)

### **¿Por qué necesito Maven?**
- **Maven** gestiona dependencias (librerías externas como JDBC driver)
- Automatiza la compilación de tu proyecto
- Es el estándar en proyectos Java enterprise
- Con un comando `mvn compile` compila todo tu proyecto

### **¿Por qué verifico JAVA_HOME?**
- **JAVA_HOME** es una variable de entorno que indica dónde está instalado Java
- Muchas herramientas (Maven, IntelliJ) buscan esta variable
- Sin ella, pueden fallar incluso si Java está instalado

### **¿Por qué verifico conexión a SQL Server?**
- Tu aplicación Java necesita conectarse a la base de datos en DigitalOcean
- Si no hay conectividad de red, el proyecto no funcionará
- Es mejor detectar problemas de red AHORA antes de escribir código

---

## 🆘 **PROBLEMAS COMUNES**

### **"Tengo Java 11 pero quiero Java 21"**

**Solución corta:**
```bash
sudo apt install openjdk-21-jdk
sudo update-alternatives --config java  # Selecciona java-21
```

---

### **"Maven no encuentra JAVA_HOME"**

**Solución:**
```bash
# Editar ~/.bashrc
nano ~/.bashrc

# Agregar:
export JAVA_HOME=$(dirname $(dirname $(readlink -f $(which java))))
export PATH=$JAVA_HOME/bin:$PATH

# Recargar
source ~/.bashrc
```

---

### **"No puedo conectar a SQL Server"**

**Diagnóstico:**
```bash
# Verificar si el puerto está bloqueado
sudo ufw status

# Permitir conexiones salientes
sudo ufw allow out 1433/tcp

# Verificar conectividad básica
ping 24.199.89.134
```

**Si sigue fallando:**
- Verifica si tu red permite conexiones a ese puerto
- Algunas redes corporativas/universitarias bloquean puertos de bases de datos
- Puede necesitar VPN o hablar con administrador de red

---

## ✅ **CUANDO TERMINES**

**Marca este paso como completado:**

1. Actualiza `JAVA_LEARNING_LOG.md`:
   ```markdown
   ### **Fase 0: Preparación**
   - [x] Checkpoint 0.1: Verificar Entorno
     - [x] JDK instalado y verificado
     - [x] Maven instalado y verificado
     - [x] Git configurado
   ```

2. **Avisa que terminaste:**
   - "✅ Completé la verificación del entorno. JDK 21, Maven 3.9.6, todo funciona."

3. **Pediré el siguiente paso:**
   - "¿Cuál es mi próxima tarea?"
   - → Te daré el siguiente paso específico

---

## 🎓 **LO QUE APRENDERÁS CON ESTE PASO**

Al completar esta verificación, habrás aprendido:
- ✅ Qué es JDK y por qué lo necesitas
- ✅ Diferencia entre `java` (ejecutar) y `javac` (compilar)
- ✅ Qué es Maven y su rol en proyectos Java
- ✅ Importancia de variables de entorno (JAVA_HOME)
- ✅ Verificar conectividad de red con herramientas de terminal

**Estos conocimientos son fundamentales para cualquier desarrollador Java.**

---

## 📚 **RECURSOS DE APOYO**

Si quieres profundizar:
- [JDK vs JRE vs JVM Explained](https://docs.oracle.com/javase/8/docs/)
- [Maven in 5 Minutes](https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html)
- [Linux Environment Variables](https://www.baeldung.com/linux/bashrc-vs-bash-profile-vs-profile)

---

**¡Estás dando tu primer paso en Java! 🚀**

**Tiempo estimado para este paso:** 10-15 minutos

**Próximo archivo a consultar después de completar:** Este mismo archivo se actualizará con el siguiente paso.
