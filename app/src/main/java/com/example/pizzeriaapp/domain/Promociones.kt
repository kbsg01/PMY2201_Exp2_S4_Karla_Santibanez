package com.example.pizzeriaapp.domain

import com.example.pizzeriaapp.model.Producto

// Identifica pizzas o combos que califican para promociones especiales.
// RUNTIME: visible para reflection durante la ejecución.
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@MustBeDocumented
annotation class Promocionable(val nombrePromocion: String, val porcentaje: Int)

// Lee la anotación de la clase real del producto (null si no es elegible).
// Usa reflection de Java, disponible en Android sin agregar la librería kotlin-reflect.
fun promocionDe(producto: Producto): Promocionable? =
    producto::class.java.getAnnotation(Promocionable::class.java)

fun productosPromocionables(productos: List<Producto>): List<Producto> =
    productos.filter { promocionDe(it) != null }
