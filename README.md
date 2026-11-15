# PizzeriaApp – Configurando Android Studio (PMY2201, Semana 4)

Proyecto Android (plantilla **Empty Activity**, Kotlin + Jetpack Compose) que traslada al entorno móvil la lógica de la pizzería desarrollada en las semanas 1 a 3.

Al ejecutar, la pantalla muestra el mensaje **«Bienvenidos a la mejor pizzería de la ciudad»**, el menú y un pedido de ejemplo cuyos totales se calculan con la lógica importada.

## Configuración del proyecto

| Parámetro | Valor |
|---|---|
| Nombre | PizzeriaApp |
| Plantilla | Empty Activity (Compose) |
| Lenguaje | Kotlin 2.0.21 |
| Minimum SDK | API 21 (Android 5.0) |
| Compile / Target SDK | API 35 |
| Android Gradle Plugin / Gradle | 8.7.3 / 8.9 |
| Paquete | `com.example.pizzeriaapp` |

## Cómo abrirlo y ejecutarlo

1. En Android Studio: **File > Open** y selecciona la carpeta `PizzeriaApp`.
2. Espera a que termine la sincronización de Gradle. Si Android Studio ofrece instalar el SDK de API 35, acepta. Si ofrece actualizar el Android Gradle Plugin, puedes omitirlo.
3. Crea el dispositivo virtual: **Device Manager > Create Virtual Device > Pixel 4**, con una imagen de sistema API 31 o superior.
4. Selecciona el emulador y presiona **Run 'app'** (Shift + F10).
5. Pruebas unitarias de la lógica: clic derecho sobre `LogicaPizzeriaTest` > **Run**, o `gradlew.bat test` en la terminal.

## Organización en paquetes

```
com.example.pizzeriaapp
├── MainActivity.kt        # Activity, Greeting y pantalla inicial (Jetpack Compose)
├── model/                 # Entidades del dominio
│   ├── Producto.kt
│   ├── Pedido.kt
│   ├── Usuario.kt         # Usuario, Cliente y Repartidor
│   ├── Categoria.kt
│   └── EstadoPedido.kt
├── domain/                # Reglas de negocio
│   ├── CalculoPedidos.kt  # Descuentos, IVA y totales
│   ├── GestionPedidos.kt  # Repartidores, notificaciones, reporte y resumen
│   └── Promociones.kt     # Anotación @Promocionable
├── data/                  # Datos y colecciones
│   ├── Menu.kt            # Pizzas, bebida e ingredientes
│   ├── Inventario.kt      # Stock y validación de pedidos
│   ├── RegistroUnico.kt   # Registro sin duplicados
│   └── DatosDemo.kt       # Datos de ejemplo de la pantalla inicial
├── util/                  # Validaciones (Regex, rangos) y formato de precios
└── ui/theme/              # Tema Material 3 de la plantilla
```

## Adaptaciones al entorno Android

| Semana 3 (consola JVM) | Semana 4 (Android) | Motivo |
|---|---|---|
| Un solo paquete `pizzeria` | Paquetes `model`, `domain`, `data` y `util` | La actividad pide organizar las clases en paquetes |
| `println()` en `Repartidor`, `notificarEstado`, `imprimirReporte`, inventario y registro | Las funciones devuelven `String`, listas o un resultado (`ResultadoPedido`) | En Android no hay consola; la interfaz decide cómo mostrar cada mensaje |
| `pedirHasta()` con `readln()` | Se elimina; quedan las funciones de validación (`esCorreoValido`, `formatearTelefono`, `validarCantidad`) | La entrada de datos se hará con campos de formulario |
| `Main.kt` con la demostración por consola | `MainActivity.kt` y `DatosDemo.kt` | El punto de entrada es la Activity |
| `Reflection.kt` con `kotlin-reflect` | Se omite; `@Promocionable` se lee con reflection de Java | Evita sumar una librería pesada al APK; era una demostración de consola |
| `java.time.LocalDate` directo | *Core library desugaring* activado en `app/build.gradle.kts` | `java.time` existe de forma nativa desde API 26 y el mínimo es API 21 |

El resto de la lógica (igualdad de `Producto` y `Cliente`, operador `+` de `Pedido`, desestructuración, descuentos, IVA, inventario) se mantiene sin cambios.

## Cumplimiento de la pauta

| Criterio | Evidencia |
|---|---|
| Instalación y configuración de Android Studio | Captura `evidencias/android-studio-instalado.png` |
| Proyecto Empty Activity, Kotlin, SDK mínimo API 21 | `app/build.gradle.kts` (`minSdk = 21`), `settings.gradle.kts` (`rootProject.name = "PizzeriaApp"`) |
| AVD configurado y en funcionamiento | Captura `evidencias/avd-app-ejecutandose.png` |
| Lógica previa integrada y organizada en paquetes | Paquetes `model`, `domain`, `data` y `util`; pruebas en `LogicaPizzeriaTest` |
| Aplicación ejecutándose con el mensaje de bienvenida | `Greeting()` en `MainActivity.kt`; captura `evidencias/avd-app-ejecutandose.png` |
| Documentación y entrega comprimida | Carpeta `evidencias/` con las capturas correspondientes |
