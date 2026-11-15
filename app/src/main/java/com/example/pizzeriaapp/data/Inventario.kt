package com.example.pizzeriaapp.data

import com.example.pizzeriaapp.model.Pedido

// Ingrediente que falta para un pedido
data class Faltante(val ingrediente: String, val requerido: Int, val disponible: Int) {
    val faltan: Int get() = requerido - disponible
}

// Resultado de validar un pedido contra el inventario.
// Adaptación a Android: reemplaza los mensajes por consola; la interfaz decide cómo mostrar los faltantes.
data class ResultadoPedido(val aceptado: Boolean, val faltantes: List<Faltante> = emptyList())

// Cantidad de cada ingrediente necesaria para preparar un lote de pizzas con la receta indicada
fun calcularNecesidad(ingredientesReceta: List<String>, pizzasEnLote: Int): Map<String, Int> =
    ingredientesReceta
        .groupingBy { it }
        .eachCount()
        .mapValues { (_, porPizza) -> porPizza * pizzasEnLote }

// Compara lo requerido con el inventario y devuelve solo los ingredientes insuficientes
fun verificarDisponibilidad(necesidad: Map<String, Int>, inventario: Map<String, Int>): List<Faltante> {
    val faltantes = mutableListOf<Faltante>()
    for ((ingrediente, requerido) in necesidad) {
        val disponible = inventario[ingrediente] ?: 0
        when {
            disponible >= requerido -> Unit // stock suficiente, no hay faltante
            else -> faltantes.add(Faltante(ingrediente, requerido, disponible))
        }
    }
    return faltantes
}

// Descuenta del inventario las cantidades necesarias (se asume que ya fue verificado)
fun descontarInventario(inventario: MutableMap<String, Int>, necesidad: Map<String, Int>) {
    necesidad.forEach { (ingrediente, cantidad) ->
        inventario[ingrediente] = (inventario[ingrediente] ?: 0) - cantidad
    }
}

// Suma o resta stock; impide dejar un ingrediente con cantidad negativa
fun ajustarStock(inventario: MutableMap<String, Int>, ingrediente: String, cantidad: Int) {
    val nuevoStock = (inventario[ingrediente] ?: 0) + cantidad
    require(nuevoStock >= 0) {
        "El stock de $ingrediente no puede quedar negativo (resultado: $nuevoStock)."
    }
    inventario[ingrediente] = nuevoStock
}

// Acepta el pedido solo si hay ingredientes suficientes; si no, informa qué reabastecer
fun aceptarPedido(pedido: Pedido, inventario: MutableMap<String, Int>): ResultadoPedido {
    val necesidad = calcularNecesidad(pedido.productos.flatMap { it.ingredientes }, 1)
    val faltantes = verificarDisponibilidad(necesidad, inventario)

    if (faltantes.isNotEmpty()) return ResultadoPedido(aceptado = false, faltantes = faltantes)

    descontarInventario(inventario, necesidad)
    return ResultadoPedido(aceptado = true)
}

fun pasosReabastecimiento(faltantes: List<Faltante>): List<String> = buildList {
    add("Pedido rechazado: ingredientes insuficientes.")
    faltantes.forEach { f ->
        add("${f.ingrediente}: requeridos ${f.requerido}, disponibles ${f.disponible} (faltan ${f.faltan})")
    }
    add("1) Confirmar con el proveedor disponibilidad de: ${faltantes.joinToString { it.ingrediente }}")
    add("2) Comprar al menos la cantidad faltante de cada ingrediente")
    add("3) Registrar la recepción en el inventario")
    add("4) Volver a validar el pedido")
}
