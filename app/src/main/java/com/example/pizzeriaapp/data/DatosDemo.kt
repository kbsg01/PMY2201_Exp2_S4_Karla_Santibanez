package com.example.pizzeriaapp.data

import com.example.pizzeriaapp.model.Cliente
import com.example.pizzeriaapp.model.EstadoPedido
import com.example.pizzeriaapp.model.Pedido
import com.example.pizzeriaapp.model.Producto
import java.time.LocalDate

// Datos de ejemplo para la pantalla inicial. En las próximas semanas vendrán de formularios y almacenamiento.
object DatosDemo {
    val menu: List<Producto> = listOf(
        PizzaMargarita(), PizzaPepperoni(), PizzaHawaiana(), PizzaVeggie(), BebidaLitro()
    )

    fun pedidoEjemplo(): Pedido = Pedido(
        cliente = Cliente("Ana Morales", "+56 9 8765 4321", "ana.morales@gmail.com"),
        productos = listOf(PizzaPepperoni(), PizzaPepperoni(), PizzaMargarita(), BebidaLitro()),
        estado = EstadoPedido.LISTO,
        fecha = LocalDate.of(2026, 11, 13)
    )
}
