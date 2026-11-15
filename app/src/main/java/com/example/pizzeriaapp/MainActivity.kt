package com.example.pizzeriaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pizzeriaapp.data.DatosDemo
import com.example.pizzeriaapp.domain.Totales
import com.example.pizzeriaapp.domain.promocionDe
import com.example.pizzeriaapp.model.Pedido
import com.example.pizzeriaapp.model.Producto
import com.example.pizzeriaapp.ui.theme.PizzeriaAppTheme
import com.example.pizzeriaapp.util.formatearPesos

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PizzeriaAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PantallaInicio(contentPadding = innerPadding)
                }
            }
        }
    }
}

// Mensaje de bienvenida solicitado en la actividad
@Composable
fun Greeting(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.mensaje_bienvenida),
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = modifier.fillMaxWidth()
    )
}

// Pantalla básica: bienvenida, menú y un pedido de ejemplo calculado con la lógica de las semanas anteriores
@Composable
fun PantallaInicio(contentPadding: PaddingValues = PaddingValues()) {
    // remember: el pedido y sus totales se calculan una sola vez, no en cada recomposición
    val pedido = remember { DatosDemo.pedidoEjemplo() }
    val totales = remember(pedido) { pedido.totales() }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Greeting(modifier = Modifier.padding(vertical = 8.dp)) }
        item { TituloSeccion(stringResource(R.string.titulo_menu)) }
        items(DatosDemo.menu, key = { it.nombre }) { producto -> TarjetaProducto(producto) }
        item { TituloSeccion(stringResource(R.string.titulo_pedido_ejemplo)) }
        item { TarjetaPedido(pedido, totales) }
    }
}

@Composable
fun TituloSeccion(texto: String) {
    Text(text = texto, style = MaterialTheme.typography.titleLarge)
}

@Composable
fun TarjetaProducto(producto: Producto) {
    val promocion = remember(producto) { promocionDe(producto) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            FilaTexto(producto.nombre, formatearPesos(producto.precio), destacado = true)
            Text(text = producto.categoria.etiqueta, style = MaterialTheme.typography.bodyMedium)
            if (producto.ingredientes.isNotEmpty()) {
                Text(
                    text = producto.ingredientes.joinToString(", "),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            // Los productos anotados con @Promocionable muestran su promoción
            promocion?.let {
                Text(
                    text = stringResource(R.string.etiqueta_promocion, it.nombrePromocion, it.porcentaje),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun TarjetaPedido(pedido: Pedido, totales: Totales) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = stringResource(R.string.etiqueta_cliente, pedido.nombreCliente()))
            Text(text = stringResource(R.string.etiqueta_estado, pedido.estado.etiqueta))
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            // Productos agrupados: "2 x Pepperoni"
            pedido.productos.groupBy { it.nombre }.forEach { (nombre, lista) ->
                FilaTexto("${lista.size} x $nombre", formatearPesos(lista.sumOf { it.precio }))
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            FilaTexto(stringResource(R.string.etiqueta_subtotal), formatearPesos(totales.subtotal))
            FilaTexto(stringResource(R.string.etiqueta_descuentos), "-" + formatearPesos(totales.descuentoTotal))
            FilaTexto(stringResource(R.string.etiqueta_iva), formatearPesos(totales.impuestos))
            FilaTexto(stringResource(R.string.etiqueta_total), formatearPesos(totales.total), destacado = true)
        }
    }
}

// Fila con un texto a la izquierda y un valor a la derecha
@Composable
fun FilaTexto(etiqueta: String, valor: String, destacado: Boolean = false) {
    val peso = if (destacado) FontWeight.Bold else FontWeight.Normal
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = etiqueta, fontWeight = peso, modifier = Modifier.weight(1f))
        Text(text = valor, fontWeight = peso)
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PizzeriaAppTheme {
        Greeting()
    }
}

@Preview(showBackground = true)
@Composable
fun PantallaInicioPreview() {
    PizzeriaAppTheme {
        PantallaInicio()
    }
}
