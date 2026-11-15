package com.example.pizzeriaapp.model

import com.example.pizzeriaapp.domain.Totales
import com.example.pizzeriaapp.domain.calcularTotales
import java.time.LocalDate

class Pedido(
    val cliente: Cliente?,
    val productos: List<Producto>,
    var estado: EstadoPedido = EstadoPedido.EN_PREPARACION,
    val notas: String? = null,
    val fecha: LocalDate = LocalDate.now()
) {
    fun nombreCliente(): String = cliente?.nombre ?: "Cliente sin registrar"

    fun cambiarEstado(nuevoEstado: EstadoPedido) {
        estado = nuevoEstado
    }

    fun totales(): Totales = calcularTotales(this)

    fun calcularTotal(): Int = totales().total

    // Sobrecarga de +: combina dos pedidos en uno nuevo; los totales y descuentos se recalculan solos
    operator fun plus(otro: Pedido): Pedido = Pedido(
        cliente = cliente ?: otro.cliente,
        productos = productos + otro.productos,
        notas = listOfNotNull(notas, otro.notas).joinToString("; ").takeIf { it.isNotEmpty() },
        fecha = fecha
    )

    // Desestructuración: val (cliente, productos, total) = pedido
    operator fun component1(): Cliente? = cliente
    operator fun component2(): List<Producto> = productos
    operator fun component3(): Int = calcularTotal()
}
