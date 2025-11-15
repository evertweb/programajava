package com.forestech;

import java.util.HashMap;

/**
 * Clase Main - Punto de entrada de la aplicación Forestech CLI.
 *
 * <p><strong>Responsabilidad:</strong></p>
 * <ul>
 *   <li>Esta clase SOLO tiene una función: iniciar la aplicación</li>
 *   <li>Toda la lógica de la aplicación está en AppController</li>
 *   <li>Main.java debe ser SIMPLE y LIMPIO</li>
 * </ul>
 *
 * <p><strong>Flujo de ejecución:</strong></p>
 * <pre>
 * 1. Usuario ejecuta: mvn exec:java -Dexec.mainClass="com.forestech.Main"
 * 2. Main.main() crea una instancia de AppController
 * 3. AppController.iniciar() toma el control de la aplicación
 * 4. El usuario interactúa con los menús de AppController
 * 5. Cuando el usuario selecciona "Salir", AppController termina
 * 6. Main.main() termina también
 * </pre>
 *
 * @author Forestech Development Team
 * @version 3.0 (CLI Interactiva completa)
 * @since Fase 6
 */
public class Main {

    /**
     * Método principal de la aplicación.
     *
     * @param args Argumentos de línea de comandos (no se utilizan actualmente)
     */
    public static void main(String[] args) {
        // PASO 1: Crear una instancia del controlador de la aplicación
        //AppController app = new AppController();

        // PASO 2: Iniciar la aplicación (esto toma control hasta que el usuario salga)
        //app.iniciar();

        // PASO 3: Cuando llegamos aquí, la aplicación ha terminado
        // (El usuario seleccionó "Salir" en el menú)

        HashMap<String, String> usuarios = new HashMap<>();




        usuarios.put("evert cardenas", "eve@evert.com");
        if (usuarios.containsKey("evert cardenas")){
            System.out.println("tu correo es " + usuarios.get("evert cardenas"));

        }else
            System.out.println("error");
    }
}
