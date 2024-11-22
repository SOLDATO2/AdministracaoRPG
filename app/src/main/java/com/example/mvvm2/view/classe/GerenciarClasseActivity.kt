package com.example.mvvm2.view.classe

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
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.mvvm2.model.database.AppDatabase
import com.example.mvvm2.model.entity.Classe
import com.example.mvvm2.viewmodel.ClasseViewModel
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

    // Reexecuta as buscas se houver um termo de busca ativo
    override fun onResume() {
        super.onResume()
        viewModel.buscarTodasAsClasses()
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

    // Estados para os campos de cadastro
    var nome by remember { mutableStateOf("") }
    var variante by remember { mutableStateOf("") }

    // Estados para os menus expansíveis
    var cadastrarClasseExpanded by remember { mutableStateOf(false) }
    var buscarTodosExpanded by remember { mutableStateOf(false) }
    var buscarPorNomeExpanded by remember { mutableStateOf(false) }
    var buscarPorVarianteExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Gerenciar Classes", style = MaterialTheme.typography.titleLarge)

        // Menu para Cadastrar Classe
        ExpandableMenu(
            title = "Cadastrar Classe",
            expanded = cadastrarClasseExpanded,
            onExpandChange = { cadastrarClasseExpanded = it }
        ) {
            CadastroClasseMenu(
                classeViewModel = classeViewModel,
                nome = nome,
                setNome = { nome = it },
                variante = variante,
                setVariante = { variante = it }
            )
        }

        // Menu para Buscar por Nome
        ExpandableMenu(
            title = "Buscar por Nome",
            expanded = buscarPorNomeExpanded,
            onExpandChange = { buscarPorNomeExpanded = it }
        ) {
            BuscaPorNome(classeViewModel = classeViewModel)
        }

        // Menu para Buscar por Variante
        ExpandableMenu(
            title = "Buscar por Variante",
            expanded = buscarPorVarianteExpanded,
            onExpandChange = { buscarPorVarianteExpanded = it }
        ) {
            BuscaPorVariante(classeViewModel = classeViewModel)
        }

        // Menu para Mostrar todas
        ExpandableMenu(
            title = "Mostrar Todas",
            expanded = buscarTodosExpanded,
            onExpandChange = { buscarTodosExpanded = it }
        ) {
            ListaClasses(classeViewModel = classeViewModel)
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
fun CadastroClasseMenu(
    classeViewModel: ClasseViewModel,
    nome: String,
    setNome: (String) -> Unit,
    variante: String,
    setVariante: (String) -> Unit
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
                Toast.makeText(context, "Classe cadastrada!", Toast.LENGTH_LONG).show()
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Criar")
        }
    }
    Divider(color = Color(0xFF8A2BE2), thickness = 1.dp)
}

@Composable
fun ClasseList(
    listaClasses: List<Classe>,
    onEdit: (Classe) -> Unit,
    onDelete: (Classe) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var classeToDelete by remember { mutableStateOf<Classe?>(null) }

    if (listaClasses.isNotEmpty()) {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(top = 16.dp)) {
            items(listaClasses) { classe ->
                Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(text = "Nome: ${classe.nome}", modifier = Modifier.weight(1f))
                        Text(text = "Variante: ${classe.variante}")
                    }
                    Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                        Button(
                            onClick = {
                                classeToDelete = classe
                                showDialog = true
                            },
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text("Excluir")
                        }
                        Button(
                            onClick = { onEdit(classe) }
                        ) {
                            Text("Editar")
                        }
                    }
                }
            }
        }
    } else {
        Text("Nenhuma classe encontrada.", modifier = Modifier.padding(8.dp))
    }

    // Diálogo de confirmação
    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
                classeToDelete = null
            },
            title = {
                Text(text = "Confirmar Exclusão")
            },
            text = {
                Text("Tem certeza de que deseja excluir esta classe?"+
                        " Qualquer Personagem com essa classe será excluído.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        classeToDelete?.let { onDelete(it) }
                        showDialog = false
                        classeToDelete = null
                    }
                ) {
                    Text("Sim")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDialog = false
                        classeToDelete = null
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
fun ListaClasses(classeViewModel: ClasseViewModel) {
    val context = LocalContext.current
    val listaClasses by classeViewModel.listaClasses

    ClasseList(
        listaClasses = listaClasses,
        onEdit = { classe ->
            val intent = Intent(context, AtualizarClasseActivity::class.java).apply {
                putExtra("CLASSE_ID", classe.id)
                putExtra("CLASSE_NOME", classe.nome)
                putExtra("CLASSE_VARIANTE", classe.variante)
            }
            context.startActivity(intent)
        },
        onDelete = { classe ->
            classeViewModel.excluirClasse(classe)
        }
    )
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

        ClasseList(
            listaClasses = listaClassesPorNome,
            onEdit = { classe ->
                val intent = Intent(context, AtualizarClasseActivity::class.java).apply {
                    putExtra("CLASSE_ID", classe.id)
                    putExtra("CLASSE_NOME", classe.nome)
                    putExtra("CLASSE_VARIANTE", classe.variante)
                }
                context.startActivity(intent)
            },
            onDelete = { classe ->
                classeViewModel.excluirClasse(classe)
            }
        )
    }
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
            label = { Text("Variante") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = { classeViewModel.buscarPorVariante(buscaVariante) },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Buscar")
        }

        ClasseList(
            listaClasses = listaClassesPorVariante,
            onEdit = { classe ->
                val intent = Intent(context, AtualizarClasseActivity::class.java).apply {
                    putExtra("CLASSE_ID", classe.id)
                    putExtra("CLASSE_NOME", classe.nome)
                    putExtra("CLASSE_VARIANTE", classe.variante)
                }
                context.startActivity(intent)
            },
            onDelete = { classe ->
                classeViewModel.excluirClasse(classe)
            }
        )
    }
}
