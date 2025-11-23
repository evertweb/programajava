# Propuesta de Diseño UI: Estilo "Hospital Enterprise"

Esta propuesta detalla la transformación de la interfaz de **ForestechOil** hacia un estilo visual "Hospital/Clínico". Este estilo prioriza la limpieza, la claridad, la reducción de la carga cognitiva y una sensación de higiene y precisión.

## 1. Filosofía de Diseño

*   **Esterilidad y Limpieza:** Uso predominante del blanco y grises muy claros para el fondo. Evitar el desorden visual.
*   **Confianza y Calma:** Uso de tonos Teal (Verde Azulado), Cian o Menta como colores primarios. Estos colores evocan salud, tecnología médica y tranquilidad.
*   **Alta Legibilidad:** Tipografía clara, buen contraste y espaciado generoso para evitar errores de lectura.
*   **Enfoque en la Información:** La UI debe "desaparecer" para que los datos sean los protagonistas.

## 2. Paleta de Colores

Se sustituirá el azul corporativo estándar por una paleta más fresca y clínica.

| Variable | Color | Hex | Uso |
| :--- | :--- | :--- | :--- |
| **Primary** | **Teal Medico** | `#009688` | Botones principales, estados activos, encabezados clave. |
| **Primary Light** | **Soft Mint** | `#B2DFDB` | Fondos de selección, hovers suaves. |
| **Primary Dark** | **Deep Teal** | `#00695C` | Estados hover de botones, texto de alto énfasis. |
| **Secondary** | **Slate Blue** | `#455A64` | Elementos de navegación, iconos secundarios. |
| **Background** | **Pure White** | `#FFFFFF` | Contenedores, tarjetas, áreas de trabajo. |
| **Background Alt** | **Clinical Grey** | `#F8F9FA` | Fondo general de la aplicación (detrás de las tarjetas). |
| **Text Primary** | **Dark Grey** | `#263238` | Texto principal (casi negro, pero más suave). |
| **Text Secondary** | **Mid Grey** | `#546E7A` | Etiquetas, subtítulos. |
| **Error** | **Emergency Red** | `#D32F2F` | Alertas críticas. |
| **Warning** | **Alert Orange** | `#F57C00` | Advertencias. |
| **Success** | **Safe Green** | `#388E3C` | Confirmaciones. |

## 3. Tipografía

Mantendremos `Roboto` por su excelente legibilidad, pero ajustaremos los pesos y el espaciado.

*   **Headings:** Peso `500` o `600`. Color `Primary Dark` o `Text Primary`.
*   **Body:** Peso `400`. Color `Text Primary`.
*   **Data/Numbers:** Fuente monoespaciada opcional para tablas financieras o de inventario, o `Roboto` con `font-feature-settings: "tnum"` (tabular nums) para alineación perfecta.

## 4. Componentes UI

### 4.1. Tarjetas y Contenedores (Paper)
*   **Bordes:** Redondeados suaves (`borderRadius: 8px` o `12px`).
*   **Sombras:** Muy sutiles y difusas (`box-shadow: 0 2px 12px rgba(0,0,0,0.08)`). Efecto de "flotar" suavemente sobre el fondo clínico.
*   **Bordes:** Eliminación de bordes duros (`1px solid #e0e0e0`) en favor de sombras suaves o bordes muy tenues.

### 4.2. Botones
*   **Forma:** "Pill" (completamente redondeados) o con radio amplio (`borderRadius: 20px`).
*   **Estilo:**
    *   *Contained:* Fondo Teal, texto blanco. Sin elevación (flat) hasta hacer hover.
    *   *Outlined:* Borde fino Teal, texto Teal.
*   **Feedback:** Efecto "Ripple" sutil.

### 4.3. Navegación (Sidebar)
*   **Estilo:** Fondo blanco o gris muy pálido (`#F8F9FA`).
*   **Item Activo:** Indicador lateral izquierdo (barra vertical) color Teal, fondo suave menta (`#E0F2F1`), texto Teal negrita.
*   **Iconos:** Estilo "Outlined" o "Two Tone" para una apariencia más ligera y moderna.

### 4.4. Tablas (DataGrid)
*   **Encabezado:** Fondo gris muy claro, texto en mayúsculas pequeñas (small caps) o peso medio.
*   **Filas:** Altura cómoda (`density: standard` o `comfortable`).
*   **Zebra Striping:** Opcional, pero muy sutil (`#FAFAFA` vs `#FFFFFF`).
*   **Líneas:** Solo horizontales para guiar la lectura, eliminando las verticales para reducir ruido visual.

## 5. Plan de Implementación

1.  **Actualizar `src/theme/theme.ts`:**
    *   Definir la nueva paleta de colores `clinicalColors`.
    *   Ajustar `shape.borderRadius`.
    *   Sobrescribir componentes (`MuiButton`, `MuiPaper`, `MuiAppBar`, `MuiDataGrid`).
2.  **Refactorizar `MainLayout.tsx`:**
    *   Adaptar el Sidebar al nuevo estilo visual (colores claros).
    *   Ajustar la AppBar para que se sienta integrada (posiblemente blanca con sombra suave).
3.  **Ajustar `Dashboard.tsx`:**
    *   Rediseñar las `MetricCard` para usar el nuevo estilo de sombras suaves y colores clínicos.

---

¿Qué te parece esta dirección visual? ¿Deseas ajustar algún aspecto antes de proceder con el código?