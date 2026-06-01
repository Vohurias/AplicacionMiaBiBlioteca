package com.example.coneccionmarkdown

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.activity.enableEdgeToEdge
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import java.io.IOException
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack

import androidx.compose.material3.Scaffold

import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import dev.jeziellago.compose.markdowntext.MarkdownText
import io.github.teogor.composed.markdown.Markdown
import io.github.teogor.composed.markdown.MarkdownDefaults
data class ItemBiblioteca(
    val nombre: String,
    val descripcionBreve: String,
    val categoria: String,
    val nombreArchivoAssets: String, // Apunta directamente al archivo .md
    val iconoResId: Int
)

data class ItemMenu(
    val nombre: String,
    val descripcionBreve: String,
    val iconoOpcion: Int
)

// ====================================
//2. Repositorio de pa biblioteca - menu
///===============================
object Menu{
    val listaOpciones = listOf(
        ItemMenu(
            nombre ="Terminal",
            descripcionBreve = "Programas de terminal",
            iconoOpcion = R.drawable.terminal
        )
    )
}




// ==========================================
// 2. REPOSITORIO DE DATOS (Índice de la aplicación)
// ==========================================
object BibliotecaDatabase {
    val listaItems = listOf(
        ItemBiblioteca(
            nombre = "Hola Mundo",
            descripcionBreve = "Archivo de prueba inicial de la plataforma.",
            categoria = "Pruebas",
            nombreArchivoAssets = "hola_mundo.md",
            iconoResId = R.drawable.python
        ),
        ItemBiblioteca(
            nombre = "ls",
            descripcionBreve = "Lista el contenido de un directorio en el sistema.",
            categoria = "Archivos",
            nombreArchivoAssets = "ls.md",
            iconoResId = R.drawable.python
        ),
        ItemBiblioteca(
            nombre = "grep",
            descripcionBreve = "Busca patrones de texto dentro de archivos o salidas.",
            categoria = "Texto",
            nombreArchivoAssets = "grep.md",
            iconoResId = R.drawable.python
        ),
        ItemBiblioteca(
            nombre = "Bash",
            descripcionBreve = "comandos de bash",
            categoria = "terminal",
            nombreArchivoAssets = "Bash.md",
            iconoResId = R.drawable.python
        )

    )
}

// ==========================================
// 3. CONTROLADOR DE PANTALLAS (Navegación por Estado)
// ==========================================
sealed class Pantalla {
    object MenuPrincipal : Pantalla() // <-- 1. Agregada la nueva ruta del menú
    object Lista : Pantalla()
    data class VisorMarkdown(val item: ItemBiblioteca) : Pantalla()
}

// ==========================================
// 4. ACTIVIDAD PRINCIPAL
// ==========================================
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.BLACK)
        )
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black
                ) {
                    AppNavegacionBiblioteca()
                }
            }
        }
    }
}

@Composable
fun AppNavegacionBiblioteca() {
    var pantallaActual by remember { mutableStateOf<Pantalla>(Pantalla.MenuPrincipal) }

    when (val pantalla = pantallaActual) {
        is Pantalla.MenuPrincipal -> {
            // Si está en el menú principal y presiona atrás, no hacemos nada (deja que salga de la app)
        }
        is Pantalla.Lista -> {
            BackHandler {
                pantallaActual = Pantalla.MenuPrincipal // Si está en la lista, regresa al Menú
            }
        }
        is Pantalla.VisorMarkdown -> {
            BackHandler {
                pantallaActual = Pantalla.Lista // Si está viendo el Markdown, regresa a la Lista
            }
        }
    }


    when (val pantalla = pantallaActual) {
        is Pantalla.MenuPrincipal -> PantallaMenuOpciones(
            onOpcionSeleccionada = { opcion ->
                // Al presionar la opción "Terminal", navegamos a la lista de comandos
                if (opcion.nombre == "Terminal") {
                    pantallaActual = Pantalla.Lista
                }
            }
        )
        is Pantalla.Lista -> PantallaListaComandos(
            onItemSeleccionado = { item ->
                pantallaActual = Pantalla.VisorMarkdown(item)
            },
            onVolver = {
                pantallaActual = Pantalla.MenuPrincipal // Regresar al menú de opciones
            }
        )
        is Pantalla.VisorMarkdown -> PantallaLectorAssets(
            item = pantalla.item,
            onVolver = {
                pantallaActual = Pantalla.Lista
            }
        )
    }
}


// ==========================================
// 5. COMPONENTES DE LA INTERFAZ DE USUARIO (UI)
// ==========================================

@Composable
fun PantallaMenuOpciones(onOpcionSeleccionada: (ItemMenu) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(24.dp)
    ) {
        Text(
            text = "Menú Principal",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(bottom = 24.dp, top = 24.dp)
        )

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(Menu.listaOpciones) { opcion ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable { onOpcionSeleccionada(opcion) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Black), // Gris oscuro
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = opcion.iconoOpcion),
                            contentDescription = opcion.nombre,
                            modifier = Modifier
                                .size(48.dp)
                                .padding(end = 12.dp)
                        )
                        Column {
                            Text(
                                text = opcion.nombre,
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = opcion.descripcionBreve,
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaListaComandos(
    onItemSeleccionado: (ItemBiblioteca) -> Unit,
    onVolver: () -> Unit) {
    var textoBusqueda by remember { mutableStateOf("") }

    // Filtro dinámico según lo que escribas en la barra de búsqueda
    val itemsFiltrados = remember(textoBusqueda) {
        BibliotecaDatabase.listaItems.filter {
            it.nombre.contains(textoBusqueda, ignoreCase = true) ||
                    it.descripcionBreve.contains(textoBusqueda, ignoreCase = true) ||
                    it.categoria.contains(textoBusqueda, ignoreCase = true)
        }
    }

    Column(modifier = Modifier.fillMaxSize()
        .windowInsetsPadding(WindowInsets.systemBars)
        ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onVolver) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver al menú",
                    tint = Color.White
                )
            }
            Text(
                text = "Comandos Terminal",
                color = Color.White,
                fontSize = 20.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }
        // Barra de búsqueda funcional
        OutlinedTextField(
            value = textoBusqueda,
            onValueChange = { textoBusqueda = it },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.White,
                unfocusedTextColor = Color.Gray,
                focusedTextColor = Color.White,
                errorBorderColor = Color.Red,
            ),
            placeholder = { Text("Buscar comando o categoría...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true
        )

        // Renderizado de las tarjetas utilizando LazyColumn
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(itemsFiltrados) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clickable { onItemSeleccionado(item) }// Al dar clic, pasamos el objeto completo
                        ,
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Image(
                                painter = painterResource(id = item.iconoResId),
                                contentDescription = item.nombre,
                                modifier = Modifier
                                    .size(48.dp)
                                    .padding(end = 12.dp)
                            )

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {

                                    Text(
                                        text = item.nombre,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )

                                    SuggestionChip(
                                        onClick = {},
                                        label = {
                                            Text(
                                                item.categoria,
                                                fontSize = 11.sp
                                            )
                                        }
                                    )
                                }
                                Text(
                                    text = item.descripcionBreve
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaLectorAssets(item: ItemBiblioteca, onVolver: () -> Unit) {
    val contexto = LocalContext.current

    // Leemos dinámicamente el archivo que la tarjeta nos indicó en su propiedad
    val contenidoLeido = remember(item.nombreArchivoAssets) {
        leerArchivoMarkdownDesdeAssets(contexto, item.nombreArchivoAssets)
    }

    Scaffold(
        modifier = Modifier.windowInsetsPadding(WindowInsets.systemBars),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black, // Fondo negro
                    titleContentColor = Color.White // Texto blanco
                ),
                title = { Text(item.nombre, fontFamily = FontFamily.Monospace) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .background(Color.Black)
                .padding(16.dp)

        ) {
// Usamos la nueva librería que permite control absoluto de colores
            io.github.teogor.composed.markdown.Markdown(
                content = contenidoLeido,
                colors = MarkdownDefaults.markdownColors(
                    // 1. Aquí cambias el color de las letras normales de todo tu Markdown
                    text = Color.White,
                    codeText = Color(0xFF4FA847), // Verde Kotlin para el texto de código corto
                    linkText = Color.Cyan
                ),
                components = MarkdownDefaults.markdownComponents(
                    // 2. Personalizamos qué pasa cuando el Markdown detecta un bloque de código (``` o ~~~)
                    codeBlock = { code ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .background(
                                    color = Color(0xFF1E2429), // Fondo gris oscuro para el bloque de código
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            // Aquí se pinta el texto del bloque de código
                            // Si quieres resaltado de sintaxis real por palabras, necesitas un tokenizador manual:
                            Text(
                                text = code,
                                color = Color(0xFFF8F8F2), // Color de texto tipo temática Dracula/Monokai
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp
                            )
                        }
                    }
                )
            )
        }
    }
}
// Función para colorear palabras reservadas de comandos/código manualmente
fun colorearCodigo(codigo: String): androidx.compose.ui.text.AnnotatedString {
    val builder = androidx.compose.ui.text.AnnotatedString.Builder(codigo)

    // Lista de comandos o palabras clave que quieres que brillen en verde (Ej: comandos)
    val palabrasClaveVerdes = listOf("ls", "grep", "cd", "sudo", "apt", "bash", "clear")
    // Parámetros o flags que quieres que brillen en naranja/rojo
    val palabrasClaveNaranjas = listOf("-l", "-a", "-R", "--help", "-i", "-v")

    // 1. Pintar comandos en Verde
    palabrasClaveVerdes.forEach { palabra ->
        val regex = "\\b$palabra\\b".toRegex()
        regex.findAll(codigo).forEach { resultado ->
            builder.addStyle(
                style = androidx.compose.ui.text.SpanStyle(color = Color(0xFF4FA847), fontWeight = FontWeight.Bold),
                start = resultado.range.first,
                end = resultado.range.last + 1
            )
        }
    }

    // 2. Pintar flags en Naranja
    palabrasClaveNaranjas.forEach { flag ->
        val regex = " $flag\\b".toRegex()
        regex.findAll(codigo).forEach { resultado ->
            builder.addStyle(
                style = androidx.compose.ui.text.SpanStyle(color = Color(0xFFFFB86C)),
                start = resultado.range.first + 1,
                end = resultado.range.last + 1
            )
        }
    }

    return builder.toAnnotatedString()
}
// ==========================================
// 6. FUNCIÓN DE LECTURA DE ASSETS
// ==========================================
fun leerArchivoMarkdownDesdeAssets(context: Context, nombreArchivo: String): String {
    return try {
        context.assets.open(nombreArchivo).bufferedReader().use { it.readText() }
    } catch (e: IOException) {
        "Error: No se pudo encontrar o leer el archivo '$nombreArchivo' en la carpeta assets."
    }
}