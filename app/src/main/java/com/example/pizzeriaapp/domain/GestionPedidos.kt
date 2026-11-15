package com.example.pizzeriaapp.domain

import com.example.pizzeriaapp.model.EstadoPedido
import com.example.pizzeriaapp.model.Pedido
import com.example.pizzeriaapp.model.Repartidor
import com.example.pizzeriaapp.util.formatearPesos
import kotlin.math.roundToInt

// Adaptación a Android: estas funciones devuelven texto en lugar de imprimir en consola (println),
// para que la interfaz decida cómo mostrarlo (Text, Snackbar, notificación, etc.).

// Asigna el repartidor disponible más cercano (ordena por distancia y usa find)
fun asignarRepartidor(pedido: Pedido, repartidores: List<Repartidor>): Repartidor? {
    val elegido = repartidores.sortedBy { it.distanciaKm }.find { it.disponible }
    elegido?.entregarPedido(pedido)
    return elegido
}

// Mensaje de estado para el cliente; null si el pedido no tiene cliente registrado
fun mensajeEstado(pedido: Pedido): String? =
    pedido.cliente?.let { cliente ->
        "[Notificación para ${cliente.nombre}] Su pedido está: ${pedido.estado.etiqueta}"
    }

// Una línea por pedido con cliente, productos, total y estado
fun lineaReporte(pedido: Pedido): String {
    val productos = pedido.productos
        .groupingBy { it.nombre }
        .eachCount()
        .entries
        .joinToString(", ") { (nombre, cantidad) -> "${cantidad}x $nombre" }
    return "Cliente: ${pedido.nombreCliente()} | Productos: $productos | " +
        "Total: ${formatearPesos(pedido.calcularTotal())} | Estado: ${pedido.estado.etiqueta}"
}

// Reporte del día; "completados" = En camino, "pendientes" = En preparación o Listo
fun generarReporte(pedidos: List<Pedido>): List<String> {
    val completados = pedidos.count { it.estado == EstadoPedido.EN_CAMINO }
    return buildList {
        add("REPORTE DE PEDIDOS DEL DÍA")
        pedidos.forEach { add(lineaReporte(it)) }
        add("Completados: $completados | Pendientes: ${pedidos.size - completados}")
    }
}

// Resumen de confirmación con datos del cliente (desestructurados), productos, promociones y totales
fun generarResumen(pedido: Pedido): String {
    val (cliente, productos, total) = pedido
    val totales = calcularTotales(pedido)

    val datosCliente = cliente?.let { (nombre, correo, telefono) ->
        listOf("Cliente : $nombre", "Teléfono: $telefono", "Correo  : $correo")
    } ?: listOf("Cliente : Cliente sin registrar", "Teléfono: No registrado", "Correo  : No registrado")

    val lineasProductos = productos
        .groupBy { it.nombre }
        .map { (nombreProducto, lista) ->
            val precio = lista.first().precio
            "  - $nombreProducto | ${formatearPesos(precio)} x ${lista.size} = ${formatearPesos(precio * lista.size)}"
        }

    // distinctBy: una línea por producto promocionable, aunque se repita en el pedido
    val promociones = productos.distinctBy { it.nombre }.mapNotNull { p ->
        promocionDe(p)?.let { "  - ${p.nombre}: ${it.nombrePromocion} (${it.porcentaje}%)" }
    }

    val lineasDescuento = buildList {
        if (totales.descuentoCantidad > 0) {
            add("Descuento por cantidad ($PORCENTAJE_DESCUENTO_CANTIDAD%): -${formatearPesos(totales.descuentoCantidad)}")
        }
        if (totales.descuentoPromocional > 0) {
            add("Descuento promocional: -${formatearPesos(totales.descuentoPromocional)}")
        }
        if (totales.descuentoTemporada > 0) {
            add("Descuento temporada ($PORCENTAJE_TEMPORADA%): -${formatearPesos(totales.descuentoTemporada)}")
        }
        if (isEmpty()) add("Descuentos: no aplica")
    }

    return buildList {
        add("RESUMEN DEL PEDIDO")
        addAll(datosCliente)
        add("Notas   : ${pedido.notas ?: "Sin notas"}")
        add("Fecha   : ${pedido.fecha}")
        add("")
        add("Productos:")
        addAll(lineasProductos)
        if (promociones.isNotEmpty()) {
            add("Promociones identificadas:")
            addAll(promociones)
        }
        add("")
        add("Subtotal : ${formatearPesos(totales.subtotal)}")
        addAll(lineasDescuento)
        add("Impuestos (IVA ${(TASA_IVA * 100).roundToInt()}%): ${formatearPesos(totales.impuestos)}")
        add("TOTAL    : ${formatearPesos(total)}")
        add("")
        add("Estado   : ${pedido.estado.etiqueta}")
    }.joinToString("\n")
}
