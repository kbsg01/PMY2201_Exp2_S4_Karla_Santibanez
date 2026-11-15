package com.example.pizzeriaapp.data

import com.example.pizzeriaapp.domain.Promocionable
import com.example.pizzeriaapp.model.Categoria
import com.example.pizzeriaapp.model.Producto

// Cada producto es una clase: así la anotación @Promocionable identifica cada elemento del menú

class PizzaMargarita : Producto("Margarita", 7990, Categoria.PIZZA, listOf("masa", "salsa de tomate", "queso mozzarella", "albahaca"))

@Promocionable("Pizza del mes", 15)
class PizzaPepperoni : Producto("Pepperoni", 9990, Categoria.PIZZA, listOf("masa", "salsa de tomate", "queso mozzarella", "pepperoni"))

class PizzaHawaiana : Producto("Hawaiana", 9490, Categoria.PIZZA, listOf("masa", "salsa de tomate", "queso mozzarella", "jamón", "piña"))

class PizzaVeggie : Producto("Veggie", 10490, Categoria.PIZZA, listOf("masa", "salsa de tomate", "queso mozzarella", "champiñones", "aceitunas"))

@Promocionable("Bebida en combo", 10)
class BebidaLitro : Producto("Bebida 1,5 L", 2490, Categoria.BEBIDA)

class IngredienteExtra(nombre: String, precio: Int, ingrediente: String) :
    Producto(nombre, precio, Categoria.INGREDIENTE, listOf(ingrediente))
