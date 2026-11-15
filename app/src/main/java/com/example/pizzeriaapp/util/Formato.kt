package com.example.pizzeriaapp.util

// Formato chileno: $12.990
fun formatearPesos(monto: Int): String =
    "\$" + monto.toString().reversed().chunked(3).joinToString(".").reversed()
