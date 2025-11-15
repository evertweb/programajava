# FASE 09: INTERFAZ GRÁFICA CON SWING (GUI)

## 🎯 Objetivo de la Fase

Transformar **Forestech CLI** (aplicación de consola) en una **aplicación de escritorio con ventanas**, usando Java Swing.

**Resultado esperado al finalizar:**
- ✅ Ventanas con botones, campos de texto, tablas y menús
- ✅ Formularios interactivos para productos, vehículos y movimientos
- ✅ Integración completa con tu capa de Services (ProductServices, VehicleServices, etc.)
- ✅ Validaciones visuales de Foreign Keys
- ✅ Archivo `.jar` ejecutable (doble clic)
- ✅ Archivo `.exe` para Windows

---

## 📁 CÓDIGO IMPLEMENTADO

**NOTA IMPORTANTE:** Esta fase usa **metodología invertida** (código primero, documentación después).
El código ya está implementado. Tu tarea es **leerlo y entenderlo** usando esta documentación como guía.

### Archivos Java Creados

| Checkpoint | Archivo | Ubicación | Descripción |
|-----------|---------|-----------|-------------|
| **9.1** | HelloSwingApp.java | [Ver código](../forestech-cli-java/src/main/java/com/forestech/ui/HelloSwingApp.java) | Primera ventana Swing + EDT |
| **9.2** | ButtonExampleApp.java | [Ver código](../forestech-cli-java/src/main/java/com/forestech/ui/ButtonExampleApp.java) | Botones + ActionListener |
| **9.3** | FormularioProductoSimple.java | [Ver código](../forestech-cli-java/src/main/java/com/forestech/ui/FormularioProductoSimple.java) | Formularios con validación |
| **9.4** | FormularioConComboBox.java | [Ver código](../forestech-cli-java/src/main/java/com/forestech/ui/FormularioConComboBox.java) | JComboBox (listas desplegables) |
| **9.5** | TablaProductosApp.java | [Ver código](../forestech-cli-java/src/main/java/com/forestech/ui/TablaProductosApp.java) | JTable + DefaultTableModel |
| **9.6** | ProductManagerGUI.java | [Ver código](../forestech-cli-java/src/main/java/com/forestech/ui/ProductManagerGUI.java) | ⭐ CRUD completo con BD |
| **9.7** | MainMenuGUI.java | [Ver código](../forestech-cli-java/src/main/java/com/forestech/ui/MainMenuGUI.java) | JMenuBar + Mnemonics + Accelerators |
| **9.8** | ProductDialogForm.java | [Ver código](../forestech-cli-java/src/main/java/com/forestech/ui/ProductDialogForm.java) | JDialog modal para formularios |
| **9.9** | ForestechMainGUI.java | [Ver código](../forestech-cli-java/src/main/java/com/forestech/ui/ForestechMainGUI.java) | ⭐⭐⭐ Aplicación principal (JTabbedPane) |
| **9.10** | VehicleManagerGUI.java | [Ver código](../forestech-cli-java/src/main/java/com/forestech/ui/VehicleManagerGUI.java) | CRUD vehículos + validación FK |
| **9.10** | VehicleDialogForm.java | [Ver código](../forestech-cli-java/src/main/java/com/forestech/ui/VehicleDialogForm.java) | Formulario con combo de productos |
| **9.11** | MovementManagerGUI.java | [Ver código](../forestech-cli-java/src/main/java/com/forestech/ui/MovementManagerGUI.java) | CRUD movimientos + validación 3 FKs |
| **9.11** | MovementDialogForm.java | [Ver código](../forestech-cli-java/src/main/java/com/forestech/ui/MovementDialogForm.java) | Formulario movimientos con combos |
| **9.12** | LookAndFeelDemo.java | [Ver código](../forestech-cli-java/src/main/java/com/forestech/ui/LookAndFeelDemo.java) | Personalización visual |
| **9.13** | ForestechProfessionalApp.java | [Ver código](../forestech-cli-java/src/main/java/com/forestech/ui/ForestechProfessionalApp.java) | ⭐⭐⭐ App profesional (JSplitPane + CardLayout + Dashboard + Facturas CRUD) |

### Cómo ejecutar el código

```bash
# Opción 1: Ejecutar aplicación profesional (RECOMENDADO - Checkpoint 9.13)
cd /home/hp/forestechOil/forestech-cli-java
mvn exec:java -Dexec.mainClass="com.forestech.ui.ForestechProfessionalApp"

# Opción 2: Ejecutar aplicación con pestañas (Checkpoint 9.9)
mvn exec:java -Dexec.mainClass="com.forestech.ui.ForestechMainGUI"

# Opción 3: Ejecutar un checkpoint específico
mvn exec:java -Dexec.mainClass="com.forestech.ui.HelloSwingApp"
mvn exec:java -Dexec.mainClass="com.forestech.ui.ProductManagerGUI"
mvn exec:java -Dexec.mainClass="com.forestech.ui.MainMenuGUI"
mvn exec:java -Dexec.mainClass="com.forestech.ui.VehicleManagerGUI"
mvn exec:java -Dexec.mainClass="com.forestech.ui.LookAndFeelDemo"

# Opción 4: Crear JAR ejecutable (doble clic)
mvn clean package
# Resultado: target/forestech-app.jar
java -jar target/forestech-app.jar
```

### Atajos de teclado en ForestechMainGUI

- **Ctrl+P** → Ir a pestaña Productos
- **Ctrl+Shift+V** → Ir a pestaña Vehículos
- **Ctrl+M** → Ir a pestaña Movimientos
- **Ctrl+R** → Refrescar todas las tablas
- **Ctrl+Q** → Salir
- **Alt+A** → Menú Archivo
- **Alt+V** → Menú Ver
- **Alt+Y** → Menú Ayuda

---

## 📖 Introducción: ¿Qué es Swing?

**Swing** es una librería de Java (incluida en el JDK) que permite crear interfaces gráficas (GUI = Graphical User Interface).

### Analogía con Forestech CLI:

```
┌─────────────────────────────────────────────────────────────────┐
│                    CLI vs GUI                                    │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  CLI (Consola):                                                  │
│  ═════════════                                                   │
│  while (true) {                                                  │
│      mostrarMenu();         →  Ventana con botones              │
│      opcion = scanner.nextInt(); → Clic en botón                │
│      ejecutarOpcion();      →  ActionListener                   │
│  }                                                               │
│                                                                  │
│  System.out.println()       →  JLabel (texto fijo)              │
│  Scanner.nextLine()         →  JTextField (campo editable)      │
│  for (Product p : lista)    →  JTable (tabla tipo Excel)        │
│  if (opcion == 1)           →  boton.addActionListener()        │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### Componentes Swing más usados:

| Componente | Analogía física | Uso en Forestech | Código |
|------------|-----------------|------------------|--------|
| **JFrame** | Ventana de tu casa | Ventana principal de la app | `new JFrame("Título")` |
| **JPanel** | Caja organizadora | Agrupar botones/campos | `new JPanel()` |
| **JButton** | Botón físico | "Crear", "Guardar", "Eliminar" | `new JButton("Texto")` |
| **JLabel** | Etiqueta pegada | "Nombre:", "Precio:" | `new JLabel("Texto")` |
| **JTextField** | Espacio para escribir | Ingresar nombre de producto | `new JTextField()` |
| **JTextArea** | Hoja de papel | Comentarios multilínea | `new JTextArea()` |
| **JTable** | Hoja de Excel | Mostrar lista de productos | `new JTable()` |
| **JComboBox** | Lista desplegable | Seleccionar unidad de medida | `new JComboBox<>()` |
| **JMenuBar** | Menú de restaurante | Archivo, Editar, Ver, Ayuda | `new JMenuBar()` |
| **JDialog** | Ventana emergente | Crear producto (modal) | `new JDialog()` |

---

## ⚡ CONCEPTOS FUNDAMENTALES (LEER ANTES DE EMPEZAR)

### 1. Event Dispatch Thread (EDT)

**🚨 CRÍTICO DE ENTENDER:**

Swing NO es thread-safe. Todos los componentes Swing deben crearse y modificarse en un hilo especial llamado **Event Dispatch Thread (EDT)**.

#### ¿Qué es el EDT?

```
┌────────────────────────────────────────────────────────────┐
│              HILOS EN UNA APLICACIÓN SWING                  │
├────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌──────────────┐         ┌─────────────────────────┐     │
│  │  Main Thread │         │  Event Dispatch Thread  │     │
│  │              │         │         (EDT)           │     │
│  │   main()     │───crea──│                         │     │
│  │              │         │  ┌─────────────────┐    │     │
│  └──────────────┘         │  │ Crea componentes│    │     │
│                           │  │ Actualiza GUI   │    │     │
│       ❌ NO crear         │  │ Maneja eventos  │    │     │
│       componentes         │  └─────────────────┘    │     │
│       aquí                │                         │     │
│                           │  ✅ ÚNICA forma         │     │
│                           │     segura              │     │
│                           └─────────────────────────┘     │
│                                                             │
└────────────────────────────────────────────────────────────┘
```

#### ¿Por qué es importante?

**❌ INCORRECTO (puede causar bugs extraños):**

```java
public static void main(String[] args) {
    JFrame ventana = new JFrame("App");  // ❌ Creado en main thread
    ventana.setVisible(true);
}
```

**✅ CORRECTO:**

```java
public static void main(String[] args) {
    // Delegar la creación de GUI al EDT
    SwingUtilities.invokeLater(() -> {
        JFrame ventana = new JFrame("App");  // ✅ Creado en EDT
        ventana.setVisible(true);
    });
}
```

#### Analogía:

Imagina que el EDT es un chef en una cocina:
- **Chef (EDT):** Único autorizado para cocinar (modificar GUI)
- **Meseros (otros hilos):** Pueden tomar pedidos, pero deben pasar recetas al chef
- Si un mesero intenta cocinar (modificar GUI desde otro hilo) → 💥 caos en la cocina

**Regla de oro:** SIEMPRE usa `SwingUtilities.invokeLater()` o `SwingUtilities.invokeAndWait()` para modificar GUI desde otro hilo.

---

### 2. Layout Managers (Administradores de Diseño)

Los Layout Managers controlan **cómo se organizan los componentes** dentro de un contenedor (JFrame, JPanel).

#### 2.1 BorderLayout (Por defecto en JFrame)

Divide el contenedor en 5 zonas:

```
┌─────────────────────────────────────┐
│            NORTH (arriba)            │
├──────┬─────────────────────┬─────────┤
│ WEST │       CENTER        │  EAST   │
│ izq. │     (principal)     │  der.   │
├──────┴─────────────────────┴─────────┤
│           SOUTH (abajo)              │
└─────────────────────────────────────┘
```

**Ejemplo:**

```java
JFrame ventana = new JFrame();
ventana.setLayout(new BorderLayout());

ventana.add(new JButton("Arriba"), BorderLayout.NORTH);
ventana.add(new JButton("Abajo"), BorderLayout.SOUTH);
ventana.add(new JButton("Izquierda"), BorderLayout.WEST);
ventana.add(new JButton("Derecha"), BorderLayout.EAST);
ventana.add(new JButton("Centro"), BorderLayout.CENTER);
```

**Uso típico en Forestech:**
```java
ventana.add(menuBar, BorderLayout.NORTH);       // Menú arriba
ventana.add(formulario, BorderLayout.WEST);     // Formulario izquierda
ventana.add(tabla, BorderLayout.CENTER);        // Tabla centro
ventana.add(botones, BorderLayout.SOUTH);       // Botones abajo
```

---

#### 2.2 FlowLayout (Por defecto en JPanel)

Organiza componentes en fila, como texto:

```
┌─────────────────────────────────────┐
│ [Btn1] [Btn2] [Btn3]                │
│ [Btn4] [Btn5]                       │  ← Si no cabe, salta a la siguiente línea
└─────────────────────────────────────┘
```

**Ejemplo:**

```java
JPanel panel = new JPanel(new FlowLayout());  // FlowLayout por defecto
panel.add(new JButton("Guardar"));
panel.add(new JButton("Cancelar"));
panel.add(new JButton("Eliminar"));
```

**Uso típico:** Panel de botones horizontales

---

#### 2.3 GridLayout (Cuadrícula)

Divide el contenedor en una cuadrícula de filas y columnas iguales:

```
GridLayout(3, 2)  // 3 filas, 2 columnas
┌─────────────┬─────────────┐
│  Celda 1    │  Celda 2    │  Fila 1
├─────────────┼─────────────┤
│  Celda 3    │  Celda 4    │  Fila 2
├─────────────┼─────────────┤
│  Celda 5    │  Celda 6    │  Fila 3
└─────────────┴─────────────┘
```

**Ejemplo:**

```java
JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));  // 3 filas, 2 columnas, gap 10px
panel.add(new JLabel("Nombre:"));
panel.add(new JTextField());
panel.add(new JLabel("Precio:"));
panel.add(new JTextField());
panel.add(new JLabel(""));
panel.add(new JButton("Guardar"));
```

**Uso típico:** Formularios con etiquetas y campos

---

#### 2.4 BoxLayout (Apilado vertical u horizontal)

Apila componentes en una línea (vertical u horizontal):

```
BoxLayout.Y_AXIS (vertical):
┌─────────────┐
│  Componente1│
├─────────────┤
│  Componente2│
├─────────────┤
│  Componente3│
└─────────────┘

BoxLayout.X_AXIS (horizontal):
┌──────┬──────┬──────┐
│ Comp1│ Comp2│ Comp3│
└──────┴──────┴──────┘
```

**Ejemplo:**

```java
JPanel panel = new JPanel();
panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
panel.add(new JButton("Botón 1"));
panel.add(Box.createVerticalStrut(10));  // Espacio de 10px
panel.add(new JButton("Botón 2"));
```

**Uso típico:** Menús verticales, barras de herramientas

---

#### 2.5 GridBagLayout (El más potente y complejo)

Permite posicionar componentes con precisión (como una tabla pero flexible):

```
┌─────────┬─────────┬─────────┐
│ (0,0)   │ (1,0)   │ (2,0)   │
├─────────┴─────────┼─────────┤
│ (0,1) colspan=2   │ (2,1)   │  ← Puede ocupar múltiples celdas
├───────────────────┴─────────┤
│ (0,2) colspan=3             │
└─────────────────────────────┘
```

**Ejemplo simplificado:**

```java
JPanel panel = new JPanel(new GridBagLayout());
GridBagConstraints gbc = new GridBagConstraints();

gbc.gridx = 0;  // Columna 0
gbc.gridy = 0;  // Fila 0
panel.add(new JLabel("Nombre:"), gbc);

gbc.gridx = 1;  // Columna 1
gbc.gridy = 0;  // Fila 0
gbc.fill = GridBagConstraints.HORIZONTAL;  // Expandir horizontal
panel.add(new JTextField(), gbc);
```

**Nota:** GridBagLayout es muy potente pero complejo. En Forestech usaremos principalmente BorderLayout, GridLayout y FlowLayout.

---

#### 2.6 CardLayout (Cambiar entre paneles)

Permite tener varios paneles en el mismo espacio y cambiar entre ellos (como pestañas):

```
┌─────────────────────────────────────┐
│  [Panel A activo]                   │  ← Solo uno visible a la vez
│                                     │
│  Panel B (oculto)                   │
│  Panel C (oculto)                   │
└─────────────────────────────────────┘
```

**Ejemplo:**

```java
JPanel contenedor = new JPanel(new CardLayout());
contenedor.add(panelProductos, "productos");
contenedor.add(panelVehiculos, "vehiculos");

// Cambiar entre paneles
CardLayout cl = (CardLayout) contenedor.getLayout();
cl.show(contenedor, "vehiculos");  // Mostrar panel de vehículos
```

**Uso típico:** Asistentes paso a paso, cambiar vistas sin abrir ventanas nuevas

---

#### 2.7 null Layout (Posicionamiento absoluto)

Desactiva el Layout Manager y posiciona componentes manualmente:

```java
JPanel panel = new JPanel(null);  // Sin layout
JButton btn = new JButton("Click");
btn.setBounds(50, 100, 120, 30);  // x, y, ancho, alto
panel.add(btn);
```

**⚠️ NO RECOMENDADO porque:**
- No se adapta a cambios de tamaño
- Difícil de mantener
- Problemas en diferentes resoluciones

**Usa null layout SOLO si:**
- Usas un IDE con editor visual (NetBeans, IntelliJ)
- Necesitas posicionamiento pixel-perfect (muy raro)

---

### 3. Listeners (Escuchadores de Eventos)

Los Listeners detectan acciones del usuario (clic, tecla, cambio de texto, etc.)

#### 3.1 ActionListener (El más común)

Detecta clics en botones, menús, Enter en campos de texto:

```java
JButton boton = new JButton("Guardar");

// Opción 1: Clase anónima (la más común)
boton.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("Botón presionado");
    }
});

// Opción 2: Lambda (más corta)
boton.addActionListener(e -> {
    System.out.println("Botón presionado");
});

// Opción 3: Referencia a método
boton.addActionListener(e -> guardarProducto());
```

---

#### 3.2 KeyListener (Detectar teclas)

Detecta cuando el usuario presiona/suelta teclas:

```java
JTextField campo = new JTextField();

campo.addKeyListener(new KeyAdapter() {
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            System.out.println("Usuario presionó Enter");
        }
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            System.out.println("Usuario presionó Escape");
        }
    }
});
```

**Uso típico:** Atajos de teclado (Ctrl+S, Enter, Escape)

---

#### 3.3 WindowListener (Detectar eventos de ventana)

Detecta cuando la ventana se cierra, minimiza, etc.:

```java
ventana.addWindowListener(new WindowAdapter() {
    @Override
    public void windowClosing(WindowEvent e) {
        int opcion = JOptionPane.showConfirmDialog(
            ventana,
            "¿Seguro que quieres salir?",
            "Confirmar",
            JOptionPane.YES_NO_OPTION
        );

        if (opcion == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
});
```

**Uso típico:** Confirmar antes de cerrar, guardar cambios

---

#### 3.4 MouseListener (Detectar clics del mouse)

Detecta clics, hover, etc.:

```java
tabla.addMouseListener(new MouseAdapter() {
    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {  // Doble clic
            int fila = tabla.getSelectedRow();
            System.out.println("Doble clic en fila: " + fila);
        }
    }
});
```

**Uso típico:** Doble clic en tabla para editar

---

### 4. Patrón MVC en Swing

**MVC = Model-View-Controller** (Modelo-Vista-Controlador)

```
┌────────────────────────────────────────────────────────────┐
│                        PATRÓN MVC                           │
├────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌─────────────┐        ┌──────────────┐                  │
│  │   MODEL     │◄───────│ CONTROLLER   │                  │
│  │  (Datos)    │        │  (Lógica)    │                  │
│  │             │        │              │                  │
│  │ - Product   │        │ ActionListener│                  │
│  │ - Services  │        │ - guardar()  │                  │
│  └─────────────┘        │ - eliminar() │                  │
│        ▲                └──────────────┘                  │
│        │                       ▲                           │
│        │                       │                           │
│        │                       │                           │
│  ┌─────┴────────┐       ┌──────┴───────┐                  │
│  │   VIEW       │───────│              │                  │
│  │  (UI/GUI)    │       │              │                  │
│  │              │       │              │                  │
│  │ - JFrame     │       │              │                  │
│  │ - JTable     │       │              │                  │
│  │ - JTextField │       │              │                  │
│  └──────────────┘       └──────────────┘                  │
│                                                             │
└────────────────────────────────────────────────────────────┘
```

**En Forestech:**

```
MODEL:
├── Product.java
├── Vehicle.java
├── Movement.java
└── *Services.java

CONTROLLER:
├── ProductController.java
└── ActionListeners en cada botón

VIEW:
├── ProductManagerGUI.java (JFrame)
├── ProductFormDialog.java (JDialog)
└── ProductTablePanel.java (JPanel)
```

**Regla de separación:**
- **Model:** Clases de datos y lógica de negocio (NO conoce Swing)
- **View:** Solo componentes visuales (NO conoce lógica de negocio)
- **Controller:** Conecta ambos (maneja eventos y llama a Services)

---

## 🗺️ ROADMAP DE APRENDIZAJE (12 Checkpoints)

---

### **Checkpoint 9.1: Tu Primera Ventana** ⏱️ 30 min

#### 📚 Conceptos:
- JFrame (ventana principal)
- setSize(), setVisible()
- setDefaultCloseOperation()
- SwingUtilities.invokeLater()

#### 🎯 Ejercicio Práctico:

**Crear `com/forestech/ui/HelloSwingApp.java`:**

```java
package com.forestech.ui;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Tu primera ventana en Swing.
 *
 * CONCEPTOS CLAVE:
 * 1. JFrame = Ventana principal con borde, título y botones minimizar/cerrar
 * 2. SwingUtilities.invokeLater() = Ejecuta código en el EDT (thread seguro)
 * 3. setDefaultCloseOperation() = Define qué hacer al cerrar
 */
public class HelloSwingApp {

    public static void main(String[] args) {
        // PASO 1: Delegar la creación de GUI al Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            crearYMostrarVentana();
        });
    }

    private static void crearYMostrarVentana() {
        // PASO 2: Crear la ventana
        JFrame ventana = new JFrame("Mi Primera Ventana - Forestech");

        // PASO 3: Configurar tamaño (ancho, alto en píxeles)
        ventana.setSize(500, 400);

        // PASO 4: Definir qué hacer al cerrar
        // EXIT_ON_CLOSE = Terminar programa al cerrar ventana
        // HIDE_ON_CLOSE = Solo ocultar (programa sigue corriendo)
        // DO_NOTHING_ON_CLOSE = No hacer nada (útil con WindowListener)
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // PASO 5: Centrar en la pantalla
        // null = centrar respecto a la pantalla
        // Si pasas otro componente, centra respecto a ese componente
        ventana.setLocationRelativeTo(null);

        // PASO 6: Hacer visible (SIEMPRE al final)
        ventana.setVisible(true);

        System.out.println("✅ Ventana creada exitosamente");
    }
}
```

#### 🔍 Explicación Detallada:

**¿Qué hace `SwingUtilities.invokeLater()`?**

```java
SwingUtilities.invokeLater(() -> {
    // Este código se ejecuta en el Event Dispatch Thread (EDT)
    crearYMostrarVentana();
});
```

Es equivalente a:

```java
SwingUtilities.invokeLater(new Runnable() {
    @Override
    public void run() {
        crearYMostrarVentana();
    }
});
```

**Diferencia entre EXIT_ON_CLOSE y DISPOSE_ON_CLOSE:**

```java
// EXIT_ON_CLOSE: Al cerrar ventana, termina TODO el programa
ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
// Equivale a: System.exit(0);

// DISPOSE_ON_CLOSE: Al cerrar ventana, solo libera recursos de esta ventana
ventana.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
// Útil cuando tienes múltiples ventanas

// HIDE_ON_CLOSE: Al cerrar ventana, solo la oculta (sigue existiendo en memoria)
ventana.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
// Útil para ventanas que abres/cierras repetidamente

// DO_NOTHING_ON_CLOSE: No hace nada al cerrar
ventana.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
// Útil con WindowListener para mostrar confirmación
```

#### ✅ Criterio de Éxito:
- [x] Al ejecutar, aparece una ventana vacía de 500x400 píxeles
- [x] Tiene título "Mi Primera Ventana - Forestech"
- [x] Está centrada en la pantalla
- [x] Al cerrar con X, el programa termina
- [x] En consola aparece "✅ Ventana creada exitosamente"

#### 🏃 Cómo ejecutar:

```bash
# Opción 1: Desde línea de comandos
cd /home/hp/forestechOil/forestech-cli-java
mvn clean compile
mvn exec:java -Dexec.mainClass="com.forestech.ui.HelloSwingApp"

# Opción 2: Desde IDE (IntelliJ, Eclipse, VSCode)
# Clic derecho en HelloSwingApp.java → Run
```

---

### **Checkpoint 9.2: Botones y Etiquetas** ⏱️ 45 min

#### 📚 Conceptos:
- JLabel (texto estático)
- JButton (botón clickeable)
- ActionListener (detectar clics)
- BorderLayout (organizar componentes)

#### 🎯 Ejercicio Práctico:

**Crear `com/forestech/ui/ButtonExampleApp.java`:**

```java
package com.forestech.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Ejemplo de botones y etiquetas con ActionListener.
 *
 * CONCEPTOS CLAVE:
 * 1. JLabel = Texto estático (no editable por usuario)
 * 2. JButton = Botón clickeable
 * 3. ActionListener = Evento que se ejecuta al hacer clic
 * 4. BorderLayout = Organiza componentes en 5 zonas (NORTH, SOUTH, EAST, WEST, CENTER)
 */
public class ButtonExampleApp {

    private static int contador = 0;  // Contador de clics

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            crearYMostrarVentana();
        });
    }

    private static void crearYMostrarVentana() {
        JFrame ventana = new JFrame("Ejemplo de Botones - Forestech");
        ventana.setSize(450, 250);
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // ========================================================================
        // COMPONENTE 1: Etiqueta (texto fijo)
        // ========================================================================
        JLabel etiqueta = new JLabel("Haz clic en el botón", SwingConstants.CENTER);
        etiqueta.setFont(new Font("Arial", Font.BOLD, 16));
        etiqueta.setForeground(Color.BLUE);  // Color del texto

        // ========================================================================
        // COMPONENTE 2: Botón
        // ========================================================================
        JButton boton = new JButton("Haz clic aquí");
        boton.setFont(new Font("Arial", Font.PLAIN, 14));

        // ========================================================================
        // COMPONENTE 3: Evento del botón (qué hacer al hacer clic)
        // ========================================================================
        // Opción A: Clase anónima (la más común en tutoriales)
        boton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                contador++;
                etiqueta.setText("¡Botón presionado " + contador + " veces!");
                System.out.println("Usuario hizo clic (total: " + contador + ")");

                // Cambiar color del texto según contador
                if (contador % 2 == 0) {
                    etiqueta.setForeground(Color.GREEN);
                } else {
                    etiqueta.setForeground(Color.RED);
                }
            }
        });

        // Opción B: Lambda (más corta, equivalente a la anterior)
        // boton.addActionListener(e -> {
        //     etiqueta.setText("¡Botón presionado!");
        // });

        // ========================================================================
        // ORGANIZAR COMPONENTES (usando BorderLayout)
        // ========================================================================
        ventana.setLayout(new BorderLayout(10, 10));  // Espaciado de 10px
        ventana.add(etiqueta, BorderLayout.NORTH);    // Etiqueta arriba
        ventana.add(boton, BorderLayout.CENTER);      // Botón al centro

        // Panel inferior con información
        JLabel lblInfo = new JLabel("BorderLayout: NORTH (arriba) y CENTER (centro)");
        lblInfo.setFont(new Font("Arial", Font.ITALIC, 10));
        lblInfo.setHorizontalAlignment(SwingConstants.CENTER);
        ventana.add(lblInfo, BorderLayout.SOUTH);

        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
    }
}
```

#### 🔍 Explicación Detallada:

**1. ActionListener - Analogía del timbre:**

```
MUNDO REAL                          JAVA SWING
═══════════════                     ═══════════

Tu casa:                            Tu ventana:
┌────────────┐                      ┌────────────┐
│  [Timbre]  │ ← presionar         │  [JButton] │ ← clic
└──────┬─────┘                      └──────┬─────┘
       │                                   │
       │ activación                        │ actionPerformed()
       ▼                                   ▼
┌────────────┐                      ┌────────────┐
│  Campana   │ ← suena              │ Código     │ ← se ejecuta
│  suena     │                      │ dentro del │
└────────────┘                      │ listener   │
                                    └────────────┘
```

**2. Diferencia entre clase anónima y lambda:**

```java
// OPCIÓN 1: Clase anónima (verbosa pero clara)
boton.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("Clic");
    }
});

// OPCIÓN 2: Lambda (más corta, Java 8+)
boton.addActionListener(e -> {
    System.out.println("Clic");
});

// OPCIÓN 3: Lambda de una sola línea
boton.addActionListener(e -> System.out.println("Clic"));

// OPCIÓN 4: Referencia a método
boton.addActionListener(e -> manejarClic());

private static void manejarClic() {
    System.out.println("Clic");
}
```

**3. BorderLayout explicado visualmente:**

```
ventana.setLayout(new BorderLayout());
ventana.add(componente1, BorderLayout.NORTH);
ventana.add(componente2, BorderLayout.CENTER);
ventana.add(componente3, BorderLayout.SOUTH);

Resultado:
┌─────────────────────────────────────┐
│         componente1 (NORTH)          │ ← Altura ajustada al contenido
├─────────────────────────────────────┤
│                                     │
│       componente2 (CENTER)          │ ← Toma todo el espacio restante
│                                     │
├─────────────────────────────────────┤
│         componente3 (SOUTH)          │ ← Altura ajustada al contenido
└─────────────────────────────────────┘
```

**4. Métodos útiles de JLabel:**

```java
JLabel label = new JLabel("Texto");

// Alineación horizontal
label.setHorizontalAlignment(SwingConstants.CENTER);  // Centro
label.setHorizontalAlignment(SwingConstants.LEFT);    // Izquierda
label.setHorizontalAlignment(SwingConstants.RIGHT);   // Derecha

// Alineación vertical
label.setVerticalAlignment(SwingConstants.TOP);       // Arriba
label.setVerticalAlignment(SwingConstants.CENTER);    // Centro
label.setVerticalAlignment(SwingConstants.BOTTOM);    // Abajo

// Cambiar texto
label.setText("Nuevo texto");

// Cambiar color
label.setForeground(Color.RED);       // Color de texto
label.setBackground(Color.YELLOW);    // Color de fondo
label.setOpaque(true);                // NECESARIO para ver el fondo

// Cambiar fuente
label.setFont(new Font("Arial", Font.BOLD, 16));
```

**5. Métodos útiles de JButton:**

```java
JButton btn = new JButton("Guardar");

// Cambiar texto
btn.setText("Nuevo texto");

// Habilitar/deshabilitar
btn.setEnabled(false);  // Botón deshabilitado (gris, no clickeable)
btn.setEnabled(true);   // Botón habilitado

// Cambiar color
btn.setBackground(Color.GREEN);

// Cambiar fuente
btn.setFont(new Font("Arial", Font.BOLD, 14));

// Agregar tooltip (texto al pasar el mouse)
btn.setToolTipText("Haz clic para guardar el producto");

// Agregar ícono
ImageIcon icon = new ImageIcon("ruta/icono.png");
btn.setIcon(icon);

// Simular clic desde código
btn.doClick();
```

#### ✅ Criterio de Éxito:
- [x] Al hacer clic en el botón, el texto de la etiqueta cambia
- [x] El contador incrementa con cada clic
- [x] El color del texto alterna entre verde y rojo
- [x] En consola aparece el número total de clics

---

### **Checkpoint 9.3: Campos de Texto y Formularios** ⏱️ 1 hora

#### 📚 Conceptos:
- JTextField (campo de texto de una línea)
- JTextArea (campo multilínea)
- JPasswordField (campo de contraseña)
- GridLayout (cuadrícula)
- JOptionPane (ventanas emergentes)

#### 🎯 Ejercicio Práctico:

**Crear `com/forestech/ui/FormularioProductoSimple.java`:**

```java
package com.forestech.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Formulario simple para crear un producto (sin guardar en BD).
 *
 * CONCEPTOS CLAVE:
 * 1. JTextField = Campo de texto de una sola línea
 * 2. GridLayout = Organiza componentes en cuadrícula (filas x columnas)
 * 3. JOptionPane = Ventanas emergentes (alertas, confirmaciones)
 * 4. Validación básica de campos
 */
public class FormularioProductoSimple {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            crearYMostrarFormulario();
        });
    }

    private static void crearYMostrarFormulario() {
        JFrame ventana = new JFrame("Crear Producto - Forestech");
        ventana.setSize(450, 250);
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // ========================================================================
        // PANEL PRINCIPAL con GridLayout (4 filas, 2 columnas)
        // ========================================================================
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));  // Márgenes

        // ========================================================================
        // FILA 1: Nombre
        // ========================================================================
        JLabel lblNombre = new JLabel("Nombre:");
        lblNombre.setFont(new Font("Arial", Font.BOLD, 12));
        JTextField txtNombre = new JTextField();
        txtNombre.setToolTipText("Ingresa el nombre del producto (ej: ACPM)");

        // ========================================================================
        // FILA 2: Unidad de Medida
        // ========================================================================
        JLabel lblUnidad = new JLabel("Unidad:");
        lblUnidad.setFont(new Font("Arial", Font.BOLD, 12));
        JTextField txtUnidad = new JTextField();
        txtUnidad.setToolTipText("Unidad: GALON, GARRAFA, CUARTO, CANECA");

        // ========================================================================
        // FILA 3: Precio
        // ========================================================================
        JLabel lblPrecio = new JLabel("Precio:");
        lblPrecio.setFont(new Font("Arial", Font.BOLD, 12));
        JTextField txtPrecio = new JTextField();
        txtPrecio.setToolTipText("Precio por unidad (números solamente)");

        // ========================================================================
        // FILA 4: Botones
        // ========================================================================
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");

        // Evento: Guardar
        btnGuardar.addActionListener(e -> {
            String nombre = txtNombre.getText().trim();
            String unidad = txtUnidad.getText().trim();
            String precioStr = txtPrecio.getText().trim();

            // VALIDACIÓN 1: Campos vacíos
            if (nombre.isEmpty() || unidad.isEmpty() || precioStr.isEmpty()) {
                JOptionPane.showMessageDialog(
                    ventana,
                    "❌ Todos los campos son obligatorios",
                    "Error de Validación",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            // VALIDACIÓN 2: Precio debe ser número
            try {
                double precio = Double.parseDouble(precioStr);

                // VALIDACIÓN 3: Precio debe ser positivo
                if (precio <= 0) {
                    JOptionPane.showMessageDialog(
                        ventana,
                        "❌ El precio debe ser mayor a cero",
                        "Error de Validación",
                        JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }

                // ✅ TODO VÁLIDO: Mostrar resumen
                String mensaje = String.format(
                    "Producto creado exitosamente:\n\n" +
                    "Nombre: %s\n" +
                    "Unidad: %s\n" +
                    "Precio: $%,.2f",
                    nombre, unidad, precio
                );

                JOptionPane.showMessageDialog(
                    ventana,
                    mensaje,
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE
                );

                // Limpiar campos
                txtNombre.setText("");
                txtUnidad.setText("");
                txtPrecio.setText("");

                // Poner foco en el primer campo
                txtNombre.requestFocus();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                    ventana,
                    "❌ El precio debe ser un número válido\n" +
                    "Ejemplo: 8500 o 8500.50",
                    "Error de Validación",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });

        // Evento: Cancelar
        btnCancelar.addActionListener(e -> {
            int opcion = JOptionPane.showConfirmDialog(
                ventana,
                "¿Estás seguro de cancelar?\nSe perderán los datos ingresados.",
                "Confirmar Cancelación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );

            if (opcion == JOptionPane.YES_OPTION) {
                txtNombre.setText("");
                txtUnidad.setText("");
                txtPrecio.setText("");
            }
        });

        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);

        // ========================================================================
        // AGREGAR COMPONENTES AL PANEL
        // ========================================================================
        panel.add(lblNombre);
        panel.add(txtNombre);
        panel.add(lblUnidad);
        panel.add(txtUnidad);
        panel.add(lblPrecio);
        panel.add(txtPrecio);
        panel.add(new JLabel(""));  // Celda vacía
        panel.add(panelBotones);

        ventana.add(panel);
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);

        // Poner foco inicial en el primer campo
        txtNombre.requestFocus();
    }
}
```

#### 🔍 Explicación Detallada:

**1. GridLayout explicado:**

```java
new GridLayout(4, 2, 10, 10)
               │  │   │   │
               │  │   │   └─ Espaciado vertical (10px)
               │  │   └───── Espaciado horizontal (10px)
               │  └───────── Columnas (2)
               └──────────── Filas (4)

Resultado visual:
┌──────────────┬──────────────┐
│ lblNombre    │ txtNombre    │  Fila 1
├──────────────┼──────────────┤
│ lblUnidad    │ txtUnidad    │  Fila 2
├──────────────┼──────────────┤
│ lblPrecio    │ txtPrecio    │  Fila 3
├──────────────┼──────────────┤
│ (vacío)      │ botones      │  Fila 4
└──────────────┴──────────────┘
   Columna 1      Columna 2
```

**2. JOptionPane - Tipos de mensajes:**

```java
// MENSAJE DE ERROR (X roja)
JOptionPane.showMessageDialog(
    ventana,
    "❌ Error al guardar",
    "Error",
    JOptionPane.ERROR_MESSAGE
);

// MENSAJE DE ÉXITO (i azul)
JOptionPane.showMessageDialog(
    ventana,
    "✅ Guardado exitosamente",
    "Éxito",
    JOptionPane.INFORMATION_MESSAGE
);

// MENSAJE DE ADVERTENCIA (! amarillo)
JOptionPane.showMessageDialog(
    ventana,
    "⚠️ Operación peligrosa",
    "Advertencia",
    JOptionPane.WARNING_MESSAGE
);

// PREGUNTA (? azul)
JOptionPane.showMessageDialog(
    ventana,
    "¿Sabías que...?",
    "Información",
    JOptionPane.QUESTION_MESSAGE
);

// SIMPLE (sin ícono)
JOptionPane.showMessageDialog(
    ventana,
    "Mensaje simple",
    "Título",
    JOptionPane.PLAIN_MESSAGE
);
```

**3. JOptionPane - Confirmaciones:**

```java
int respuesta = JOptionPane.showConfirmDialog(
    ventana,
    "¿Estás seguro?",
    "Confirmar",
    JOptionPane.YES_NO_OPTION  // Botones: Sí, No
);

if (respuesta == JOptionPane.YES_OPTION) {
    System.out.println("Usuario dijo SÍ");
} else if (respuesta == JOptionPane.NO_OPTION) {
    System.out.println("Usuario dijo NO");
}

// Otras opciones:
// YES_NO_CANCEL_OPTION → Botones: Sí, No, Cancelar
// OK_CANCEL_OPTION → Botones: Aceptar, Cancelar
```

**4. JOptionPane - Entrada de datos:**

```java
// Pedir texto al usuario
String nombre = JOptionPane.showInputDialog(
    ventana,
    "Ingresa tu nombre:",
    "Entrada de Datos",
    JOptionPane.QUESTION_MESSAGE
);

if (nombre != null && !nombre.trim().isEmpty()) {
    System.out.println("Nombre: " + nombre);
} else {
    System.out.println("Usuario canceló o no ingresó nada");
}
```

**5. Métodos útiles de JTextField:**

```java
JTextField campo = new JTextField();

// Obtener texto
String texto = campo.getText();

// Establecer texto
campo.setText("Nuevo texto");

// Limitar caracteres (por ejemplo, máximo 20)
campo.setColumns(20);

// Hacer solo lectura (no editable)
campo.setEditable(false);

// Cambiar color de fondo
campo.setBackground(Color.YELLOW);

// Tooltip (ayuda al pasar el mouse)
campo.setToolTipText("Ingresa tu nombre aquí");

// Poner foco en este campo
campo.requestFocus();

// Seleccionar todo el texto
campo.selectAll();

// Agregar evento al presionar Enter
campo.addActionListener(e -> {
    System.out.println("Usuario presionó Enter");
});
```

**6. BorderFactory - Crear bordes:**

```java
// Borde vacío (márgenes)
panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
//                                              top  left bottom right

// Borde con línea
panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
//                                             color      grosor

// Borde con título
panel.setBorder(BorderFactory.createTitledBorder("Datos del Producto"));

// Combinar bordes
Border borde1 = BorderFactory.createLineBorder(Color.GRAY);
Border borde2 = BorderFactory.createEmptyBorder(10, 10, 10, 10);
panel.setBorder(BorderFactory.createCompoundBorder(borde1, borde2));
```

#### ✅ Criterio de Éxito:
- [x] Formulario con 3 campos organizados en cuadrícula
- [x] Botón "Guardar" valida campos vacíos
- [x] Botón "Guardar" valida que el precio sea número
- [x] Botón "Guardar" valida que el precio sea positivo
- [x] Muestra ventana emergente con resumen del producto
- [x] Limpia los campos después de guardar
- [x] Botón "Cancelar" pide confirmación

---

### **Checkpoint 9.4: JComboBox (Listas Desplegables)** ⏱️ 45 min

#### 📚 Conceptos:
- JComboBox (lista desplegable)
- Agregar/obtener elementos
- ItemListener (detectar cambios de selección)

#### 🎯 Ejercicio Práctico:

**Crear `com/forestech/ui/FormularioConComboBox.java`:**

```java
package com.forestech.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Formulario con JComboBox (lista desplegable).
 *
 * USO EN FORESTECH:
 * - Seleccionar unidad de medida (GALON, GARRAFA, CUARTO, CANECA)
 * - Seleccionar tipo de movimiento (ENTRADA, SALIDA)
 * - Seleccionar producto de una lista
 */
public class FormularioConComboBox {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            crearFormulario();
        });
    }

    private static void crearFormulario() {
        JFrame ventana = new JFrame("JComboBox - Forestech");
        ventana.setSize(450, 300);
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ========================================================================
        // CAMPO 1: Nombre (JTextField normal)
        // ========================================================================
        panel.add(new JLabel("Nombre:"));
        JTextField txtNombre = new JTextField();
        panel.add(txtNombre);

        // ========================================================================
        // CAMPO 2: Unidad de Medida (JComboBox)
        // ========================================================================
        panel.add(new JLabel("Unidad:"));

        // Opción A: Agregar items uno por uno
        JComboBox<String> cmbUnidad = new JComboBox<>();
        cmbUnidad.addItem("GALON");
        cmbUnidad.addItem("GARRAFA");
        cmbUnidad.addItem("CUARTO");
        cmbUnidad.addItem("CANECA");

        // Opción B: Pasar array en el constructor
        // String[] unidades = {"GALON", "GARRAFA", "CUARTO", "CANECA"};
        // JComboBox<String> cmbUnidad = new JComboBox<>(unidades);

        // Seleccionar el primer elemento por defecto
        cmbUnidad.setSelectedIndex(0);

        panel.add(cmbUnidad);

        // ========================================================================
        // CAMPO 3: Categoría (JComboBox con opción "Seleccione...")
        // ========================================================================
        panel.add(new JLabel("Categoría:"));

        String[] categorias = {
            "-- Seleccione --",
            "Combustible",
            "Lubricante",
            "Grasa",
            "Otro"
        };
        JComboBox<String> cmbCategoria = new JComboBox<>(categorias);
        panel.add(cmbCategoria);

        // ========================================================================
        // CAMPO 4: Precio
        // ========================================================================
        panel.add(new JLabel("Precio:"));
        JTextField txtPrecio = new JTextField();
        panel.add(txtPrecio);

        // ========================================================================
        // CAMPO 5: Etiqueta de estado (cambia según selección)
        // ========================================================================
        JLabel lblEstado = new JLabel("Selecciona una unidad y categoría");
        lblEstado.setFont(new Font("Arial", Font.ITALIC, 10));
        lblEstado.setHorizontalAlignment(SwingConstants.CENTER);

        // ========================================================================
        // EVENTO: Detectar cambio en ComboBox de unidad
        // ========================================================================
        cmbUnidad.addActionListener(e -> {
            String unidadSeleccionada = (String) cmbUnidad.getSelectedItem();
            lblEstado.setText("Unidad seleccionada: " + unidadSeleccionada);
        });

        // ========================================================================
        // EVENTO: Detectar cambio en ComboBox de categoría
        // ========================================================================
        cmbCategoria.addActionListener(e -> {
            int indice = cmbCategoria.getSelectedIndex();
            String categoriaSeleccionada = (String) cmbCategoria.getSelectedItem();

            if (indice == 0) {  // "-- Seleccione --"
                lblEstado.setForeground(Color.RED);
                lblEstado.setText("⚠️ Debes seleccionar una categoría");
            } else {
                lblEstado.setForeground(Color.BLUE);
                lblEstado.setText("Categoría: " + categoriaSeleccionada);
            }
        });

        // ========================================================================
        // BOTÓN GUARDAR
        // ========================================================================
        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(e -> {
            String nombre = txtNombre.getText().trim();
            String unidad = (String) cmbUnidad.getSelectedItem();
            int categoriaIndex = cmbCategoria.getSelectedIndex();
            String categoria = (String) cmbCategoria.getSelectedItem();
            String precioStr = txtPrecio.getText().trim();

            // Validación: Categoría
            if (categoriaIndex == 0) {
                JOptionPane.showMessageDialog(
                    ventana,
                    "❌ Debes seleccionar una categoría válida",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            // Validación: Otros campos
            if (nombre.isEmpty() || precioStr.isEmpty()) {
                JOptionPane.showMessageDialog(
                    ventana,
                    "❌ Completa todos los campos",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            try {
                double precio = Double.parseDouble(precioStr);

                String mensaje = String.format(
                    "Producto:\n\n" +
                    "Nombre: %s\n" +
                    "Unidad: %s\n" +
                    "Categoría: %s\n" +
                    "Precio: $%,.2f",
                    nombre, unidad, categoria, precio
                );

                JOptionPane.showMessageDialog(
                    ventana,
                    mensaje,
                    "Producto Creado",
                    JOptionPane.INFORMATION_MESSAGE
                );

                // Limpiar formulario
                txtNombre.setText("");
                cmbUnidad.setSelectedIndex(0);
                cmbCategoria.setSelectedIndex(0);
                txtPrecio.setText("");
                txtNombre.requestFocus();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                    ventana,
                    "❌ El precio debe ser un número",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });

        panel.add(lblEstado);
        panel.add(btnGuardar);

        ventana.add(panel);
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
    }
}
```

#### 🔍 Métodos útiles de JComboBox:

```java
JComboBox<String> combo = new JComboBox<>();

// AGREGAR ITEMS
combo.addItem("Item 1");
combo.addItem("Item 2");

// O pasar array en constructor
String[] items = {"Item 1", "Item 2", "Item 3"};
JComboBox<String> combo2 = new JComboBox<>(items);

// OBTENER ITEM SELECCIONADO
String seleccionado = (String) combo.getSelectedItem();
int indice = combo.getSelectedIndex();  // -1 si no hay selección

// SELECCIONAR ITEM PROGRAMÁTICAMENTE
combo.setSelectedIndex(0);      // Por índice
combo.setSelectedItem("Item 2"); // Por valor

// ELIMINAR ITEM
combo.removeItem("Item 1");
combo.removeItemAt(0);  // Por índice

// ELIMINAR TODOS LOS ITEMS
combo.removeAllItems();

// CANTIDAD DE ITEMS
int cantidad = combo.getItemCount();

// HACER EDITABLE (el usuario puede escribir)
combo.setEditable(true);

// DESHABILITAR
combo.setEnabled(false);

// EVENTO: Detectar cambio de selección
combo.addActionListener(e -> {
    String seleccionado = (String) combo.getSelectedItem();
    System.out.println("Seleccionado: " + seleccionado);
});
```

#### ✅ Criterio de Éxito:
- [x] ComboBox de unidad muestra 4 opciones
- [x] ComboBox de categoría tiene opción "-- Seleccione --"
- [x] Al cambiar selección, se actualiza la etiqueta de estado
- [x] Validación: no permite guardar si categoría es "-- Seleccione --"
- [x] Muestra resumen con valores seleccionados

---

### **Checkpoint 9.5: JTable (Tablas)** ⏱️ 1 hora 30 min

#### 📚 Conceptos:
- JTable (tabla tipo Excel)
- DefaultTableModel (modelo de datos)
- JScrollPane (scroll para tablas largas)
- Agregar/eliminar filas dinámicamente
- Detectar selección de fila

#### 🎯 Ejercicio Práctico:

**Crear `com/forestech/ui/TablaProductosApp.java`:**

```java
package com.forestech.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Ejemplo de JTable para mostrar productos.
 *
 * CONCEPTOS:
 * 1. JTable = Componente visual de la tabla
 * 2. DefaultTableModel = Maneja los datos (filas y columnas)
 * 3. JScrollPane = Agrega scroll si hay muchas filas
 * 4. getSelectedRow() = Obtener fila seleccionada
 */
public class TablaProductosApp {

    private static DefaultTableModel modelo;
    private static JTable tabla;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            crearVentana();
        });
    }

    private static void crearVentana() {
        JFrame ventana = new JFrame("JTable - Lista de Productos");
        ventana.setSize(700, 500);
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setLayout(new BorderLayout());

        // ========================================================================
        // PANEL SUPERIOR: Formulario para agregar productos
        // ========================================================================
        JPanel panelFormulario = crearPanelFormulario(ventana);
        ventana.add(panelFormulario, BorderLayout.NORTH);

        // ========================================================================
        // PANEL CENTRAL: Tabla
        // ========================================================================
        JPanel panelTabla = crearPanelTabla();
        ventana.add(panelTabla, BorderLayout.CENTER);

        // ========================================================================
        // PANEL INFERIOR: Botones de acción
        // ========================================================================
        JPanel panelBotones = crearPanelBotones(ventana);
        ventana.add(panelBotones, BorderLayout.SOUTH);

        // Agregar productos de ejemplo
        agregarProductosEjemplo();

        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
    }

    private static JPanel crearPanelFormulario(JFrame ventana) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Agregar Producto"));

        panel.add(new JLabel("Nombre:"));
        JTextField txtNombre = new JTextField(15);
        panel.add(txtNombre);

        panel.add(new JLabel("Unidad:"));
        String[] unidades = {"GALON", "GARRAFA", "CUARTO", "CANECA"};
        JComboBox<String> cmbUnidad = new JComboBox<>(unidades);
        panel.add(cmbUnidad);

        panel.add(new JLabel("Precio:"));
        JTextField txtPrecio = new JTextField(10);
        panel.add(txtPrecio);

        JButton btnAgregar = new JButton("Agregar a Tabla");
        btnAgregar.addActionListener(e -> {
            String nombre = txtNombre.getText().trim();
            String unidad = (String) cmbUnidad.getSelectedItem();
            String precioStr = txtPrecio.getText().trim();

            if (nombre.isEmpty() || precioStr.isEmpty()) {
                JOptionPane.showMessageDialog(ventana, "Completa todos los campos");
                return;
            }

            try {
                double precio = Double.parseDouble(precioStr);

                // Generar ID simulado
                String id = "FUE-" + String.format("%08d", modelo.getRowCount() + 1);

                // Agregar fila a la tabla
                modelo.addRow(new Object[]{
                    id,
                    nombre,
                    unidad,
                    String.format("$%,.2f", precio)
                });

                // Limpiar campos
                txtNombre.setText("");
                txtPrecio.setText("");
                txtNombre.requestFocus();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(ventana, "El precio debe ser un número");
            }
        });
        panel.add(btnAgregar);

        return panel;
    }

    private static JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Productos Registrados"));

        // ========================================================================
        // DEFINIR COLUMNAS
        // ========================================================================
        String[] columnas = {"ID", "Nombre", "Unidad", "Precio"};

        // ========================================================================
        // CREAR MODELO DE TABLA (no editable)
        // ========================================================================
        modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;  // Ninguna celda es editable
            }
        };

        // ========================================================================
        // CREAR TABLA CON EL MODELO
        // ========================================================================
        tabla = new JTable(modelo);

        // Personalizar tabla
        tabla.setFont(new Font("Arial", Font.PLAIN, 12));
        tabla.setRowHeight(25);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);  // Una sola fila a la vez
        tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tabla.getTableHeader().setBackground(new Color(70, 130, 180));
        tabla.getTableHeader().setForeground(Color.WHITE);

        // Ajustar ancho de columnas
        tabla.getColumnModel().getColumn(0).setPreferredWidth(120);  // ID
        tabla.getColumnModel().getColumn(1).setPreferredWidth(200);  // Nombre
        tabla.getColumnModel().getColumn(2).setPreferredWidth(100);  // Unidad
        tabla.getColumnModel().getColumn(3).setPreferredWidth(100);  // Precio

        // ========================================================================
        // AGREGAR SCROLL (importante para tablas grandes)
        // ========================================================================
        JScrollPane scrollPane = new JScrollPane(tabla);
        panel.add(scrollPane, BorderLayout.CENTER);

        // ========================================================================
        // EVENTO: Doble clic en fila
        // ========================================================================
        tabla.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {  // Doble clic
                    int fila = tabla.getSelectedRow();
                    if (fila != -1) {
                        String id = (String) modelo.getValueAt(fila, 0);
                        String nombre = (String) modelo.getValueAt(fila, 1);
                        JOptionPane.showMessageDialog(
                            null,
                            "Producto: " + nombre + "\nID: " + id,
                            "Detalle",
                            JOptionPane.INFORMATION_MESSAGE
                        );
                    }
                }
            }
        });

        return panel;
    }

    private static JPanel crearPanelBotones(JFrame ventana) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        // Botón: Eliminar seleccionado
        JButton btnEliminar = new JButton("Eliminar Seleccionado");
        btnEliminar.addActionListener(e -> {
            int filaSeleccionada = tabla.getSelectedRow();

            if (filaSeleccionada == -1) {
                JOptionPane.showMessageDialog(ventana, "Selecciona un producto de la tabla");
                return;
            }

            String nombre = (String) modelo.getValueAt(filaSeleccionada, 1);

            int confirmacion = JOptionPane.showConfirmDialog(
                ventana,
                "¿Eliminar el producto '" + nombre + "'?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION
            );

            if (confirmacion == JOptionPane.YES_OPTION) {
                modelo.removeRow(filaSeleccionada);
                JOptionPane.showMessageDialog(ventana, "Producto eliminado");
            }
        });
        panel.add(btnEliminar);

        // Botón: Limpiar tabla
        JButton btnLimpiar = new JButton("Limpiar Tabla");
        btnLimpiar.addActionListener(e -> {
            int confirmacion = JOptionPane.showConfirmDialog(
                ventana,
                "¿Eliminar TODOS los productos?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );

            if (confirmacion == JOptionPane.YES_OPTION) {
                modelo.setRowCount(0);  // Eliminar todas las filas
                JOptionPane.showMessageDialog(ventana, "Tabla limpiada");
            }
        });
        panel.add(btnLimpiar);

        // Botón: Contar filas
        JButton btnContar = new JButton("Contar Productos");
        btnContar.addActionListener(e -> {
            int total = modelo.getRowCount();
            JOptionPane.showMessageDialog(
                ventana,
                "Total de productos: " + total,
                "Información",
                JOptionPane.INFORMATION_MESSAGE
            );
        });
        panel.add(btnContar);

        return panel;
    }

    private static void agregarProductosEjemplo() {
        modelo.addRow(new Object[]{"FUE-00000001", "ACPM", "GALON", "$8,500.00"});
        modelo.addRow(new Object[]{"FUE-00000002", "Mobil Delvac", "GARRAFA", "$45,000.00"});
        modelo.addRow(new Object[]{"FUE-00000003", "Aceite Hidráulico", "CUARTO", "$12,000.00"});
        modelo.addRow(new Object[]{"FUE-00000004", "Grasa Industrial", "CANECA", "$25,000.00"});
    }
}
```

#### 🔍 Métodos útiles de JTable y DefaultTableModel:

```java
// DEFAULTTABLEMODEL

// Agregar fila
modelo.addRow(new Object[]{"ID1", "Producto", "GALON", 8500.0});

// Eliminar fila por índice
modelo.removeRow(0);

// Eliminar todas las filas
modelo.setRowCount(0);

// Obtener cantidad de filas
int filas = modelo.getRowCount();

// Obtener cantidad de columnas
int columnas = modelo.getColumnCount();

// Obtener valor de celda
Object valor = modelo.getValueAt(fila, columna);
String texto = (String) modelo.getValueAt(0, 1);  // Fila 0, columna 1

// Establecer valor de celda
modelo.setValueAt("Nuevo valor", fila, columna);

// Insertar fila en posición específica
modelo.insertRow(0, new Object[]{"ID", "Nombre", "Unidad", "Precio"});

// JTABLE

// Obtener fila seleccionada (-1 si no hay selección)
int fila = tabla.getSelectedRow();

// Obtener columna seleccionada
int columna = tabla.getSelectedColumn();

// Obtener múltiples filas seleccionadas
int[] filas = tabla.getSelectedRows();

// Seleccionar fila programáticamente
tabla.setRowSelectionInterval(0, 0);  // Seleccionar fila 0

// Limpiar selección
tabla.clearSelection();

// Cambiar altura de filas
tabla.setRowHeight(30);

// Cambiar ancho de columna
tabla.getColumnModel().getColumn(0).setPreferredWidth(150);

// Deshabilitar reordenamiento de columnas
tabla.getTableHeader().setReorderingAllowed(false);

// Deshabilitar redimensionamiento de columnas
tabla.getTableHeader().setResizingAllowed(false);

// Cambiar modo de selección
tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);  // Una fila
tabla.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);  // Múltiples filas
```

#### ✅ Criterio de Éxito:
- [x] Tabla muestra productos con 4 columnas
- [x] Formulario superior permite agregar productos
- [x] Botón "Eliminar" elimina la fila seleccionada
- [x] Botón "Limpiar" elimina todas las filas
- [x] Doble clic en fila muestra ventana emergente con detalles
- [x] Tabla tiene scroll si hay muchas filas

---

Continuaré con el siguiente checkpoint. El archivo es muy extenso, ¿quieres que continúe escribiéndolo completo o prefieres que lo divida en múltiples partes?
### **Checkpoint 9.6: Integración con ProductServices - CRUD Completo** ⏱️ 2 horas

#### 🎯 Objetivo:
Conectar la interfaz gráfica con tu capa de Services (ProductServices) para hacer CRUD real en la base de datos MySQL.

#### 📝 Explicación previa:

Hasta ahora hemos trabajado con datos "falsos" (hardcodeados). Ahora vamos a:
1. Leer productos REALES desde la base de datos (ProductServices.getAllProducts())
2. Insertar productos REALES (ProductServices.insertProduct())
3. Eliminar productos REALES (ProductServices.deleteProduct())

**Arquitectura:**

```
┌──────────────────────────────────────────────────────┐
│                  ProductManagerGUI                    │
│                    (JFrame - Vista)                   │
├──────────────────────────────────────────────────────┤
│  ┌───────────────────────┐  ┌──────────────────────┐│
│  │   Formulario          │  │   Tabla              ││
│  │   (JPanel)            │  │   (JTable)           ││
│  │                       │  │                      ││
│  │  [Nombre]  [____]     │  │  ID  │ Nombre │ ... ││
│  │  [Unidad]  [____]     │  │  ... │ ...    │ ... ││
│  │  [Precio]  [____]     │  │  ... │ ...    │ ... ││
│  │                       │  │                      ││
│  │  [Guardar] [Eliminar] │  │                      ││
│  └───────────────────────┘  └──────────────────────┘│
│                    │                    │            │
│                    ▼                    ▼            │
│         btnGuardar.addActionListener() │            │
│                    │                    │            │
└────────────────────┼────────────────────┼────────────┘
                     │                    │
                     ▼                    ▼
        ┌────────────────────────────────────────┐
        │      ProductServices.java              │
        │         (Capa de Servicios)            │
        ├────────────────────────────────────────┤
        │  + insertProduct(Product)              │
        │  + getAllProducts(): List<Product>     │
        │  + deleteProduct(String id)            │
        │  + updateProduct(Product)              │
        └────────────────┬───────────────────────┘
                         │
                         ▼
              ┌──────────────────────┐
              │    MySQL Database    │
              │   (oil_products)     │
              └──────────────────────┘
```

#### 🎯 Ejercicio Práctico:

**Crear `com/forestech/ui/ProductManagerGUI.java`:**

```java
package com.forestech.ui;

import com.forestech.models.Product;
import com.forestech.services.ProductServices;
import com.forestech.exceptions.DatabaseException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Gestor de productos con integración a base de datos real.
 *
 * FUNCIONALIDADES:
 * - Listar productos desde MySQL
 * - Crear productos (guardar en BD)
 * - Eliminar productos (borrar de BD)
 * - Refrescar datos desde BD
 */
public class ProductManagerGUI extends JFrame {

    // Componentes de UI
    private JTable tabla;
    private DefaultTableModel modelo;
    private JTextField txtNombre, txtPrecio;
    private JComboBox<String> cmbUnidad;
    private JButton btnAgregar, btnEliminar, btnRefrescar, btnActualizar;

    public ProductManagerGUI() {
        configurarVentana();
        inicializarComponentes();
        cargarProductosDesdeDB();
        
        setVisible(true);
    }

    private void configurarVentana() {
        setTitle("Gestión de Productos - Forestech");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(null);
    }

    private void inicializarComponentes() {
        // PANEL NORTE: Formulario
        add(crearPanelFormulario(), BorderLayout.NORTH);

        // PANEL CENTRO: Tabla
        add(crearPanelTabla(), BorderLayout.CENTER);

        // PANEL SUR: Botones de acción
        add(crearPanelBotones(), BorderLayout.SOUTH);
    }

    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Datos del Producto"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Nombre
        panel.add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        panel.add(txtNombre);

        // Unidad de Medida
        panel.add(new JLabel("Unidad:"));
        String[] unidades = {"GALON", "GARRAFA", "CUARTO", "CANECA"};
        cmbUnidad = new JComboBox<>(unidades);
        panel.add(cmbUnidad);

        // Precio
        panel.add(new JLabel("Precio:"));
        txtPrecio = new JTextField();
        panel.add(txtPrecio);

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        btnAgregar = new JButton("Agregar");
        btnAgregar.addActionListener(e -> agregarProducto());
        btnAgregar.setBackground(new Color(46, 204, 113));
        btnAgregar.setForeground(Color.WHITE);
        btnAgregar.setFocusPainted(false);
        
        btnActualizar = new JButton("Actualizar");
        btnActualizar.addActionListener(e -> {
            // TODO: Implementar en fase avanzada
            JOptionPane.showMessageDialog(this, "Funcionalidad en desarrollo");
        });
        btnActualizar.setEnabled(false);

        panelBotones.add(btnAgregar);
        panelBotones.add(btnActualizar);

        panel.add(new JLabel(""));
        panel.add(panelBotones);

        return panel;
    }

    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Productos en Base de Datos"));

        // Definir columnas
        String[] columnas = {"ID", "Nombre", "Unidad", "Precio"};
        
        // Crear modelo no editable
        modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Crear tabla
        tabla = new JTable(modelo);
        tabla.setFont(new Font("Arial", Font.PLAIN, 12));
        tabla.setRowHeight(25);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Personalizar encabezado
        tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tabla.getTableHeader().setBackground(new Color(52, 73, 94));
        tabla.getTableHeader().setForeground(Color.WHITE);
        tabla.getTableHeader().setReorderingAllowed(false);

        // Ajustar anchos de columna
        tabla.getColumnModel().getColumn(0).setPreferredWidth(120);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(250);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(100);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(120);

        // Agregar scroll
        JScrollPane scrollPane = new JScrollPane(tabla);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Etiqueta de info
        JLabel lblInfo = new JLabel("Doble clic en una fila para ver detalles");
        lblInfo.setFont(new Font("Arial", Font.ITALIC, 10));
        lblInfo.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(lblInfo, BorderLayout.SOUTH);

        // Evento: Doble clic
        tabla.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    mostrarDetalleProducto();
                }
            }
        });

        return panel;
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        btnEliminar = new JButton("Eliminar Seleccionado");
        btnEliminar.addActionListener(e -> eliminarProducto());
        btnEliminar.setBackground(new Color(231, 76, 60));
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.setFocusPainted(false);

        btnRefrescar = new JButton("Refrescar Datos");
        btnRefrescar.addActionListener(e -> cargarProductosDesdeDB());
        btnRefrescar.setBackground(new Color(52, 152, 219));
        btnRefrescar.setForeground(Color.WHITE);
        btnRefrescar.setFocusPainted(false);

        panel.add(btnEliminar);
        panel.add(btnRefrescar);

        return panel;
    }

    // ============================================================================
    // MÉTODOS DE INTEGRACIÓN CON BASE DE DATOS
    // ============================================================================

    /**
     * Carga productos desde la base de datos y los muestra en la tabla.
     */
    private void cargarProductosDesdeDB() {
        try {
            // Limpiar tabla actual
            modelo.setRowCount(0);

            // Obtener productos desde ProductServices
            List<Product> productos = ProductServices.getAllProducts();

            // Agregar cada producto a la tabla
            for (Product p : productos) {
                modelo.addRow(new Object[]{
                    p.getId(),
                    p.getName(),
                    p.getUnidadDeMedida(),
                    String.format("$%,.2f", p.getPriceXUnd())
                });
            }

            // Actualizar estado en barra de título
            setTitle("Gestión de Productos - Forestech (" + productos.size() + " productos)");

        } catch (DatabaseException e) {
            mostrarError(
                "Error al Cargar Productos",
                "No se pudieron cargar los productos desde la base de datos.\n\n" +
                "Detalles: " + e.getMessage() + "\n\n" +
                "Verifica que:\n" +
                "- MySQL esté ejecutándose\n" +
                "- La base de datos FORESTECHOIL exista\n" +
                "- La tabla oil_products exista"
            );
        }
    }

    /**
     * Agrega un nuevo producto a la base de datos.
     */
    private void agregarProducto() {
        try {
            // PASO 1: Validar campos
            String nombre = txtNombre.getText().trim();
            String unidad = (String) cmbUnidad.getSelectedItem();
            String precioStr = txtPrecio.getText().trim();

            if (nombre.isEmpty() || precioStr.isEmpty()) {
                JOptionPane.showMessageDialog(
                    this,
                    "Todos los campos son obligatorios",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            double precio = Double.parseDouble(precioStr);

            if (precio <= 0) {
                JOptionPane.showMessageDialog(
                    this,
                    "El precio debe ser mayor a cero",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            // PASO 2: Crear objeto Product
            Product nuevoProducto = new Product(nombre, unidad, precio);

            // PASO 3: Guardar en BD a través de ProductServices
            ProductServices.insertProduct(nuevoProducto);

            // PASO 4: Mostrar confirmación
            JOptionPane.showMessageDialog(
                this,
                "Producto creado exitosamente:\n\n" +
                "ID: " + nuevoProducto.getId() + "\n" +
                "Nombre: " + nombre + "\n" +
                "Unidad: " + unidad + "\n" +
                "Precio: $" + String.format("%,.2f", precio),
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE
            );

            // PASO 5: Limpiar formulario
            limpiarFormulario();

            // PASO 6: Recargar tabla
            cargarProductosDesdeDB();

        } catch (NumberFormatException e) {
            mostrarError(
                "Precio Inválido",
                "El precio debe ser un número válido.\nEjemplo: 8500 o 8500.50"
            );
        } catch (DatabaseException e) {
            mostrarError(
                "Error al Guardar",
                "No se pudo guardar el producto en la base de datos.\n\n" +
                "Detalles: " + e.getMessage()
            );
        }
    }

    /**
     * Elimina el producto seleccionado de la base de datos.
     */
    private void eliminarProducto() {
        int filaSeleccionada = tabla.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(
                this,
                "Selecciona un producto de la tabla",
                "Validación",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String productId = (String) modelo.getValueAt(filaSeleccionada, 0);
        String nombre = (String) modelo.getValueAt(filaSeleccionada, 1);

        int confirmacion = JOptionPane.showConfirmDialog(
            this,
            "¿Estás seguro de eliminar este producto?\n\n" +
            "ID: " + productId + "\n" +
            "Nombre: " + nombre + "\n\n" +
            "⚠️ ADVERTENCIA: Si el producto tiene movimientos asociados,\n" +
            "no podrá ser eliminado (restricción de FK).",
            "Confirmar Eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                // Eliminar desde BD
                boolean eliminado = ProductServices.deleteProduct(productId);

                if (eliminado) {
                    JOptionPane.showMessageDialog(
                        this,
                        "Producto eliminado exitosamente",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE
                    );

                    // Recargar tabla
                    cargarProductosDesdeDB();
                } else {
                    JOptionPane.showMessageDialog(
                        this,
                        "No se encontró el producto en la base de datos",
                        "Advertencia",
                        JOptionPane.WARNING_MESSAGE
                    );
                }

            } catch (DatabaseException e) {
                // Error típico: Foreign Key Constraint
                if (e.getMessage().contains("foreign key") || 
                    e.getMessage().contains("movimientos asociados")) {
                    mostrarError(
                        "No se puede Eliminar",
                        "Este producto NO puede ser eliminado porque tiene\n" +
                        "movimientos asociados en la tabla Movement.\n\n" +
                        "Para eliminarlo:\n" +
                        "1. Elimina primero todos los movimientos de este producto\n" +
                        "2. Luego intenta eliminar el producto nuevamente"
                    );
                } else {
                    mostrarError(
                        "Error al Eliminar",
                        "No se pudo eliminar el producto.\n\n" +
                        "Detalles: " + e.getMessage()
                    );
                }
            }
        }
    }

    /**
     * Muestra detalles del producto seleccionado.
     */
    private void mostrarDetalleProducto() {
        int fila = tabla.getSelectedRow();
        if (fila != -1) {
            String id = (String) modelo.getValueAt(fila, 0);
            String nombre = (String) modelo.getValueAt(fila, 1);
            String unidad = (String) modelo.getValueAt(fila, 2);
            String precio = (String) modelo.getValueAt(fila, 3);

            String mensaje = String.format(
                "DETALLES DEL PRODUCTO\n\n" +
                "ID:            %s\n" +
                "Nombre:        %s\n" +
                "Unidad:        %s\n" +
                "Precio:        %s",
                id, nombre, unidad, precio
            );

            JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Detalle de Producto",
                JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    // ============================================================================
    // MÉTODOS AUXILIARES
    // ============================================================================

    private void limpiarFormulario() {
        txtNombre.setText("");
        txtPrecio.setText("");
        cmbUnidad.setSelectedIndex(0);
        txtNombre.requestFocus();
    }

    private void mostrarError(String titulo, String mensaje) {
        JOptionPane.showMessageDialog(
            this,
            mensaje,
            titulo,
            JOptionPane.ERROR_MESSAGE
        );
    }

    // ============================================================================
    // MÉTODO MAIN
    // ============================================================================

    public static void main(String[] args) {
        // Ejecutar en el EDT (Event Dispatch Thread)
        SwingUtilities.invokeLater(() -> {
            try {
                // Cambiar Look and Feel (opcional)
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // Si falla, usar el look and feel por defecto
            }

            new ProductManagerGUI();
        });
    }
}
```

#### 🔍 Explicación Detallada:

**1. Flujo de Agregar Producto:**

```
Usuario llena formulario
         │
         ▼
btnAgregar.addActionListener()
         │
         ├─ Validar campos (isEmpty, precio > 0)
         │
         ├─ Crear objeto Product
         │      Product p = new Product(nombre, unidad, precio);
         │
         ├─ Llamar a ProductServices
         │      ProductServices.insertProduct(p);
         │      │
         │      └──► INSERT INTO oil_products (id, name, ...)
         │
         ├─ Mostrar mensaje de éxito
         │
         ├─ Limpiar formulario
         │
         └─ Recargar tabla
                cargarProductosDesdeDB();
```

**2. Flujo de Cargar Productos:**

```
Usuario abre ventana o presiona "Refrescar"
         │
         ▼
cargarProductosDesdeDB()
         │
         ├─ Limpiar tabla (modelo.setRowCount(0))
         │
         ├─ Llamar a ProductServices
         │      List<Product> productos = ProductServices.getAllProducts();
         │      │
         │      └──► SELECT id, name, ... FROM oil_products
         │
         ├─ Recorrer lista de productos
         │      for (Product p : productos) {
         │          modelo.addRow(new Object[]{...});
         │      }
         │
         └─ Actualizar título de ventana con total
```

**3. Manejo de errores de Foreign Key:**

```java
try {
    ProductServices.deleteProduct(productId);
} catch (DatabaseException e) {
    if (e.getMessage().contains("foreign key")) {
        // Error específico de FK
        mostrarError(
            "No se puede Eliminar",
            "Este producto tiene movimientos asociados"
        );
    } else {
        // Otro error genérico
        mostrarError("Error", e.getMessage());
    }
}
```

**4. Diferencia entre JFrame y extends JFrame:**

```java
// OPCIÓN A: Crear instancia de JFrame (usado en checkpoints anteriores)
JFrame ventana = new JFrame("Título");
ventana.add(componente);
ventana.setVisible(true);

// OPCIÓN B: Extender JFrame (usado aquí)
public class ProductManagerGUI extends JFrame {
    public ProductManagerGUI() {
        setTitle("Título");  // this.setTitle()
        add(componente);      // this.add()
        setVisible(true);     // this.setVisible()
    }
}

// ¿Cuándo usar cada uno?
// - Opción A: Ventanas simples, ejemplos de aprendizaje
// - Opción B: Aplicaciones reales, múltiples ventanas, código organizado
```

#### ✅ Criterio de Éxito:
- [x] Ventana muestra productos REALES desde MySQL
- [x] Botón "Agregar" inserta productos en la BD
- [x] Botón "Eliminar" borra productos de la BD
- [x] Botón "Refrescar" recarga datos desde BD
- [x] Maneja error de FK cuando producto tiene movimientos
- [x] Formulario valida campos vacíos y precio inválido

#### 🏃 Cómo ejecutar:

```bash
cd /home/hp/forestechOil/forestech-cli-java
mvn clean compile
mvn exec:java -Dexec.mainClass="com.forestech.ui.ProductManagerGUI"
```

**🎉 FELICIDADES! Ahora tienes una interfaz gráfica funcional conectada a tu base de datos real.**

---

## 📋 Checkpoint 9.7: JMenuBar - Menús de Navegación (45 min)

### 🎯 Objetivo:
Crear un sistema de menús profesional para navegar entre diferentes módulos de Forestech.

### 📚 Concepto: JMenuBar

Un **JMenuBar** es la barra de menús que aparece en la parte superior de la ventana (como "Archivo", "Editar", "Ayuda" en cualquier programa).

**Componentes del sistema de menús:**

```
JMenuBar (Barra completa)
│
├── JMenu ("Archivo")
│   ├── JMenuItem ("Nuevo")
│   ├── JMenuItem ("Abrir")
│   ├── JSeparator (línea divisoria)
│   └── JMenuItem ("Salir")
│
├── JMenu ("Productos")
│   ├── JMenuItem ("Ver Todos")
│   └── JMenuItem ("Agregar Nuevo")
│
└── JMenu ("Ayuda")
    └── JMenuItem ("Acerca de")
```

**Diagrama ASCII de la barra de menús:**

```
┌─────────────────────────────────────────────────────┐
│ Archivo  Productos  Vehículos  Movimientos  Ayuda  │ ← JMenuBar
└─────────────────────────────────────────────────────┘
     │
     └──> Al hacer clic se despliega:
          ┌──────────────────┐
          │ Nuevo        (N) │ ← JMenuItem con mnemonic
          │ Abrir        (A) │
          │ ─────────────── │ ← JSeparator
          │ Salir        (S) │
          └──────────────────┘
```

### 💡 Conceptos Clave:

1. **Mnemonic (Atajo de Teclado):**
   ```java
   menuItem.setMnemonic('N');  // Alt+N para activar
   ```

2. **Accelerator (Combinación de Teclas):**
   ```java
   menuItem.setAccelerator(KeyStroke.getKeyStroke("control N"));
   // Ctrl+N funciona desde cualquier parte de la ventana
   ```

3. **Difference Between Mnemonic and Accelerator:**
   - **Mnemonic:** Alt+letra (solo con el menú abierto)
   - **Accelerator:** Ctrl+letra (funciona siempre, menú cerrado o abierto)

### 📝 Ejercicio Guiado:

Vamos a crear `MainMenuGUI.java` - la ventana principal de Forestech con sistema de menús completo.

#### Paso 1: Crear la estructura básica

```java
package com.forestech.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class MainMenuGUI extends JFrame {

    public MainMenuGUI() {
        setTitle("Forestech Oil Management System");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Crear la barra de menús
        JMenuBar menuBar = crearMenuBar();
        setJMenuBar(menuBar);  // IMPORTANTE: setJMenuBar() no add()

        // Panel central con mensaje de bienvenida
        JPanel panelCentral = crearPanelBienvenida();
        add(panelCentral, BorderLayout.CENTER);

        setVisible(true);
    }

    private JMenuBar crearMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // Crear cada menú
        menuBar.add(crearMenuArchivo());
        menuBar.add(crearMenuProductos());
        menuBar.add(crearMenuVehiculos());
        menuBar.add(crearMenuMovimientos());
        menuBar.add(crearMenuAyuda());

        return menuBar;
    }

    // Continuaremos implementando cada método...
}
```

#### Paso 2: Implementar Menú "Archivo"

```java
private JMenu crearMenuArchivo() {
    JMenu menuArchivo = new JMenu("Archivo");
    menuArchivo.setMnemonic('A');  // Alt+A

    // Item: Nuevo
    JMenuItem itemNuevo = new JMenuItem("Nuevo");
    itemNuevo.setMnemonic('N');
    itemNuevo.setAccelerator(KeyStroke.getKeyStroke("control N"));
    itemNuevo.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(MainMenuGUI.this,
                "Crear nuevo registro",
                "Nuevo",
                JOptionPane.INFORMATION_MESSAGE);
        }
    });

    // Item: Salir
    JMenuItem itemSalir = new JMenuItem("Salir");
    itemSalir.setMnemonic('S');
    itemSalir.setAccelerator(KeyStroke.getKeyStroke("control Q"));
    itemSalir.addActionListener(e -> {
        int confirmacion = JOptionPane.showConfirmDialog(
            MainMenuGUI.this,
            "¿Está seguro que desea salir?",
            "Confirmar Salida",
            JOptionPane.YES_NO_OPTION
        );

        if (confirmacion == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    });

    // Agregar items al menú
    menuArchivo.add(itemNuevo);
    menuArchivo.addSeparator();  // Línea divisoria
    menuArchivo.add(itemSalir);

    return menuArchivo;
}
```

#### Paso 3: Implementar Menú "Productos"

```java
private JMenu crearMenuProductos() {
    JMenu menuProductos = new JMenu("Productos");
    menuProductos.setMnemonic('P');

    // Item: Ver Todos
    JMenuItem itemVerTodos = new JMenuItem("Ver Todos los Productos");
    itemVerTodos.setMnemonic('V');
    itemVerTodos.setAccelerator(KeyStroke.getKeyStroke("control P"));
    itemVerTodos.addActionListener(e -> abrirVentanaProductos());

    // Item: Agregar Nuevo
    JMenuItem itemAgregarNuevo = new JMenuItem("Agregar Nuevo Producto");
    itemAgregarNuevo.setMnemonic('A');
    itemAgregarNuevo.addActionListener(e -> {
        JOptionPane.showMessageDialog(this,
            "Abrir formulario de nuevo producto",
            "Nuevo Producto",
            JOptionPane.INFORMATION_MESSAGE);
    });

    // Item: Buscar
    JMenuItem itemBuscar = new JMenuItem("Buscar Producto");
    itemBuscar.setMnemonic('B');
    itemBuscar.setAccelerator(KeyStroke.getKeyStroke("control F"));
    itemBuscar.addActionListener(e -> {
        String busqueda = JOptionPane.showInputDialog(this,
            "Ingrese el nombre del producto:",
            "Buscar Producto",
            JOptionPane.QUESTION_MESSAGE);

        if (busqueda != null && !busqueda.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Buscando: " + busqueda,
                "Resultado",
                JOptionPane.INFORMATION_MESSAGE);
        }
    });

    menuProductos.add(itemVerTodos);
    menuProductos.add(itemAgregarNuevo);
    menuProductos.addSeparator();
    menuProductos.add(itemBuscar);

    return menuProductos;
}
```

#### Paso 4: Implementar Menú "Vehículos"

```java
private JMenu crearMenuVehiculos() {
    JMenu menuVehiculos = new JMenu("Vehículos");
    menuVehiculos.setMnemonic('V');

    JMenuItem itemVerTodos = new JMenuItem("Ver Todos los Vehículos");
    itemVerTodos.setMnemonic('T');
    itemVerTodos.setAccelerator(KeyStroke.getKeyStroke("control shift V"));
    itemVerTodos.addActionListener(e -> {
        JOptionPane.showMessageDialog(this,
            "Abrir ventana de vehículos",
            "Vehículos",
            JOptionPane.INFORMATION_MESSAGE);
    });

    JMenuItem itemAgregarNuevo = new JMenuItem("Agregar Nuevo Vehículo");
    itemAgregarNuevo.addActionListener(e -> {
        JOptionPane.showMessageDialog(this,
            "Formulario para nuevo vehículo",
            "Nuevo Vehículo",
            JOptionPane.INFORMATION_MESSAGE);
    });

    menuVehiculos.add(itemVerTodos);
    menuVehiculos.add(itemAgregarNuevo);

    return menuVehiculos;
}
```

#### Paso 5: Implementar Menú "Movimientos"

```java
private JMenu crearMenuMovimientos() {
    JMenu menuMovimientos = new JMenu("Movimientos");
    menuMovimientos.setMnemonic('M');

    // Submenú para Entradas
    JMenu subMenuEntradas = new JMenu("Entradas");
    JMenuItem itemNuevaEntrada = new JMenuItem("Registrar Entrada");
    itemNuevaEntrada.addActionListener(e -> {
        JOptionPane.showMessageDialog(this,
            "Formulario para ENTRADA de combustible",
            "Nueva Entrada",
            JOptionPane.INFORMATION_MESSAGE);
    });
    subMenuEntradas.add(itemNuevaEntrada);

    // Submenú para Salidas
    JMenu subMenuSalidas = new JMenu("Salidas");
    JMenuItem itemNuevaSalida = new JMenuItem("Registrar Salida");
    itemNuevaSalida.addActionListener(e -> {
        JOptionPane.showMessageDialog(this,
            "Formulario para SALIDA de combustible",
            "Nueva Salida",
            JOptionPane.INFORMATION_MESSAGE);
    });
    subMenuSalidas.add(itemNuevaSalida);

    // Item: Ver Historial
    JMenuItem itemHistorial = new JMenuItem("Ver Historial Completo");
    itemHistorial.setAccelerator(KeyStroke.getKeyStroke("control H"));
    itemHistorial.addActionListener(e -> {
        JOptionPane.showMessageDialog(this,
            "Tabla con historial de movimientos",
            "Historial",
            JOptionPane.INFORMATION_MESSAGE);
    });

    menuMovimientos.add(subMenuEntradas);
    menuMovimientos.add(subMenuSalidas);
    menuMovimientos.addSeparator();
    menuMovimientos.add(itemHistorial);

    return menuMovimientos;
}
```

#### Paso 6: Implementar Menú "Ayuda"

```java
private JMenu crearMenuAyuda() {
    JMenu menuAyuda = new JMenu("Ayuda");
    menuAyuda.setMnemonic('Y');

    JMenuItem itemDocumentacion = new JMenuItem("Documentación");
    itemDocumentacion.setAccelerator(KeyStroke.getKeyStroke("F1"));
    itemDocumentacion.addActionListener(e -> {
        JOptionPane.showMessageDialog(this,
            "Abrir documentación del sistema",
            "Documentación",
            JOptionPane.INFORMATION_MESSAGE);
    });

    JMenuItem itemAcercaDe = new JMenuItem("Acerca de Forestech");
    itemAcercaDe.addActionListener(e -> mostrarAcercaDe());

    menuAyuda.add(itemDocumentacion);
    menuAyuda.addSeparator();
    menuAyuda.add(itemAcercaDe);

    return menuAyuda;
}

private void mostrarAcercaDe() {
    String mensaje = "Forestech Oil Management System\n" +
                     "Versión: 1.0.0\n" +
                     "Desarrollado con Java Swing\n" +
                     "Base de datos: MySQL\n\n" +
                     "© 2025 Forestech Development Team";

    JOptionPane.showMessageDialog(this,
        mensaje,
        "Acerca de Forestech",
        JOptionPane.INFORMATION_MESSAGE);
}
```

#### Paso 7: Crear Panel de Bienvenida

```java
private JPanel crearPanelBienvenida() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBackground(new Color(245, 245, 245));

    // Título principal
    JLabel lblTitulo = new JLabel("FORESTECH OIL MANAGEMENT", JLabel.CENTER);
    lblTitulo.setFont(new Font("Arial", Font.BOLD, 28));
    lblTitulo.setForeground(new Color(25, 135, 84));
    lblTitulo.setBorder(BorderFactory.createEmptyBorder(50, 0, 20, 0));

    // Subtítulo
    JLabel lblSubtitulo = new JLabel(
        "Sistema de Gestión de Combustibles",
        JLabel.CENTER
    );
    lblSubtitulo.setFont(new Font("Arial", Font.PLAIN, 16));
    lblSubtitulo.setForeground(Color.GRAY);

    // Instrucciones
    JTextArea txtInstrucciones = new JTextArea();
    txtInstrucciones.setText(
        "\n\n  Bienvenido al Sistema de Gestión de Forestech\n\n" +
        "  Utiliza el menú superior para navegar:\n\n" +
        "  • Productos: Gestionar catálogo de combustibles\n" +
        "  • Vehículos: Administrar flota vehicular\n" +
        "  • Movimientos: Registrar entradas y salidas\n" +
        "  • Ayuda: Documentación y soporte\n\n" +
        "  Atajos de Teclado:\n" +
        "  • Ctrl+P: Ver productos\n" +
        "  • Ctrl+H: Historial de movimientos\n" +
        "  • F1: Ayuda\n" +
        "  • Ctrl+Q: Salir"
    );
    txtInstrucciones.setEditable(false);
    txtInstrucciones.setFont(new Font("Arial", Font.PLAIN, 14));
    txtInstrucciones.setBackground(new Color(245, 245, 245));
    txtInstrucciones.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

    // Panel para el contenido central
    JPanel panelCentro = new JPanel();
    panelCentro.setLayout(new BoxLayout(panelCentro, BoxLayout.Y_AXIS));
    panelCentro.setBackground(new Color(245, 245, 245));
    panelCentro.add(lblTitulo);
    panelCentro.add(lblSubtitulo);
    panelCentro.add(txtInstrucciones);

    panel.add(panelCentro, BorderLayout.CENTER);

    return panel;
}
```

#### Paso 8: Método para abrir ventana de Productos

```java
private void abrirVentanaProductos() {
    // Cerrar ventana actual
    dispose();

    // Abrir ventana de productos (del checkpoint anterior)
    SwingUtilities.invokeLater(() -> {
        new ProductManagerGUI();
    });
}
```

#### Paso 9: Método main para ejecutar

```java
public static void main(String[] args) {
    // Configurar Look and Feel del sistema operativo
    try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
        e.printStackTrace();
    }

    // Lanzar aplicación en EDT
    SwingUtilities.invokeLater(() -> {
        new MainMenuGUI();
    });
}
```

### 🔍 Análisis del Código:

#### 1. **setJMenuBar() vs add()**
```java
setJMenuBar(menuBar);  // ✅ Correcto - para JMenuBar
add(componente);       // ❌ Incorrecto - JMenuBar no es un componente normal
```

**¿Por qué?** JMenuBar tiene un espacio especial reservado en JFrame, no va en el panel central.

#### 2. **Mnemonic vs Accelerator**

```java
// Mnemonic (Alt+P abre menú, luego Alt+V activa item)
menuProductos.setMnemonic('P');
itemVerTodos.setMnemonic('V');

// Accelerator (Ctrl+P funciona desde cualquier lugar)
itemVerTodos.setAccelerator(KeyStroke.getKeyStroke("control P"));
```

**Diferencia Visual:**
```
Mnemonic:  Archivo  ← Aparece subrayado: "A"rchivo
Accelerator: Salir   Ctrl+Q  ← Aparece a la derecha del item
```

#### 3. **Submenús (JMenu dentro de JMenu)**

```java
JMenu menuMovimientos = new JMenu("Movimientos");

JMenu subMenuEntradas = new JMenu("Entradas");  // Submenú
subMenuEntradas.add(new JMenuItem("Item 1"));

menuMovimientos.add(subMenuEntradas);  // Agregar submenú al menú principal
```

**Diagrama:**
```
Movimientos →
           ├─ Entradas →
           │           ├─ Registrar Entrada
           │           └─ Ver Entradas
           ├─ Salidas →
           │          └─ Registrar Salida
           └─ Ver Historial
```

#### 4. **KeyStroke Formats**

```java
// Formato básico
KeyStroke.getKeyStroke("control N")        // Ctrl+N
KeyStroke.getKeyStroke("alt F")            // Alt+F
KeyStroke.getKeyStroke("shift DELETE")     // Shift+Del

// Teclas de función
KeyStroke.getKeyStroke("F1")               // F1
KeyStroke.getKeyStroke("F5")               // F5

// Combinaciones múltiples
KeyStroke.getKeyStroke("control shift V")  // Ctrl+Shift+V
KeyStroke.getKeyStroke("control alt D")    // Ctrl+Alt+D
```

#### 5. **Separadores (JSeparator)**

```java
menu.addSeparator();  // Agrega línea divisoria horizontal
```

**Visual:**
```
┌──────────────┐
│ Nuevo        │
│ Abrir        │
│ ───────────  │ ← addSeparator()
│ Salir        │
└──────────────┘
```

### 💾 Código Completo: `MainMenuGUI.java`

Crea el archivo en `src/main/java/com/forestech/ui/MainMenuGUI.java`:

```java
package com.forestech.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenuGUI extends JFrame {

    public MainMenuGUI() {
        setTitle("Forestech Oil Management System");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JMenuBar menuBar = crearMenuBar();
        setJMenuBar(menuBar);

        JPanel panelCentral = crearPanelBienvenida();
        add(panelCentral, BorderLayout.CENTER);

        setVisible(true);
    }

    private JMenuBar crearMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(crearMenuArchivo());
        menuBar.add(crearMenuProductos());
        menuBar.add(crearMenuVehiculos());
        menuBar.add(crearMenuMovimientos());
        menuBar.add(crearMenuAyuda());
        return menuBar;
    }

    private JMenu crearMenuArchivo() {
        JMenu menuArchivo = new JMenu("Archivo");
        menuArchivo.setMnemonic('A');

        JMenuItem itemNuevo = new JMenuItem("Nuevo");
        itemNuevo.setMnemonic('N');
        itemNuevo.setAccelerator(KeyStroke.getKeyStroke("control N"));
        itemNuevo.addActionListener(e ->
            JOptionPane.showMessageDialog(this, "Crear nuevo registro")
        );

        JMenuItem itemSalir = new JMenuItem("Salir");
        itemSalir.setMnemonic('S');
        itemSalir.setAccelerator(KeyStroke.getKeyStroke("control Q"));
        itemSalir.addActionListener(e -> {
            int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro que desea salir?",
                "Confirmar Salida",
                JOptionPane.YES_NO_OPTION
            );
            if (confirmacion == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });

        menuArchivo.add(itemNuevo);
        menuArchivo.addSeparator();
        menuArchivo.add(itemSalir);
        return menuArchivo;
    }

    private JMenu crearMenuProductos() {
        JMenu menuProductos = new JMenu("Productos");
        menuProductos.setMnemonic('P');

        JMenuItem itemVerTodos = new JMenuItem("Ver Todos los Productos");
        itemVerTodos.setMnemonic('V');
        itemVerTodos.setAccelerator(KeyStroke.getKeyStroke("control P"));
        itemVerTodos.addActionListener(e -> abrirVentanaProductos());

        JMenuItem itemAgregarNuevo = new JMenuItem("Agregar Nuevo Producto");
        itemAgregarNuevo.setMnemonic('A');
        itemAgregarNuevo.addActionListener(e ->
            JOptionPane.showMessageDialog(this, "Formulario de nuevo producto")
        );

        JMenuItem itemBuscar = new JMenuItem("Buscar Producto");
        itemBuscar.setMnemonic('B');
        itemBuscar.setAccelerator(KeyStroke.getKeyStroke("control F"));
        itemBuscar.addActionListener(e -> {
            String busqueda = JOptionPane.showInputDialog(this,
                "Ingrese el nombre del producto:",
                "Buscar Producto",
                JOptionPane.QUESTION_MESSAGE);
            if (busqueda != null && !busqueda.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Buscando: " + busqueda);
            }
        });

        menuProductos.add(itemVerTodos);
        menuProductos.add(itemAgregarNuevo);
        menuProductos.addSeparator();
        menuProductos.add(itemBuscar);
        return menuProductos;
    }

    private JMenu crearMenuVehiculos() {
        JMenu menuVehiculos = new JMenu("Vehículos");
        menuVehiculos.setMnemonic('V');

        JMenuItem itemVerTodos = new JMenuItem("Ver Todos los Vehículos");
        itemVerTodos.setMnemonic('T');
        itemVerTodos.setAccelerator(KeyStroke.getKeyStroke("control shift V"));
        itemVerTodos.addActionListener(e ->
            JOptionPane.showMessageDialog(this, "Ventana de vehículos")
        );

        JMenuItem itemAgregarNuevo = new JMenuItem("Agregar Nuevo Vehículo");
        itemAgregarNuevo.addActionListener(e ->
            JOptionPane.showMessageDialog(this, "Formulario de nuevo vehículo")
        );

        menuVehiculos.add(itemVerTodos);
        menuVehiculos.add(itemAgregarNuevo);
        return menuVehiculos;
    }

    private JMenu crearMenuMovimientos() {
        JMenu menuMovimientos = new JMenu("Movimientos");
        menuMovimientos.setMnemonic('M');

        JMenu subMenuEntradas = new JMenu("Entradas");
        JMenuItem itemNuevaEntrada = new JMenuItem("Registrar Entrada");
        itemNuevaEntrada.addActionListener(e ->
            JOptionPane.showMessageDialog(this, "Formulario para ENTRADA")
        );
        subMenuEntradas.add(itemNuevaEntrada);

        JMenu subMenuSalidas = new JMenu("Salidas");
        JMenuItem itemNuevaSalida = new JMenuItem("Registrar Salida");
        itemNuevaSalida.addActionListener(e ->
            JOptionPane.showMessageDialog(this, "Formulario para SALIDA")
        );
        subMenuSalidas.add(itemNuevaSalida);

        JMenuItem itemHistorial = new JMenuItem("Ver Historial Completo");
        itemHistorial.setAccelerator(KeyStroke.getKeyStroke("control H"));
        itemHistorial.addActionListener(e ->
            JOptionPane.showMessageDialog(this, "Tabla con historial")
        );

        menuMovimientos.add(subMenuEntradas);
        menuMovimientos.add(subMenuSalidas);
        menuMovimientos.addSeparator();
        menuMovimientos.add(itemHistorial);
        return menuMovimientos;
    }

    private JMenu crearMenuAyuda() {
        JMenu menuAyuda = new JMenu("Ayuda");
        menuAyuda.setMnemonic('Y');

        JMenuItem itemDocumentacion = new JMenuItem("Documentación");
        itemDocumentacion.setAccelerator(KeyStroke.getKeyStroke("F1"));
        itemDocumentacion.addActionListener(e ->
            JOptionPane.showMessageDialog(this, "Documentación del sistema")
        );

        JMenuItem itemAcercaDe = new JMenuItem("Acerca de Forestech");
        itemAcercaDe.addActionListener(e -> mostrarAcercaDe());

        menuAyuda.add(itemDocumentacion);
        menuAyuda.addSeparator();
        menuAyuda.add(itemAcercaDe);
        return menuAyuda;
    }

    private void mostrarAcercaDe() {
        String mensaje = "Forestech Oil Management System\n" +
                         "Versión: 1.0.0\n" +
                         "Desarrollado con Java Swing\n" +
                         "Base de datos: MySQL\n\n" +
                         "© 2025 Forestech Development Team";
        JOptionPane.showMessageDialog(this, mensaje, "Acerca de Forestech",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private JPanel crearPanelBienvenida() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245));

        JLabel lblTitulo = new JLabel("FORESTECH OIL MANAGEMENT", JLabel.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitulo.setForeground(new Color(25, 135, 84));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(50, 0, 20, 0));

        JLabel lblSubtitulo = new JLabel("Sistema de Gestión de Combustibles", JLabel.CENTER);
        lblSubtitulo.setFont(new Font("Arial", Font.PLAIN, 16));
        lblSubtitulo.setForeground(Color.GRAY);

        JTextArea txtInstrucciones = new JTextArea();
        txtInstrucciones.setText(
            "\n\n  Bienvenido al Sistema de Gestión de Forestech\n\n" +
            "  Utiliza el menú superior para navegar:\n\n" +
            "  • Productos: Gestionar catálogo de combustibles\n" +
            "  • Vehículos: Administrar flota vehicular\n" +
            "  • Movimientos: Registrar entradas y salidas\n" +
            "  • Ayuda: Documentación y soporte\n\n" +
            "  Atajos de Teclado:\n" +
            "  • Ctrl+P: Ver productos\n" +
            "  • Ctrl+H: Historial de movimientos\n" +
            "  • F1: Ayuda\n" +
            "  • Ctrl+Q: Salir"
        );
        txtInstrucciones.setEditable(false);
        txtInstrucciones.setFont(new Font("Arial", Font.PLAIN, 14));
        txtInstrucciones.setBackground(new Color(245, 245, 245));
        txtInstrucciones.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JPanel panelCentro = new JPanel();
        panelCentro.setLayout(new BoxLayout(panelCentro, BoxLayout.Y_AXIS));
        panelCentro.setBackground(new Color(245, 245, 245));
        panelCentro.add(lblTitulo);
        panelCentro.add(lblSubtitulo);
        panelCentro.add(txtInstrucciones);

        panel.add(panelCentro, BorderLayout.CENTER);
        return panel;
    }

    private void abrirVentanaProductos() {
        dispose();
        SwingUtilities.invokeLater(() -> new ProductManagerGUI());
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new MainMenuGUI());
    }
}
```

### ✅ Criterio de Éxito:
- [x] Barra de menús visible con 5 menús
- [x] Mnemonics funcionan (Alt+A abre Archivo)
- [x] Accelerators funcionan (Ctrl+P abre productos)
- [x] Separadores visibles entre grupos de items
- [x] Submenús desplegables (Entradas/Salidas)
- [x] Confirmación al salir
- [x] Navegación a ProductManagerGUI funcional

### 🏃 Cómo ejecutar:

```bash
cd /home/hp/forestechOil/forestech-cli-java
mvn clean compile
mvn exec:java -Dexec.mainClass="com.forestech.ui.MainMenuGUI"
```

**Prueba estos atajos:**
- `Alt+A` → Abre menú Archivo
- `Ctrl+P` → Abre ventana de productos directamente
- `Ctrl+Q` → Intenta salir (pide confirmación)
- `F1` → Abre documentación

---

## 📋 Checkpoint 9.8: JDialog - Ventanas Modales (45 min)

### 🎯 Objetivo:
Aprender a crear ventanas emergentes (diálogos) para formularios complejos que requieran su propia ventana independiente.

### 📚 Concepto: JDialog vs JOptionPane

**JOptionPane** (que ya usamos): Diálogos simples con botones predefinidos (OK, Yes/No).

```java
JOptionPane.showMessageDialog(this, "Mensaje simple");
```

**JDialog**: Ventanas emergentes personalizadas con cualquier contenido que necesites.

```
┌────────────────────────────────────┐
│  Ventana Principal (JFrame)        │
│                                    │
│  [Ver Productos]  [Agregar]       │ ← Click
│                    │               │
│  ┌─────────────────┼────────────┐  │
│  │  JDialog Modal  ▼            │  │
│  │  ┌─────────────────────────┐ │  │
│  │  │ Agregar Producto        │ │  │
│  │  │                         │ │  │
│  │  │ Nombre: [_________]     │ │  │
│  │  │ Precio: [_________]     │ │  │
│  │  │                         │ │  │
│  │  │   [Guardar] [Cancelar]  │ │  │
│  │  └─────────────────────────┘ │  │
│  │  (Bloquea ventana padre)    │  │
│  └──────────────────────────────┘  │
│  ← No puedes interactuar aquí     │
└────────────────────────────────────┘
```

### 💡 Conceptos Clave:

#### 1. **Modal vs Non-Modal**

```java
// MODAL: Bloquea la ventana padre hasta cerrar el diálogo
dialog.setModal(true);  // Usuario DEBE cerrar el diálogo para volver

// NON-MODAL: Permite interactuar con la ventana padre
dialog.setModal(false); // Usuario puede cambiar entre ventanas
```

**Ejemplo Visual:**

```
MODAL:
Ventana Principal (🔒 bloqueada)
         ↓
    JDialog (activo)
    Usuario DEBE cerrar esto primero

NON-MODAL:
Ventana Principal (✅ activa)
         ↕
    JDialog (✅ activo)
    Usuario puede cambiar libremente
```

#### 2. **Tipos de Modal**

```java
// APPLICATION_MODAL: Bloquea TODAS las ventanas de la aplicación
dialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);

// DOCUMENT_MODAL: Solo bloquea la ventana padre
dialog.setModalityType(Dialog.ModalityType.DOCUMENT_MODAL);

// MODELESS: No bloquea nada
dialog.setModalityType(Dialog.ModalityType.MODELESS);
```

### 📝 Ejercicio Guiado:

Vamos a crear `ProductDialogForm.java` - Un diálogo modal para agregar productos con validación completa.

#### Paso 1: Estructura básica del JDialog

```java
package com.forestech.ui;

import com.forestech.exceptions.DatabaseException;
import com.forestech.models.Product;
import com.forestech.services.ProductServices;

import javax.swing.*;
import java.awt.*;

public class ProductDialogForm extends JDialog {

    // Componentes del formulario
    private JTextField txtNombre;
    private JTextField txtPrecio;
    private JComboBox<String> cmbUnidad;

    // Variable para saber si se guardó exitosamente
    private boolean guardadoExitoso = false;

    /**
     * Constructor que recibe la ventana padre
     * @param parent Ventana padre (JFrame)
     * @param modal true para bloquear ventana padre
     */
    public ProductDialogForm(JFrame parent, boolean modal) {
        super(parent, "Agregar Nuevo Producto", modal);

        // Configuración básica
        setSize(400, 300);
        setLocationRelativeTo(parent);  // Centrar respecto al padre
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        // Crear interfaz
        inicializarComponentes();

        setVisible(true);
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(10, 10));

        // Panel del formulario (CENTRO)
        JPanel panelFormulario = crearPanelFormulario();
        add(panelFormulario, BorderLayout.CENTER);

        // Panel de botones (SUR)
        JPanel panelBotones = crearPanelBotones();
        add(panelBotones, BorderLayout.SOUTH);
    }

    // Continuaremos con los métodos...
}
```

#### Paso 2: Crear el formulario

```java
private JPanel crearPanelFormulario() {
    JPanel panel = new JPanel(new GridBagLayout());
    panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    // Fila 0: Nombre
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 0.3;
    panel.add(new JLabel("Nombre:"), gbc);

    gbc.gridx = 1;
    gbc.weightx = 0.7;
    txtNombre = new JTextField(20);
    panel.add(txtNombre, gbc);

    // Fila 1: Unidad de Medida
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.weightx = 0.3;
    panel.add(new JLabel("Unidad:"), gbc);

    gbc.gridx = 1;
    gbc.weightx = 0.7;
    cmbUnidad = new JComboBox<>(new String[]{"Galón", "Litro", "Barril"});
    panel.add(cmbUnidad, gbc);

    // Fila 2: Precio
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.weightx = 0.3;
    panel.add(new JLabel("Precio:"), gbc);

    gbc.gridx = 1;
    gbc.weightx = 0.7;
    txtPrecio = new JTextField(20);
    panel.add(txtPrecio, gbc);

    // Fila 3: Nota informativa
    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.gridwidth = 2;
    JLabel lblNota = new JLabel("* Todos los campos son obligatorios");
    lblNota.setFont(new Font("Arial", Font.ITALIC, 11));
    lblNota.setForeground(Color.GRAY);
    panel.add(lblNota, gbc);

    return panel;
}
```

#### Paso 3: Crear panel de botones

```java
private JPanel crearPanelBotones() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // Botón Guardar
    JButton btnGuardar = new JButton("Guardar");
    btnGuardar.setPreferredSize(new Dimension(100, 30));
    btnGuardar.setBackground(new Color(25, 135, 84));
    btnGuardar.setForeground(Color.WHITE);
    btnGuardar.addActionListener(e -> guardarProducto());

    // Botón Cancelar
    JButton btnCancelar = new JButton("Cancelar");
    btnCancelar.setPreferredSize(new Dimension(100, 30));
    btnCancelar.addActionListener(e -> dispose());  // Cerrar diálogo

    panel.add(btnCancelar);
    panel.add(btnGuardar);

    return panel;
}
```

#### Paso 4: Lógica de validación y guardado

```java
private void guardarProducto() {
    // Validación 1: Campos vacíos
    String nombre = txtNombre.getText().trim();
    String precioTexto = txtPrecio.getText().trim();

    if (nombre.isEmpty() || precioTexto.isEmpty()) {
        JOptionPane.showMessageDialog(
            this,
            "Todos los campos son obligatorios",
            "Error de Validación",
            JOptionPane.ERROR_MESSAGE
        );
        return;
    }

    // Validación 2: Precio numérico y positivo
    double precio;
    try {
        precio = Double.parseDouble(precioTexto);
        if (precio <= 0) {
            throw new NumberFormatException();
        }
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(
            this,
            "El precio debe ser un número positivo",
            "Error de Validación",
            JOptionPane.ERROR_MESSAGE
        );
        txtPrecio.requestFocus();
        return;
    }

    // Validación 3: Insertar en base de datos
    try {
        String unidad = (String) cmbUnidad.getSelectedItem();
        Product nuevoProducto = new Product(nombre, unidad, precio);

        ProductServices.insertProduct(nuevoProducto);

        guardadoExitoso = true;

        JOptionPane.showMessageDialog(
            this,
            "Producto guardado exitosamente:\n" + nuevoProducto.getId(),
            "Éxito",
            JOptionPane.INFORMATION_MESSAGE
        );

        dispose();  // Cerrar diálogo

    } catch (DatabaseException e) {
        JOptionPane.showMessageDialog(
            this,
            "Error al guardar en base de datos:\n" + e.getMessage(),
            "Error de Base de Datos",
            JOptionPane.ERROR_MESSAGE
        );
    }
}
```

#### Paso 5: Método público para verificar si se guardó

```java
/**
 * Verifica si el usuario guardó el producto exitosamente.
 * Útil para que la ventana padre sepa si debe refrescar la tabla.
 *
 * @return true si se guardó, false si canceló
 */
public boolean isGuardadoExitoso() {
    return guardadoExitoso;
}
```

### 🔗 Integración con ProductManagerGUI

Ahora modifica `ProductManagerGUI.java` (del checkpoint 9.6) para usar el diálogo:

```java
// ANTES (en ProductManagerGUI):
private void agregarProducto() {
    try {
        String nombre = txtNombre.getText().trim();
        // ... código que obtiene datos de los campos de formulario
        ProductServices.insertProduct(nuevoProducto);
    } catch (DatabaseException e) {
        // ...
    }
}

// DESPUÉS (usando JDialog):
private void agregarProducto() {
    // Abrir diálogo modal
    ProductDialogForm dialogo = new ProductDialogForm(this, true);

    // Cuando el diálogo se cierra, verificar si guardó algo
    if (dialogo.isGuardadoExitoso()) {
        cargarProductosDesdeDB();  // Refrescar tabla
    }
}
```

### 💾 Código Completo: `ProductDialogForm.java`

```java
package com.forestech.ui;

import com.forestech.exceptions.DatabaseException;
import com.forestech.models.Product;
import com.forestech.services.ProductServices;

import javax.swing.*;
import java.awt.*;

public class ProductDialogForm extends JDialog {

    private JTextField txtNombre;
    private JTextField txtPrecio;
    private JComboBox<String> cmbUnidad;
    private boolean guardadoExitoso = false;

    public ProductDialogForm(JFrame parent, boolean modal) {
        super(parent, "Agregar Nuevo Producto", modal);
        setSize(400, 300);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        inicializarComponentes();
        setVisible(true);
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(10, 10));
        add(crearPanelFormulario(), BorderLayout.CENTER);
        add(crearPanelBotones(), BorderLayout.SOUTH);
    }

    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Nombre
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        panel.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtNombre = new JTextField(20);
        panel.add(txtNombre, gbc);

        // Unidad
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        panel.add(new JLabel("Unidad:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        cmbUnidad = new JComboBox<>(new String[]{"Galón", "Litro", "Barril"});
        panel.add(cmbUnidad, gbc);

        // Precio
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        panel.add(new JLabel("Precio:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtPrecio = new JTextField(20);
        panel.add(txtPrecio, gbc);

        // Nota
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JLabel lblNota = new JLabel("* Todos los campos son obligatorios");
        lblNota.setFont(new Font("Arial", Font.ITALIC, 11));
        lblNota.setForeground(Color.GRAY);
        panel.add(lblNota, gbc);

        return panel;
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.setPreferredSize(new Dimension(100, 30));
        btnGuardar.setBackground(new Color(25, 135, 84));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.addActionListener(e -> guardarProducto());

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setPreferredSize(new Dimension(100, 30));
        btnCancelar.addActionListener(e -> dispose());

        panel.add(btnCancelar);
        panel.add(btnGuardar);

        return panel;
    }

    private void guardarProducto() {
        String nombre = txtNombre.getText().trim();
        String precioTexto = txtPrecio.getText().trim();

        if (nombre.isEmpty() || precioTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Todos los campos son obligatorios",
                "Error de Validación",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        double precio;
        try {
            precio = Double.parseDouble(precioTexto);
            if (precio <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "El precio debe ser un número positivo",
                "Error de Validación",
                JOptionPane.ERROR_MESSAGE);
            txtPrecio.requestFocus();
            return;
        }

        try {
            String unidad = (String) cmbUnidad.getSelectedItem();
            Product nuevoProducto = new Product(nombre, unidad, precio);
            ProductServices.insertProduct(nuevoProducto);

            guardadoExitoso = true;

            JOptionPane.showMessageDialog(this,
                "Producto guardado exitosamente:\n" + nuevoProducto.getId(),
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE);

            dispose();

        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(this,
                "Error al guardar:\n" + e.getMessage(),
                "Error de Base de Datos",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isGuardadoExitoso() {
        return guardadoExitoso;
    }
}
```

### 🔍 Análisis de Diferencias Clave:

#### 1. **JFrame vs JDialog - Constructor**

```java
// JFrame
public class MiVentana extends JFrame {
    public MiVentana() {
        setTitle("Título");
        // ...
    }
}

// JDialog
public class MiDialogo extends JDialog {
    public MiDialogo(JFrame parent, boolean modal) {
        super(parent, "Título", modal);  // REQUIERE padre
        // ...
    }
}
```

**Diferencia:** JDialog SIEMPRE necesita una ventana padre.

#### 2. **setLocationRelativeTo()**

```java
// Centrar en la pantalla
setLocationRelativeTo(null);

// Centrar respecto a la ventana padre
setLocationRelativeTo(parent);
```

**Efecto Visual:**

```
parent = null:
    [Pantalla]
       │
    [Dialog] ← Centro de la pantalla

parent = JFrame:
  ┌─────────────┐
  │   JFrame    │
  │  ┌───────┐  │
  │  │Dialog │  │ ← Centro del JFrame
  │  └───────┘  │
  └─────────────┘
```

#### 3. **DISPOSE_ON_CLOSE vs EXIT_ON_CLOSE**

```java
// JFrame (ventana principal)
setDefaultCloseOperation(EXIT_ON_CLOSE);  // Cierra aplicación

// JDialog (ventana secundaria)
setDefaultCloseOperation(DISPOSE_ON_CLOSE); // Solo cierra el diálogo
```

**¿Por qué?** Si usas EXIT_ON_CLOSE en un JDialog, cerrar el diálogo cierra TODA la aplicación.

#### 4. **Patrón de Comunicación Padre-Hijo**

```java
// EN PARENT (ProductManagerGUI.java):
private void btnAgregarClick() {
    ProductDialogForm dialogo = new ProductDialogForm(this, true);
    //                                                 ^^^^  ^^^^
    //                                                 │     └─ modal
    //                                                 └─ padre (this)

    // El código se BLOQUEA aquí hasta que el diálogo se cierre

    if (dialogo.isGuardadoExitoso()) {
        cargarProductosDesdeDB();  // Refrescar
    }
}
```

**Flujo Temporal:**

```
1. Usuario click "Agregar" en ProductManagerGUI
2. Se crea ProductDialogForm (modal = true)
3. ⏸️  ProductManagerGUI se BLOQUEA
4. Usuario llena formulario en ProductDialogForm
5. Usuario click "Guardar" → guardadoExitoso = true
6. dialogo.dispose() cierra el diálogo
7. ▶️  ProductManagerGUI continúa ejecución
8. if (dialogo.isGuardadoExitoso()) → true
9. Refrescar tabla
```

### ✅ Criterio de Éxito:
- [x] JDialog se abre centrado respecto a la ventana padre
- [x] Diálogo es modal (bloquea ventana padre)
- [x] Valida campos vacíos
- [x] Valida precio numérico positivo
- [x] Guarda en base de datos correctamente
- [x] Retorna estado de guardado a la ventana padre
- [x] Ventana padre refresca tabla automáticamente

### 🏃 Cómo probar:

1. Modifica `ProductManagerGUI.java` para usar el diálogo:

```java
// En el método donde agregas productos
btnAgregar.addActionListener(e -> {
    ProductDialogForm dialogo = new ProductDialogForm(this, true);
    if (dialogo.isGuardadoExitoso()) {
        cargarProductosDesdeDB();
    }
});
```

2. Ejecuta:

```bash
cd /home/hp/forestechOil/forestech-cli-java
mvn clean compile
mvn exec:java -Dexec.mainClass="com.forestech.ui.ProductManagerGUI"
```

3. Prueba:
   - Click en "Agregar Producto"
   - Intenta interactuar con la ventana principal (debe estar bloqueada)
   - Llena el formulario y guarda
   - Verifica que la tabla se refresca automáticamente

---

## 📋 Checkpoint 9.9: JTabbedPane - Pestañas para Múltiples Vistas (1 hora)

### 🎯 Objetivo:
Aprender a organizar múltiples paneles en una ventana usando pestañas (tabs), permitiendo navegar entre diferentes secciones sin abrir ventanas adicionales.

### 📚 Concepto: JTabbedPane

Un **JTabbedPane** es un componente que organiza múltiples paneles en pestañas, como un navegador web con múltiples páginas abiertas.

**Diagrama Visual:**

```
┌───────────────────────────────────────────────┐
│  [Productos] [Vehículos] [Movimientos]       │ ← JTabbedPane (pestañas)
├───────────────────────────────────────────────┤
│                                               │
│   Contenido de la pestaña "Productos"        │
│                                               │
│   [Tabla con productos...]                   │
│                                               │
│   [Agregar] [Eliminar] [Refrescar]           │
│                                               │
└───────────────────────────────────────────────┘

// Si hago click en "Vehículos":

┌───────────────────────────────────────────────┐
│  [Productos] [Vehículos] [Movimientos]       │
├───────────────────────────────────────────────┤
│                                               │
│   Contenido de la pestaña "Vehículos"        │
│                                               │
│   [Tabla con vehículos...]                   │
│                                               │
│   [Agregar] [Eliminar] [Refrescar]           │
│                                               │
└───────────────────────────────────────────────┘
```

### 💡 Conceptos Clave:

#### 1. **Posición de las Pestañas**

```java
// Pestañas arriba (defecto)
tabbedPane.setTabPlacement(JTabbedPane.TOP);

// Pestañas abajo
tabbedPane.setTabPlacement(JTabbedPane.BOTTOM);

// Pestañas a la izquierda
tabbedPane.setTabPlacement(JTabbedPane.LEFT);

// Pestañas a la derecha
tabbedPane.setTabPlacement(JTabbedPane.RIGHT);
```

**Visual:**

```
TOP:                           LEFT:
┌─[Tab1][Tab2][Tab3]───┐      ┌──┬────────────┐
│                      │      │T │            │
│  Contenido           │      │a │ Contenido  │
│                      │      │b │            │
└──────────────────────┘      │1 │            │
                              └──┴────────────┘

BOTTOM:                        RIGHT:
┌──────────────────────┐      ┌────────────┬──┐
│                      │      │            │T │
│  Contenido           │      │ Contenido  │a │
│                      │      │            │b │
└─[Tab1][Tab2][Tab3]───┘      │            │1 │
                              └────────────┴──┘
```

#### 2. **Agregar Pestañas**

```java
JTabbedPane tabbedPane = new JTabbedPane();

// Método 1: Solo título
tabbedPane.addTab("Título", panelContenido);

// Método 2: Título e ícono
tabbedPane.addTab("Título", icono, panelContenido);

// Método 3: Título, ícono y tooltip
tabbedPane.addTab("Título", icono, panelContenido, "Texto de ayuda");
```

#### 3. **Detectar Cambio de Pestaña**

```java
tabbedPane.addChangeListener(new ChangeListener() {
    @Override
    public void stateChanged(ChangeEvent e) {
        int indiceActual = tabbedPane.getSelectedIndex();
        String titulo = tabbedPane.getTitleAt(indiceActual);
        System.out.println("Cambiaste a la pestaña: " + titulo);
    }
});
```

### 📝 Ejercicio Guiado:

Vamos a crear `ForestechMainGUI.java` - La ventana principal con pestañas para Productos, Vehículos y Movimientos.

#### Paso 1: Estructura básica con JTabbedPane

```java
package com.forestech.ui;

import javax.swing.*;
import java.awt.*;

public class ForestechMainGUI extends JFrame {

    private JTabbedPane tabbedPane;

    public ForestechMainGUI() {
        setTitle("Forestech Oil Management System");
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Crear contenido
        inicializarComponentes();

        setVisible(true);
    }

    private void inicializarComponentes() {
        // Layout principal
        setLayout(new BorderLayout());

        // Panel superior con título
        JPanel panelTitulo = crearPanelTitulo();
        add(panelTitulo, BorderLayout.NORTH);

        // Pestañas en el centro
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));

        // Agregar pestañas
        tabbedPane.addTab("📦 Productos", crearPanelProductos());
        tabbedPane.addTab("🚛 Vehículos", crearPanelVehiculos());
        tabbedPane.addTab("📊 Movimientos", crearPanelMovimientos());
        tabbedPane.addTab("📋 Reportes", crearPanelReportes());

        // Detectar cambios de pestaña
        tabbedPane.addChangeListener(e -> {
            int index = tabbedPane.getSelectedIndex();
            String titulo = tabbedPane.getTitleAt(index);
            System.out.println("Pestaña activa: " + titulo);
        });

        add(tabbedPane, BorderLayout.CENTER);

        // Panel inferior con información
        JPanel panelInferior = crearPanelInferior();
        add(panelInferior, BorderLayout.SOUTH);
    }

    // Continuaremos con los métodos...
}
```

#### Paso 2: Panel de título

```java
private JPanel crearPanelTitulo() {
    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());
    panel.setBackground(new Color(25, 135, 84));
    panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

    // Título principal
    JLabel lblTitulo = new JLabel("FORESTECH OIL MANAGEMENT SYSTEM");
    lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
    lblTitulo.setForeground(Color.WHITE);

    // Subtítulo
    JLabel lblSubtitulo = new JLabel("Sistema Integrado de Gestión");
    lblSubtitulo.setFont(new Font("Arial", Font.PLAIN, 14));
    lblSubtitulo.setForeground(new Color(200, 255, 200));

    // Panel izquierdo con títulos
    JPanel panelTextos = new JPanel(new GridLayout(2, 1, 0, 5));
    panelTextos.setBackground(new Color(25, 135, 84));
    panelTextos.add(lblTitulo);
    panelTextos.add(lblSubtitulo);

    // Botón de ayuda (derecha)
    JButton btnAyuda = new JButton("❓ Ayuda");
    btnAyuda.setBackground(new Color(255, 193, 7));
    btnAyuda.setForeground(Color.BLACK);
    btnAyuda.setFocusPainted(false);
    btnAyuda.addActionListener(e -> mostrarAyuda());

    panel.add(panelTextos, BorderLayout.WEST);
    panel.add(btnAyuda, BorderLayout.EAST);

    return panel;
}
```

#### Paso 3: Pestaña de Productos

```java
private JPanel crearPanelProductos() {
    JPanel panel = new JPanel(new BorderLayout(10, 10));
    panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    panel.setBackground(Color.WHITE);

    // Título de la sección
    JLabel lblTitulo = new JLabel("Gestión de Productos de Combustible");
    lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
    lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

    // Tabla de productos
    String[] columnas = {"ID", "Nombre", "Unidad", "Precio"};
    Object[][] datos = {
        {"PROD-001", "Diesel", "Galón", "$3.50"},
        {"PROD-002", "Gasolina Regular", "Galón", "$3.80"},
        {"PROD-003", "Gasolina Premium", "Galón", "$4.20"}
    };

    JTable tabla = new JTable(datos, columnas);
    tabla.setRowHeight(25);
    tabla.setFont(new Font("Arial", Font.PLAIN, 13));
    tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
    tabla.getTableHeader().setBackground(new Color(230, 230, 230));

    JScrollPane scrollPane = new JScrollPane(tabla);

    // Panel de botones
    JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
    panelBotones.setBackground(Color.WHITE);

    JButton btnAgregar = new JButton("➕ Agregar Producto");
    btnAgregar.setBackground(new Color(25, 135, 84));
    btnAgregar.setForeground(Color.WHITE);
    btnAgregar.setPreferredSize(new Dimension(180, 35));

    JButton btnEliminar = new JButton("🗑️ Eliminar");
    btnEliminar.setBackground(new Color(220, 53, 69));
    btnEliminar.setForeground(Color.WHITE);
    btnEliminar.setPreferredSize(new Dimension(150, 35));

    JButton btnRefrescar = new JButton("🔄 Refrescar");
    btnRefrescar.setPreferredSize(new Dimension(150, 35));

    panelBotones.add(btnAgregar);
    panelBotones.add(btnEliminar);
    panelBotones.add(btnRefrescar);

    // Ensamblar panel
    JPanel panelSuperior = new JPanel(new BorderLayout());
    panelSuperior.setBackground(Color.WHITE);
    panelSuperior.add(lblTitulo, BorderLayout.NORTH);

    panel.add(panelSuperior, BorderLayout.NORTH);
    panel.add(scrollPane, BorderLayout.CENTER);
    panel.add(panelBotones, BorderLayout.SOUTH);

    return panel;
}
```

#### Paso 4: Pestaña de Vehículos

```java
private JPanel crearPanelVehiculos() {
    JPanel panel = new JPanel(new BorderLayout(10, 10));
    panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    panel.setBackground(Color.WHITE);

    JLabel lblTitulo = new JLabel("Gestión de Flota Vehicular");
    lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
    lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

    // Tabla de vehículos
    String[] columnas = {"ID", "Nombre", "Categoría", "Capacidad (L)", "Combustible"};
    Object[][] datos = {
        {"VEH-001", "Excavadora CAT 320", "Excavadora", "350.0", "Diesel"},
        {"VEH-002", "Camión Volvo FH16", "Camión", "800.0", "Diesel"},
        {"VEH-003", "Retroexcavadora JCB", "Retroexcavadora", "250.0", "Diesel"}
    };

    JTable tabla = new JTable(datos, columnas);
    tabla.setRowHeight(25);
    tabla.setFont(new Font("Arial", Font.PLAIN, 13));
    tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
    tabla.getTableHeader().setBackground(new Color(230, 230, 230));

    JScrollPane scrollPane = new JScrollPane(tabla);

    // Panel de botones
    JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
    panelBotones.setBackground(Color.WHITE);

    JButton btnAgregar = new JButton("➕ Agregar Vehículo");
    btnAgregar.setBackground(new Color(25, 135, 84));
    btnAgregar.setForeground(Color.WHITE);
    btnAgregar.setPreferredSize(new Dimension(180, 35));

    JButton btnEliminar = new JButton("🗑️ Eliminar");
    btnEliminar.setBackground(new Color(220, 53, 69));
    btnEliminar.setForeground(Color.WHITE);
    btnEliminar.setPreferredSize(new Dimension(150, 35));

    JButton btnRefrescar = new JButton("🔄 Refrescar");
    btnRefrescar.setPreferredSize(new Dimension(150, 35));

    panelBotones.add(btnAgregar);
    panelBotones.add(btnEliminar);
    panelBotones.add(btnRefrescar);

    panel.add(lblTitulo, BorderLayout.NORTH);
    panel.add(scrollPane, BorderLayout.CENTER);
    panel.add(panelBotones, BorderLayout.SOUTH);

    return panel;
}
```

#### Paso 5: Pestaña de Movimientos

```java
private JPanel crearPanelMovimientos() {
    JPanel panel = new JPanel(new BorderLayout(10, 10));
    panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    panel.setBackground(Color.WHITE);

    JLabel lblTitulo = new JLabel("Historial de Movimientos");
    lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
    lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

    // Filtros
    JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
    panelFiltros.setBackground(Color.WHITE);

    panelFiltros.add(new JLabel("Tipo:"));
    JComboBox<String> cmbTipo = new JComboBox<>(new String[]{"Todos", "ENTRADA", "SALIDA"});
    panelFiltros.add(cmbTipo);

    panelFiltros.add(new JLabel("Producto:"));
    JComboBox<String> cmbProducto = new JComboBox<>(new String[]{"Todos", "Diesel", "Gasolina"});
    panelFiltros.add(cmbProducto);

    JButton btnFiltrar = new JButton("🔍 Filtrar");
    panelFiltros.add(btnFiltrar);

    // Tabla de movimientos
    String[] columnas = {"ID", "Fecha", "Tipo", "Producto", "Cantidad", "Vehículo"};
    Object[][] datos = {
        {"MOV-001", "2025-01-10", "SALIDA", "Diesel", "150.0 L", "Excavadora CAT 320"},
        {"MOV-002", "2025-01-11", "ENTRADA", "Diesel", "500.0 L", "---"},
        {"MOV-003", "2025-01-11", "SALIDA", "Gasolina", "80.0 L", "Camión Volvo"}
    };

    JTable tabla = new JTable(datos, columnas);
    tabla.setRowHeight(25);
    tabla.setFont(new Font("Arial", Font.PLAIN, 13));
    tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
    tabla.getTableHeader().setBackground(new Color(230, 230, 230));

    JScrollPane scrollPane = new JScrollPane(tabla);

    // Panel de botones
    JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
    panelBotones.setBackground(Color.WHITE);

    JButton btnEntrada = new JButton("⬇️ Registrar Entrada");
    btnEntrada.setBackground(new Color(40, 167, 69));
    btnEntrada.setForeground(Color.WHITE);
    btnEntrada.setPreferredSize(new Dimension(180, 35));

    JButton btnSalida = new JButton("⬆️ Registrar Salida");
    btnSalida.setBackground(new Color(0, 123, 255));
    btnSalida.setForeground(Color.WHITE);
    btnSalida.setPreferredSize(new Dimension(180, 35));

    JButton btnRefrescar = new JButton("🔄 Refrescar");
    btnRefrescar.setPreferredSize(new Dimension(150, 35));

    panelBotones.add(btnEntrada);
    panelBotones.add(btnSalida);
    panelBotones.add(btnRefrescar);

    // Ensamblar
    JPanel panelSuperior = new JPanel(new BorderLayout());
    panelSuperior.setBackground(Color.WHITE);
    panelSuperior.add(lblTitulo, BorderLayout.NORTH);
    panelSuperior.add(panelFiltros, BorderLayout.CENTER);

    panel.add(panelSuperior, BorderLayout.NORTH);
    panel.add(scrollPane, BorderLayout.CENTER);
    panel.add(panelBotones, BorderLayout.SOUTH);

    return panel;
}
```

#### Paso 6: Pestaña de Reportes

```java
private JPanel crearPanelReportes() {
    JPanel panel = new JPanel(new BorderLayout(10, 10));
    panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    panel.setBackground(Color.WHITE);

    JLabel lblTitulo = new JLabel("Reportes y Estadísticas");
    lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
    lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

    // Panel central con estadísticas
    JPanel panelEstadisticas = new JPanel(new GridLayout(2, 2, 20, 20));
    panelEstadisticas.setBackground(Color.WHITE);
    panelEstadisticas.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    // Tarjetas de estadísticas
    panelEstadisticas.add(crearTarjetaEstadistica("Stock Total", "1,250 Litros", new Color(25, 135, 84)));
    panelEstadisticas.add(crearTarjetaEstadistica("Movimientos Hoy", "12 registros", new Color(0, 123, 255)));
    panelEstadisticas.add(crearTarjetaEstadistica("Vehículos Activos", "8 unidades", new Color(255, 193, 7)));
    panelEstadisticas.add(crearTarjetaEstadistica("Productos", "5 tipos", new Color(108, 117, 125)));

    panel.add(lblTitulo, BorderLayout.NORTH);
    panel.add(panelEstadisticas, BorderLayout.CENTER);

    return panel;
}

private JPanel crearTarjetaEstadistica(String titulo, String valor, Color color) {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBackground(color);
    panel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(color.darker(), 2),
        BorderFactory.createEmptyBorder(20, 20, 20, 20)
    ));

    JLabel lblTitulo = new JLabel(titulo);
    lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
    lblTitulo.setForeground(Color.WHITE);
    lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

    JLabel lblValor = new JLabel(valor);
    lblValor.setFont(new Font("Arial", Font.BOLD, 32));
    lblValor.setForeground(Color.WHITE);
    lblValor.setAlignmentX(Component.CENTER_ALIGNMENT);

    panel.add(Box.createVerticalGlue());
    panel.add(lblTitulo);
    panel.add(Box.createRigidArea(new Dimension(0, 10)));
    panel.add(lblValor);
    panel.add(Box.createVerticalGlue());

    return panel;
}
```

#### Paso 7: Panel inferior con información

```java
private JPanel crearPanelInferior() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
    panel.setBackground(new Color(248, 249, 250));
    panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));

    JLabel lblConexion = new JLabel("🟢 Conectado a MySQL");
    lblConexion.setFont(new Font("Arial", Font.PLAIN, 12));

    JLabel lblUsuario = new JLabel("👤 Usuario: Admin");
    lblUsuario.setFont(new Font("Arial", Font.PLAIN, 12));

    JLabel lblFecha = new JLabel("📅 " + java.time.LocalDate.now());
    lblFecha.setFont(new Font("Arial", Font.PLAIN, 12));

    panel.add(lblConexion);
    panel.add(new JSeparator(SwingConstants.VERTICAL));
    panel.add(lblUsuario);
    panel.add(new JSeparator(SwingConstants.VERTICAL));
    panel.add(lblFecha);

    return panel;
}

private void mostrarAyuda() {
    JOptionPane.showMessageDialog(this,
        "Forestech Oil Management System\n\n" +
        "Navegación por Pestañas:\n" +
        "• Productos: Gestión del catálogo\n" +
        "• Vehículos: Administración de flota\n" +
        "• Movimientos: Registro de entradas/salidas\n" +
        "• Reportes: Estadísticas y análisis\n\n" +
        "Para más ayuda, consulta la documentación.",
        "Ayuda",
        JOptionPane.INFORMATION_MESSAGE);
}
```

#### Paso 8: Método main

```java
public static void main(String[] args) {
    try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
        e.printStackTrace();
    }

    SwingUtilities.invokeLater(() -> new ForestechMainGUI());
}
```

### 🔍 Análisis de Conceptos Clave:

#### 1. **addTab() - Agregar Pestañas**

```java
// Sintaxis básica
tabbedPane.addTab("Título", panel);

// Con ícono (emoji como texto funciona)
tabbedPane.addTab("📦 Productos", panel);

// Obtener índice de la pestaña agregada
int index = tabbedPane.getTabCount() - 1;
```

#### 2. **Navegación Programática**

```java
// Seleccionar pestaña por índice
tabbedPane.setSelectedIndex(0);  // Primera pestaña

// Seleccionar pestaña por componente
tabbedPane.setSelectedComponent(miPanel);

// Obtener pestaña actual
int indiceActual = tabbedPane.getSelectedIndex();
Component componenteActual = tabbedPane.getSelectedComponent();
```

#### 3. **Habilitar/Deshabilitar Pestañas**

```java
// Deshabilitar una pestaña
tabbedPane.setEnabledAt(2, false);  // Deshabilita la 3ra pestaña

// Verificar si está habilitada
boolean habilitada = tabbedPane.isEnabledAt(2);
```

**Visual:**

```
┌─[Productos][Vehículos][Movimientos]───┐
                         ^^^^^^^^^^^^^^
                         (deshabilitada, aparece gris)
```

#### 4. **ChangeListener - Detectar Cambios**

```java
tabbedPane.addChangeListener(e -> {
    int index = tabbedPane.getSelectedIndex();

    // Cargar datos solo cuando se abre la pestaña
    if (index == 0) {
        cargarProductos();
    } else if (index == 1) {
        cargarVehiculos();
    }
});
```

**¿Por qué es útil?** Evita cargar datos de todas las pestañas al inicio. Solo carga cuando el usuario las abre.

### ✅ Criterio de Éxito:
- [x] JTabbedPane visible con 4 pestañas
- [x] Cada pestaña tiene su propio contenido independiente
- [x] Navegación fluida entre pestañas
- [x] ChangeListener detecta cambio de pestañas
- [x] Panel superior con título y botón de ayuda
- [x] Panel inferior con información de estado
- [x] Diseño profesional con colores diferenciados

### 🏃 Cómo ejecutar:

```bash
cd /home/hp/forestechOil/forestech-cli-java
mvn clean compile
mvn exec:java -Dexec.mainClass="com.forestech.ui.ForestechMainGUI"
```

**Prueba:**
1. Navega entre las 4 pestañas
2. Observa cómo cambia el contenido
3. Click en el botón "❓ Ayuda"
4. Verifica que cada pestaña tiene sus propios botones funcionales

### 🎨 Mejoras Opcionales:

```java
// Agregar tooltips a las pestañas
tabbedPane.setToolTipTextAt(0, "Gestionar productos de combustible");

// Cambiar color de fondo de una pestaña
tabbedPane.setBackgroundAt(1, new Color(255, 240, 240));

// Cerrar pestaña con botón X (avanzado)
tabbedPane.setTabComponentAt(0, crearTabConBotonCerrar("Productos"));
```

---

## 📋 Checkpoint 9.10: Vehicle GUI Integration - CRUD Completo (2 horas)

### 🎯 Objetivo:
Crear la interfaz gráfica completa para gestionar vehículos, integrando con `VehicleServices` y validando la FK `fuel_product_id` con ProductServices.

### 🔗 Recordatorio de Relaciones FK:

Según `.claude/DB_SCHEMA_REFERENCE.md`:

```
vehicles.fuel_product_id → oil_products.id
   ON DELETE SET NULL (si borras el producto, el vehículo queda sin fuel_product_id)
   ON UPDATE CASCADE (si cambias el id del producto, se actualiza en vehículos)
```

**Validación requerida:** Antes de insertar un vehículo, si `fuel_product_id` NO es NULL, debe existir en `oil_products`.

### 📝 Ejercicio Guiado:

Vamos a crear `VehicleManagerGUI.java` - Interfaz completa para gestionar vehículos con validación de FK.

#### Paso 1: Estructura básica con tabla

```java
package com.forestech.ui;

import com.forestech.exceptions.DatabaseException;
import com.forestech.models.Product;
import com.forestech.models.Vehicle;
import com.forestech.services.ProductServices;
import com.forestech.services.VehicleServices;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class VehicleManagerGUI extends JFrame {

    private JTable tabla;
    private DefaultTableModel modelo;
    private JButton btnAgregar, btnEditar, btnEliminar, btnRefrescar;

    public VehicleManagerGUI() {
        setTitle("Gestión de Vehículos - Forestech");
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        inicializarComponentes();
        cargarVehiculosDesdeDB();

        setVisible(true);
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(10, 10));

        // Panel superior con título
        JPanel panelTitulo = crearPanelTitulo();
        add(panelTitulo, BorderLayout.NORTH);

        // Panel central con tabla
        JPanel panelTabla = crearPanelTabla();
        add(panelTabla, BorderLayout.CENTER);

        // Panel inferior con botones
        JPanel panelBotones = crearPanelBotones();
        add(panelBotones, BorderLayout.SOUTH);
    }

    // Continuaremos...
}
```

#### Paso 2: Panel de título

```java
private JPanel crearPanelTitulo() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBackground(new Color(0, 123, 255));
    panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

    JLabel lblTitulo = new JLabel("🚛 GESTIÓN DE FLOTA VEHICULAR");
    lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
    lblTitulo.setForeground(Color.WHITE);

    JLabel lblSubtitulo = new JLabel("Administra los vehículos que consumen combustible");
    lblSubtitulo.setFont(new Font("Arial", Font.PLAIN, 13));
    lblSubtitulo.setForeground(new Color(200, 230, 255));

    JPanel panelTextos = new JPanel(new GridLayout(2, 1, 0, 5));
    panelTextos.setBackground(new Color(0, 123, 255));
    panelTextos.add(lblTitulo);
    panelTextos.add(lblSubtitulo);

    panel.add(panelTextos, BorderLayout.WEST);

    return panel;
}
```

#### Paso 3: Panel con tabla de vehículos

```java
private JPanel crearPanelTabla() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // Modelo de tabla
    String[] columnas = {"ID", "Nombre", "Categoría", "Capacidad (L)",
                         "Combustible", "Horómetro"};
    modelo = new DefaultTableModel(columnas, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;  // No editable directamente en tabla
        }
    };

    tabla = new JTable(modelo);
    tabla.setRowHeight(30);
    tabla.setFont(new Font("Arial", Font.PLAIN, 13));
    tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
    tabla.getTableHeader().setBackground(new Color(230, 230, 230));
    tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    // Ajustar anchos de columnas
    tabla.getColumnModel().getColumn(0).setPreferredWidth(120);  // ID
    tabla.getColumnModel().getColumn(1).setPreferredWidth(200);  // Nombre
    tabla.getColumnModel().getColumn(2).setPreferredWidth(120);  // Categoría
    tabla.getColumnModel().getColumn(3).setPreferredWidth(100);  // Capacidad
    tabla.getColumnModel().getColumn(4).setPreferredWidth(150);  // Combustible
    tabla.getColumnModel().getColumn(5).setPreferredWidth(80);   // Horómetro

    JScrollPane scrollPane = new JScrollPane(tabla);
    panel.add(scrollPane, BorderLayout.CENTER);

    return panel;
}
```

#### Paso 4: Panel de botones

```java
private JPanel crearPanelBotones() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
    panel.setBackground(new Color(248, 249, 250));

    btnAgregar = new JButton("➕ Agregar Vehículo");
    btnAgregar.setBackground(new Color(40, 167, 69));
    btnAgregar.setForeground(Color.WHITE);
    btnAgregar.setPreferredSize(new Dimension(180, 40));
    btnAgregar.setFont(new Font("Arial", Font.BOLD, 13));
    btnAgregar.addActionListener(e -> abrirDialogoAgregar());

    btnEditar = new JButton("✏️ Editar");
    btnEditar.setPreferredSize(new Dimension(150, 40));
    btnEditar.setFont(new Font("Arial", Font.BOLD, 13));
    btnEditar.addActionListener(e -> editarVehiculo());

    btnEliminar = new JButton("🗑️ Eliminar");
    btnEliminar.setBackground(new Color(220, 53, 69));
    btnEliminar.setForeground(Color.WHITE);
    btnEliminar.setPreferredSize(new Dimension(150, 40));
    btnEliminar.setFont(new Font("Arial", Font.BOLD, 13));
    btnEliminar.addActionListener(e -> eliminarVehiculo());

    btnRefrescar = new JButton("🔄 Refrescar");
    btnRefrescar.setBackground(new Color(108, 117, 125));
    btnRefrescar.setForeground(Color.WHITE);
    btnRefrescar.setPreferredSize(new Dimension(150, 40));
    btnRefrescar.setFont(new Font("Arial", Font.BOLD, 13));
    btnRefrescar.addActionListener(e -> cargarVehiculosDesdeDB());

    panel.add(btnAgregar);
    panel.add(btnEditar);
    panel.add(btnEliminar);
    panel.add(btnRefrescar);

    return panel;
}
```

#### Paso 5: Cargar vehículos desde BD

```java
private void cargarVehiculosDesdeDB() {
    try {
        // Limpiar tabla
        modelo.setRowCount(0);

        // Obtener vehículos desde VehicleServices
        List<Vehicle> vehiculos = VehicleServices.getAllVehicles();

        // Llenar tabla
        for (Vehicle v : vehiculos) {
            // Obtener nombre del producto de combustible
            String nombreCombustible = "---";
            if (v.getFuelProductId() != null && !v.getFuelProductId().trim().isEmpty()) {
                try {
                    Product producto = ProductServices.getProductById(v.getFuelProductId());
                    if (producto != null) {
                        nombreCombustible = producto.getName();
                    }
                } catch (DatabaseException ex) {
                    nombreCombustible = "Error al cargar";
                }
            }

            String horometro = v.isHaveHorometer() ? "Sí" : "No";

            modelo.addRow(new Object[]{
                v.getId(),
                v.getName(),
                v.getCategory(),
                String.format("%.2f L", v.getCapacity()),
                nombreCombustible,
                horometro
            });
        }

        System.out.println("✅ Se cargaron " + vehiculos.size() + " vehículos");

    } catch (DatabaseException e) {
        mostrarError("Error al Cargar Vehículos",
            "No se pudieron cargar los vehículos desde la base de datos:\n" + e.getMessage());
    }
}
```

#### Paso 6: Diálogo para agregar vehículo

```java
private void abrirDialogoAgregar() {
    VehicleDialogForm dialogo = new VehicleDialogForm(this, true, null);
    if (dialogo.isGuardadoExitoso()) {
        cargarVehiculosDesdeDB();
    }
}
```

#### Paso 7: Editar vehículo

```java
private void editarVehiculo() {
    int filaSeleccionada = tabla.getSelectedRow();

    if (filaSeleccionada == -1) {
        JOptionPane.showMessageDialog(this,
            "Selecciona un vehículo de la tabla",
            "Sin Selección",
            JOptionPane.WARNING_MESSAGE);
        return;
    }

    String vehicleId = (String) modelo.getValueAt(filaSeleccionada, 0);

    try {
        // Obtener vehículo completo desde la BD
        Vehicle vehicle = VehicleServices.getVehicleById(vehicleId);

        if (vehicle == null) {
            mostrarError("Error", "No se encontró el vehículo en la base de datos");
            return;
        }

        // Abrir diálogo de edición
        VehicleDialogForm dialogo = new VehicleDialogForm(this, true, vehicle);

        if (dialogo.isGuardadoExitoso()) {
            cargarVehiculosDesdeDB();
        }

    } catch (DatabaseException e) {
        mostrarError("Error al Editar", e.getMessage());
    }
}
```

#### Paso 8: Eliminar vehículo

```java
private void eliminarVehiculo() {
    int filaSeleccionada = tabla.getSelectedRow();

    if (filaSeleccionada == -1) {
        JOptionPane.showMessageDialog(this,
            "Selecciona un vehículo de la tabla",
            "Sin Selección",
            JOptionPane.WARNING_MESSAGE);
        return;
    }

    String vehicleId = (String) modelo.getValueAt(filaSeleccionada, 0);
    String vehicleName = (String) modelo.getValueAt(filaSeleccionada, 1);

    // Confirmación
    int confirmacion = JOptionPane.showConfirmDialog(this,
        "¿Estás seguro de eliminar el vehículo?\n\n" +
        "ID: " + vehicleId + "\n" +
        "Nombre: " + vehicleName + "\n\n" +
        "ADVERTENCIA: Si este vehículo tiene movimientos asociados,\n" +
        "esos movimientos quedarán sin vehículo (vehicle_id = NULL).",
        "Confirmar Eliminación",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.WARNING_MESSAGE);

    if (confirmacion != JOptionPane.YES_OPTION) {
        return;
    }

    try {
        boolean eliminado = VehicleServices.deleteVehicle(vehicleId);

        if (eliminado) {
            JOptionPane.showMessageDialog(this,
                "Vehículo eliminado exitosamente",
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE);
            cargarVehiculosDesdeDB();
        } else {
            mostrarError("Error", "No se pudo eliminar el vehículo");
        }

    } catch (DatabaseException e) {
        mostrarError("Error al Eliminar", e.getMessage());
    }
}
```

#### Paso 9: Método auxiliar para errores

```java
private void mostrarError(String titulo, String mensaje) {
    JOptionPane.showMessageDialog(this,
        mensaje,
        titulo,
        JOptionPane.ERROR_MESSAGE);
}
```

#### Paso 10: Main

```java
public static void main(String[] args) {
    try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
        e.printStackTrace();
    }

    SwingUtilities.invokeLater(() -> new VehicleManagerGUI());
}
```

---

### 📝 Parte 2: VehicleDialogForm.java (Diálogo con validación FK)

Este diálogo maneja tanto AGREGAR como EDITAR vehículos.

```java
package com.forestech.ui;

import com.forestech.exceptions.DatabaseException;
import com.forestech.models.Product;
import com.forestech.models.Vehicle;
import com.forestech.services.ProductServices;
import com.forestech.services.VehicleServices;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class VehicleDialogForm extends JDialog {

    private JTextField txtNombre;
    private JComboBox<String> cmbCategoria;
    private JTextField txtCapacidad;
    private JComboBox<ProductoItem> cmbCombustible;
    private JCheckBox chkHorometro;

    private Vehicle vehiculoExistente;  // null si es NUEVO, objeto si es EDITAR
    private boolean guardadoExitoso = false;

    /**
     * Constructor para AGREGAR (vehiculoExistente = null)
     * o EDITAR (vehiculoExistente != null)
     */
    public VehicleDialogForm(JFrame parent, boolean modal, Vehicle vehiculoExistente) {
        super(parent, vehiculoExistente == null ? "Agregar Vehículo" : "Editar Vehículo", modal);

        this.vehiculoExistente = vehiculoExistente;

        setSize(500, 450);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        inicializarComponentes();

        // Si es edición, llenar campos con datos existentes
        if (vehiculoExistente != null) {
            cargarDatosVehiculo();
        }

        setVisible(true);
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(10, 10));

        add(crearPanelFormulario(), BorderLayout.CENTER);
        add(crearPanelBotones(), BorderLayout.SOUTH);
    }

    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Fila 0: Nombre
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        panel.add(new JLabel("Nombre:"), gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        txtNombre = new JTextField(25);
        panel.add(txtNombre, gbc);

        // Fila 1: Categoría
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        panel.add(new JLabel("Categoría:"), gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        cmbCategoria = new JComboBox<>(new String[]{
            "Camión", "Excavadora", "Retroexcavadora", "Bulldozer",
            "Cargador Frontal", "Motoniveladora", "Grúa", "Otro"
        });
        panel.add(cmbCategoria, gbc);

        // Fila 2: Capacidad
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        panel.add(new JLabel("Capacidad (L):"), gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        txtCapacidad = new JTextField(25);
        panel.add(txtCapacidad, gbc);

        // Fila 3: Combustible (FK → oil_products)
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.3;
        JLabel lblCombustible = new JLabel("Combustible:");
        panel.add(lblCombustible, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        cmbCombustible = new JComboBox<>();
        cargarProductosCombustible();
        panel.add(cmbCombustible, gbc);

        // Fila 4: Tiene Horómetro
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.3;
        panel.add(new JLabel("Horómetro:"), gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        chkHorometro = new JCheckBox("Tiene horómetro");
        panel.add(chkHorometro, gbc);

        // Fila 5: Nota informativa
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        JLabel lblNota = new JLabel("* Los campos Nombre, Categoría y Capacidad son obligatorios");
        lblNota.setFont(new Font("Arial", Font.ITALIC, 11));
        lblNota.setForeground(Color.GRAY);
        panel.add(lblNota, gbc);

        return panel;
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnGuardar = new JButton(vehiculoExistente == null ? "Guardar" : "Actualizar");
        btnGuardar.setPreferredSize(new Dimension(120, 35));
        btnGuardar.setBackground(new Color(40, 167, 69));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.addActionListener(e -> guardarVehiculo());

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setPreferredSize(new Dimension(120, 35));
        btnCancelar.addActionListener(e -> dispose());

        panel.add(btnCancelar);
        panel.add(btnGuardar);

        return panel;
    }

    /**
     * Carga todos los productos desde la BD en el JComboBox
     */
    private void cargarProductosCombustible() {
        try {
            // Opción para "Sin Combustible"
            cmbCombustible.addItem(new ProductoItem(null, "--- Sin combustible ---"));

            // Cargar productos reales
            List<Product> productos = ProductServices.getAllProducts();

            for (Product p : productos) {
                cmbCombustible.addItem(new ProductoItem(p.getId(), p.getName()));
            }

        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar productos de combustible:\n" + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Carga datos del vehículo existente en los campos (modo EDITAR)
     */
    private void cargarDatosVehiculo() {
        txtNombre.setText(vehiculoExistente.getName());
        cmbCategoria.setSelectedItem(vehiculoExistente.getCategory());
        txtCapacidad.setText(String.valueOf(vehiculoExistente.getCapacity()));
        chkHorometro.setSelected(vehiculoExistente.isHaveHorometer());

        // Seleccionar el producto en el combo
        String fuelId = vehiculoExistente.getFuelProductId();
        if (fuelId != null && !fuelId.trim().isEmpty()) {
            for (int i = 0; i < cmbCombustible.getItemCount(); i++) {
                ProductoItem item = cmbCombustible.getItemAt(i);
                if (item.id != null && item.id.equals(fuelId)) {
                    cmbCombustible.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    /**
     * Guarda o actualiza el vehículo
     */
    private void guardarVehiculo() {
        // Validación 1: Campos obligatorios
        String nombre = txtNombre.getText().trim();
        String categoriaStr = (String) cmbCategoria.getSelectedItem();
        String capacidadStr = txtCapacidad.getText().trim();

        if (nombre.isEmpty() || categoriaStr == null || capacidadStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Los campos Nombre, Categoría y Capacidad son obligatorios",
                "Error de Validación",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validación 2: Capacidad numérica positiva
        double capacidad;
        try {
            capacidad = Double.parseDouble(capacidadStr);
            if (capacidad <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "La capacidad debe ser un número positivo",
                "Error de Validación",
                JOptionPane.ERROR_MESSAGE);
            txtCapacidad.requestFocus();
            return;
        }

        // Obtener fuel_product_id seleccionado (puede ser null)
        ProductoItem productoSeleccionado = (ProductoItem) cmbCombustible.getSelectedItem();
        String fuelProductId = productoSeleccionado != null ? productoSeleccionado.id : null;

        boolean tieneHorometro = chkHorometro.isSelected();

        try {
            if (vehiculoExistente == null) {
                // MODO: AGREGAR NUEVO
                Vehicle nuevoVehiculo = new Vehicle(nombre, categoriaStr, capacidad,
                                                     fuelProductId, tieneHorometro);

                VehicleServices.insertVehicle(nuevoVehiculo);

                guardadoExitoso = true;

                JOptionPane.showMessageDialog(this,
                    "Vehículo agregado exitosamente:\n" + nuevoVehiculo.getId(),
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);

            } else {
                // MODO: EDITAR EXISTENTE
                vehiculoExistente.setName(nombre);
                vehiculoExistente.setCategory(categoriaStr);
                vehiculoExistente.setCapacity(capacidad);
                vehiculoExistente.setFuelProductId(fuelProductId);
                vehiculoExistente.setHaveHorometer(tieneHorometro);

                boolean actualizado = VehicleServices.updateVehicle(vehiculoExistente);

                if (actualizado) {
                    guardadoExitoso = true;

                    JOptionPane.showMessageDialog(this,
                        "Vehículo actualizado exitosamente",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                        "No se pudo actualizar el vehículo",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }

            if (guardadoExitoso) {
                dispose();
            }

        } catch (DatabaseException e) {
            // Captura errores como fuel_product_id inexistente
            JOptionPane.showMessageDialog(this,
                "Error al guardar:\n" + e.getMessage(),
                "Error de Base de Datos",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isGuardadoExitoso() {
        return guardadoExitoso;
    }

    /**
     * Clase auxiliar para almacenar ID y Nombre en el JComboBox
     */
    private static class ProductoItem {
        String id;
        String nombre;

        ProductoItem(String id, String nombre) {
            this.id = id;
            this.nombre = nombre;
        }

        @Override
        public String toString() {
            return nombre;  // Esto es lo que muestra el JComboBox
        }
    }
}
```

### 🔍 Análisis de Conceptos Clave:

#### 1. **JComboBox con Objetos Personalizados**

```java
// Clase auxiliar para almacenar id + nombre
private static class ProductoItem {
    String id;
    String nombre;

    @Override
    public String toString() {
        return nombre;  // El combo muestra esto
    }
}

// Uso
cmbCombustible.addItem(new ProductoItem("PROD-001", "Diesel"));

// Obtener el objeto seleccionado
ProductoItem seleccionado = (ProductoItem) cmbCombustible.getSelectedItem();
String fuelProductId = seleccionado.id;  // Acceder al ID interno
```

**¿Por qué?** El JComboBox muestra el `nombre` al usuario, pero internamente guardamos el `id` para usarlo en la BD.

#### 2. **Diálogo Multi-Propósito (Agregar + Editar)**

```java
// Constructor recibe el objeto a editar (o null si es nuevo)
public VehicleDialogForm(JFrame parent, boolean modal, Vehicle vehiculoExistente) {
    if (vehiculoExistente == null) {
        // MODO AGREGAR
    } else {
        // MODO EDITAR
        cargarDatosVehiculo();
    }
}
```

**Ventaja:** Un solo diálogo para ambas operaciones, reutilizando código.

#### 3. **Validación de FK en VehicleServices**

Según VehicleServices.java (líneas 42-51), antes de insertar se valida:

```java
if (vehicle.getFuelProductId() != null && !vehicle.getFuelProductId().trim().isEmpty()) {
    if (!ProductServices.existsProduct(vehicle.getFuelProductId())) {
        throw new DatabaseException("ERROR: El producto NO existe...");
    }
}
```

**Flujo:**
```
Usuario selecciona "Diesel" en combo
   ↓
ProductoItem con id = "PROD-001"
   ↓
Se llama VehicleServices.insertVehicle()
   ↓
VehicleServices valida que "PROD-001" existe en oil_products
   ↓
Si NO existe → DatabaseException
Si existe → INSERT en vehicles
```

### ✅ Criterio de Éxito:
- [x] Tabla muestra vehículos reales desde MySQL
- [x] Botón "Agregar" abre diálogo con validación de FK
- [x] Botón "Editar" carga datos existentes y actualiza
- [x] Botón "Eliminar" elimina con confirmación
- [x] JComboBox carga productos REALES desde ProductServices
- [x] Validación de fuel_product_id funciona correctamente
- [x] Errores de FK se muestran al usuario
- [x] Tabla refresca automáticamente después de operaciones

### 🏃 Cómo ejecutar:

```bash
cd /home/hp/forestechOil/forestech-cli-java
mvn clean compile
mvn exec:java -Dexec.mainClass="com.forestech.ui.VehicleManagerGUI"
```

**Pruebas:**
1. Agregar vehículo con combustible válido → Debe guardar
2. Agregar vehículo sin combustible → Debe guardar (NULL permitido)
3. Intentar agregar vehículo con combustible que no existe (crear caso extremo) → Debe mostrar error
4. Editar vehículo existente → Debe actualizar
5. Eliminar vehículo → Debe solicitar confirmación

---

## 📋 Checkpoint 9.11: Movement GUI - Validación Completa de FKs y Stock (3 horas)

### 🎯 Objetivo:
Crear la interfaz gráfica más compleja del sistema: gestión de movimientos (ENTRADA/SALIDA) con validación de **3 foreign keys** y verificación de **stock insuficiente**.

### 🔗 Recordatorio de Relaciones FK:

Según `.claude/DB_SCHEMA_REFERENCE.md` y MovementServices.java:

```
Movement.product_id → oil_products.id  (OBLIGATORIO)
Movement.vehicle_id → vehicles.id       (OPCIONAL, solo para SALIDA)
Movement.numero_factura → facturas.numero_factura (OPCIONAL, solo para ENTRADA)
```

**Validaciones de MovementServices** (líneas 87-131):

1. **product_id:** OBLIGATORIO, debe existir en oil_products
2. **vehicle_id:** Si NO es NULL, debe existir en vehicles
3. **numero_factura:** Si NO es NULL, debe existir en facturas
4. **SALIDA:** Valida stock suficiente, lanza `InsufficientStockException` si no hay

### 💡 Reglas de Negocio:

```
ENTRADA (compra de combustible):
   product_id:  ✅ OBLIGATORIO (qué producto entra)
   vehicle_id:  ❌ NULL (no se asigna a vehículo aún)
   numero_factura: ✅ OBLIGATORIO (qué factura respalda)

SALIDA (despacho a vehículo):
   product_id:  ✅ OBLIGATORIO (qué producto sale)
   vehicle_id:  ✅ OBLIGATORIO (a qué vehículo va)
   numero_factura: ❌ NULL (no hay factura de compra)
```

### 📝 Ejercicio Guiado:

Vamos a crear `MovementManagerGUI.java` con formularios separados para ENTRADA y SALIDA.

#### Paso 1: Estructura principal con filtros

```java
package com.forestech.ui;

import com.forestech.exceptions.DatabaseException;
import com.forestech.models.Movement;
import com.forestech.services.MovementServices;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MovementManagerGUI extends JFrame {

    private JTable tabla;
    private DefaultTableModel modelo;
    private JComboBox<String> cmbFiltroTipo;
    private JButton btnEntrada, btnSalida, btnRefrescar, btnVerDetalle;

    public MovementManagerGUI() {
        setTitle("Gestión de Movimientos - Forestech");
        setSize(1200, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        inicializarComponentes();
        cargarMovimientosDesdeDB();

        setVisible(true);
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(10, 10));

        add(crearPanelTitulo(), BorderLayout.NORTH);
        add(crearPanelFiltros(), BorderLayout.WEST);
        add(crearPanelTabla(), BorderLayout.CENTER);
        add(crearPanelBotones(), BorderLayout.SOUTH);
    }

    // Continuaremos...
}
```

#### Paso 2: Panel de título

```java
private JPanel crearPanelTitulo() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBackground(new Color(108, 117, 125));
    panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

    JLabel lblTitulo = new JLabel("📊 HISTORIAL DE MOVIMIENTOS");
    lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
    lblTitulo.setForeground(Color.WHITE);

    JLabel lblSubtitulo = new JLabel("Entradas y Salidas de Combustible");
    lblSubtitulo.setFont(new Font("Arial", Font.PLAIN, 13));
    lblSubtitulo.setForeground(new Color(220, 220, 220));

    JPanel panelTextos = new JPanel(new GridLayout(2, 1, 0, 5));
    panelTextos.setBackground(new Color(108, 117, 125));
    panelTextos.add(lblTitulo);
    panelTextos.add(lblSubtitulo);

    panel.add(panelTextos, BorderLayout.WEST);

    return panel;
}
```

#### Paso 3: Panel de filtros (izquierda)

```java
private JPanel crearPanelFiltros() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createEmptyBorder(10, 10, 10, 10),
        BorderFactory.createTitledBorder("Filtros")
    ));
    panel.setPreferredSize(new Dimension(200, 0));

    // Filtro por tipo
    JLabel lblTipo = new JLabel("Tipo de Movimiento:");
    lblTipo.setAlignmentX(Component.LEFT_ALIGNMENT);

    cmbFiltroTipo = new JComboBox<>(new String[]{"Todos", "ENTRADA", "SALIDA"});
    cmbFiltroTipo.setMaximumSize(new Dimension(180, 30));
    cmbFiltroTipo.setAlignmentX(Component.LEFT_ALIGNMENT);
    cmbFiltroTipo.addActionListener(e -> aplicarFiltros());

    panel.add(lblTipo);
    panel.add(Box.createRigidArea(new Dimension(0, 5)));
    panel.add(cmbFiltroTipo);
    panel.add(Box.createRigidArea(new Dimension(0, 20)));

    // Información de stock
    JLabel lblInfo = new JLabel("<html><b>Leyenda:</b><br>" +
        "🟢 ENTRADA: Compra<br>" +
        "🔴 SALIDA: Despacho</html>");
    lblInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
    lblInfo.setFont(new Font("Arial", Font.PLAIN, 11));
    panel.add(lblInfo);

    return panel;
}

private void aplicarFiltros() {
    String tipoSeleccionado = (String) cmbFiltroTipo.getSelectedItem();

    // Ocultar/mostrar filas según filtro
    for (int i = 0; i < modelo.getRowCount(); i++) {
        String tipo = (String) modelo.getValueAt(i, 2);  // Columna "Tipo"

        // Si es "Todos" o coincide con el tipo, no hacer nada (ya visible)
        // Si no coincide, esta implementación básica recarga la tabla
    }

    // Alternativa simple: recargar toda la tabla filtrada
    cargarMovimientosDesdeDB();
}
```

#### Paso 4: Panel de tabla

```java
private JPanel crearPanelTabla() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    String[] columnas = {"ID", "Fecha", "Tipo", "Producto", "Cantidad",
                         "Vehículo", "Factura", "Costo Total"};
    modelo = new DefaultTableModel(columnas, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    tabla = new JTable(modelo);
    tabla.setRowHeight(30);
    tabla.setFont(new Font("Arial", Font.PLAIN, 13));
    tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
    tabla.getTableHeader().setBackground(new Color(230, 230, 230));
    tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    // Ajustar anchos
    tabla.getColumnModel().getColumn(0).setPreferredWidth(120);  // ID
    tabla.getColumnModel().getColumn(1).setPreferredWidth(100);  // Fecha
    tabla.getColumnModel().getColumn(2).setPreferredWidth(80);   // Tipo
    tabla.getColumnModel().getColumn(3).setPreferredWidth(150);  // Producto
    tabla.getColumnModel().getColumn(4).setPreferredWidth(100);  // Cantidad
    tabla.getColumnModel().getColumn(5).setPreferredWidth(150);  // Vehículo
    tabla.getColumnModel().getColumn(6).setPreferredWidth(100);  // Factura
    tabla.getColumnModel().getColumn(7).setPreferredWidth(120);  // Costo

    JScrollPane scrollPane = new JScrollPane(tabla);
    panel.add(scrollPane, BorderLayout.CENTER);

    return panel;
}
```

#### Paso 5: Cargar movimientos con JOINs

```java
private void cargarMovimientosDesdeDB() {
    try {
        modelo.setRowCount(0);

        // Obtener filtro seleccionado
        String filtroTipo = (String) cmbFiltroTipo.getSelectedItem();

        List<Movement> movimientos;

        if (filtroTipo != null && !filtroTipo.equals("Todos")) {
            // Filtrar por tipo
            movimientos = MovementServices.getMovementsByType(filtroTipo);
        } else {
            // Todos
            movimientos = MovementServices.getAllMovements();
        }

        for (Movement m : movimientos) {
            // Obtener nombres en lugar de IDs (simulado, necesitarías JOINs reales)
            String productoNombre = m.getProductId();  // Idealmente: ProductServices.getById().getName()
            String vehiculoNombre = m.getVehicleId() != null ? m.getVehicleId() : "---";
            String factura = m.getNumeroFactura() != null ? m.getNumeroFactura() : "---";

            // Emoji según tipo
            String tipoConEmoji = m.getMovementType().equals("ENTRADA") ? "🟢 ENTRADA" : "🔴 SALIDA";

            modelo.addRow(new Object[]{
                m.getId(),
                m.getMovementDate(),
                tipoConEmoji,
                productoNombre,
                String.format("%.2f %s", m.getQuantity(), m.getUnitOfMeasurement()),
                vehiculoNombre,
                factura,
                String.format("$%,.2f", m.getCostPerUnit() * m.getQuantity())
            });
        }

        System.out.println("✅ Se cargaron " + movimientos.size() + " movimientos");

    } catch (DatabaseException e) {
        mostrarError("Error al Cargar Movimientos", e.getMessage());
    }
}
```

#### Paso 6: Panel de botones

```java
private JPanel crearPanelBotones() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
    panel.setBackground(new Color(248, 249, 250));

    btnEntrada = new JButton("⬇️ Nueva Entrada");
    btnEntrada.setBackground(new Color(40, 167, 69));
    btnEntrada.setForeground(Color.WHITE);
    btnEntrada.setPreferredSize(new Dimension(180, 40));
    btnEntrada.setFont(new Font("Arial", Font.BOLD, 13));
    btnEntrada.addActionListener(e -> abrirDialogoEntrada());

    btnSalida = new JButton("⬆️ Nueva Salida");
    btnSalida.setBackground(new Color(0, 123, 255));
    btnSalida.setForeground(Color.WHITE);
    btnSalida.setPreferredSize(new Dimension(180, 40));
    btnSalida.setFont(new Font("Arial", Font.BOLD, 13));
    btnSalida.addActionListener(e -> abrirDialogoSalida());

    btnVerDetalle = new JButton("👁️ Ver Detalle");
    btnVerDetalle.setPreferredSize(new Dimension(150, 40));
    btnVerDetalle.setFont(new Font("Arial", Font.BOLD, 13));
    btnVerDetalle.addActionListener(e -> verDetalleMovimiento());

    btnRefrescar = new JButton("🔄 Refrescar");
    btnRefrescar.setBackground(new Color(108, 117, 125));
    btnRefrescar.setForeground(Color.WHITE);
    btnRefrescar.setPreferredSize(new Dimension(150, 40));
    btnRefrescar.setFont(new Font("Arial", Font.BOLD, 13));
    btnRefrescar.addActionListener(e -> cargarMovimientosDesdeDB());

    panel.add(btnEntrada);
    panel.add(btnSalida);
    panel.add(btnVerDetalle);
    panel.add(btnRefrescar);

    return panel;
}
```

#### Paso 7: Abrir diálogos ENTRADA y SALIDA

```java
private void abrirDialogoEntrada() {
    MovementDialogForm dialogo = new MovementDialogForm(this, true, "ENTRADA");
    if (dialogo.isGuardadoExitoso()) {
        cargarMovimientosDesdeDB();
    }
}

private void abrirDialogoSalida() {
    MovementDialogForm dialogo = new MovementDialogForm(this, true, "SALIDA");
    if (dialogo.isGuardadoExitoso()) {
        cargarMovimientosDesdeDB();
    }
}

private void verDetalleMovimiento() {
    int filaSeleccionada = tabla.getSelectedRow();

    if (filaSeleccionada == -1) {
        JOptionPane.showMessageDialog(this,
            "Selecciona un movimiento de la tabla",
            "Sin Selección",
            JOptionPane.WARNING_MESSAGE);
        return;
    }

    String movementId = (String) modelo.getValueAt(filaSeleccionada, 0);

    try {
        Movement movement = MovementServices.getMovementById(movementId);

        if (movement == null) {
            mostrarError("Error", "No se encontró el movimiento");
            return;
        }

        // Mostrar detalle completo
        String detalle = "ID: " + movement.getId() + "\n" +
                         "Fecha: " + movement.getMovementDate() + "\n" +
                         "Tipo: " + movement.getMovementType() + "\n" +
                         "Producto: " + movement.getProductId() + "\n" +
                         "Cantidad: " + movement.getQuantity() + " " + movement.getUnitOfMeasurement() + "\n" +
                         "Vehículo: " + (movement.getVehicleId() != null ? movement.getVehicleId() : "N/A") + "\n" +
                         "Factura: " + (movement.getNumeroFactura() != null ? movement.getNumeroFactura() : "N/A") + "\n" +
                         "Costo Unitario: $" + movement.getCostPerUnit() + "\n" +
                         "Costo Total: $" + (movement.getCostPerUnit() * movement.getQuantity());

        JOptionPane.showMessageDialog(this,
            detalle,
            "Detalle del Movimiento",
            JOptionPane.INFORMATION_MESSAGE);

    } catch (DatabaseException e) {
        mostrarError("Error", e.getMessage());
    }
}
```

#### Paso 8: Método auxiliar

```java
private void mostrarError(String titulo, String mensaje) {
    JOptionPane.showMessageDialog(this,
        mensaje,
        titulo,
        JOptionPane.ERROR_MESSAGE);
}

public static void main(String[] args) {
    try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
        e.printStackTrace();
    }

    SwingUtilities.invokeLater(() -> new MovementManagerGUI());
}
```

---

### 📝 Parte 2: MovementDialogForm.java (Formulario Dinámico)

Este diálogo cambia dinámicamente según si es ENTRADA o SALIDA.

```java
package com.forestech.ui;

import com.forestech.exceptions.DatabaseException;
import com.forestech.exceptions.InsufficientStockException;
import com.forestech.models.Movement;
import com.forestech.models.Product;
import com.forestech.models.Vehicle;
import com.forestech.services.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MovementDialogForm extends JDialog {

    private String tipoMovimiento;  // "ENTRADA" o "SALIDA"

    // Campos comunes
    private JComboBox<ProductoItem> cmbProducto;
    private JTextField txtCantidad;
    private JTextField txtCostoUnitario;
    private JComboBox<String> cmbUnidad;

    // Campos específicos ENTRADA
    private JComboBox<FacturaItem> cmbFactura;

    // Campos específicos SALIDA
    private JComboBox<VehiculoItem> cmbVehiculo;

    private boolean guardadoExitoso = false;

    public MovementDialogForm(JFrame parent, boolean modal, String tipoMovimiento) {
        super(parent,
              tipoMovimiento.equals("ENTRADA") ? "Nueva Entrada de Combustible" : "Nueva Salida de Combustible",
              modal);

        this.tipoMovimiento = tipoMovimiento;

        setSize(550, tipoMovimiento.equals("ENTRADA") ? 500 : 550);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        inicializarComponentes();

        setVisible(true);
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(10, 10));

        add(crearPanelInfo(), BorderLayout.NORTH);
        add(crearPanelFormulario(), BorderLayout.CENTER);
        add(crearPanelBotones(), BorderLayout.SOUTH);
    }

    private JPanel crearPanelInfo() {
        JPanel panel = new JPanel(new BorderLayout());
        Color bgColor = tipoMovimiento.equals("ENTRADA") ?
                        new Color(40, 167, 69) : new Color(0, 123, 255);
        panel.setBackground(bgColor);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        String emoji = tipoMovimiento.equals("ENTRADA") ? "⬇️" : "⬆️";
        String descripcion = tipoMovimiento.equals("ENTRADA") ?
                             "Registra la compra de combustible con factura de respaldo" :
                             "Registra el despacho de combustible a un vehículo";

        JLabel lblTitulo = new JLabel(emoji + " " + tipoMovimiento);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setForeground(Color.WHITE);

        JLabel lblDesc = new JLabel(descripcion);
        lblDesc.setFont(new Font("Arial", Font.PLAIN, 12));
        lblDesc.setForeground(new Color(230, 230, 230));

        JPanel panelTextos = new JPanel(new GridLayout(2, 1, 0, 5));
        panelTextos.setBackground(bgColor);
        panelTextos.add(lblTitulo);
        panelTextos.add(lblDesc);

        panel.add(panelTextos, BorderLayout.WEST);

        return panel;
    }

    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int fila = 0;

        // Fila 0: Producto (OBLIGATORIO para ambos)
        gbc.gridx = 0; gbc.gridy = fila; gbc.weightx = 0.3;
        panel.add(new JLabel("Producto: *"), gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        cmbProducto = new JComboBox<>();
        cargarProductos();
        panel.add(cmbProducto, gbc);
        fila++;

        // Fila 1: Cantidad
        gbc.gridx = 0; gbc.gridy = fila; gbc.weightx = 0.3;
        panel.add(new JLabel("Cantidad: *"), gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        txtCantidad = new JTextField(20);
        panel.add(txtCantidad, gbc);
        fila++;

        // Fila 2: Unidad de medida
        gbc.gridx = 0; gbc.gridy = fila; gbc.weightx = 0.3;
        panel.add(new JLabel("Unidad: *"), gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        cmbUnidad = new JComboBox<>(new String[]{"GALÓN", "LITRO", "BARRIL"});
        panel.add(cmbUnidad, gbc);
        fila++;

        // Fila 3: Costo unitario
        gbc.gridx = 0; gbc.gridy = fila; gbc.weightx = 0.3;
        panel.add(new JLabel("Costo Unitario: *"), gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        txtCostoUnitario = new JTextField(20);
        panel.add(txtCostoUnitario, gbc);
        fila++;

        // CAMPOS ESPECÍFICOS según tipo
        if (tipoMovimiento.equals("ENTRADA")) {
            // Fila 4: Factura (OBLIGATORIO para ENTRADA)
            gbc.gridx = 0; gbc.gridy = fila; gbc.weightx = 0.3;
            panel.add(new JLabel("Factura: *"), gbc);

            gbc.gridx = 1; gbc.weightx = 0.7;
            cmbFactura = new JComboBox<>();
            cargarFacturas();
            panel.add(cmbFactura, gbc);
            fila++;

        } else if (tipoMovimiento.equals("SALIDA")) {
            // Fila 4: Vehículo (OBLIGATORIO para SALIDA)
            gbc.gridx = 0; gbc.gridy = fila; gbc.weightx = 0.3;
            panel.add(new JLabel("Vehículo: *"), gbc);

            gbc.gridx = 1; gbc.weightx = 0.7;
            cmbVehiculo = new JComboBox<>();
            cargarVehiculos();
            panel.add(cmbVehiculo, gbc);
            fila++;
        }

        // Nota informativa
        gbc.gridx = 0; gbc.gridy = fila; gbc.gridwidth = 2;
        JLabel lblNota = new JLabel("* Campos obligatorios");
        lblNota.setFont(new Font("Arial", Font.ITALIC, 11));
        lblNota.setForeground(Color.GRAY);
        panel.add(lblNota, gbc);

        return panel;
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.setPreferredSize(new Dimension(120, 35));
        btnGuardar.setBackground(tipoMovimiento.equals("ENTRADA") ?
                                  new Color(40, 167, 69) : new Color(0, 123, 255));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.addActionListener(e -> guardarMovimiento());

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setPreferredSize(new Dimension(120, 35));
        btnCancelar.addActionListener(e -> dispose());

        panel.add(btnCancelar);
        panel.add(btnGuardar);

        return panel;
    }

    // Cargar datos para JComboBox
    private void cargarProductos() {
        try {
            List<Product> productos = ProductServices.getAllProducts();
            for (Product p : productos) {
                cmbProducto.addItem(new ProductoItem(p.getId(), p.getName()));
            }
        } catch (DatabaseException e) {
            mostrarError("Error al cargar productos", e.getMessage());
        }
    }

    private void cargarFacturas() {
        try {
            List<com.forestech.models.Factura> facturas = FacturaServices.getAllFacturas();
            for (com.forestech.models.Factura f : facturas) {
                cmbFactura.addItem(new FacturaItem(
                    f.getNumeroFactura(),
                    f.getNumeroFactura() + " - " + f.getFechaEmision()
                ));
            }
        } catch (DatabaseException e) {
            mostrarError("Error al cargar facturas", e.getMessage());
        }
    }

    private void cargarVehiculos() {
        try {
            List<Vehicle> vehiculos = VehicleServices.getAllVehicles();
            for (Vehicle v : vehiculos) {
                cmbVehiculo.addItem(new VehiculoItem(v.getId(), v.getName()));
            }
        } catch (DatabaseException e) {
            mostrarError("Error al cargar vehículos", e.getMessage());
        }
    }

    private void guardarMovimiento() {
        // Validación 1: Campos obligatorios
        ProductoItem productoSeleccionado = (ProductoItem) cmbProducto.getSelectedItem();
        String cantidadStr = txtCantidad.getText().trim();
        String costoStr = txtCostoUnitario.getText().trim();

        if (productoSeleccionado == null || cantidadStr.isEmpty() || costoStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Todos los campos marcados con * son obligatorios",
                "Error de Validación",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validación 2: Cantidad y costo numéricos
        double cantidad, costo;
        try {
            cantidad = Double.parseDouble(cantidadStr);
            costo = Double.parseDouble(costoStr);
            if (cantidad <= 0 || costo <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "La cantidad y el costo deben ser números positivos",
                "Error de Validación",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Obtener datos según tipo
        String productId = productoSeleccionado.id;
        String unidad = (String) cmbUnidad.getSelectedItem();
        String vehicleId = null;
        String numeroFactura = null;

        if (tipoMovimiento.equals("ENTRADA")) {
            FacturaItem facturaSeleccionada = (FacturaItem) cmbFactura.getSelectedItem();
            if (facturaSeleccionada == null) {
                JOptionPane.showMessageDialog(this,
                    "Debes seleccionar una factura para la entrada",
                    "Error de Validación",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            numeroFactura = facturaSeleccionada.numero;

        } else if (tipoMovimiento.equals("SALIDA")) {
            VehiculoItem vehiculoSeleccionado = (VehiculoItem) cmbVehiculo.getSelectedItem();
            if (vehiculoSeleccionado == null) {
                JOptionPane.showMessageDialog(this,
                    "Debes seleccionar un vehículo para la salida",
                    "Error de Validación",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            vehicleId = vehiculoSeleccionado.id;
        }

        // Crear Movement
        try {
            Movement nuevoMovimiento = new Movement(
                tipoMovimiento,
                productId,
                vehicleId,
                numeroFactura,
                unidad,
                cantidad,
                costo
            );

            // INSERTAR (MovementServices valida todas las FKs automáticamente)
            MovementServices.insertMovement(nuevoMovimiento);

            guardadoExitoso = true;

            JOptionPane.showMessageDialog(this,
                "Movimiento registrado exitosamente:\n" + nuevoMovimiento.getId(),
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE);

            dispose();

        } catch (InsufficientStockException e) {
            // Error específico de stock insuficiente
            JOptionPane.showMessageDialog(this,
                "⚠️ STOCK INSUFICIENTE\n\n" + e.getMessage() + "\n\n" +
                "No puedes despachar más combustible del que hay en inventario.",
                "Error de Stock",
                JOptionPane.ERROR_MESSAGE);

        } catch (DatabaseException e) {
            // Errores de FK o conexión
            String mensajeUsuario = e.getMessage();

            // Mensajes amigables según el error
            if (mensajeUsuario.contains("product_id")) {
                mensajeUsuario = "El producto seleccionado no existe en la base de datos.";
            } else if (mensajeUsuario.contains("vehicle_id")) {
                mensajeUsuario = "El vehículo seleccionado no existe en la base de datos.";
            } else if (mensajeUsuario.contains("numero_factura")) {
                mensajeUsuario = "La factura seleccionada no existe en la base de datos.";
            }

            JOptionPane.showMessageDialog(this,
                "Error al guardar el movimiento:\n\n" + mensajeUsuario,
                "Error de Base de Datos",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarError(String titulo, String mensaje) {
        JOptionPane.showMessageDialog(this,
            mensaje,
            titulo,
            JOptionPane.ERROR_MESSAGE);
    }

    public boolean isGuardadoExitoso() {
        return guardadoExitoso;
    }

    // Clases auxiliares para JComboBox
    private static class ProductoItem {
        String id;
        String nombre;

        ProductoItem(String id, String nombre) {
            this.id = id;
            this.nombre = nombre;
        }

        @Override
        public String toString() {
            return nombre;
        }
    }

    private static class VehiculoItem {
        String id;
        String nombre;

        VehiculoItem(String id, String nombre) {
            this.id = id;
            this.nombre = nombre;
        }

        @Override
        public String toString() {
            return nombre;
        }
    }

    private static class FacturaItem {
        String numero;
        String descripcion;

        FacturaItem(String numero, String descripcion) {
            this.numero = numero;
            this.descripcion = descripcion;
        }

        @Override
        public String toString() {
            return descripcion;
        }
    }
}
```

### 🔍 Análisis de Conceptos Clave:

#### 1. **Formulario Dinámico según Tipo**

```java
// Constructor recibe el tipo
public MovementDialogForm(JFrame parent, boolean modal, String tipoMovimiento) {
    if (tipoMovimiento.equals("ENTRADA")) {
        // Mostrar campo factura
        cmbFactura = new JComboBox<>();
    } else {
        // Mostrar campo vehículo
        cmbVehiculo = new JComboBox<>();
    }
}
```

**Visual:**

```
ENTRADA:
┌────────────────────────┐
│ Producto: [Diesel ▼]  │
│ Cantidad: [____]       │
│ Factura:  [10734 ▼]   │ ← Solo en ENTRADA
└────────────────────────┘

SALIDA:
┌────────────────────────┐
│ Producto: [Diesel ▼]  │
│ Cantidad: [____]       │
│ Vehículo: [CAT 320 ▼] │ ← Solo en SALIDA
└────────────────────────┘
```

#### 2. **Manejo de InsufficientStockException**

```java
try {
    MovementServices.insertMovement(nuevoMovimiento);
} catch (InsufficientStockException e) {
    // Error específico - el usuario intenta sacar más de lo que hay
    JOptionPane.showMessageDialog(this,
        "⚠️ STOCK INSUFICIENTE\n\n" + e.getMessage(),
        "Error de Stock",
        JOptionPane.ERROR_MESSAGE);
} catch (DatabaseException e) {
    // Otros errores (FKs inválidas, etc.)
    JOptionPane.showMessageDialog(this,
        "Error de base de datos:\n" + e.getMessage(),
        "Error",
        JOptionPane.ERROR_MESSAGE);
}
```

#### 3. **Validaciones Automáticas en MovementServices**

Según MovementServices.java (líneas 87-154), al llamar `insertMovement()`:

```java
// Validación 1: product_id OBLIGATORIO
if (productId == null || productId.isEmpty()) {
    throw new DatabaseException("product_id es OBLIGATORIO");
}
if (!ProductServices.existsProduct(productId)) {
    throw new DatabaseException("El producto NO existe");
}

// Validación 2: vehicle_id (si NO es NULL)
if (vehicleId != null && !vehicleId.isEmpty()) {
    if (!VehicleServices.existsVehicle(vehicleId)) {
        throw new DatabaseException("El vehículo NO existe");
    }
}

// Validación 3: numero_factura (si NO es NULL)
if (numeroFactura != null && !numeroFactura.isEmpty()) {
    if (!FacturaServices.existsFactura(numeroFactura)) {
        throw new DatabaseException("La factura NO existe");
    }
}

// Validación 4: Stock para SALIDA
if (tipo.equals("SALIDA")) {
    double stockActual = getProductStock(productId);
    if (stockActual < cantidad) {
        throw new InsufficientStockException(...);
    }
}
```

**Flujo Completo:**

```
Usuario llena formulario SALIDA:
   Producto: Diesel (PROD-001)
   Cantidad: 150 litros
   Vehículo: Excavadora (VEH-001)
         ↓
Click "Guardar"
         ↓
MovementDialogForm valida campos vacíos
         ↓
Crea Movement object
         ↓
Llama MovementServices.insertMovement()
         ↓
MovementServices valida:
   1. PROD-001 existe? → Sí ✅
   2. VEH-001 existe? → Sí ✅
   3. Stock actual >= 150? → NO ❌ (solo hay 100)
         ↓
Lanza InsufficientStockException
         ↓
DialogForm captura excepción
         ↓
Muestra mensaje al usuario:
"⚠️ STOCK INSUFICIENTE
Stock actual: 100.0 litros
Solicitado: 150.0 litros"
```

### ✅ Criterio de Éxito:
- [x] Tabla muestra movimientos con filtro por tipo
- [x] Botón "Nueva Entrada" abre formulario con factura
- [x] Botón "Nueva Salida" abre formulario con vehículo
- [x] JComboBox carga datos REALES desde ProductServices, VehicleServices, FacturaServices
- [x] Validación de 3 FKs funciona correctamente
- [x] InsufficientStockException se captura y muestra mensaje amigable
- [x] Tabla se refresca automáticamente después de guardar
- [x] Ver detalle muestra información completa del movimiento

### 🏃 Cómo ejecutar:

```bash
cd /home/hp/forestechOil/forestech-cli-java
mvn clean compile
mvn exec:java -Dexec.mainClass="com.forestech.ui.MovementManagerGUI"
```

**Pruebas Críticas:**

1. **Entrada exitosa:** Seleccionar producto, factura válida, cantidad → Debe guardar
2. **Salida exitosa con stock:** Producto con stock suficiente, vehículo válido → Debe guardar
3. **Salida con stock insuficiente:** Cantidad > stock actual → Debe mostrar error específico
4. **FK inválida - Producto:** (caso extremo) → Debe mostrar error
5. **FK inválida - Vehículo:** (caso extremo) → Debe mostrar error
6. **FK inválida - Factura:** (caso extremo) → Debe mostrar error
7. **Filtro por tipo:** Cambiar entre "Todos", "ENTRADA", "SALIDA" → Debe filtrar tabla

---

## 📋 Checkpoint 9.12: Look and Feel - Personalización Visual (45 min)

### 🎯 Objetivo:
Aprender a cambiar la apariencia visual de toda la aplicación Swing usando Look and Feel (LaF), incluyendo temas del sistema operativo y temas personalizados.

### 📚 Concepto: Look and Feel

**Look and Feel** controla la apariencia visual de TODOS los componentes Swing de tu aplicación:
- Botones, tablas, menús, diálogos, etc.
- Colores, fuentes, bordes, íconos

Java Swing incluye varios LaF por defecto:
- **Metal** (por defecto de Java, multiplataforma)
- **System** (imita el SO: Windows, macOS, Linux)
- **Nimbus** (moderno, multiplataforma)
- **Motif** (antiguo Unix)

### 💡 Conceptos Clave:

#### 1. **Configurar Look and Feel**

```java
// ANTES de crear cualquier ventana, en el main:
public static void main(String[] args) {
    try {
        // Opción 1: Look and Feel del sistema operativo
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        // Opción 2: Nimbus (moderno)
        UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");

        // Opción 3: Metal (por defecto)
        UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");

    } catch (Exception e) {
        e.printStackTrace();
    }

    SwingUtilities.invokeLater(() -> new MiVentana());
}
```

**Visual - Diferencias entre LaFs:**

```
METAL (Java Default):
┌──────────────────┐
│ [Button]   Gray  │
└──────────────────┘

WINDOWS (Sistema):
┌──────────────────┐
│ [Button]   Blue  │
└──────────────────┘

NIMBUS (Moderno):
┌──────────────────┐
│ [Button]  Smooth │
└──────────────────┘
```

#### 2. **Listar Look and Feels Disponibles**

```java
UIManager.LookAndFeelInfo[] lafs = UIManager.getInstalledLookAndFeels();
for (UIManager.LookAndFeelInfo laf : lafs) {
    System.out.println(laf.getName() + ": " + laf.getClassName());
}
```

**Salida típica:**
```
Metal: javax.swing.plaf.metal.MetalLookAndFeel
Nimbus: javax.swing.plaf.nimbus.NimbusLookAndFeel
CDE/Motif: com.sun.java.swing.plaf.motif.MotifLookAndFeel
Windows: com.sun.java.swing.plaf.windows.WindowsLookAndFeel  (solo en Windows)
Mac OS X: com.apple.laf.AquaLookAndFeel  (solo en macOS)
```

### 📝 Ejercicio Guiado:

Crear `LookAndFeelDemo.java` - Ventana que permite cambiar el LaF en tiempo real.

```java
package com.forestech.ui;

import javax.swing.*;
import java.awt.*;

public class LookAndFeelDemo extends JFrame {

    private JComboBox<String> cmbLookAndFeel;
    private JPanel panelDemo;

    public LookAndFeelDemo() {
        setTitle("Demo: Look and Feel - Forestech");
        setSize(700, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        inicializarComponentes();

        setVisible(true);
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(10, 10));

        // Panel superior con selector
        JPanel panelSelector = crearPanelSelector();
        add(panelSelector, BorderLayout.NORTH);

        // Panel central con componentes de demostración
        panelDemo = crearPanelDemostracion();
        add(panelDemo, BorderLayout.CENTER);

        // Panel inferior con información
        JPanel panelInfo = crearPanelInfo();
        add(panelInfo, BorderLayout.SOUTH);
    }

    private JPanel crearPanelSelector() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        panel.setBackground(new Color(240, 240, 240));

        JLabel lblTitulo = new JLabel("Selecciona Look and Feel:");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 14));

        // Cargar LaFs disponibles
        cmbLookAndFeel = new JComboBox<>();
        UIManager.LookAndFeelInfo[] lafs = UIManager.getInstalledLookAndFeels();

        for (UIManager.LookAndFeelInfo laf : lafs) {
            cmbLookAndFeel.addItem(laf.getName());
        }

        // Detectar LaF actual
        String lafActual = UIManager.getLookAndFeel().getName();
        cmbLookAndFeel.setSelectedItem(lafActual);

        JButton btnAplicar = new JButton("Aplicar");
        btnAplicar.addActionListener(e -> aplicarLookAndFeel());

        panel.add(lblTitulo);
        panel.add(cmbLookAndFeel);
        panel.add(btnAplicar);

        return panel;
    }

    private JPanel crearPanelDemostracion() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Título
        JLabel lblTitulo = new JLabel("Componentes de Demostración");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBotones.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelBotones.add(new JButton("Botón Normal"));
        panelBotones.add(new JButton("Botón Deshabilitado") {{
            setEnabled(false);
        }});

        // TextField
        JPanel panelTexto = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelTexto.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelTexto.add(new JLabel("Texto:"));
        panelTexto.add(new JTextField("Campo de texto", 20));

        // CheckBox y RadioButton
        JPanel panelChecks = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelChecks.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelChecks.add(new JCheckBox("Checkbox", true));
        ButtonGroup group = new ButtonGroup();
        JRadioButton rb1 = new JRadioButton("Opción 1", true);
        JRadioButton rb2 = new JRadioButton("Opción 2");
        group.add(rb1);
        group.add(rb2);
        panelChecks.add(rb1);
        panelChecks.add(rb2);

        // ComboBox
        JPanel panelCombo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelCombo.add(new JLabel("Combo:"));
        panelCombo.add(new JComboBox<>(new String[]{"Opción A", "Opción B", "Opción C"}));

        // Tabla
        String[] columnas = {"Col 1", "Col 2", "Col 3"};
        Object[][] datos = {
            {"Dato 1", "Dato 2", "Dato 3"},
            {"Dato 4", "Dato 5", "Dato 6"}
        };
        JTable tabla = new JTable(datos, columnas);
        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPane.setMaximumSize(new Dimension(600, 100));

        // Agregar todo
        panel.add(lblTitulo);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(panelBotones);
        panel.add(panelTexto);
        panel.add(panelChecks);
        panel.add(panelCombo);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(scrollPane);

        return panel;
    }

    private JPanel crearPanelInfo() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        panel.setBackground(new Color(255, 248, 220));

        JLabel lblInfo = new JLabel("<html>" +
            "<b>Información:</b><br>" +
            "El Look and Feel afecta la apariencia de TODOS los componentes.<br>" +
            "• <b>System:</b> Imita el sistema operativo (recomendado)<br>" +
            "• <b>Nimbus:</b> Moderno y multiplataforma<br>" +
            "• <b>Metal:</b> Por defecto de Java (antiguo)" +
            "</html>");
        lblInfo.setFont(new Font("Arial", Font.PLAIN, 12));

        panel.add(lblInfo, BorderLayout.CENTER);

        return panel;
    }

    private void aplicarLookAndFeel() {
        String lafSeleccionado = (String) cmbLookAndFeel.getSelectedItem();

        // Buscar el className del LaF seleccionado
        UIManager.LookAndFeelInfo[] lafs = UIManager.getInstalledLookAndFeels();
        String className = null;

        for (UIManager.LookAndFeelInfo laf : lafs) {
            if (laf.getName().equals(lafSeleccionado)) {
                className = laf.getClassName();
                break;
            }
        }

        if (className == null) {
            JOptionPane.showMessageDialog(this,
                "No se pudo encontrar el Look and Feel seleccionado",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Cambiar Look and Feel
            UIManager.setLookAndFeel(className);

            // Actualizar TODOS los componentes de TODAS las ventanas abiertas
            SwingUtilities.updateComponentTreeUI(this);

            // Revalidar y repintar
            this.pack();
            this.setSize(700, 500);

            JOptionPane.showMessageDialog(this,
                "Look and Feel aplicado exitosamente:\n" + lafSeleccionado,
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al aplicar Look and Feel:\n" + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Iniciar con LaF del sistema
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new LookAndFeelDemo());
    }
}
```

### 🎨 Personalización Avanzada: UIManager Properties

Puedes personalizar colores y fuentes sin cambiar todo el LaF:

```java
// Cambiar color de fondo de tablas
UIManager.put("Table.background", new Color(255, 255, 240));

// Cambiar color de selección
UIManager.put("Table.selectionBackground", new Color(184, 207, 229));

// Cambiar fuente de botones
UIManager.put("Button.font", new Font("Arial", Font.BOLD, 14));

// Cambiar color de botones
UIManager.put("Button.background", new Color(25, 135, 84));

// IMPORTANTE: Aplicar ANTES de crear componentes
SwingUtilities.invokeLater(() -> new MiVentana());
```

### 🔍 Análisis de Conceptos Clave:

#### 1. **SwingUtilities.updateComponentTreeUI()**

```java
// Después de cambiar LaF, actualizar todos los componentes
UIManager.setLookAndFeel(nuevoLaF);
SwingUtilities.updateComponentTreeUI(ventana);  // Actualiza esta ventana
```

**¿Qué hace?** Recorre TODOS los componentes de la ventana y les aplica el nuevo LaF.

#### 2. **Configuración Recomendada para Forestech**

```java
public static void main(String[] args) {
    try {
        // Usar LaF del sistema operativo (se ve nativo)
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        // Personalizaciones opcionales
        UIManager.put("Table.alternateRowColor", new Color(245, 245, 245));

    } catch (Exception e) {
        e.printStackTrace();
    }

    SwingUtilities.invokeLater(() -> new ForestechMainGUI());
}
```

#### 3. **Diferencias Visuales por Sistema Operativo**

**Windows:**
- `getSystemLookAndFeelClassName()` → WindowsLookAndFeel
- Botones azules, menús estilo Windows 10/11

**macOS:**
- `getSystemLookAndFeelClassName()` → AquaLookAndFeel
- Botones redondeados, menús estilo macOS

**Linux:**
- `getSystemLookAndFeelClassName()` → GTKLookAndFeel (si GTK disponible) o Metal
- Depende del entorno de escritorio (GNOME, KDE, etc.)

### ✅ Criterio de Éxito:
- [x] Aplicación inicia con LaF del sistema operativo
- [x] Selector permite cambiar entre LaFs disponibles
- [x] Botón "Aplicar" actualiza la interfaz en tiempo real
- [x] Todos los componentes se actualizan correctamente
- [x] LaF aplicado persiste al abrir nuevas ventanas

### 🏃 Cómo ejecutar:

```bash
cd /home/hp/forestechOil/forestech-cli-java
mvn clean compile
mvn exec:java -Dexec.mainClass="com.forestech.ui.LookAndFeelDemo"
```

**Pruebas:**
1. Inicia la aplicación → Debe usar LaF del sistema
2. Cambia a "Nimbus" y click "Aplicar" → Debe verse moderno
3. Cambia a "Metal" → Debe verse como Java clásico
4. Cambia a "CDE/Motif" → Debe verse antiguo Unix

### 🎨 Aplicar a Forestech

Para aplicar LaF del sistema a todas las ventanas de Forestech:

```java
// En cada main() de tus GUIs (ProductManagerGUI, VehicleManagerGUI, etc.):
public static void main(String[] args) {
    try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
        e.printStackTrace();
    }

    SwingUtilities.invokeLater(() -> new ProductManagerGUI());
}
```

---

## Checkpoint 9.13: Arquitectura Profesional con JSplitPane + CardLayout + Dashboard ⭐⭐⭐ (2-3 horas)

### 🎯 Objetivo

Crear **ForestechProfessionalApp**: la aplicación definitiva que integra TODOS los módulos (Productos, Vehículos, Movimientos, Facturas) con:
- **JSplitPane**: Panel de navegación lateral izquierdo
- **CardLayout**: Cambio entre vistas sin crear ventanas nuevas
- **Dashboard**: Panel de inicio con estadísticas en tiempo real
- **Panel de Facturas**: CRUD completo con tabla y formulario
- **Arquitectura modular**: Reutiliza todo el código anterior

**Este es el PROYECTO FINAL de la Fase 9** 🎓

---

### 📚 Conceptos Nuevos

#### 1️⃣ **JSplitPane - Panel Divisible**

**¿Qué es?**
Un contenedor que divide el espacio en **DOS partes** (izquierda/derecha o arriba/abajo) con un divisor ajustable.

**Analogía:**
```
Es como una habitación con una pared móvil:
┌─────────────────────────────────┐
│         │                       │
│  Panel  │  Panel de Contenido   │
│   de    │                       │
│  Nav.   │  (más grande)         │
│         │                       │
└─────────────────────────────────┘
    ↑
  Divisor (puedes moverlo)
```

**Código básico:**
```java
JSplitPane splitPane = new JSplitPane(
    JSplitPane.HORIZONTAL_SPLIT,  // Dividir horizontalmente
    panelIzquierdo,                // Componente izquierdo
    panelDerecho                   // Componente derecho
);

// Configuraciones útiles:
splitPane.setDividerLocation(200);      // Posición inicial del divisor
splitPane.setOneTouchExpandable(true);  // Botones de colapso rápido
splitPane.setResizeWeight(0.0);         // 0.0 = izquierdo fijo al redimensionar
```

**Tipos de división:**
- `HORIZONTAL_SPLIT`: Izquierda | Derecha
- `VERTICAL_SPLIT`: Arriba | Abajo

---

#### 2️⃣ **CardLayout - Alternancia de Paneles**

**¿Qué es?**
Un Layout Manager que muestra **UN SOLO panel a la vez**, como una baraja de cartas donde solo ves la carta superior.

**Analogía:**
```
Imagina una pila de transparencias:
┌─────────────────────┐
│  Dashboard (visible)│  ← Solo esta está visible
├─────────────────────┤
│  Productos (oculta) │
├─────────────────────┤
│  Vehículos (oculta) │
├─────────────────────┤
│  Movimientos (...)  │
└─────────────────────┘
```

**Código básico:**
```java
// 1. Crear el contenedor con CardLayout
JPanel contenedor = new JPanel();
CardLayout cardLayout = new CardLayout();
contenedor.setLayout(cardLayout);

// 2. Agregar paneles con nombres (identificadores)
contenedor.add(panelDashboard, "dashboard");
contenedor.add(panelProductos, "productos");
contenedor.add(panelVehiculos, "vehiculos");

// 3. Mostrar un panel específico
cardLayout.show(contenedor, "productos");  // Muestra panel de productos

// 4. Navegación secuencial
cardLayout.next(contenedor);      // Siguiente panel
cardLayout.previous(contenedor);  // Panel anterior
```

**Métodos clave:**
- `show(container, name)` - Muestra panel por nombre
- `next(container)` - Siguiente panel
- `previous(container)` - Panel anterior
- `first(container)` - Primera tarjeta
- `last(container)` - Última tarjeta

---

#### 3️⃣ **Dashboard - Panel de Estadísticas**

**¿Qué es?**
Un panel de inicio que muestra **resumen** de información importante del sistema.

**Componentes típicos:**
- Tarjetas con números grandes (total productos, stock, movimientos)
- Botones de acceso rápido a funciones principales
- Gráficas o indicadores visuales

**Diseño de Dashboard en Forestech:**
```
┌──────────────────────────────────────────────┐
│  📊 DASHBOARD DE FORESTECH                   │
├──────────────────────────────────────────────┤
│                                              │
│  ┌───────────┐  ┌───────────┐  ┌──────────┐│
│  │ 📦 Total  │  │ 🚛 Total  │  │ 📊 Movim.││
│  │ Productos │  │ Vehículos │  │ Hoy      ││
│  │           │  │           │  │          ││
│  │    15     │  │     8     │  │    23    ││
│  └───────────┘  └───────────┘  └──────────┘│
│                                              │
│  ┌───────────┐  ┌───────────┐               │
│  │ 🧾 Total  │  │ 💰 Stock  │               │
│  │ Facturas  │  │ Bajo      │               │
│  │           │  │           │               │
│  │    42     │  │     3     │               │
│  └───────────┘  └───────────┘               │
│                                              │
│  ACCESOS RÁPIDOS:                            │
│  [Nuevo Producto] [Nuevo Vehículo]          │
│  [Registrar Movimiento] [Nueva Factura]     │
└──────────────────────────────────────────────┘
```

---

### 🏗️ Arquitectura de ForestechProfessionalApp

```
┌─────────────────────────────────────────────────────────────┐
│  ForestechProfessionalApp (JFrame principal)                │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  JMenuBar (Archivo, Ver, Movimientos, Facturas, Ayuda)│  │
│  └───────────────────────────────────────────────────────┘  │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  JSplitPane (dividido horizontalmente)                │  │
│  │  ┌──────────┬──────────────────────────────────────┐  │  │
│  │  │ Panel de │  Contenedor con CardLayout            │  │  │
│  │  │ Navegac. │                                       │  │  │
│  │  │          │  ┌───────────────────────────────┐   │  │  │
│  │  │ [Inicio] │  │ "dashboard" → DashboardPanel  │   │  │  │
│  │  │          │  ├───────────────────────────────┤   │  │  │
│  │  │[Product.]│  │ "productos" → ProductosPanel  │   │  │  │
│  │  │          │  ├───────────────────────────────┤   │  │  │
│  │  │[Vehícul.]│  │ "vehiculos" → VehiculosPanel  │   │  │  │
│  │  │          │  ├───────────────────────────────┤   │  │  │
│  │  │[Movimien]│  │ "movimientos" → MovimPanel    │   │  │  │
│  │  │          │  ├───────────────────────────────┤   │  │  │
│  │  │[Facturas]│  │ "facturas" → FacturasPanel    │   │  │  │
│  │  │          │  └───────────────────────────────┘   │  │  │
│  │  │          │  (Solo 1 visible a la vez)        │  │  │
│  │  └──────────┴──────────────────────────────────────┘  │  │
│  └───────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

**Flujo de navegación:**
1. Usuario hace clic en botón "Productos" (panel izquierdo)
2. ActionListener llama a `cardLayout.show(contenedor, "productos")`
3. CardLayout oculta panel actual y muestra panel de productos
4. Sin crear ventanas nuevas, todo en la misma JFrame ✅

---

### 💻 Implementación: ForestechProfessionalApp.java

**Ubicación:** `forestech-cli-java/src/main/java/com/forestech/ui/ForestechProfessionalApp.java`

**Estructura del archivo (1000+ líneas):**
```java
package com.forestech.ui;

import com.forestech.exceptions.DatabaseException;
import com.forestech.models.*;
import com.forestech.services.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Checkpoint 9.13: ForestechProfessionalApp
 * 
 * APLICACIÓN PROFESIONAL que integra TODOS los módulos de Forestech.
 * 
 * CONCEPTOS DEMOSTRADOS:
 * ======================
 * 1. JSplitPane - Panel divisible (navegación | contenido)
 * 2. CardLayout - Alternancia entre vistas sin ventanas nuevas
 * 3. Dashboard - Panel de estadísticas con datos reales de BD
 * 4. Arquitectura modular - Reutiliza Services existentes
 * 5. CRUD completo de Facturas (tabla + formulario)
 * 6. Integración total de 5 módulos en 1 sola aplicación
 * 
 * ARQUITECTURA:
 * =============
 * JSplitPane (horizontal)
 *   ├── Panel Navegación (izquierda, 200px fijo)
 *   │   ├── Botón "Inicio"
 *   │   ├── Botón "Productos"
 *   │   ├── Botón "Vehículos"
 *   │   ├── Botón "Movimientos"
 *   │   └── Botón "Facturas"
 *   └── Contenedor CardLayout (derecha, dinámico)
 *       ├── Card "dashboard" → DashboardPanel
 *       ├── Card "productos" → Panel con tabla de productos
 *       ├── Card "vehiculos" → Panel con tabla de vehículos
 *       ├── Card "movimientos" → Panel con tabla de movimientos
 *       └── Card "facturas" → Panel con tabla de facturas + CRUD
 * 
 * @author Forestech Learning Project
 * @version 1.0
 */
public class ForestechProfessionalApp extends JFrame {

    // CardLayout y contenedor para cambiar entre vistas
    private CardLayout cardLayout;
    private JPanel contenedorPrincipal;
    
    // Tablas para cada módulo
    private JTable tablaProductos;
    private JTable tablaVehiculos;
    private JTable tablaMovimientos;
    private JTable tablaFacturas;
    
    // Modelos de tabla
    private DefaultTableModel modeloProductos;
    private DefaultTableModel modeloVehiculos;
    private DefaultTableModel modeloMovimientos;
    private DefaultTableModel modeloFacturas;
    
    // Etiquetas del Dashboard (para actualizar estadísticas)
    private JLabel lblTotalProductos;
    private JLabel lblTotalVehiculos;
    private JLabel lblMovimientosHoy;
    private JLabel lblTotalFacturas;
    
    // Botones de navegación (para marcar cual está activo)
    private JButton btnInicio;
    private JButton btnProductos;
    private JButton btnVehiculos;
    private JButton btnMovimientos;
    private JButton btnFacturas;
    
    /**
     * Constructor principal.
     */
    public ForestechProfessionalApp() {
        // Configuración de la ventana
        setTitle("Forestech Oil Management System - Professional Edition");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Crear menú bar
        crearMenuBar();
        
        // Crear layout principal con JSplitPane
        crearLayoutPrincipal();
        
        // Cargar datos iniciales del dashboard
        cargarDashboard();
        
        setVisible(true);
    }
    
    /**
     * Crea el JSplitPane con panel de navegación y contenedor de vistas.
     */
    private void crearLayoutPrincipal() {
        // Panel de navegación (izquierda)
        JPanel panelNavegacion = crearPanelNavegacion();
        
        // Contenedor principal con CardLayout (derecha)
        cardLayout = new CardLayout();
        contenedorPrincipal = new JPanel(cardLayout);
        
        // Agregar todas las "tarjetas" (vistas)
        contenedorPrincipal.add(crearDashboardPanel(), "dashboard");
        contenedorPrincipal.add(crearPanelProductos(), "productos");
        contenedorPrincipal.add(crearPanelVehiculos(), "vehiculos");
        contenedorPrincipal.add(crearPanelMovimientos(), "movimientos");
        contenedorPrincipal.add(crearPanelFacturas(), "facturas");
        
        // Crear JSplitPane
        JSplitPane splitPane = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT,  // División horizontal (izq | der)
            panelNavegacion,               // Componente izquierdo
            contenedorPrincipal            // Componente derecho
        );
        
        // Configuración del JSplitPane
        splitPane.setDividerLocation(200);      // Ancho del panel de navegación
        splitPane.setOneTouchExpandable(true);  // Botones de colapso
        splitPane.setDividerSize(8);            // Grosor del divisor
        splitPane.setResizeWeight(0.0);         // Panel izq no crece al redimensionar
        
        add(splitPane, BorderLayout.CENTER);
    }
    
    // ... (continúa con métodos auxiliares)
}
```

Este archivo será MUY extenso (~1500 líneas) pero ALTAMENTE EDUCATIVO porque:
- Cada método tiene un propósito claro
- Reutiliza patrones de checkpoints anteriores
- Demuestra arquitectura profesional real

---

### ✅ Criterios de Éxito

- [ ] Aplicación inicia mostrando el Dashboard
- [ ] Panel de navegación lateral con 5 botones funcionales
- [ ] Al hacer clic en cada botón, cambia la vista (CardLayout)
- [ ] Dashboard muestra estadísticas reales desde la BD
- [ ] Panel de Productos muestra tabla + botones Agregar/Eliminar/Refrescar
- [ ] Panel de Vehículos muestra tabla + botones funcionales
- [ ] Panel de Movimientos muestra tabla + botón Registrar
- [ ] Panel de Facturas muestra tabla + botón Nueva Factura + formulario funcional
- [ ] CRUD de Facturas completo (crear, leer, ver detalles)
- [ ] JSplitPane permite redimensionar panel de navegación
- [ ] Aplicación NO crea ventanas nuevas, todo en una sola JFrame
- [ ] Menú bar con opciones de navegación rápida
- [ ] Atajos de teclado funcionan (Ctrl+1 a Ctrl+5 para navegación)

---

### 🏃 Cómo ejecutar

```bash
cd /home/hp/forestechOil/forestech-cli-java
mvn clean compile
mvn exec:java -Dexec.mainClass="com.forestech.ui.ForestechProfessionalApp"
```

O después de compilar el JAR:
```bash
mvn clean package
java -jar target/forestech-app.jar
```

---

### 🎨 Diseño Visual Completo

#### Vista 1: Dashboard (Inicial)
```
┌──────────────────────────────────────────────────────────────┐
│ Forestech - Professional Edition                    [_][□][X]│
├──────────────────────────────────────────────────────────────┤
│ Archivo  Ver  Movimientos  Facturas  Ayuda                   │
├────────────┬─────────────────────────────────────────────────┤
│            │  📊 DASHBOARD - RESUMEN GENERAL                 │
│ [🏠 Inicio]│                                                  │
│            │  ┌──────────┐ ┌──────────┐ ┌──────────┐         │
│ [ Producto]│  │ 📦 Total │ │ 🚛 Total │ │ 📊 Movim.│         │
│            │  │ Producto │ │ Vehículo │ │ Hoy      │         │
│ [ Vehículo]│  │    15    │ │    8     │ │   23     │         │
│            │  └──────────┘ └──────────┘ └──────────┘         │
│ [Movimient]│                                                  │
│            │  ┌──────────┐ ┌──────────┐                      │
│ [ Facturas]│  │ 🧾 Total │ │ ⚠️  Stock │                      │
│            │  │ Facturas │ │ Bajo     │                      │
│            │  │    42    │ │    3     │                      │
│            │  └──────────┘ └──────────┘                      │
│            │                                                  │
│            │  ACCESOS RÁPIDOS:                                │
│            │  [Nuevo Producto] [Nuevo Vehículo]              │
│            │  [Registrar Movimiento] [Nueva Factura]         │
│            │                                                  │
└────────────┴─────────────────────────────────────────────────┘
```

#### Vista 2: Productos (al hacer clic en botón "Productos")
```
┌──────────────────────────────────────────────────────────────┐
│ Forestech - Professional Edition                    [_][□][X]│
├──────────────────────────────────────────────────────────────┤
│ Archivo  Ver  Movimientos  Facturas  Ayuda                   │
├────────────┬─────────────────────────────────────────────────┤
│            │  📦 GESTIÓN DE PRODUCTOS                        │
│ [ Inicio]  │                                                  │
│            │  ┌────────────────────────────────────────────┐ │
│ [🔹Product]│  │ ID  │ Nombre      │ Precio   │ Unidad     │ │
│            │  ├────────────────────────────────────────────┤ │
│ [ Vehículo]│  │ P001│ Diesel      │ $8,500.00│ Litros    │ │
│            │  │ P002│ Gasolina    │ $9,200.00│ Litros    │ │
│ [Movimient]│  │ P003│ Aceite 20W50│ $45,000  │ Litros    │ │
│            │  │ ...                                         │ │
│ [ Facturas]│  └────────────────────────────────────────────┘ │
│            │                                                  │
│            │  [Agregar Producto] [Eliminar] [Refrescar]      │
│            │                                                  │
└────────────┴─────────────────────────────────────────────────┘
```

#### Vista 5: Facturas (NUEVO en este checkpoint)
```
┌──────────────────────────────────────────────────────────────┐
│ Forestech - Professional Edition                    [_][□][X]│
├──────────────────────────────────────────────────────────────┤
│ Archivo  Ver  Movimientos  Facturas  Ayuda                   │
├────────────┬─────────────────────────────────────────────────┤
│            │  🧾 GESTIÓN DE FACTURAS                         │
│ [ Inicio]  │                                                  │
│            │  ┌────────────────────────────────────────────┐ │
│ [ Producto]│  │ Nº Factura │ Fecha     │ Proveedor│ Total │ │
│            │  ├────────────────────────────────────────────┤ │
│ [ Vehículo]│  │ F-2024-001 │ 2024-01-15│ SUPP001 │$350k │ │
│            │  │ F-2024-002 │ 2024-01-18│ SUPP002 │$280k │ │
│ [Movimient]│  │ F-2024-003 │ 2024-01-20│ SUPP001 │$420k │ │
│            │  │ ...                                         │ │
│ [🔹Factura]│  └────────────────────────────────────────────┘ │
│            │                                                  │
│            │  [Nueva Factura] [Ver Detalles] [Refrescar]    │
│            │                                                  │
└────────────┴─────────────────────────────────────────────────┘
```

---

### 📝 Análisis Detallado del Código

#### Parte 1: JSplitPane - División del Espacio

```java
// Crear JSplitPane horizontal (izquierda | derecha)
JSplitPane splitPane = new JSplitPane(
    JSplitPane.HORIZONTAL_SPLIT,  // Tipo de división
    panelNavegacion,               // Componente izquierdo (200px)
    contenedorPrincipal            // Componente derecho (resto)
);

// ¿Por qué estas configuraciones?
splitPane.setDividerLocation(200);
// → Panel de navegación tiene 200px de ancho fijo

splitPane.setOneTouchExpandable(true);
// → Agrega botones ◀ ▶ para colapsar/expandir rápidamente

splitPane.setResizeWeight(0.0);
// → Al redimensionar ventana, panel izq mantiene su tamaño
//   Solo crece/reduce el panel derecho
```

**Alternativa: División vertical (arriba | abajo)**
```java
JSplitPane splitPane = new JSplitPane(
    JSplitPane.VERTICAL_SPLIT,    // Cambia a vertical
    panelSuperior,                 // Arriba
    panelInferior                  // Abajo
);
splitPane.setDividerLocation(100);  // 100px arriba
```

---

#### Parte 2: CardLayout - Gestión de Vistas

```java
// 1. Crear contenedor con CardLayout
cardLayout = new CardLayout();
contenedorPrincipal = new JPanel(cardLayout);

// 2. Agregar paneles con identificadores únicos
contenedorPrincipal.add(dashboardPanel, "dashboard");
contenedorPrincipal.add(productosPanel, "productos");
contenedorPrincipal.add(vehiculosPanel, "vehiculos");
// ... más paneles

// 3. Función para cambiar vista
private void mostrarVista(String nombreVista) {
    cardLayout.show(contenedorPrincipal, nombreVista);
}

// 4. Uso desde botones de navegación
btnProductos.addActionListener(e -> {
    mostrarVista("productos");      // Cambia a vista de productos
    marcarBotonActivo(btnProductos); // Cambia color del botón
    cargarProductos();               // Refresca datos
});
```

**¿Por qué CardLayout?**
- ✅ Sin ventanas nuevas (todo en 1 JFrame)
- ✅ Transiciones instantáneas
- ✅ Menor consumo de memoria
- ✅ Experiencia de usuario fluida

**Alternativas descartadas:**
- ❌ `JTabbedPane`: Tabs visibles siempre (menos limpio)
- ❌ `setVisible(true/false)`: Complejo de gestionar con muchos paneles
- ❌ Ventanas separadas: Usuario pierde contexto, muchas ventanas abiertas

---

#### Parte 3: Dashboard - Consultas Agregadas

```java
private void cargarDashboard() {
    try {
        // Consulta 1: Total de productos
        List<Product> productos = ProductServices.getAllProducts();
        lblTotalProductos.setText(String.valueOf(productos.size()));
        
        // Consulta 2: Total de vehículos
        List<Vehicle> vehiculos = VehicleServices.getAllVehicles();
        lblTotalVehiculos.setText(String.valueOf(vehiculos.size()));
        
        // Consulta 3: Movimientos de hoy
        List<Movement> movimientos = MovementServices.getAllMovements();
        long movimientosHoy = movimientos.stream()
            .filter(m -> m.getMovementDate().equals(java.time.LocalDate.now()))
            .count();
        lblMovimientosHoy.setText(String.valueOf(movimientosHoy));
        
        // Consulta 4: Total facturas
        List<Factura> facturas = FacturaServices.getAllFacturas();
        lblTotalFacturas.setText(String.valueOf(facturas.size()));
        
    } catch (DatabaseException e) {
        JOptionPane.showMessageDialog(this,
            "Error al cargar estadísticas: " + e.getMessage(),
            "Error", JOptionPane.ERROR_MESSAGE);
    }
}
```

**Mejora futura:** Agregar consultas SQL directas más eficientes:
```sql
-- En lugar de traer todos los registros y contar en Java
SELECT COUNT(*) FROM products;
SELECT COUNT(*) FROM vehicles;
SELECT COUNT(*) FROM movements WHERE DATE(movement_date) = CURDATE();
```

---

#### Parte 4: Panel de Facturas - CRUD Completo

```java
private JPanel crearPanelFacturas() {
    JPanel panel = new JPanel(new BorderLayout(10, 10));
    panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    
    // Título
    JLabel titulo = new JLabel("🧾 GESTIÓN DE FACTURAS", JLabel.CENTER);
    titulo.setFont(new Font("Arial", Font.BOLD, 20));
    panel.add(titulo, BorderLayout.NORTH);
    
    // Tabla de facturas
    String[] columnas = {"Nº Factura", "Fecha Emisión", "Proveedor", 
                         "Subtotal", "IVA", "Total"};
    modeloFacturas = new DefaultTableModel(columnas, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;  // Tabla de solo lectura
        }
    };
    
    tablaFacturas = new JTable(modeloFacturas);
    tablaFacturas.setRowHeight(25);
    configurarEstiloTabla(tablaFacturas);
    
    JScrollPane scroll = new JScrollPane(tablaFacturas);
    panel.add(scroll, BorderLayout.CENTER);
    
    // Panel de botones
    JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
    
    JButton btnNuevaFactura = new JButton("Nueva Factura");
    btnNuevaFactura.setBackground(new Color(100, 200, 100));
    btnNuevaFactura.addActionListener(e -> mostrarFormularioNuevaFactura());
    panelBotones.add(btnNuevaFactura);
    
    JButton btnVerDetalles = new JButton("Ver Detalles");
    btnVerDetalles.addActionListener(e -> verDetallesFactura());
    panelBotones.add(btnVerDetalles);
    
    JButton btnRefrescar = new JButton("Refrescar");
    btnRefrescar.addActionListener(e -> cargarFacturas());
    panelBotones.add(btnRefrescar);
    
    panel.add(panelBotones, BorderLayout.SOUTH);
    
    return panel;
}

private void cargarFacturas() {
    try {
        List<Factura> facturas = FacturaServices.getAllFacturas();
        modeloFacturas.setRowCount(0);
        
        for (Factura f : facturas) {
            modeloFacturas.addRow(new Object[]{
                f.getNumeroFactura(),
                f.getFechaEmision(),
                f.getSupplierId() != null ? f.getSupplierId() : "N/A",
                String.format("$%,.2f", f.getSubtotal()),
                String.format("$%,.2f", f.getIva()),
                String.format("$%,.2f", f.getTotal())
            });
        }
        
        System.out.println("✅ Facturas cargadas: " + facturas.size());
        
    } catch (DatabaseException e) {
        JOptionPane.showMessageDialog(this,
            "Error al cargar facturas: " + e.getMessage(),
            "Error", JOptionPane.ERROR_MESSAGE);
    }
}
```

---

#### Parte 5: Formulario de Nueva Factura (JDialog)

```java
private void mostrarFormularioNuevaFactura() {
    // Crear JDialog modal
    JDialog dialog = new JDialog(this, "Nueva Factura", true);
    dialog.setSize(500, 400);
    dialog.setLocationRelativeTo(this);
    dialog.setLayout(new BorderLayout(10, 10));
    
    // Panel de formulario
    JPanel panelForm = new JPanel(new GridLayout(6, 2, 10, 10));
    panelForm.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    
    // Campos
    JTextField txtNumeroFactura = new JTextField();
    JTextField txtFechaEmision = new JTextField(java.time.LocalDate.now().toString());
    JTextField txtFechaVencimiento = new JTextField();
    
    // ComboBox de proveedores
    JComboBox<String> cmbProveedor = new JComboBox<>();
    try {
        List<Supplier> proveedores = SupplierServices.getAllSuppliers();
        cmbProveedor.addItem("--- Sin proveedor ---");
        for (Supplier s : proveedores) {
            cmbProveedor.addItem(s.getId() + " - " + s.getName());
        }
    } catch (DatabaseException e) {
        e.printStackTrace();
    }
    
    JTextField txtSubtotal = new JTextField();
    JTextField txtIva = new JTextField();
    
    // Agregar componentes
    panelForm.add(new JLabel("Nº Factura:"));
    panelForm.add(txtNumeroFactura);
    panelForm.add(new JLabel("Fecha Emisión:"));
    panelForm.add(txtFechaEmision);
    panelForm.add(new JLabel("Fecha Vencimiento:"));
    panelForm.add(txtFechaVencimiento);
    panelForm.add(new JLabel("Proveedor:"));
    panelForm.add(cmbProveedor);
    panelForm.add(new JLabel("Subtotal:"));
    panelForm.add(txtSubtotal);
    panelForm.add(new JLabel("IVA (%):"));
    panelForm.add(txtIva);
    
    dialog.add(panelForm, BorderLayout.CENTER);
    
    // Botones
    JPanel panelBotones = new JPanel();
    JButton btnGuardar = new JButton("Guardar");
    JButton btnCancelar = new JButton("Cancelar");
    
    btnGuardar.addActionListener(e -> {
        // Validar y guardar
        try {
            String numero = txtNumeroFactura.getText().trim();
            if (numero.isEmpty()) {
                throw new IllegalArgumentException("Nº de factura requerido");
            }
            
            double subtotal = Double.parseDouble(txtSubtotal.getText());
            double iva = Double.parseDouble(txtIva.getText());
            double total = subtotal + (subtotal * iva / 100);
            
            String proveedorId = null;
            if (cmbProveedor.getSelectedIndex() > 0) {
                String seleccion = (String) cmbProveedor.getSelectedItem();
                proveedorId = seleccion.split(" - ")[0];
            }
            
            Factura factura = new Factura(
                numero,
                java.time.LocalDate.parse(txtFechaEmision.getText()),
                java.time.LocalDate.parse(txtFechaVencimiento.getText()),
                proveedorId,
                subtotal,
                subtotal * iva / 100,
                total,
                "",  // observaciones
                "EFECTIVO",  // forma_pago
                null  // cuenta_bancaria
            );
            
            // Crear con lista vacía de detalles (simplificado)
            FacturaServices.createFacturaWithDetails(factura, new java.util.ArrayList<>());
            
            JOptionPane.showMessageDialog(dialog, "Factura creada correctamente");
            dialog.dispose();
            cargarFacturas();  // Refrescar tabla
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dialog,
                "Error: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    });
    
    btnCancelar.addActionListener(e -> dialog.dispose());
    
    panelBotones.add(btnGuardar);
    panelBotones.add(btnCancelar);
    dialog.add(panelBotones, BorderLayout.SOUTH);
    
    dialog.setVisible(true);
}
```

---

### 🎯 Mejoras Opcionales (Desafíos)

Una vez que funcione la aplicación básica, puedes agregar:

#### 1. **Indicador de Vista Activa**
```java
private void marcarBotonActivo(JButton botonActivo) {
    // Resetear todos los botones
    btnInicio.setBackground(null);
    btnProductos.setBackground(null);
    btnVehiculos.setBackground(null);
    btnMovimientos.setBackground(null);
    btnFacturas.setBackground(null);
    
    // Marcar el activo
    botonActivo.setBackground(new Color(100, 150, 250));
}
```

#### 2. **Búsqueda en Tiempo Real**
```java
// Agregar campo de búsqueda sobre cada tabla
JTextField txtBuscar = new JTextField(20);
txtBuscar.addKeyListener(new KeyAdapter() {
    @Override
    public void keyReleased(KeyEvent e) {
        String filtro = txtBuscar.getText().toLowerCase();
        filtrarTabla(tablaProductos, filtro);
    }
});
```

#### 3. **Gráfica de Movimientos (JFreeChart)**
```java
// En el dashboard, agregar gráfica de barras:
// Movimientos por día de la última semana
```

#### 4. **Exportar a Excel**
```java
// Agregar botón "Exportar" en cada tabla
// Usar Apache POI para generar archivo .xlsx
```

---

### 🔗 Integración con Checkpoints Anteriores

Este checkpoint **reutiliza** todo lo aprendido:

| Checkpoint | Concepto Reutilizado | Dónde se usa en ForestechProfessionalApp |
|-----------|---------------------|------------------------------------------|
| **9.1** | JFrame, EDT | Ventana principal, SwingUtilities.invokeLater() |
| **9.2** | JButton, ActionListener | Botones de navegación, botones CRUD |
| **9.3** | JTextField, validaciones | Formularios de nueva factura |
| **9.4** | JComboBox | Selector de proveedores en formulario |
| **9.5** | JTable, DefaultTableModel | Todas las tablas (productos, vehículos, etc.) |
| **9.6** | Integración con Services | ProductServices, VehicleServices, etc. |
| **9.7** | JMenuBar | Menú superior con navegación rápida |
| **9.8** | JDialog modal | Formulario de nueva factura |
| **9.9** | Organización modular | Paneles separados para cada módulo |
| **9.12** | Look and Feel | Aplicar estilo del sistema |
| **NUEVO** | JSplitPane | División navegación | contenido |
| **NUEVO** | CardLayout | Cambio entre vistas sin ventanas nuevas |
| **NUEVO** | Dashboard | Panel de estadísticas inicial |

---

### 🎓 Lecciones Clave de este Checkpoint

1. **JSplitPane es ideal para navegación lateral**
   - Panel izquierdo fijo con menú
   - Panel derecho dinámico con contenido

2. **CardLayout > múltiples JFrames**
   - Más eficiente en memoria
   - Experiencia de usuario más fluida
   - Todo el contexto en una ventana

3. **Dashboard mejora UX profesional**
   - Usuario ve resumen al iniciar
   - No necesita navegar para ver info clave
   - Estadísticas en tiempo real

4. **Reutilización de código es poder**
   - No reescribes ProductServices, VehicleServices
   - Solo creas la capa visual
   - Arquitectura MVC en acción

5. **Arquitectura modular = escalabilidad**
   - Agregar nuevo módulo = crear nuevo panel + agregar botón
   - No afecta módulos existentes
   - Fácil mantenimiento

---

## 🎓 Conclusión de la Fase 9

**¡FELICIDADES!** Has completado la Fase 9: Swing GUI.

### 📊 Lo que lograste:

1. **Fundamentos de Swing** (Checkpoints 9.1-9.5)
   - Creaste ventanas, botones, formularios, combos y tablas
   - Entendiste el Event Dispatch Thread (EDT)
   - Dominaste los 7 Layout Managers
   - Implementaste 4 tipos de Listeners

2. **Integración con Base de Datos** (Checkpoints 9.6, 9.10, 9.11)
   - Conectaste GUI con ProductServices, VehicleServices, MovementServices
   - Validaste 3 foreign keys diferentes
   - Manejaste InsufficientStockException para control de stock
   - Creaste formularios CRUD completos

3. **Componentes Avanzados** (Checkpoints 9.7-9.9)
   - Implementaste JMenuBar con atajos de teclado
   - Creaste JDialog modal para formularios
   - Organizaste múltiples vistas con JTabbedPane

4. **Personalización** (Checkpoint 9.12)
   - Configuraste Look and Feel del sistema
   - Aprendiste a cambiar apariencia en tiempo real

5. **Arquitectura Profesional** (Checkpoint 9.13) ⭐⭐⭐
   - Creaste ForestechProfessionalApp: aplicación integral completa
   - Implementaste JSplitPane para navegación lateral ajustable
   - Dominaste CardLayout para cambio fluido entre vistas
   - Desarrollaste Dashboard con estadísticas en tiempo real
   - Completaste CRUD de Facturas con transacciones
   - Integraste 5 módulos en una sola aplicación profesional

### 🎯 Próximos Pasos:

1. **Packaging como JAR ejecutable** (ver sección siguiente)
2. **Crear EXE con Launch4j** (ver sección siguiente)
3. **Fase 10:** Conceptos avanzados (Streams, Lambdas, Optional)

---

## 📦 BONUS: Empaquetar como JAR Ejecutable

### 🎯 Objetivo:
Crear un archivo `.jar` que pueda ejecutarse con doble clic, sin necesitar `mvn exec:java`.

### 📝 Paso 1: Configurar `pom.xml`

Agrega el plugin `maven-jar-plugin` en la sección `<build>`:

```xml
<build>
    <plugins>
        <!-- Plugin para compilar -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.11.0</version>
            <configuration>
                <source>17</source>
                <target>17</target>
            </configuration>
        </plugin>

        <!-- Plugin para crear JAR ejecutable -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-jar-plugin</artifactId>
            <version>3.3.0</version>
            <configuration>
                <archive>
                    <manifest>
                        <mainClass>com.forestech.ui.ForestechMainGUI</mainClass>
                    </manifest>
                </archive>
            </configuration>
        </plugin>

        <!-- Plugin para incluir dependencias en el JAR -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-shade-plugin</artifactId>
            <version>3.5.0</version>
            <executions>
                <execution>
                    <phase>package</phase>
                    <goals>
                        <goal>shade</goal>
                    </goals>
                    <configuration>
                        <transformers>
                            <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                                <mainClass>com.forestech.ui.ForestechMainGUI</mainClass>
                            </transformer>
                        </transformers>
                        <finalName>forestech-app</finalName>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

### 📝 Paso 2: Compilar y Empaquetar

```bash
cd /home/hp/forestechOil/forestech-cli-java

# Limpiar, compilar y empaquetar
mvn clean package

# Esto crea: target/forestech-app.jar
```

### 📝 Paso 3: Ejecutar el JAR

```bash
# Opción 1: Desde terminal
java -jar target/forestech-app.jar

# Opción 2: Doble clic en el archivo forestech-app.jar
# (si tienes Java instalado y asociado con .jar)
```

### ⚠️ Problema Común: MySQL Connector

Si al ejecutar el JAR obtienes error de conexión MySQL, asegúrate de que `mysql-connector-j` esté en el classpath:

```bash
# Verificar que maven-shade-plugin incluyó las dependencias
jar tf target/forestech-app.jar | grep mysql

# Debe mostrar archivos como:
# com/mysql/cj/jdbc/Driver.class
```

---

## 🪟 BONUS: Crear EXE con Launch4j (Solo Windows)

### 🎯 Objetivo:
Convertir el archivo `.jar` en un ejecutable `.exe` de Windows con ícono personalizado.

### 📝 Paso 1: Descargar Launch4j

1. Descarga desde: https://sourceforge.net/projects/launch4j/
2. Instala en Windows
3. Abre Launch4j

### 📝 Paso 2: Configurar Launch4j

**En la pestaña "Basic":**
- **Output file:** `C:\Users\TuUsuario\forestech.exe`
- **Jar:** `C:\...\forestech-app.jar`
- **Icon:** (opcional) Selecciona un archivo `.ico`

**En la pestaña "JRE":**
- **Min JRE version:** `17` (o la versión de Java que uses)
- **Max JRE version:** (dejar vacío)

**En la pestaña "Version Info" (opcional):**
- **File version:** `1.0.0.0`
- **Product name:** `Forestech Oil Management`
- **Company name:** `Forestech Development`
- **File description:** `Sistema de Gestión de Combustibles`

### 📝 Paso 3: Generar EXE

1. Click en el ícono de engranaje (⚙️ Build wrapper)
2. Espera el mensaje "Successfully created..."
3. El archivo `forestech.exe` está listo en la ubicación especificada

### 📝 Paso 4: Distribuir

**Archivos necesarios para distribución:**
```
forestech-distribucion/
├── forestech.exe
├── forestech-app.jar  (Launch4j lo busca automáticamente)
└── README.txt
```

**README.txt:**
```
FORESTECH OIL MANAGEMENT SYSTEM
================================

Requisitos:
- Windows 10 o superior
- Java 17 o superior instalado
- MySQL instalado y corriendo

Instalación:
1. Verifica que Java esté instalado: java -version
2. Configura la base de datos MySQL
3. Ejecuta forestech.exe

Soporte:
contacto@forestech.com
```

---

## 🐛 Errores Comunes y Soluciones

### Error 1: "ClassNotFoundException: mysql.cj.jdbc.Driver"

**Causa:** Maven Shade no incluyó el driver MySQL en el JAR.

**Solución:**
```xml
<!-- En pom.xml, verifica que mysql-connector-j esté en <dependencies> -->
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <version>8.0.33</version>
</dependency>
```

```bash
# Recompila
mvn clean package
```

### Error 2: "Main-Attribute missing from manifest"

**Causa:** El MANIFEST.MF del JAR no especifica la clase principal.

**Solución:**
```xml
<!-- En maven-jar-plugin, agrega: -->
<configuration>
    <archive>
        <manifest>
            <mainClass>com.forestech.ui.ForestechMainGUI</mainClass>
        </manifest>
    </archive>
</configuration>
```

### Error 3: "Swing components not displaying correctly"

**Causa:** LaF no se aplicó en EDT.

**Solución:**
```java
public static void main(String[] args) {
    try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
        e.printStackTrace();
    }

    // IMPORTANTE: Usar SwingUtilities.invokeLater
    SwingUtilities.invokeLater(() -> new ForestechMainGUI());
}
```

### Error 4: "DatabaseException: Communications link failure"

**Causa:** No se puede conectar a MySQL.

**Solución:**
1. Verificar que MySQL esté corriendo:
   ```bash
   sudo systemctl status mysql
   ```

2. Verificar credenciales en `DatabaseConnection.java`:
   ```java
   private static final String URL = "jdbc:mysql://localhost:3306/FORESTECHOIL";
   private static final String USER = "root";
   private static final String PASSWORD = "hp";
   ```

3. Verificar que la base de datos exista:
   ```bash
   mysql -u root -p'hp'
   SHOW DATABASES;
   USE FORESTECHOIL;
   ```

---

## 🎉 Proyecto Completado

Has creado un sistema completo de gestión de combustibles con:

✅ **Backend sólido:**
- 5 servicios CRUD (Product, Vehicle, Supplier, Factura, Movement)
- Validación de 3 foreign keys
- Manejo de stock insuficiente
- Transacciones

✅ **Frontend profesional:**
- 5+ ventanas GUI completas
- Menús de navegación
- Formularios con validación
- Tablas con datos reales
- Diálogos modales

✅ **Empaquetado:**
- JAR ejecutable
- EXE para Windows (opcional)

### 🚀 Siguientes Desafíos:

1. Agregar filtros avanzados en tablas (por rango de fechas, por producto, etc.)
2. Implementar reportes en PDF (usando JasperReports o iText)
3. Agregar gráficas con JFreeChart (consumo por vehículo, stock por mes, etc.)
4. Implementar sistema de usuarios con autenticación
5. Migrar a JavaFX para interfaces más modernas

**¡Excelente trabajo llegando hasta aquí!** 🎓

---

