package com.example.mvvm2.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.mvvm2.model.database.AppDatabase
import com.example.mvvm2.viewmodel.RacaViewModel
import com.example.mvvm2.viewmodel.factory.RacaViewModelFactory

class GerenciarRacaActivity : ComponentActivity() {
    private val viewModel: RacaViewModel by viewModels {
        val database = AppDatabase.getDatabase(applicationContext)
        RacaViewModelFactory(database.racaDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GerenciarRacaScreen(viewModel)
        }
    }
    //isso aqui é novo para nos. Para atualizar a lista raças quando for feito um update em AtualizarRacaActivity
    //iremos precisar atualizar as listas novamente quando retornarmos do AtualizarRacaActivity
    override fun onResume() {
        super.onResume()
        viewModel.buscarTodasAsRacas()
        // Reexecuta as buscas se houver um termo de busca ativo
        if (viewModel.buscaNome.value.isNotEmpty()) {
            viewModel.buscarPorNome(viewModel.buscaNome.value)
        }
        if (viewModel.buscaHabilidade.value.isNotEmpty()) {
            viewModel.buscarPorHabilidadeEspecifica(viewModel.buscaHabilidade.value)
        }

    }
}

@Composable
fun GerenciarRacaScreen(racaViewModel: RacaViewModel) {

    // Campos de cadastro
    var nome by remember { mutableStateOf("") }
    var habilidadeEspecifica by remember { mutableStateOf("") }

    // Estados para os menus colapsáveis
    var cadastrarRacaExpanded by remember { mutableStateOf(false) }
    var buscarTodosExpanded by remember { mutableStateOf(false) }
    var buscarPorNomeExpanded by remember { mutableStateOf(false) }
    var buscarPorHabilidadeExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Gerenciar Raças", style = MaterialTheme.typography.titleLarge)

        // Menu para Cadastrar Raça
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Cadastrar Raça", modifier = Modifier.align(Alignment.CenterVertically))
            IconButton(onClick = { cadastrarRacaExpanded = !cadastrarRacaExpanded }) {
                val arrowIcon = if (cadastrarRacaExpanded) {
                    Icons.Filled.KeyboardArrowUp
                } else {
                    Icons.Filled.KeyboardArrowDown
                }
                Icon(imageVector = arrowIcon, contentDescription = "Expandir/Colapsar")
            }
        }
        if (cadastrarRacaExpanded) {
            CadastroRacaMenu(
                racaViewModel = racaViewModel,
                nome = nome,
                setNome = { nome = it },
                habilidadeEspecifica = habilidadeEspecifica,
                setHabilidadeEspecifica = { habilidadeEspecifica = it }
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
            BuscaPorNome(racaViewModel = racaViewModel)
        }

        // Menu para Buscar por Habilidade
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Buscar por Habilidade", modifier = Modifier.align(Alignment.CenterVertically))
            IconButton(onClick = { buscarPorHabilidadeExpanded = !buscarPorHabilidadeExpanded }) {
                val arrowIcon = if (buscarPorHabilidadeExpanded) {
                    Icons.Filled.KeyboardArrowUp
                } else {
                    Icons.Filled.KeyboardArrowDown
                }
                Icon(imageVector = arrowIcon, contentDescription = "Expandir/Colapsar")
            }
        }
        if (buscarPorHabilidadeExpanded) {
            // Agora, chamamos sem os parâmetros buscaHabilidade e setBuscaHabilidade
            BuscaPorHabilidade(racaViewModel = racaViewModel)
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
            ListaRacas(racaViewModel = racaViewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CadastroRacaMenu(racaViewModel: RacaViewModel, nome: String, setNome: (String) -> Unit, habilidadeEspecifica: String, setHabilidadeEspecifica: (String) -> Unit) {
    Column(modifier = Modifier.padding(8.dp)) {
        TextField(
            value = nome,
            onValueChange = setNome,
            label = { Text("Nome") },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = habilidadeEspecifica,
            onValueChange = setHabilidadeEspecifica,
            label = { Text("Habilidade Específica") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )
        Button(
            onClick = {
                racaViewModel.salvarRaca(nome, habilidadeEspecifica)
                setNome("")  // Limpa o campo 'Nome'
                setHabilidadeEspecifica("")  // Limpa o campo 'Habilidade Específica'
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Criar")
        }
    }
    Divider(color = Color(0xFF8A2BE2), thickness = 1.dp) // Linha roxa
}

@Composable
fun ListaRacas(racaViewModel: RacaViewModel) {
    val context = LocalContext.current
    val listaRacas by racaViewModel.listaRacas

    if (listaRacas.isNotEmpty()) {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(top = 16.dp)) {
            items(listaRacas) { raca ->
                Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                    Text(text = "Nome: ${raca.nome}", modifier = Modifier.weight(1f))
                    Text(text = "Habilidade: ${raca.habilidadeEspecifica}")
                }
                Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                    // Botão Excluir
                    Button(
                        onClick = { racaViewModel.excluirRaca(raca) },
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text("Excluir")
                    }
                    // Botão Editar
                    Button(
                        onClick = {
                            // Iniciar a AtualizarRacaActivity com dados da raça

                            val intent = Intent(context, AtualizarRacaActivity::class.java).apply {
                                putExtra("RACA_ID", raca.id)
                                putExtra("RACA_NOME", raca.nome)
                                putExtra("RACA_HABILIDADE", raca.habilidadeEspecifica)
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
        Text("Nenhuma raça encontrada.", modifier = Modifier.padding(8.dp))
    }
    Divider(color = Color(0xFF8A2BE2), thickness = 1.dp) // Linha roxa
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuscaPorNome(racaViewModel: RacaViewModel) {
    val context = LocalContext.current
    val buscaNome by racaViewModel.buscaNome
    val setBuscaNome: (String) -> Unit = { racaViewModel.setBuscaNome(it) }
    val listaRacasPorNome by racaViewModel.listaRacasPorNome

    Column(modifier = Modifier.padding(8.dp)) {
        TextField(
            value = buscaNome,
            onValueChange = setBuscaNome,
            label = { Text("Nome") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = { racaViewModel.buscarPorNome(buscaNome) },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Buscar")
        }

        if (listaRacasPorNome.isNotEmpty()) {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(top = 16.dp)) {
                items(listaRacasPorNome) { raca ->
                    Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                        Text(text = "Nome: ${raca.nome}", modifier = Modifier.weight(1f))
                        Text(text = "Habilidade: ${raca.habilidadeEspecifica}")
                    }
                    Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                        // Botão Excluir
                        Button(
                            onClick = {
                                racaViewModel.excluirRaca(raca)
                            },
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Text("Excluir")
                        }
                        // Botão Editar
                        Button(
                            onClick = {
                                val intent = Intent(context, AtualizarRacaActivity::class.java).apply {
                                    putExtra("RACA_ID", raca.id)
                                    putExtra("RACA_NOME", raca.nome)
                                    putExtra("RACA_HABILIDADE", raca.habilidadeEspecifica)
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
            Text("Nenhuma raça encontrada.", modifier = Modifier.padding(8.dp))
        }
    }
    Divider(color = Color(0xFF8A2BE2), thickness = 1.dp)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuscaPorHabilidade(racaViewModel: RacaViewModel) {
    val context = LocalContext.current
    val buscaHabilidade by racaViewModel.buscaHabilidade
    val setBuscaHabilidade: (String) -> Unit = { racaViewModel.setBuscaHabilidade(it) }
    val listaRacasPorHabilidadeEspecifica by racaViewModel.listaRacasPorHabilidadeEspecifica

    Column(modifier = Modifier.padding(8.dp)) {
        TextField(
            value = buscaHabilidade,
            onValueChange = setBuscaHabilidade,
            label = { Text("Habilidade Específica") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = { racaViewModel.buscarPorHabilidadeEspecifica(buscaHabilidade) },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Buscar")
        }

        if (listaRacasPorHabilidadeEspecifica.isNotEmpty()) {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(top = 16.dp)) {
                items(listaRacasPorHabilidadeEspecifica) { raca ->
                    Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                        Text(text = "Nome: ${raca.nome}", modifier = Modifier.weight(1f))
                        Text(text = "Habilidade: ${raca.habilidadeEspecifica}")
                    }
                    Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                        // Botão Excluir
                        Button(
                            onClick = {
                                racaViewModel.excluirRaca(raca)
                            },
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Text("Excluir")
                        }
                        // Botão Editar
                        Button(
                            onClick = {
                                val intent = Intent(context, AtualizarRacaActivity::class.java).apply {
                                    putExtra("RACA_ID", raca.id)
                                    putExtra("RACA_NOME", raca.nome)
                                    putExtra("RACA_HABILIDADE", raca.habilidadeEspecifica)
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
            Text("Nenhuma raça encontrada.", modifier = Modifier.padding(8.dp))
        }
    }
    Divider(color = Color(0xFF8A2BE2), thickness = 1.dp)
}