package com.example.pizzeriaapp.model

import com.example.pizzeriaapp.util.validarCorreo
import com.example.pizzeriaapp.util.validarTelefono
import java.util.Objects

// Clase base con atributos comunes; los datos de contacto se validan al construir el objeto
open class Usuario(
    val nombre: String,
    telefonoIngresado: String?,
    correoIngresado: String?
) {
    val telefono: String = validarTelefono(telefonoIngresado)
    val correo: String = validarCorreo(correoIngresado)

    open fun rol(): String = "Usuario"

    fun contacto(): String = "$telefono / $correo"
}

class Cliente(
    nombre: String,
    telefono: String?,
    correo: String?
) : Usuario(nombre, telefono, correo) {

    override fun rol(): String = "Cliente"

    // Dos clientes son iguales si coinciden nombre y correo
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Cliente) return false
        return nombre == other.nombre && correo == other.correo
    }

    override fun hashCode(): Int = Objects.hash(nombre, correo)

    // Desestructuración: val (nombre, correo, telefono) = cliente
    operator fun component1(): String = nombre
    operator fun component2(): String = correo
    operator fun component3(): String = telefono
}

class Repartidor(
    nombre: String,
    telefono: String?,
    correo: String?,
    val distanciaKm: Double,
    var disponible: Boolean = true
) : Usuario(nombre, telefono, correo) {

    override fun rol(): String = "Repartidor"

    // Asigna el pedido: cambia su estado a "En camino" y marca al repartidor como ocupado.
    // Adaptación a Android: devuelve el mensaje en lugar de imprimirlo en consola, para mostrarlo en la interfaz.
    fun entregarPedido(pedido: Pedido): String {
        pedido.cambiarEstado(EstadoPedido.EN_CAMINO)
        disponible = false
        return "$nombre entrega el pedido de ${pedido.nombreCliente()}."
    }
}
