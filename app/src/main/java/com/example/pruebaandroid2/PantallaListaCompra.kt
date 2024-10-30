package com.example.pruebaandroid2

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun PantallaListaCompra(db: FirebaseFirestore) {
    var nombreProducto by remember { mutableStateOf("") }
    var cantidadProducto by remember { mutableStateOf("") }
    var precioProducto by remember { mutableStateOf("") }
    var productos by remember { mutableStateOf(listOf<Producto>()) }
    var precioTotal by remember { mutableStateOf(0.0) }

    Column(
        modifier = Modifier
            .padding(top = 48.dp, start = 16.dp, end = 16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BasicTextField(
            value = nombreProducto,
            onValueChange = { nombreProducto = it },
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally),
            decorationBox = { innerTextField ->
                if (nombreProducto.isEmpty()) {
                    Text("Nombre del Producto")
                }
                innerTextField()
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        BasicTextField(
            value = cantidadProducto,
            onValueChange = { cantidadProducto = it },
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally),
            decorationBox = { innerTextField ->
                if (cantidadProducto.isEmpty()) {
                    Text("Cantidad (opcional)")
                }
                innerTextField()
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        BasicTextField(
            value = precioProducto,
            onValueChange = { precioProducto = it },
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally),
            decorationBox = { innerTextField ->
                if (precioProducto.isEmpty()) {
                    Text("Precio (opcional)")
                }
                innerTextField()
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                val cantidad = cantidadProducto.toIntOrNull() ?: 0
                val precio = precioProducto.toDoubleOrNull() ?: 0.0
                if (nombreProducto.isNotEmpty() && cantidad >= 0 && precio >= 0.0) {
                    val producto = Producto(
                        nombre = nombreProducto,
                        cantidad = cantidad,
                        precio = precio
                    )
                    db.collection("productos").add(producto)
                    nombreProducto = ""
                    cantidadProducto = ""
                    precioProducto = ""
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Añadir Producto")
        }
        Spacer(modifier = Modifier.height(32.dp))
        Text("Total de Productos: ${productos.size}", modifier = Modifier.align(Alignment.CenterHorizontally))
        Text("Precio Total: $precioTotal", modifier = Modifier.align(Alignment.CenterHorizontally))
        LazyColumn(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            items(productos) { producto ->
                Text("${producto.nombre} - ${producto.cantidad} - ${producto.precio}")
            }
        }
    }

    LaunchedEffect(Unit) {
        db.collection("productos").addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                productos = snapshot.toObjects(Producto::class.java)
                precioTotal = productos.filter { it.precio > 0.0 }.sumOf { it.precio * it.cantidad }
            }
        }
    }
}