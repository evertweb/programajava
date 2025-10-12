# 🎓 JAVA LEARNING ROADMAP - Forestech CLI Project

> **Objetivo:** Aprender Java desde cero construyendo un sistema CLI real de gestión de combustibles conectado a SQL Server.

---

## 📚 **FILOSOFÍA DE APRENDIZAJE**

### **Principios Fundamentales:**
1. **Aprender Haciendo** - Cada concepto se practica con código real
2. **Progresión Incremental** - NO crear todo de una vez
3. **Entender, No Copiar** - Explicar cada línea de código
4. **Proyecto Real** - Aplicación útil para Forestech
5. **Iterativo** - Mejorar el código a medida que aprendes más

### **Metodología:**
- ✅ Agente IA explica concepto
- ✅ Escribes código junto con el agente
- ✅ Pruebas el código
- ✅ Entiendes qué hace y por qué
- ✅ Pasas al siguiente paso

---

## 🤖 **CÓMO USAR ESTE ROADMAP CON TU AGENTE IA**

### **⚠️ IMPORTANTE: Este NO es un documento solo de lectura**

Este roadmap está diseñado para ser **interactivo**. En cada checkpoint encontrarás **secciones especiales** para trabajar con tu agente de IA (Copilot, IntelliJ AI Assistant, ChatGPT, etc.)

### **🎯 Estructura de Cada Checkpoint:**

```
1. ✅ Lista de tareas
2. 📖 Conceptos clave (breve)
3. 💻 Código de ejemplo
4. 🤖 TRABAJA CON TU IA - Prompts para entender ANTES de escribir
5. 💡 SI NO ENTIENDES - Prompts para profundizar
6. 🎯 EJERCICIO PRÁCTICO - Mini-retos
7. ✅ VALIDA TU COMPRENSIÓN - Quiz antes de avanzar
```

### **📋 Reglas de Oro:**

1. **SIEMPRE** lee el concepto primero
2. **SIEMPRE** usa los prompts de "🤖 TRABAJA CON TU IA" antes de escribir código
3. **NUNCA** le pidas al agente IA todo el código de una vez
4. **SIEMPRE** valida tu comprensión con el quiz antes de avanzar
5. Si algo NO está claro, usa la sección "💡 SI NO ENTIENDES"

### **✅ Ejemplo de Flujo Correcto:**

```
Tú: "Estoy en Checkpoint 2.1 - Primera Clase.
     Explícame con una analogía: ¿qué es una clase?"

IA: [Explica concepto]

Tú: "Entendido. Ahora muéstrame SOLO la estructura básica 
     de la clase Movement, sin los métodos."

IA: [Muestra estructura básica]

Tú: [Escribes el código tú mismo]

Tú: "Aquí está mi código: [pega código]
     ¿Está correcto? ¿Qué puedo mejorar?"

IA: [Revisa y da feedback]

Tú: "Completé Checkpoint 2.1.
     Hazme 3 preguntas para validar que entendí."
```

### **❌ Ejemplo de Flujo INCORRECTO (Evitar):**

```
Tú: "Dame el código completo del proyecto Forestech CLI"

IA: [Genera 2000 líneas de código]

Tú: [Copias todo sin entender]

❌ NO APRENDISTE NADA
```

### **💬 Prompts Genéricos que Puedes Usar en Cualquier Momento:**

```
"No entiendo [concepto]. Explícalo con una analogía simple."

"¿Por qué usamos [X] en lugar de [Y] en este contexto?"

"Muéstrame un ejemplo de [concepto] aplicado al proyecto Forestech."

"Dame un ejercicio pequeño para practicar [concepto]."

"Revisa mi código y dame feedback constructivo: [pega código]"

"Hazme un quiz de N preguntas sobre [tema] para validar comprensión."

"Estoy atascado con este error: [pega error]. Ayúdame a entenderlo."

"Antes de escribir código, explícame qué voy a hacer y por qué."
```

### **🎓 Recordatorio Constante:**

**Tu agente de IA es tu tutor personal, NO tu generador de código automático.**

Úsalo para:
- ✅ Entender conceptos
- ✅ Aclarar dudas
- ✅ Validar tu código
- ✅ Recibir feedback
- ✅ Practicar con ejercicios

NO lo uses para:
- ❌ Generar todo el código
- ❌ Copiar sin entender
- ❌ Saltarte pasos del roadmap
- ❌ Avanzar sin validar comprensión

---

## 🗺️ **ROADMAP COMPLETO**

### **FASE 0: PREPARACIÓN (Semana 0)**
**Objetivo:** Tener todo listo para empezar a programar

#### **Checkpoint 0.1: Verificar Entorno**
- [x] JDK instalado (Java 17 o 21)
- [x] IntelliJ IDEA configurado
- [x] VS Code con extensiones Java
- [x] Maven instalado
- [x] Git configurado

#### **Checkpoint 0.2: Crear Proyecto Base**
- [x] Estructura de carpetas creada
- [x] `pom.xml` configurado (Maven)
- [x] SQL Server JDBC driver agregado
- [x] Primera ejecución exitosa de "Hello World"

**Conceptos a Aprender:**
- ¿Qué es JDK vs JRE vs JVM?
- ¿Qué es Maven y por qué lo usamos?
- ¿Qué es un archivo `.class` vs `.java`?
- ¿Cómo se compila y ejecuta Java?

**Tiempo Estimado:** 1-2 días

---

### **FASE 1: FUNDAMENTOS DE JAVA (Semana 1-2)**
**Objetivo:** Entender sintaxis básica y estructura de un programa Java

#### **Checkpoint 1.1: Primer Programa**
- [x] Crear `Main.java` con método `main()`
- [x] Imprimir en consola con `System.out.println()`
- [x] Compilar y ejecutar desde terminal
- [x] Compilar y ejecutar desde IntelliJ

**Conceptos:**
- Estructura de un programa Java
- Método `main()` como punto de entrada
- `public static void main(String[] args)`
- Compilación vs Ejecución

**Código a Escribir:**
```java
public class Main {
    public static void main(String[] args) {
        System.out.println("🚀 Forestech CLI - Sistema de Combustibles");
    }
}
```

---

#### **Checkpoint 1.2: Variables y Tipos de Datos**
- [x] Declarar variables (`int`, `double`, `String`, `boolean`)
- [x] Entender tipos primitivos vs objetos
- [x] Usar `final` para constantes
- [x] Conversión de tipos (casting)

**Conceptos:**
- Tipos primitivos: `int`, `double`, `boolean`, `char`, `long`, `float`
- Tipos de referencia: `String`, arrays, objetos
- Diferencia entre `int` y `Integer`
- Constantes con `final`

**Código a Escribir:**
```java
String projectName = "Forestech CLI";
int currentYear = 2025;
double version = 1.0;
boolean isActive = true;
final String DATABASE = "DBforestech";
```

**🤖 TRABAJA CON TU IA:**
```
"Estoy en Checkpoint 1.2 - Variables y Tipos de Datos.
Antes de escribir código, explícame con una analogía simple:
¿Qué es una variable? ¿Por qué necesito diferentes tipos?"
```

**📝 DESPUÉS DE ESCRIBIR EL CÓDIGO:**
```
"Ya escribí el código de variables. Explícame:
1. ¿Por qué 'double' para version y no 'int'?
2. ¿Qué pasa si cambio el valor de 'final String DATABASE'?
3. ¿Cuándo uso String vs int en el proyecto Forestech?"
```

**✅ VALIDA TU COMPRENSIÓN:**
```
"He completado Checkpoint 1.2. 
Hazme un quiz de 3 preguntas sobre variables y tipos de datos.
Usa ejemplos del proyecto Forestech."
```

---

#### **Checkpoint 1.3: Operadores y Expresiones**
- [ ] Operadores aritméticos (`+`, `-`, `*`, `/`, `%`)
- [ ] Operadores de comparación (`==`, `!=`, `>`, `<`)
- [ ] Operadores lógicos (`&&`, `||`, `!`)
- [ ] Calcular total de movimiento (cantidad × precio)

**Código a Escribir:**
```java
double quantity = 100.5;
double unitPrice = 12500.0;
double totalValue = quantity * unitPrice;
System.out.println("Total: $" + totalValue);
```

**🤖 TRABAJA CON TU IA:**
```
"Checkpoint 1.3 - Operadores.
Muéstrame paso a paso cómo calcular el total de un movimiento.
Explica cada operador que usamos y por qué."
```

**💡 SI NO ENTIENDES ALGO:**
```
"No entiendo [el operador % / la diferencia entre == y equals() / etc].
Explícamelo con un ejemplo del sistema de combustibles."
```

**✅ EJERCICIO PRÁCTICO:**
```
"Dame 2 ejercicios pequeños usando operadores para practicar.
Que estén relacionados con cálculos de combustibles."
```

---

#### **Checkpoint 1.4: Control de Flujo**
- [ ] Estructura `if-else`
- [ ] Estructura `switch-case`
- [ ] Validar tipo de movimiento (ENTRADA vs SALIDA)
- [ ] Menú simple con opciones

**Conceptos:**
- Bloques de código con `{}`
- Indentación y legibilidad
- `switch` vs `if-else`

**Código a Escribir:**
```java
String movementType = "ENTRADA";

if (movementType.equals("ENTRADA")) {
    System.out.println("Creando movimiento de entrada...");
} else if (movementType.equals("SALIDA")) {
    System.out.println("Creando movimiento de salida...");
} else {
    System.out.println("Tipo de movimiento inválido");
}
```

**🤖 ANTES DE EMPEZAR:**
```
"Checkpoint 1.4 - Control de Flujo.
Explícame con una analogía cotidiana: ¿qué es un if-else?
¿Por qué lo necesito en mi programa?"
```

**🔍 PROFUNDIZA SI ES NECESARIO:**
```
"No entiendo [por qué uso .equals() y no == / cuándo usar switch vs if-else / etc].
Dame más ejemplos del proyecto Forestech."
```

**✅ VALIDA ANTES DE AVANZAR:**
```
"Completé el código de if-else.
Hazme un quiz de 3 preguntas sobre control de flujo.
Incluye un ejercicio donde deba escribir código."
```

---

#### **Checkpoint 1.5: Bucles (Loops)**
- [ ] Bucle `for`
- [ ] Bucle `while`
- [ ] Bucle `do-while`
- [ ] Iterar sobre un array de movimientos

**Código a Escribir:**
```java
// Listar opciones del menú
String[] menuOptions = {"ENTRADA", "SALIDA", "INVENTARIO", "SALIR"};
for (int i = 0; i < menuOptions.length; i++) {
    System.out.println((i + 1) + ". " + menuOptions[i]);
}
```

**🤖 TRABAJA CON TU IA:**
```
"Checkpoint 1.5 - Bucles.
Explícame la diferencia entre for, while y do-while.
¿Cuándo uso cada uno en el proyecto Forestech?"
```

**🎯 PRÁCTICA GUIADA:**
```
"Guíame paso a paso para crear un bucle que:
1. Muestre el menú principal
2. Siga ejecutándose hasta que el usuario elija 'SALIR'
Explica cada línea mientras la escribo."
```

**✅ VALIDACIÓN:**
```
"Terminé Checkpoint 1.5.
Hazme un mini-reto: dame un problema pequeño que requiera usar un bucle.
Luego revisa mi solución y dame feedback."
```

---

#### **Checkpoint 1.6: Entrada de Usuario (Scanner)**
- [ ] Importar clase `Scanner`
- [ ] Leer input del usuario
- [ ] Menú interactivo básico
- [ ] Validar entrada del usuario

**Conceptos:**
- Import statements
- Crear instancias de clases (`new Scanner()`)
- Cerrar recursos con `.close()`

**Código a Escribir:**
```java
import java.util.Scanner;

Scanner scanner = new Scanner(System.in);
System.out.print("Ingresa opción: ");
int option = scanner.nextInt();
scanner.nextLine(); // Consumir newline
```

**🤖 TRABAJA CON TU IA:**
```
"Checkpoint 1.6 - Scanner.
¿Qué significa 'import' y 'new'? 
Explícame paso a paso cómo leer input del usuario."
```

**💡 PREGUNTA SI NO ENTIENDES:**
```
"¿Por qué necesito scanner.nextLine() después de nextInt()?
Muéstrame qué pasa si no lo pongo."
```

**🎯 EJERCICIO PRÁCTICO:**
```
"Ayúdame a crear un menú interactivo simple que:
1. Muestre 3 opciones
2. Lea la opción del usuario
3. Valide que sea un número entre 1 y 3
Guíame paso a paso."
```

**✅ ANTES DE CONTINUAR A FASE 2:**
```
"Completé Fase 1 - Fundamentos de Java.
Hazme un quiz de 5 preguntas que cubra TODOS los temas de Fase 1:
variables, operadores, if-else, loops, y Scanner.
Quiero asegurarme de que entendí todo antes de avanzar a POO."
```

---

### **FASE 2: PROGRAMACIÓN ORIENTADA A OBJETOS (Semana 3-4)**
**Objetivo:** Crear clases y objetos para modelar el dominio

#### **Checkpoint 2.1: Primera Clase - Movement**
- [ ] Crear clase `Movement` en carpeta `models/`
- [ ] Definir atributos (id, type, fuelType, quantity, unitPrice)
- [ ] Entender visibilidad: `private`, `public`, `protected`
- [ ] Crear constructor

**Conceptos:**
- Clases vs Objetos
- Atributos (fields) de una clase
- Constructor: método especial para crear objetos
- Encapsulamiento con `private`

**Código a Escribir:**
```java
package models;

public class Movement {
    private String id;
    private String type;
    private String fuelType;
    private double quantity;
    private double unitPrice;
    
    // Constructor
    public Movement(String type, String fuelType, double quantity, double unitPrice) {
        this.id = java.util.UUID.randomUUID().toString();
        this.type = type;
        this.fuelType = fuelType;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }
}
```

**🤖 ANTES DE EMPEZAR POO:**
```
"Voy a empezar Fase 2 - POO. Este es un concepto nuevo y fundamental.
Explícame con una analogía del mundo real:
¿Qué es una clase? ¿Qué es un objeto? ¿Cuál es la diferencia?
Usa ejemplos de Forestech (vehiculos, combustibles, movimientos)."
```

**📚 MIENTRAS ESCRIBES EL CÓDIGO:**
```
"Estoy escribiendo mi primera clase Movement.
Explícame línea por línea:
1. ¿Qué significa 'private'?
2. ¿Qué es 'this.'?
3. ¿Por qué el constructor no tiene 'void'?
4. ¿Qué hace UUID.randomUUID()?"
```

**✅ VALIDA TU COMPRENSIÓN:**
```
"Completé Checkpoint 2.1.
Hazme 3 preguntas sobre clases y constructores.
Luego dame un mini-reto: pedirme crear otra clase simple."
```

---

#### **Checkpoint 2.2: Getters y Setters**
- [ ] Crear métodos `get` para leer atributos
- [ ] Crear métodos `set` para modificar atributos
- [ ] Entender por qué usar getters/setters (encapsulamiento)
- [ ] Crear método `getTotalValue()`

**Conceptos:**
- Acceso controlado a atributos
- Nomenclatura: `getName()`, `setName()`
- Métodos de cálculo (business logic)

**Código a Escribir:**
```java
public String getId() {
    return id;
}

public String getType() {
    return type;
}

public double getTotalValue() {
    return quantity * unitPrice;
}

public void setQuantity(double quantity) {
    if (quantity > 0) {
        this.quantity = quantity;
    } else {
        throw new IllegalArgumentException("Cantidad debe ser positiva");
    }
}
```

**🤖 TRABAJA CON TU IA:**
```
"Checkpoint 2.2 - Getters y Setters.
¿Por qué no accedo directamente a los atributos si ya son de mi clase?
¿Qué beneficio tiene usar getters y setters?
Explícalo con un ejemplo de por qué validar quantity > 0."
```

**💡 PROFUNDIZA:**
```
"Veo que getTotalValue() no tiene 'set'. ¿Por qué?
¿Cuándo creo getter sin setter y viceversa?"
```

**✅ MINI-RETO:**
```
"Dame un ejercicio: pedirme agregar un setter con validación.
Luego revisa mi código y dame feedback."
```

---

#### **Checkpoint 2.3: Método toString()**
- [ ] Sobrescribir método `toString()`
- [ ] Mostrar información legible del objeto
- [ ] Entender la anotación `@Override`

**Conceptos:**
- Herencia implícita de `Object`
- Sobrescritura de métodos
- String formatting

**Código a Escribir:**
```java
@Override
public String toString() {
    return String.format("Movement[id=%s, type=%s, fuel=%s, qty=%.2f, price=$%.2f, total=$%.2f]",
        id, type, fuelType, quantity, unitPrice, getTotalValue());
}
```

**🤖 PREGUNTA A TU IA:**
```
"Checkpoint 2.3 - toString().
¿Qué es @Override? ¿Para qué sirve?
¿Por qué necesito sobrescribir toString()?
Muéstrame qué pasa si imprimo el objeto sin y con toString()."
```

---

#### **Checkpoint 2.4: Más Clases del Dominio**
- [ ] Crear clase `Vehicle` (id, plateNumber, model, category)
- [ ] Crear clase `Supplier` (id, name, phone, email)
- [ ] Crear clase `Product` (id, name, type, unit)
- [ ] Instanciar objetos de cada clase

**Conceptos:**
- Diseño de dominio
- Relaciones entre clases
- Instanciación de objetos con `new`

**🎯 TRABAJO INDEPENDIENTE:**
```
"Checkpoint 2.4 - Más clases del dominio.
NO me des el código completo. 
Dame solo la estructura de la clase Vehicle.
Yo la escribiré y tú revisas mi código."
```

**✅ DESPUÉS DE ESCRIBIR:**
```
"Aquí está mi clase Vehicle: [pega tu código]
Revisa mi código y dame feedback:
¿Está bien estructurado? ¿Falta algo? ¿Qué puedo mejorar?"
```

---

#### **Checkpoint 2.5: Listas y Colecciones**
- [ ] Importar `ArrayList` de `java.util`
- [ ] Crear lista de movimientos
- [ ] Agregar, eliminar y buscar elementos
- [ ] Iterar con `for-each`

**Conceptos:**
- Generics: `ArrayList<Movement>`
- Colecciones dinámicas vs arrays fijos
- Métodos: `add()`, `remove()`, `get()`, `size()`

**Código a Escribir:**
```java
import java.util.ArrayList;
import java.util.List;

List<Movement> movements = new ArrayList<>();
movements.add(new Movement("ENTRADA", "ACPM", 100.0, 12500.0));
movements.add(new Movement("SALIDA", "GASOLINA", 50.0, 14000.0));

for (Movement movement : movements) {
    System.out.println(movement);
}
```

**🤖 TRABAJA CON TU IA:**
```
"Checkpoint 2.5 - ArrayList.
Explícame la diferencia entre array y ArrayList.
¿Por qué uso List<Movement> y no ArrayList<Movement> directamente?
¿Qué significa el <Movement> entre <>?"
```

**🔍 SI NO ENTIENDES:**
```
"No entiendo el for-each loop. 
¿Cómo funciona diferente al for normal?
Muéstrame ambos ejemplos comparados."
```

**✅ ANTES DE PASAR A FASE 3:**
```
"Completé Fase 2 - POO.
Esto es un tema fundamental. 
Hazme un quiz completo de 6 preguntas sobre:
- Clases y objetos
- Constructores
- Getters/Setters
- toString()
- ArrayList
Incluye preguntas teóricas y de código."
```

---

### **FASE 3: CONEXIÓN A SQL SERVER (Semana 5)**
**Objetivo:** Conectar Java con la base de datos

#### **Checkpoint 3.1: JDBC Driver**
- [ ] Agregar dependencia JDBC en `pom.xml`
- [ ] Maven descarga el driver automáticamente
- [ ] Entender qué es JDBC y cómo funciona

**Conceptos:**
- JDBC: Java Database Connectivity
- Driver como puente entre Java y SQL Server
- Dependencias Maven

**pom.xml:**
```xml
<dependency>
    <groupId>com.microsoft.sqlserver</groupId>
    <artifactId>mssql-jdbc</artifactId>
    <version>12.8.1.jre11</version>
</dependency>
```

**🤖 ANTES DE EMPEZAR:**
```
"Voy a empezar Fase 3 - Conexión a SQL Server.
Antes de tocar código, explícame:
¿Qué es JDBC? ¿Cómo se conecta Java a una base de datos?
Usa una analogía simple."
```

**💡 MIENTRAS CONFIGURAS:**
```
"Estoy agregando la dependencia en pom.xml.
¿Qué hace Maven con esta dependencia?
¿Dónde descarga el archivo JAR?"
```

---

#### **Checkpoint 3.2: Clase DatabaseConnection**
- [ ] Crear clase `DatabaseConnection` en `config/`
- [ ] Definir constantes de conexión (URL, USER, PASSWORD)
- [ ] Crear método `getConnection()`
- [ ] Probar conexión con `testConnection()`

**Conceptos:**
- Connection String: `jdbc:sqlserver://host:port;database=...`
- Try-with-resources para cerrar conexiones automáticamente
- Manejo de excepciones con `SQLException`

**Código a Escribir:**
```java
package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:sqlserver://24.199.89.134:1433;databaseName=DBforestech;encrypt=true;trustServerCertificate=true";
    private static final String USER = "SA";
    private static final String PASSWORD = "Forestech2024!SecureDB";
    
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    
    public static void testConnection() {
        try (Connection conn = getConnection()) {
            System.out.println("✅ Conexión exitosa!");
            System.out.println("📊 Database: " + conn.getCatalog());
        } catch (SQLException e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
```

**🤖 TRABAJA CON TU IA:**
```
"Checkpoint 3.2 - DatabaseConnection.
Explícame línea por línea este código:
1. ¿Qué es 'static final'?
2. ¿Qué significa 'try-with-resources' (el try con paréntesis)?
3. ¿Por qué 'throws SQLException'?
4. ¿Qué hace DriverManager.getConnection()?"
```

**⚠️ SI HAY ERRORES:**
```
"Tengo este error al conectar: [pega el error]
Ayúdame a entender qué significa y cómo solucionarlo."
```

**✅ DESPUÉS DE PROBAR:**
```
"¡Mi conexión funcionó! Ahora explícame:
¿Por qué es importante cerrar la conexión?
¿Qué pasa si no uso try-with-resources?"
```

---

#### **Checkpoint 3.3: Primera Query - SELECT**
- [ ] Crear método para listar productos
- [ ] Usar `Statement` o `PreparedStatement`
- [ ] Iterar sobre `ResultSet`
- [ ] Imprimir resultados en consola

**Conceptos:**
- `Statement` vs `PreparedStatement`
- SQL Injection y por qué usar PreparedStatement
- `ResultSet` como cursor de resultados

**Código a Escribir:**
```java
public static void listProducts() {
    String sql = "SELECT id, name, type, unit FROM combustibles_products";
    
    try (Connection conn = getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {
        
        System.out.println("\n📦 PRODUCTOS:");
        while (rs.next()) {
            System.out.printf("ID: %s | Nombre: %s | Tipo: %s | Unidad: %s%n",
                rs.getString("id"),
                rs.getString("name"),
                rs.getString("type"),
                rs.getString("unit")
            );
        }
    } catch (SQLException e) {
        System.err.println("Error al listar productos: " + e.getMessage());
    }
}
```

**🤖 APRENDE CON TU IA:**
```
"Checkpoint 3.3 - Primera Query SELECT.
Explícame paso a paso este código:
1. ¿Qué es un Statement?
2. ¿Qué hace executeQuery()?
3. ¿Cómo funciona el while con rs.next()?
4. ¿Por qué rs.getString('id') y no directamente el valor?"
```

**🎯 EJERCICIO:**
```
"Quiero hacer mi propia query SELECT. 
Ayúdame a listar todos los vehículos de la tabla combustibles_vehicles.
NO me des el código completo, guíame paso a paso."
```

**✅ VALIDA FASE 3:**
```
"Completé Fase 3 - Conexión a SQL Server.
Hazme un quiz de 5 preguntas sobre:
- JDBC y drivers
- Connection strings
- Try-with-resources
- Queries SELECT
- ResultSet
Quiero asegurarme de entender antes del CRUD."
```

---

### **FASE 4: OPERACIONES CRUD (Semana 6-7)**
**Objetivo:** Crear, leer, actualizar y eliminar datos

#### **Checkpoint 4.1: Crear Proveedor (INSERT)**
- [ ] Crear clase `SupplierService`
- [ ] Método `createSupplier()` con PreparedStatement
- [ ] Generar UUID para el ID
- [ ] Validar datos antes de insertar

**Conceptos:**
- PreparedStatement con placeholders `?`
- `setString()`, `setInt()`, `setDouble()`
- Transacciones implícitas
- Validación de datos

**Código a Escribir:**
```java
package services;

import config.DatabaseConnection;
import models.Supplier;
import java.sql.*;
import java.util.UUID;

public class SupplierService {
    
    public static void createSupplier(Supplier supplier) throws SQLException {
        String sql = "INSERT INTO combustibles_suppliers (id, name, phone, email, createdAt) VALUES (?, ?, ?, ?, GETDATE())";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, UUID.randomUUID().toString());
            pstmt.setString(2, supplier.getName());
            pstmt.setString(3, supplier.getPhone());
            pstmt.setString(4, supplier.getEmail());
            
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("✅ Proveedor creado: " + rowsAffected + " fila(s)");
        }
    }
}
```

**🤖 TRABAJA CON TU IA:**
```
"Checkpoint 4.1 - INSERT con PreparedStatement.
Explícame la diferencia entre Statement y PreparedStatement.
¿Por qué usar PreparedStatement es más seguro?
¿Qué es SQL Injection y cómo PreparedStatement lo previene?"
```

**💡 PROFUNDIZA:**
```
"No entiendo los placeholders '?'.
¿Cómo sabe el PreparedStatement qué valor va en cada '?'?
Muéstrame un ejemplo de query vulnerable vs query segura."
```

**🎯 EJERCICIO:**
```
"Quiero crear mi propio método INSERT.
Ayúdame a crear createVehicle() para insertar vehículos.
Guíame en la estructura pero NO me des el código completo."
```

**✅ VALIDA:**
```
"Terminé el INSERT. Hazme 3 preguntas sobre PreparedStatement."
```

---

#### **Checkpoint 4.2: Leer Movimientos (SELECT con filtros)**
- [ ] Crear `MovementService`
- [ ] Método `getMovementsByType(String type)`
- [ ] Convertir ResultSet a objetos Movement
- [ ] Usar PreparedStatement con parámetros

**Código a Escribir:**
```java
public static List<Movement> getMovementsByType(String type) throws SQLException {
    String sql = "SELECT * FROM combustibles_movements WHERE type = ?";
    List<Movement> movements = new ArrayList<>();
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setString(1, type);
        ResultSet rs = pstmt.executeQuery();
        
        while (rs.next()) {
            Movement movement = new Movement(
                rs.getString("type"),
                rs.getString("fuelType"),
                rs.getDouble("quantity"),
                rs.getDouble("unitPrice")
            );
            movement.setId(rs.getString("id")); // Setter especial para ID de BD
            movements.add(movement);
        }
    }
    return movements;
}
```

---

#### **Checkpoint 4.3: Actualizar Vehículo (UPDATE)**
- [ ] Método `updateVehicle(String id, Vehicle vehicle)`
- [ ] Verificar que el vehículo existe antes de actualizar
- [ ] Retornar número de filas afectadas

**Código a Escribir:**
```java
public static int updateVehicle(String id, Vehicle vehicle) throws SQLException {
    String sql = "UPDATE combustibles_vehicles SET plateNumber = ?, model = ?, category = ? WHERE id = ?";
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setString(1, vehicle.getPlateNumber());
        pstmt.setString(2, vehicle.getModel());
        pstmt.setString(3, vehicle.getCategory());
        pstmt.setString(4, id);
        
        return pstmt.executeUpdate();
    }
}
```

---

#### **Checkpoint 4.4: Eliminar (DELETE con confirmación)**
- [ ] Método `deleteSupplier(String id)`
- [ ] Pedir confirmación del usuario antes de eliminar
- [ ] Verificar que no haya movimientos asociados (integridad referencial)

**Código a Escribir:**
```java
public static void deleteSupplier(String id) throws SQLException {
    // Verificar si hay movimientos asociados
    String checkSql = "SELECT COUNT(*) FROM combustibles_movements WHERE supplierName = (SELECT name FROM combustibles_suppliers WHERE id = ?)";
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
        
        checkStmt.setString(1, id);
        ResultSet rs = checkStmt.executeQuery();
        
        if (rs.next() && rs.getInt(1) > 0) {
            System.out.println("⚠️ No se puede eliminar: hay movimientos asociados");
            return;
        }
    }
    
    // Eliminar proveedor
    String deleteSql = "DELETE FROM combustibles_suppliers WHERE id = ?";
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
        
        pstmt.setString(1, id);
        int rows = pstmt.executeUpdate();
        System.out.println(rows > 0 ? "✅ Proveedor eliminado" : "❌ Proveedor no encontrado");
    }
}
```

---

### **FASE 5: LÓGICA DE NEGOCIO (Semana 8-9)**
**Objetivo:** Implementar reglas de negocio complejas

#### **Checkpoint 5.1: Crear Movimiento ENTRADA**
- [ ] Validar que el proveedor existe
- [ ] Validar que el producto existe
- [ ] Calcular valor total automáticamente
- [ ] Actualizar inventario automáticamente
- [ ] Usar transacciones para atomicidad

**Conceptos:**
- Transacciones: `conn.setAutoCommit(false)`
- Rollback en caso de error
- Commit para confirmar cambios
- Integridad de datos

**Código a Escribir:**
```java
public static void createEntradaMovement(Movement movement) throws SQLException {
    Connection conn = null;
    try {
        conn = DatabaseConnection.getConnection();
        conn.setAutoCommit(false); // Iniciar transacción
        
        // 1. Insertar movimiento
        String insertSql = "INSERT INTO combustibles_movements (id, type, fuelType, quantity, unitPrice, supplierName, destinationLocation, createdAt) VALUES (?, ?, ?, ?, ?, ?, ?, GETDATE())";
        try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            pstmt.setString(1, UUID.randomUUID().toString());
            pstmt.setString(2, "ENTRADA");
            pstmt.setString(3, movement.getFuelType());
            pstmt.setDouble(4, movement.getQuantity());
            pstmt.setDouble(5, movement.getUnitPrice());
            pstmt.setString(6, movement.getSupplierName());
            pstmt.setString(7, movement.getDestinationLocation());
            pstmt.executeUpdate();
        }
        
        // 2. Actualizar inventario
        String updateInventorySql = "UPDATE combustibles_inventory SET quantity = quantity + ? WHERE fuelType = ? AND location = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(updateInventorySql)) {
            pstmt.setDouble(1, movement.getQuantity());
            pstmt.setString(2, movement.getFuelType());
            pstmt.setString(3, movement.getDestinationLocation());
            pstmt.executeUpdate();
        }
        
        conn.commit(); // Confirmar transacción
        System.out.println("✅ Movimiento ENTRADA creado exitosamente");
        
    } catch (SQLException e) {
        if (conn != null) {
            conn.rollback(); // Revertir cambios en caso de error
        }
        throw e;
    } finally {
        if (conn != null) {
            conn.setAutoCommit(true);
            conn.close();
        }
    }
}
```

---

#### **Checkpoint 5.2: Crear Movimiento SALIDA**
- [ ] Validar que el vehículo existe
- [ ] Validar que hay suficiente inventario
- [ ] Actualizar horómetro del vehículo
- [ ] Calcular horas trabajadas automáticamente
- [ ] Descontar del inventario

**Validaciones:**
```java
// Validar inventario suficiente
String checkInventorySql = "SELECT quantity FROM combustibles_inventory WHERE fuelType = ? AND location = ?";
try (PreparedStatement pstmt = conn.prepareStatement(checkInventorySql)) {
    pstmt.setString(1, movement.getFuelType());
    pstmt.setString(2, movement.getSourceLocation());
    ResultSet rs = pstmt.executeQuery();
    
    if (rs.next()) {
        double availableQty = rs.getDouble("quantity");
        if (availableQty < movement.getQuantity()) {
            throw new SQLException("Inventario insuficiente. Disponible: " + availableQty);
        }
    } else {
        throw new SQLException("No existe inventario para este combustible en esta ubicación");
    }
}
```

---

#### **Checkpoint 5.3: Consultar Inventario**
- [ ] Método `getInventoryByLocation(String location)`
- [ ] Método `getInventoryByFuelType(String fuelType)`
- [ ] Método `getLowStockAlerts()` (cantidad < umbral)
- [ ] Formatear resultados con tablas ASCII

**Código a Escribir:**
```java
public static void displayInventory() throws SQLException {
    String sql = "SELECT fuelType, location, quantity, unit FROM combustibles_inventory ORDER BY location, fuelType";
    
    try (Connection conn = DatabaseConnection.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {
        
        System.out.println("\n╔═══════════════════════════════════════════════════════╗");
        System.out.println("║                  INVENTARIO ACTUAL                    ║");
        System.out.println("╠═══════════════════════════════════════════════════════╣");
        System.out.printf("║ %-15s ║ %-18s ║ %-12s ║%n", "COMBUSTIBLE", "UBICACIÓN", "CANTIDAD");
        System.out.println("╠═══════════════════════════════════════════════════════╣");
        
        while (rs.next()) {
            System.out.printf("║ %-15s ║ %-18s ║ %10.2f %s ║%n",
                rs.getString("fuelType"),
                rs.getString("location"),
                rs.getDouble("quantity"),
                rs.getString("unit")
            );
        }
        System.out.println("╚═══════════════════════════════════════════════════════╝");
    }
}
```

---

### **FASE 6: INTERFAZ DE USUARIO (Semana 10)**
**Objetivo:** Menú interactivo profesional

#### **Checkpoint 6.1: Menú Principal**
- [ ] Crear clase `ConsoleMenu`
- [ ] Método `displayMainMenu()`
- [ ] Navegación entre submenús
- [ ] Manejo de opciones inválidas

**Código a Escribir:**
```java
package ui;

import services.*;
import java.util.Scanner;

public class ConsoleMenu {
    private Scanner scanner;
    
    public ConsoleMenu() {
        this.scanner = new Scanner(System.in);
    }
    
    public void start() {
        System.out.println("\n╔════════════════════════════════════════════════════╗");
        System.out.println("║     FORESTECH - Sistema de Gestión Combustibles   ║");
        System.out.println("╚════════════════════════════════════════════════════╝");
        
        boolean running = true;
        while (running) {
            displayMainMenu();
            int option = readInt("Selecciona opción: ");
            
            switch (option) {
                case 1:
                    movementsMenu();
                    break;
                case 2:
                    inventoryMenu();
                    break;
                case 3:
                    vehiclesMenu();
                    break;
                case 4:
                    suppliersMenu();
                    break;
                case 0:
                    System.out.println("\n¡Hasta luego! 👋");
                    running = false;
                    break;
                default:
                    System.out.println("❌ Opción inválida");
            }
        }
        scanner.close();
    }
    
    private void displayMainMenu() {
        System.out.println("\n📋 MENÚ PRINCIPAL:");
        System.out.println("1. Movimientos (ENTRADA/SALIDA)");
        System.out.println("2. Consultar Inventario");
        System.out.println("3. Gestionar Vehículos");
        System.out.println("4. Gestionar Proveedores");
        System.out.println("0. Salir");
    }
    
    private int readInt(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextInt()) {
            System.out.print("❌ Ingresa un número válido: ");
            scanner.next();
        }
        int value = scanner.nextInt();
        scanner.nextLine(); // Consumir newline
        return value;
    }
}
```

---

#### **Checkpoint 6.2: Wizard para ENTRADA**
- [ ] Formulario paso a paso
- [ ] Listar proveedores disponibles
- [ ] Listar productos disponibles
- [ ] Confirmar antes de guardar
- [ ] Mostrar resumen del movimiento creado

---

#### **Checkpoint 6.3: Wizard para SALIDA**
- [ ] Listar vehículos disponibles
- [ ] Validar horómetro > horómetro anterior
- [ ] Calcular horas trabajadas automáticamente
- [ ] Confirmar antes de guardar

---

### **FASE 7: MANEJO DE ERRORES Y EXCEPCIONES (Semana 11)**
**Objetivo:** Código robusto y resiliente

#### **Checkpoint 7.1: Excepciones Personalizadas**
- [ ] Crear `InsufficientInventoryException`
- [ ] Crear `InvalidMovementException`
- [ ] Crear `DatabaseConnectionException`
- [ ] Usar excepciones en lugar de prints

**Conceptos:**
- `extends Exception` para checked exceptions
- `extends RuntimeException` para unchecked exceptions
- Try-catch-finally
- Throw vs Throws

**Código a Escribir:**
```java
package exceptions;

public class InsufficientInventoryException extends Exception {
    private double available;
    private double required;
    
    public InsufficientInventoryException(double available, double required) {
        super(String.format("Inventario insuficiente. Disponible: %.2f, Requerido: %.2f", available, required));
        this.available = available;
        this.required = required;
    }
    
    public double getAvailable() {
        return available;
    }
    
    public double getRequired() {
        return required;
    }
}
```

---

#### **Checkpoint 7.2: Try-Catch Robusto**
- [ ] Capturar excepciones específicas primero
- [ ] Logging de errores con información útil
- [ ] Recuperación elegante de errores
- [ ] No dejar conexiones abiertas

---

### **FASE 8: CONCEPTOS AVANZADOS (Semana 12+)**
**Objetivo:** Código profesional y escalable

#### **Checkpoint 8.1: Interfaces y Polimorfismo**
- [ ] Crear interfaz `IMovementService`
- [ ] Implementar con `MovementServiceImpl`
- [ ] Entender ventajas de programar contra interfaces

**Conceptos:**
- `interface` vs `class`
- `implements` keyword
- Contratos de comportamiento
- Dependency Injection básico

---

#### **Checkpoint 8.2: Herencia**
- [ ] Clase abstracta `BaseService` con métodos comunes
- [ ] Extender de BaseService en todos los servicios
- [ ] Métodos abstractos vs concretos

**Conceptos:**
- `extends` keyword
- `abstract` class y métodos
- `super` para llamar al padre
- Jerarquía de clases

---

#### **Checkpoint 8.3: Enums**
- [ ] Crear `MovementType` enum (ENTRADA, SALIDA)
- [ ] Crear `FuelType` enum (ACPM, GASOLINA, MEZCLA)
- [ ] Usar enums en lugar de Strings

**Código a Escribir:**
```java
package enums;

public enum MovementType {
    ENTRADA("Entrada de Combustible"),
    SALIDA("Salida de Combustible");
    
    private final String description;
    
    MovementType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
```

---

#### **Checkpoint 8.4: Date/Time API (java.time)**
- [ ] Usar `LocalDateTime` en lugar de Date
- [ ] Formatear fechas con `DateTimeFormatter`
- [ ] Calcular diferencias de tiempo

**Código a Escribir:**
```java
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

LocalDateTime now = LocalDateTime.now();
DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
String formattedDate = now.format(formatter);
```

---

#### **Checkpoint 8.5: Streams y Lambda**
- [ ] Filtrar movimientos con `stream().filter()`
- [ ] Calcular totales con `stream().mapToDouble().sum()`
- [ ] Ordenar con `stream().sorted()`

**Conceptos:**
- Expresiones lambda: `(param) -> expression`
- Method references: `String::toUpperCase`
- Operaciones intermedias vs terminales

**Código a Escribir:**
```java
// Calcular total de movimientos ENTRADA
double totalEntradas = movements.stream()
    .filter(m -> m.getType().equals("ENTRADA"))
    .mapToDouble(Movement::getTotalValue)
    .sum();

// Top 5 movimientos más grandes
List<Movement> topMovements = movements.stream()
    .sorted((m1, m2) -> Double.compare(m2.getTotalValue(), m1.getTotalValue()))
    .limit(5)
    .collect(Collectors.toList());
```

---

### **FASE 9: FEATURES ADICIONALES (Semana 13+)**
**Objetivo:** Mejorar la aplicación con características avanzadas

#### **Checkpoint 9.1: Reportes**
- [ ] Reporte de movimientos por fecha
- [ ] Reporte de consumo por vehículo
- [ ] Reporte de compras por proveedor
- [ ] Exportar a CSV

---

#### **Checkpoint 9.2: Búsqueda Avanzada**
- [ ] Buscar movimientos por múltiples criterios
- [ ] Autocompletado de proveedores/vehículos
- [ ] Filtros combinados

---

#### **Checkpoint 9.3: Configuración Externa**
- [ ] Leer credenciales de archivo `.properties`
- [ ] Configuración de ambiente (dev/prod)
- [ ] No hardcodear contraseñas

**Código a Escribir:**
```java
// config.properties
db.url=jdbc:sqlserver://24.199.89.134:1433;databaseName=DBforestech
db.user=SA
db.password=Forestech2024!SecureDB

// Leer con Properties
Properties props = new Properties();
props.load(new FileInputStream("config.properties"));
String url = props.getProperty("db.url");
```

---

#### **Checkpoint 9.4: Logging con SLF4J**
- [ ] Agregar dependencia Logback
- [ ] Configurar niveles de log (INFO, DEBUG, ERROR)
- [ ] Logs estructurados en lugar de `System.out.println()`

---

#### **Checkpoint 9.5: Testing con JUnit**
- [ ] Crear primer test unitario
- [ ] Mockear conexiones a base de datos
- [ ] Test de validaciones de negocio

---

### **FASE 10: OPTIMIZACIÓN Y DEPLOYMENT (Semana 14+)**

#### **Checkpoint 10.1: Connection Pooling**
- [ ] Implementar HikariCP para pool de conexiones
- [ ] Mejorar performance de queries
- [ ] Monitorear uso de conexiones

---

#### **Checkpoint 10.2: JAR Ejecutable**
- [ ] Configurar Maven para generar JAR con dependencias
- [ ] Crear script de inicio (.sh para Linux)
- [ ] Distribuir aplicación

**pom.xml:**
```xml
<build>
    <plugins>
        <plugin>
            <artifactId>maven-assembly-plugin</artifactId>
            <configuration>
                <archive>
                    <manifest>
                        <mainClass>Main</mainClass>
                    </manifest>
                </archive>
                <descriptorRefs>
                    <descriptorRef>jar-with-dependencies</descriptorRef>
                </descriptorRefs>
            </configuration>
        </plugin>
    </plugins>
</build>
```

**Ejecutar:**
```bash
mvn clean package
java -jar target/forestech-cli-1.0-jar-with-dependencies.jar
```

---

## 📊 **INDICADORES DE PROGRESO**

### **Nivel Principiante (Fase 0-2)**
- ✅ Entiendes sintaxis básica de Java
- ✅ Puedes crear clases y objetos
- ✅ Usas if/else, loops, Scanner

### **Nivel Intermedio (Fase 3-5)**
- ✅ Conectas a SQL Server con JDBC
- ✅ Haces CRUD completo
- ✅ Manejas excepciones correctamente
- ✅ Usas transacciones

### **Nivel Avanzado (Fase 6-8)**
- ✅ Código bien estructurado (services, models, ui)
- ✅ Usas interfaces y herencia
- ✅ Implementas lógica de negocio compleja
- ✅ Streams y lambdas

### **Nivel Profesional (Fase 9-10)**
- ✅ Testing automatizado
- ✅ Logging estructurado
- ✅ Configuración externa
- ✅ Aplicación distribuible

---

## 🎯 **HITOS CLAVE**

| Semana | Hito | Entregable |
|--------|------|------------|
| 1-2 | Fundamentos de Java | Menú básico funcionando |
| 3-4 | POO | Clases del dominio creadas |
| 5 | Conexión SQL Server | Primera query exitosa |
| 6-7 | CRUD Completo | Crear/Leer/Actualizar/Eliminar funciona |
| 8-9 | Lógica de Negocio | ENTRADA y SALIDA con validaciones |
| 10 | UI Profesional | Menú interactivo completo |
| 11 | Manejo de Errores | Aplicación robusta |
| 12+ | Conceptos Avanzados | Código profesional |

---

## 📚 **RECURSOS DE APOYO**

### **Documentación Oficial:**
- [Oracle Java Tutorials](https://docs.oracle.com/javase/tutorial/)
- [Java API Specification](https://docs.oracle.com/en/java/javase/21/docs/api/)
- [JDBC Tutorial](https://docs.oracle.com/javase/tutorial/jdbc/)

### **Herramientas:**
- [IntelliJ IDEA](https://www.jetbrains.com/idea/)
- [Maven](https://maven.apache.org/)
- [Git](https://git-scm.com/)

### **Comunidad:**
- Stack Overflow (etiqueta `java`)
- Reddit r/learnjava
- Discord: Java Programming

---

## ✅ **PRÓXIMOS PASOS**

**AHORA MISMO:**
1. Lee `JAVA_PROJECT_SETUP.md` para configurar tu entorno
2. Lee `JAVA_AGENTS_INSTRUCTIONS.md` para entender cómo trabajar con IAs
3. Revisa `JAVA_NEXT_STEPS.md` para ver tu siguiente tarea específica

**RECUERDA:**
- 🎯 Aprender Java lleva tiempo - sé paciente contigo mismo
- 📝 Escribe código tú mismo, no solo copies
- ❓ Pregunta TODO lo que no entiendas
- 🔄 Practica, practica, practica
- 🎉 Celebra cada logro, por pequeño que sea

---

**¡EMPECEMOS ESTA AVENTURA! 🚀**

---

## 🎯 **RESUMEN DE CÓMO USAR ESTE ROADMAP**

### **Cada Checkpoint Ahora Tiene:**

1. **📋 Tareas y Conceptos** - Lo básico que necesitas saber
2. **💻 Código de Ejemplo** - Referencia, pero NO copies directamente
3. **🤖 TRABAJA CON TU IA** - Prompts para entender ANTES de programar
4. **💡 SI NO ENTIENDES** - Prompts para profundizar en conceptos
5. **🎯 EJERCICIO PRÁCTICO** - Mini-retos para practicar
6. **✅ VALIDA TU COMPRENSIÓN** - Quiz antes de avanzar

### **Tu Flujo de Trabajo Ideal:**

```
1. Lee el checkpoint
   ↓
2. Usa prompt "🤖 TRABAJA CON TU IA" para entender concepto
   ↓
3. IA te explica (NO te da código todavía)
   ↓
4. Tú escribes el código basándote en el ejemplo
   ↓
5. Si te atascas, usa "💡 SI NO ENTIENDES"
   ↓
6. Completas el checkpoint
   ↓
7. Haces el "🎯 EJERCICIO PRÁCTICO"
   ↓
8. Usas "✅ VALIDA TU COMPRENSIÓN" para el quiz
   ↓
9. Solo avanzas si pasas el quiz
```

### **Recuerda:**

- 🤖 **Tu IA es tu tutor, NO tu generador de código**
- 📝 **Escribe el código tú mismo, no copies**
- ❓ **Usa los prompts sugeridos en cada checkpoint**
- ✅ **SIEMPRE valida tu comprensión antes de avanzar**
- 🔄 **Si no entiendes, pregunta más - no hay prisa**

### **Los checkpoints sin secciones interactivas:**

Algunos checkpoints más avanzados (Fase 5+) no tienen todas las secciones interactivas todavía, pero **puedes aplicar el mismo patrón**:

```
"Estoy en [Checkpoint X.Y] - [Nombre].
Antes de escribir código, explícame [concepto].
Usa ejemplos del proyecto Forestech."
```

```
"Completé [Checkpoint X.Y].
Hazme un quiz de 3 preguntas para validar comprensión."
```

---

**Última actualización:** 12 de octubre de 2025
**Versión:** 2.0 - Roadmap Interactivo con Agente IA
