flowchart TD
    Start([🚀 Iniciar Aplicación])
    Menu["📋 Mostrar Menú Principal"]
    Choice{¿Qué deseas hacer?}
    
    EntryFlow["📥 Registrar ENTRADA"]
    ExitFlow["📤 Registrar SALIDA"]
    QueryFlow["🔍 Consultar movimientos"]
    Exit([❌ Cerrar Aplicación])
    
    EntryInput["Ingresa datos:<br/>Cantidad, Proveedor, Fecha"]
    EntryValidate{"¿Datos válidos?"}
    EntryError["⚠️ Error: Validación fallida"]
    EntryDB["💾 Guardar en Base de Datos"]
    EntrySuccess["✅ ENTRADA registrada"]
    
    ExitInput["Ingresa datos:<br/>Cantidad, Destino, Fecha"]
    ExitValidate{"¿Datos válidos?"}
    ExitError["⚠️ Error: Validación fallida"]
    ExitDB["💾 Guardar en Base de Datos"]
    ExitSuccess["✅ SALIDA registrada"]
    
    QueryType{"¿Qué consultar?"}
    QueryAll["📊 Todos los movimientos"]
    QueryDate["📅 Por rango de fecha"]
    QueryType2["Filtrar ENTRADA o SALIDA"]
    Display["🖥️ Mostrar resultados"]
    Resume["📈 Mostrar resumen:<br/>Total ENTRADA, Total SALIDA, Saldo"]
    
    End([🔚 Fin])
    
    Start --> Menu
    Menu --> Choice
    
    Choice -->|1 - ENTRADA| EntryFlow
    Choice -->|2 - SALIDA| ExitFlow
    Choice -->|3 - CONSULTAR| QueryFlow
    Choice -->|4 - SALIR| Exit
    
    EntryFlow --> EntryInput
    EntryInput --> EntryValidate
    EntryValidate -->|❌ No| EntryError
    EntryError --> Menu
    EntryValidate -->|✅ Sí| EntryDB
    EntryDB --> EntrySuccess
    EntrySuccess --> Menu
    
    ExitFlow --> ExitInput
    ExitInput --> ExitValidate
    ExitValidate -->|❌ No| ExitError
    ExitError --> Menu
    ExitValidate -->|✅ Sí| ExitDB
    ExitDB --> ExitSuccess
    ExitSuccess --> Menu
    
    QueryFlow --> QueryType
    QueryType -->|Todas| QueryAll
    QueryType -->|Por fecha| QueryDate
    QueryType -->|Por tipo| QueryType2
    
    QueryAll --> Display
    QueryDate --> Display
    QueryType2 --> Display
    
    Display --> Resume
    Resume --> Menu
    
    Exit --> End
