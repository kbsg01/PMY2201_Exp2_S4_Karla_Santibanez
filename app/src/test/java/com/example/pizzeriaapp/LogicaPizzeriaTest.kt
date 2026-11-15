package com.example.pizzeriaapp

import com.example.pizzeriaapp.data.BebidaLitro
import com.example.pizzeriaapp.data.DatosDemo
import com.example.pizzeriaapp.data.IngredienteExtra
import com.example.pizzeriaapp.data.PizzaHawaiana
import com.example.pizzeriaapp.data.PizzaMargarita
import com.example.pizzeriaapp.data.PizzaPepperoni
import com.example.pizzeriaapp.data.PizzaVeggie
import com.example.pizzeriaapp.data.RegistroUnico
import com.example.pizzeriaapp.data.aceptarPedido
import com.example.pizzeriaapp.domain.asignarRepartidor
import com.example.pizzeriaapp.domain.generarResumen
import com.example.pizzeriaapp.domain.mensajeEstado
import com.example.pizzeriaapp.domain.promocionDe
import com.example.pizzeriaapp.model.Cliente
import com.example.pizzeriaapp.model.EstadoPedido
import com.example.pizzeriaapp.model.Pedido
import com.example.pizzeriaapp.model.Repartidor
import com.example.pizzeriaapp.util.CORREO_PREDETERMINADO
import com.example.pizzeriaapp.util.formatearPesos
import com.example.pizzeriaapp.util.formatearTelefono
import com.example.pizzeriaapp.util.validarCantidad
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

// Pruebas locales (JVM) de la lógica importada desde las semanas 1 a 3.
class LogicaPizzeriaTest {

    @Test
    fun totalesDelPedidoDeEjemplo() {
        val totales = DatosDemo.pedidoEjemplo().totales()
        // 9.990 + 9.990 + 7.990 + 2.490
        assertEquals(30460, totales.subtotal)
        assertEquals(0, totales.descuentoCantidad)        // 3 pizzas: no supera el umbral
        assertEquals(3246, totales.descuentoPromocional)  // 15 % de dos Pepperoni + 10 % de la bebida
        assertEquals(1523, totales.descuentoTemporada)    // 5 %: la fecha está dentro del periodo
        assertEquals(4881, totales.impuestos)             // IVA 19 % sobre el neto (25.691)
        assertEquals(30572, totales.total)
    }

    @Test
    fun descuentoPorCantidadConMasDeTresPizzas() {
        val pedido = Pedido(null, List(4) { PizzaHawaiana() }, fecha = LocalDate.of(2026, 10, 30))
        val totales = pedido.totales()
        assertEquals(3796, totales.descuentoCantidad)
        assertEquals(0, totales.descuentoTemporada)       // fuera del periodo de promoción
    }

    @Test
    fun anotacionPromocionableSeLeePorReflection() {
        assertEquals("Pizza del mes", promocionDe(PizzaPepperoni())?.nombrePromocion)
        assertNull(promocionDe(PizzaMargarita()))
    }

    @Test
    fun igualdadYSobrecargaDeOperadores() {
        assertEquals(PizzaPepperoni(), PizzaPepperoni())
        assertFalse(PizzaPepperoni() == IngredienteExtra("Pepperoni", 1500, "pepperoni"))

        val combinado = Pedido(null, listOf(PizzaMargarita())) + Pedido(null, listOf(BebidaLitro()))
        assertEquals(2, combinado.productos.size)

        val registro = RegistroUnico<Cliente>()
        assertTrue(registro.registrar(Cliente("Ana Morales", null, "ana.morales@gmail.com")))
        assertFalse(registro.registrar(Cliente("Ana Morales", "987654321", "ANA.MORALES@gmail.com")))
        assertEquals(1, registro.cantidad)
    }

    @Test
    fun validacionesDeContactoYCantidad() {
        assertEquals("+56 (987) 654-321", formatearTelefono("+56 9 8765 4321"))
        assertNull(formatearTelefono("123"))
        assertEquals(CORREO_PREDETERMINADO, Cliente("Pedro", null, "pedro-soto@correo").correo)
        assertEquals(4, validarCantidad(" 4 "))
        assertNull(validarCantidad("150"))
        assertEquals("$12.990", formatearPesos(12990))
    }

    @Test
    fun inventarioRechazaPedidoSinIngredientes() {
        val inventario = mutableMapOf(
            "masa" to 5, "salsa de tomate" to 5, "queso mozzarella" to 5, "champiñones" to 2, "aceitunas" to 0
        )
        val rechazado = aceptarPedido(Pedido(null, listOf(PizzaVeggie())), inventario)
        assertFalse(rechazado.aceptado)
        assertEquals("aceitunas", rechazado.faltantes.single().ingrediente)

        inventario["aceitunas"] = 3
        assertTrue(aceptarPedido(Pedido(null, listOf(PizzaVeggie())), inventario).aceptado)
        assertEquals(4, inventario["masa"])
    }

    @Test
    fun asignacionDeRepartidorYNotificacion() {
        val pedido = DatosDemo.pedidoEjemplo()
        val repartidores = listOf(
            Repartidor("Luis Rojas", "9 8765 4321", "luis.r@gmail.com", distanciaKm = 3.5),
            Repartidor("Marta Vidal", null, "marta@pizzeria.cl", distanciaKm = 1.2)
        )
        val elegido = asignarRepartidor(pedido, repartidores)
        assertEquals("Marta Vidal", elegido?.nombre)
        assertEquals(EstadoPedido.EN_CAMINO, pedido.estado)
        assertNotNull(mensajeEstado(pedido))
        assertTrue(generarResumen(pedido).contains("TOTAL    : $30.572"))
    }
}
