package com.example.pizzeriaapp.domain

import com.example.pizzeriaapp.model.Categoria
import com.example.pizzeriaapp.model.Pedido
import java.time.LocalDate
import kotlin.math.roundToInt

const val TASA_IVA = 0.19
const val UMBRAL_DESCUENTO_PIZZAS = 3        // más de 3 pizzas => descuento por cantidad
const val PORCENTAJE_DESCUENTO_CANTIDAD = 10
const val PORCENTAJE_TEMPORADA = 5

// Periodo de promoción de temporada (rango cerrado: incluye ambos extremos)
val PERIODO_PROMOCION: ClosedRange<LocalDate> = LocalDate.of(2026, 11, 1)..LocalDate.of(2026, 12, 31)

fun estaEnPromocion(fecha: LocalDate): Boolean = fecha in PERIODO_PROMOCION

data class Totales(
    val subtotal: Int,
    val descuentoCantidad: Int,
    val descuentoPromocional: Int,   // productos con @Promocionable
    val descuentoTemporada: Int,     // pedido dentro del periodo de promoción
    val impuestos: Int               // IVA sobre el valor con descuentos
) {
    val descuentoTotal: Int get() = descuentoCantidad + descuentoPromocional + descuentoTemporada
    val total: Int get() = subtotal - descuentoTotal + impuestos
}

// Todos los descuentos se calculan sobre el subtotal; el IVA se aplica sobre el neto
fun calcularTotales(pedido: Pedido): Totales {
    val productos = pedido.productos
    val subtotal = productos.sumOf { it.precio }

    val pizzas = productos.count { it.categoria == Categoria.PIZZA }
    val descCantidad = if (pizzas > UMBRAL_DESCUENTO_PIZZAS) {
        (subtotal * PORCENTAJE_DESCUENTO_CANTIDAD / 100.0).roundToInt()
    } else {
        0
    }

    val descPromocional = productos
        .mapNotNull { p -> promocionDe(p)?.let { p.precio * it.porcentaje / 100.0 } }
        .sum()
        .roundToInt()

    val descTemporada = if (estaEnPromocion(pedido.fecha)) {
        (subtotal * PORCENTAJE_TEMPORADA / 100.0).roundToInt()
    } else {
        0
    }

    val impuestos = ((subtotal - descCantidad - descPromocional - descTemporada) * TASA_IVA).roundToInt()
    return Totales(subtotal, descCantidad, descPromocional, descTemporada, impuestos)
}
