package com.example.pizzeriaapp.data

// Registro sin duplicados: el LinkedHashSet usa equals/hashCode de la entidad.
// registrar() devuelve false cuando el elemento ya existía.
class RegistroUnico<T> {
    private val elementos = linkedSetOf<T>()

    val cantidad: Int get() = elementos.size

    fun registrar(elemento: T): Boolean = elementos.add(elemento)

    fun listar(): List<T> = elementos.toList()
}
