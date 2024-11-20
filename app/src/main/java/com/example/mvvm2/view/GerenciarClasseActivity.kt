package com.example.mvvm2.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.lazy.LazyColumn
import com.example.mvvm2.viewmodel.ClasseViewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import android.widget.Toast
import androidx.activity.viewModels
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import com.example.mvvm2.model.database.AppDatabase
import com.example.mvvm2.viewmodel.factory.ClasseViewModelFactory

class GerenciarClasseActivity : ComponentActivity() {
    private val viewModel: ClasseViewModel by viewModels {
        val database = AppDatabase.getDatabase(applicationContext)
        ClasseViewModelFactory(database.classeDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GerenciarClasseScreen(viewModel)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.buscarTodasAsClasses()
        // Reexecuta as buscas se houver um termo de busca ativo
        if (viewModel.buscaNome.value.isNotEmpty()) {
            viewModel.buscarPorNome(viewModel.buscaNome.value)
        }
        if (viewModel.buscaVariante.value.isNotEmpty()) {
            viewModel.buscarPorVariante(viewModel.buscaVariante.value)
        }
    }
}

@Composable
fun GerenciarClasseScreen(classeViewModel: ClasseViewModel) {

    // Campos de cadastro
    var nome by remember { mutableStateOf("") }
    var variante by remember { mutableStateOf("") }

    // Estados para os menus colapsáveis
    var cadastrarClasseExpanded by remember { mutableStateOf(false) }
    var buscarTodosExpanded by remember { mutableStateOf(false) }
    var buscarPorNomeExpanded by remember { mutableStateOf(false) }
    var buscarPorVarianteExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Gerenciar Classes", style = MaterialTheme.typography.titleLarge)

        // Menu para Cadastrar Classe
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Cadastrar Classe", modifier = Modifier.align(Alignment.CenterVertically))
            IconButton(onClick = { cadastrarClasseExpanded = !cadastrarClasseExpanded }) {
                val arrowIcon = if (cadastrarClasseExpanded) {
                    Icons.Filled.KeyboardArrowUp
                } else {
                    Icons.Filled.KeyboardArrowDown
                }
                Icon(imageVector = arrowIcon, contentDescription = "Expandir/Colapsar")
            }
        }
        if (cadastrarClasseExpanded) {
            CadastroClasseMenu(
                classeViewModel = classeViewModel,
                nome = nome,
                setNome = { nome = it },
                variante = variante,
                setVariante = { variante = it }
            )
        }
        // Menu para Buscar por Nome
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Buscar por Nome", modifier = Modifier.align(Alignment.CenterVertically))
            IconButton(onClick = { buscarPorNomeExpanded = !buscarPorNomeExpanded }) {
                val arrowIcon = if (buscarPorNomeExpanded) {
                    Icons.Filled.KeyboardArrowUp
                } else {
                    Icons.Filled.KeyboardArrowDown
                }
                Icon(imageVector = arrowIcon, contentDescription = "Expandir/Colapsar")
            }
        }
        if (buscarPorNomeExpanded) {
            // Agora, chamamos sem os parâmetros buscaNome e setBuscaNome
            BuscaPorNome(classeViewModel = classeViewModel)
        }

        // Menu para Buscar por Variante
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Buscar por Variante", modifier = Modifier.align(Alignment.CenterVertically))
            IconButton(onClick = { buscarPorVarianteExpanded = !buscarPorVarianteExpanded }) {
                val arrowIcon = if (buscarPorVarianteExpanded) {
                    Icons.Filled.KeyboardArrowUp
                } else {
                    Icons.Filled.KeyboardArrowDown
                }
                Icon(imageVector = arrowIcon, contentDescription = "Expandir/Colapsar")
            }
        }
        if (buscarPorVarianteExpanded) {
            // Agora, chamamos sem os parâmetros buscaHabilidade e setBuscaHabilidade
            BuscaPorVariante(classeViewModel = classeViewModel)
        }

        // Menu para Mostrar todos
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Mostrar Todos", modifier = Modifier.align(Alignment.CenterVertically))
            IconButton(onClick = { buscarTodosExpanded = !buscarTodosExpanded }) {
                val arrowIcon = if (buscarTodosExpanded) {
                    Icons.Filled.KeyboardArrowUp
                } else {
                    Icons.Filled.KeyboardArrowDown
                }
                Icon(imageVector = arrowIcon, contentDescription = "Expandir/Colapsar")
            }
        }
        if (buscarTodosExpanded) {
            ListaClasses(classeViewModel = classeViewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CadastroClasseMenu(classeViewModel: ClasseViewModel, nome: String, setNome: (String) -> Unit, variante: String, setVariante: (String) -> Unit) {
    val context = LocalContext.current
    Column(modifier = Modifier.padding(8.dp)) {
        TextField(
            value = nome,
            onValueChange = setNome,
            label = { Text("Nome") },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = variante,
            onValueChange = setVariante,
            label = { Text("Variante") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )
        Button(
            onClick = {
                classeViewModel.salvarClasse(nome, variante)
                setNome("")  // Limpa o campo 'Nome'
                setVariante("")  // Limpa o campo 'Variante'
                Toast.makeText(context, "Classe cadastrada com sucesso!", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Criar")
        }
    }
    Divider(color = Color(0xFF8A2BE2), thickness = 1.dp) // Linha roxa
}

@Composable
fun ListaClasses(classeViewModel: ClasseViewModel) {
    val context = LocalContext.current
    val listaClasses by classeViewModel.listaClasses

    if (listaClasses.isNotEmpty()) {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(top = 16.dp)) {
            items(listaClasses) { classe ->
                Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                    Text(text = "Nome: ${classe.nome}", modifier = Modifier.weight(1f))
                    Text(text = "Variante: ${classe.variante}")
                }
                Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                    // Botão Excluir
                    Button(
                        onClick = { classeViewModel.excluirClasse(classe) },
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text("Excluir")
                    }
                    // Botão Editar
                    Button(
                        onClick = {
                            val intent = Intent(context, AtualizarClasseActivity::class.java).apply {
                                putExtra("CLASSE_ID", classe.id)
                                putExtra("CLASSE_NOME", classe.nome)
                                putExtra("CLASSE_VARIANTE", classe.variante)
                            }
                            context.startActivity(intent)
                        },
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text("Editar")
                    }
                }
            }
        }
    } else {
        Text("Nenhuma classe encontrada.", modifier = Modifier.padding(8.dp))
    }
    Divider(color = Color(0xFF8A2BE2), thickness = 1.dp) // Linha roxa
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuscaPorNome(classeViewModel: ClasseViewModel) {
    val context = LocalContext.current
    val buscaNome by classeViewModel.buscaNome
    val setBuscaNome: (String) -> Unit = { classeViewModel.setBuscaNome(it) }
    val listaClassesPorNome by classeViewModel.listaClassesPorNome

    Column(modifier = Modifier.padding(8.dp)) {
        TextField(
            value = buscaNome,
            onValueChange = setBuscaNome,
            label = { Text("Nome") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = { classeViewModel.buscarPorNome(buscaNome) },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Buscar")
        }

        if (listaClassesPorNome.isNotEmpty()) {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(top = 16.dp)) {
                items(listaClassesPorNome) { classe ->
                    Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                        Text(text = "Nome: ${classe.nome}", modifier = Modifier.weight(1f))
                        Text(text = "Variante: ${classe.variante}")
                    }
                    Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                        // Botão Excluir
                        Button(
                            onClick = {
                                classeViewModel.excluirClasse(classe)
                            },
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Text("Excluir")
                        }
                        // Botão Editar
                        Button(
                            onClick = {
                                val intent = Intent(context, AtualizarRacaActivity::class.java).apply {
                                    putExtra("CLASSE_ID", classe.id)
                                    putExtra("CLASSE_NOME", classe.nome)
                                    putExtra("CLASSE_VARIANTE", classe.variante)
                                }
                                context.startActivity(intent)
                            },
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Text("Editar")
                        }
                    }
                }
            }
        } else {
            Text("Nenhuma classe encontrada.", modifier = Modifier.padding(8.dp))
        }
    }
    Divider(color = Color(0xFF8A2BE2), thickness = 1.dp)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuscaPorVariante(classeViewModel: ClasseViewModel) {
    val context = LocalContext.current
    val buscaVariante by classeViewModel.buscaVariante
    val setBuscaVariante: (String) -> Unit = { classeViewModel.setBuscaVariante(it) }
    val listaClassesPorVariante by classeViewModel.listaClassesPorVariante

    Column(modifier = Modifier.padding(8.dp)) {
        TextField(
            value = buscaVariante,
            onValueChange = setBuscaVariante,
            label = { Text("Classe Variante") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = { classeViewModel.buscarPorVariante(buscaVariante) },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Buscar")
        }

        if (listaClassesPorVariante.isNotEmpty()) {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(top = 16.dp)) {
                items(listaClassesPorVariante) { classe ->
                    Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                        Text(text = "Nome: ${classe.nome}", modifier = Modifier.weight(1f))
                        Text(text = "Variante: ${classe.variante}")
                    }
                    Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                        // Botão Excluir
                        Button(
                            onClick = {
                                classeViewModel.excluirClasse(classe)
                            },
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Text("Excluir")
                        }
                        // Botão Editar
                        Button(
                            onClick = {
                                val intent = Intent(context, AtualizarRacaActivity::class.java).apply {
                                    putExtra("CLASSE_ID", classe.id)
                                    putExtra("CLASSE_NOME", classe.nome)
                                    putExtra("CLASSE_VARIANTE", classe.variante)
                                }
                                context.startActivity(intent)
                            },
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Text("Editar")
                        }
                    }
                }
            }
        } else {
            Text("Nenhuma classe encontrada.", modifier = Modifier.padding(8.dp))
        }
    }
    Divider(color = Color(0xFF8A2BE2), thickness = 1.dp)
}