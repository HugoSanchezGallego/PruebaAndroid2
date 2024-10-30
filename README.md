# PruebaAndroid2 - Lista de Compra

## Enlace del repositorio --> [https://github.com/HugoSanchezGallego/PruebaAndroid2.git)

Este proyecto de Android en Kotlin implementa una lista de compra utilizando Firebase Firestore para almacenamiento en la nube. El usuario puede añadir productos, visualizar el total de productos y el precio total de la compra. La interfaz está desarrollada usando Jetpack Compose.

## Estructura de Clases

### 1. MainActivity

`MainActivity` es la actividad principal de la aplicación. Configura Firebase y carga el tema principal y la interfaz de usuario.

```kotlin
class MainActivity : ComponentActivity() {
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        db = FirebaseFirestore.getInstance()
        enableEdgeToEdge()
        setContent {
            PruebaAndroid2Theme {
                PantallaListaCompra(db)
            }
        }
    }
}
```

#### Métodos

- `onCreate`: Inicializa Firebase y configura la interfaz. Carga `PantallaListaCompra`, pasando una instancia de `FirebaseFirestore` como parámetro.

### 2. PantallaListaCompra

`PantallaListaCompra` es una función `@Composable` que define la interfaz de usuario para la lista de compra, permitiendo al usuario agregar productos con nombre, cantidad y precio.

```kotlin
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
```

#### Propiedades

- `nombreProducto`: Nombre del producto ingresado por el usuario.
- `cantidadProducto`: Cantidad del producto (opcional).
- `precioProducto`: Precio del producto (opcional).
- `productos`: Lista de productos obtenida de Firestore.
- `precioTotal`: Precio total de la lista de compra, calculado en función de los productos almacenados.

#### Elementos de la Interfaz

- `BasicTextField`: Campos para ingresar el nombre, cantidad y precio del producto.
- `Button`: Botón "Añadir Producto" para almacenar el producto en Firestore.
- `Text`: Muestra el total de productos y el precio total.
- `LazyColumn`: Lista dinámica que muestra los productos añadidos.

#### Métodos

- `onClick` (dentro de `Button`): Convierte los valores ingresados de `cantidadProducto` y `precioProducto` en enteros y dobles, respectivamente. Si los valores son válidos, crea una instancia de `Producto` y la almacena en Firestore. Luego, restablece los campos de entrada.
- `LaunchedEffect`: Configura un `addSnapshotListener` para Firestore que escucha actualizaciones en la colección de productos y recalcula `productos` y `precioTotal`.

### 3. Producto

`Producto` es una clase de datos que representa un producto en la lista de compra.

```kotlin
data class Producto(
    val nombre: String = "",
    val cantidad: Int = 0,
    val precio: Double = 0.0
)
```

#### Propiedades

- `nombre`: Nombre del producto.
- `cantidad`: Cantidad del producto (predeterminado a 0).
- `precio`: Precio unitario del producto (predeterminado a 0.0).

## Dependencias Externas

- `FirebaseApp`: Inicializa Firebase en la aplicación.
- `FirebaseFirestore`: Proporciona acceso a Firestore para almacenar y leer los datos de los productos.

## Ejecución y Funcionalidad

La aplicación permite al usuario:

- Ingresar el nombre, cantidad y precio de un producto.
- Guardar los productos en Firestore mediante el botón "Añadir Producto".
- Visualizar la lista de productos almacenados junto con el total de productos y el precio total calculado.

## Requerimientos Previos

Asegúrese de configurar Firebase Firestore en su proyecto de Firebase y de agregar la configuración correspondiente a `google-services.json` en el proyecto de Android.
