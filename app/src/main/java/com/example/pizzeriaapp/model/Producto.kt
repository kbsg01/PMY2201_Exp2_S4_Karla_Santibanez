package com.example.pizzeriaapp.model

import com.example.pizzeriaapp.util.formatearPesos
import java.util.Objects

// Producto del menú. En Kotlin, `==` invoca equals(); por eso se sobrescribe para comparar nombre y categoría.
open class Producto(
    val nombre: String,
    val precio: Int,
    val categoria: Categoria,
    val ingredientes: List<String> = emptyList()
) {
    fun detallarProducto(): String {
        val ingr = if (ingredientes.isEmpty()) "ninguno" else ingredientes.joinToString(", ")
        return "$nombre (${categoria.etiqueta}) - ${formatearPesos(precio)} | Ingredientes: $ingr"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Producto) return false
        return nombre.equals(other.nombre, ignoreCase = true) && categoria == other.categoria
    }

    // Debe ser coherente con equals: mismo nombre (sin mayúsculas) y categoría
    override fun hashCode(): Int = Objects.hash(nombre.lowercase(), categoria)
}
