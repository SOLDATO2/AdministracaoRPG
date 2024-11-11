package com.example.mvvm2.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.material3.AlertDialog
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
import com.example.mvvm2.model.entity.Raca
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
    //Serve para atualizar todas as listas de raças quando retornarmos do AtualizarRacaActivity
    override fun onResume() {
        super.onResume()
        viewModel.buscarTodasAsRacas()
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
        ExpandableMenu(
            title = "Cadastrar Raça",
            expanded = cadastrarRacaExpanded,
            onExpandChange = { cadastrarRacaExpanded = it }
        ) {
            CadastroRacaMenu(
                racaViewModel = racaViewModel,
                nome = nome,
                setNome = { nome = it },
                habilidadeEspecifica = habilidadeEspecifica,
                setHabilidadeEspecifica = { habilidadeEspecifica = it }
            )
        }

        // Menu para Buscar por Nome
        ExpandableMenu(
            title = "Buscar por Nome",
            expanded = buscarPorNomeExpanded,
            onExpandChange = { buscarPorNomeExpanded = it }
        ) {
            BuscaPorNome(racaViewModel = racaViewModel)
        }

        // Menu para Buscar por Habilidade
        ExpandableMenu(
            title = "Buscar por Habilidade",
            expanded = buscarPorHabilidadeExpanded,
            onExpandChange = { buscarPorHabilidadeExpanded = it }
        ) {
            BuscaPorHabilidade(racaViewModel = racaViewModel)
        }

        // Menu para Mostrar todos
        ExpandableMenu(
            title = "Mostrar Todos",
            expanded = buscarTodosExpanded,
            onExpandChange = { buscarTodosExpanded = it }
        ) {
            ListaRacas(racaViewModel = racaViewModel)
        }
    }
}

@Composable
fun ExpandableMenu(
    title: String,
    expanded: Boolean,
    onExpandChange: (Boolean) -> Unit,
    content: @Composable () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, modifier = Modifier.align(Alignment.CenterVertically))
        IconButton(onClick = { onExpandChange(!expanded) }) {
            val arrowIcon = if (expanded) {
                Icons.Filled.KeyboardArrowUp
            } else {
                Icons.Filled.KeyboardArrowDown
            }
            Icon(imageVector = arrowIcon, contentDescription = "Expandir/Colapsar")
        }
    }
    if (expanded) {
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CadastroRacaMenu(
    racaViewModel: RacaViewModel,
    nome: String,
    setNome: (String) -> Unit,
    habilidadeEspecifica: String,
    setHabilidadeEspecifica: (String) -> Unit
) {
    val context = LocalContext.current

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
                Toast.makeText(context, "Raça cadastrada!", Toast.LENGTH_LONG).show()
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Criar")
        }
    }
    Divider(color = Color(0xFF8A2BE2), thickness = 1.dp)
}

@Composable
fun RacaItem(
    raca: Raca,
    onEdit: (Raca) -> Unit,
    onDeleteRequest: (Raca) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(text = "Nome: ${raca.nome}", modifier = Modifier.weight(1f))
            Text(text = "Habilidade: ${raca.habilidadeEspecifica}")
        }
        Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            Button(
                onClick = { onDeleteRequest(raca) },
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Text("Excluir")
            }
            Button(
                onClick = { onEdit(raca) }
            ) {
                Text("Editar")
            }
        }
    }
}

@Composable
fun RacaList(
    listaRacas: List<Raca>,
    onEdit: (Raca) -> Unit,
    onDelete: (Raca) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var racaToDelete by remember { mutableStateOf<Raca?>(null) }

    if (listaRacas.isNotEmpty()) {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(top = 16.dp)) {
            items(listaRacas) { raca ->
                RacaItem(
                    raca = raca,
                    onEdit = { onEdit(raca) },
                    onDeleteRequest = {
                        racaToDelete = it
                        showDialog = true
                    }
                )
            }
        }
    } else {
        Text("Nenhuma raça encontrada.", modifier = Modifier.padding(8.dp))
    }

    // Diálogo de confirmação
    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
                racaToDelete = null
            },
            title = {
                Text(text = "Confirmar Exclusão")
            },
            text = {
                Text("Tem certeza de que deseja excluir esta raça?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        racaToDelete?.let { onDelete(it) }
                        showDialog = false
                        racaToDelete = null
                    }
                ) {
                    Text("Sim")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDialog = false
                        racaToDelete = null
                    }
                ) {
                    Text("Não")
                }
            }
        )
    }

    Divider(color = Color(0xFF8A2BE2), thickness = 1.dp)
}

@Composable
fun ListaRacas(racaViewModel: RacaViewModel) {
    val context = LocalContext.current
    val listaRacas by racaViewModel.listaRacas

    RacaList(
        listaRacas = listaRacas,
        onEdit = { raca ->
            val intent = Intent(context, AtualizarRacaActivity::class.java).apply {
                putExtra("RACA_ID", raca.id)
                putExtra("RACA_NOME", raca.nome)
                putExtra("RACA_HABILIDADE", raca.habilidadeEspecifica)
            }
            context.startActivity(intent)
        },
        onDelete = { raca ->
            racaViewModel.excluirRaca(raca)
        }
    )
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

        RacaList(
            listaRacas = listaRacasPorNome,
            onEdit = { raca ->
                val intent = Intent(context, AtualizarRacaActivity::class.java).apply {
                    putExtra("RACA_ID", raca.id)
                    putExtra("RACA_NOME", raca.nome)
                    putExtra("RACA_HABILIDADE", raca.habilidadeEspecifica)
                }
                context.startActivity(intent)
            },
            onDelete = { raca ->
                racaViewModel.excluirRaca(raca)
            }
        )
    }
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

        RacaList(
            listaRacas = listaRacasPorHabilidadeEspecifica,
            onEdit = { raca ->
                val intent = Intent(context, AtualizarRacaActivity::class.java).apply {
                    putExtra("RACA_ID", raca.id)
                    putExtra("RACA_NOME", raca.nome)
                    putExtra("RACA_HABILIDADE", raca.habilidadeEspecifica)
                }
                context.startActivity(intent)
            },
            onDelete = { raca ->
                racaViewModel.excluirRaca(raca)
            }
        )
    }
}