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
import dev.jeziellago.compose.markdowntext.MarkdownText
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

data class ItemBiblioteca(
    val nombre: String,
    val descripcionBreve: String,
    val categoria: String,
    val nombreArchivoAssets: String, // Apunta directamente al archivo .md
    val iconoResId: Int
)

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
    object Lista : Pantalla()
    data class VisorMarkdown(val item: ItemBiblioteca) : Pantalla()
}

// ==========================================
// 4. ACTIVIDAD PRINCIPAL
// ==========================================
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
    var pantallaActual by remember { mutableStateOf<Pantalla>(Pantalla.Lista) }

    when (val pantalla = pantallaActual) {
        is Pantalla.Lista -> PantallaListaComandos(
            onItemSeleccionado = { item ->
                pantallaActual = Pantalla.VisorMarkdown(item)
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaListaComandos(onItemSeleccionado: (ItemBiblioteca) -> Unit) {
    var textoBusqueda by remember { mutableStateOf("") }

    // Filtro dinámico según lo que escribas en la barra de búsqueda
    val itemsFiltrados = remember(textoBusqueda) {
        BibliotecaDatabase.listaItems.filter {
            it.nombre.contains(textoBusqueda, ignoreCase = true) ||
                    it.descripcionBreve.contains(textoBusqueda, ignoreCase = true) ||
                    it.categoria.contains(textoBusqueda, ignoreCase = true)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
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
        topBar = {
            TopAppBar(
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
                .padding(16.dp)

        ) {
            // Pintamos el Markdown del archivo correspondiente
            MarkdownText(
                markdown = contenidoLeido,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
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