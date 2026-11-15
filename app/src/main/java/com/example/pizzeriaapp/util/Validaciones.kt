package com.example.pizzeriaapp.util

const val TELEFONO_PREDETERMINADO = "(000) 000-0000"
const val CORREO_PREDETERMINADO = "correo@invalido.com"
const val CODIGO_PAIS_DEFECTO = "56"

// Rango permitido para cantidades de pizzas o ingredientes adicionales
val RANGO_CANTIDAD = 1..100

// Formato estándar: nombre@dominio.com (dominio con al menos una extensión de 2+ letras)
private val REGEX_CORREO = Regex("""^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\.[A-Za-z0-9-]+)*\.[A-Za-z]{2,}$""")

// Solo se permiten dígitos, espacios, paréntesis, guiones y un "+" inicial
private val REGEX_TELEFONO_CARACTERES = Regex("""^\+?[\d\s()-]+$""")

fun esCorreoValido(correo: String): Boolean = REGEX_CORREO.matches(correo.trim())

// Normaliza un teléfono al formato uniforme +CC (XXX) XXX-XXXX.
// Celulares chilenos (9 dígitos) quedan como +56 (XXX) XXX-XXX. Devuelve null si el formato no es válido.
fun formatearTelefono(entrada: String, codigoPais: String = CODIGO_PAIS_DEFECTO): String? {
    val limpio = entrada.trim()
    if (!REGEX_TELEFONO_CARACTERES.matches(limpio)) return null

    val digitos = limpio.filter { it.isDigit() }
    val (cc, nacional) = when (digitos.length) {
        9, 10 -> codigoPais to digitos
        11 -> if (digitos.startsWith(codigoPais)) codigoPais to digitos.drop(codigoPais.length) else return null
        12 -> digitos.take(2) to digitos.drop(2)
        else -> return null
    }
    return "+$cc (${nacional.take(3)}) ${nacional.substring(3, 6)}-${nacional.drop(6)}"
}

fun validarTelefono(entrada: String?): String =
    entrada?.let { formatearTelefono(it) } ?: TELEFONO_PREDETERMINADO

fun validarCorreo(entrada: String?): String =
    entrada?.trim()?.lowercase()?.takeIf { esCorreoValido(it) } ?: CORREO_PREDETERMINADO

// Valida una cantidad ingresada como texto (por ejemplo, desde un campo de formulario)
fun validarCantidad(texto: String): Int? = texto.trim().toIntOrNull()?.takeIf { it in RANGO_CANTIDAD }

// Convierte el texto ingresado en un costo válido; lanza IllegalArgumentException si no lo es
fun ingresarPrecio(texto: String): Int {
    val valor = texto.trim().toIntOrNull()
        ?: throw IllegalArgumentException("El costo '$texto' no es un número válido.")
    require(valor >= 0) { "El costo no puede ser negativo (ingresaste $valor)." }
    return valor
}
