package com.example.mvvm2.view.personagem

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.mvvm2.model.PersonagemComDetalhes
import com.example.mvvm2.model.database.AppDatabase
import com.example.mvvm2.model.entity.Classe
import com.example.mvvm2.model.entity.Raca
import com.example.mvvm2.viewmodel.PersonagemViewModel
import com.example.mvvm2.viewmodel.factory.PersonagemViewModelFactory

class GerenciarPersonagensActivity : ComponentActivity() {
    private val viewModel: PersonagemViewModel by viewModels {
        val database = AppDatabase.getDatabase(applicationContext)
        PersonagemViewModelFactory(
            database.personagemDao(),
            database.classeDao(),
            database.racaDao()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GerenciarPersonagensScreen(viewModel)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.carregarPersonagens()
        // Reexecuta as buscas se os campos não estiverem vazios
        if (viewModel.buscaNome.value.isNotEmpty()) {
            viewModel.buscarPorNome(viewModel.buscaNome.value)
        }
        if (viewModel.buscaClasse.value.isNotEmpty()) {
            viewModel.buscarPorClasse(viewModel.buscaClasse.value)
        }
        if (viewModel.buscaRaca.value.isNotEmpty()) {
            viewModel.buscarPorRaca(viewModel.buscaRaca.value)
        }
        if (viewModel.buscaNivel.value.isNotEmpty()) {
            val nivel = viewModel.buscaNivel.value.toIntOrNull()
            if (nivel != null) {
                viewModel.buscarPorNivel(nivel)
            }
        }
    }
}

@Composable
fun GerenciarPersonagensScreen(personagemViewModel: PersonagemViewModel) {

    // Campos de cadastro
    var nome by remember { mutableStateOf("") }
    var classeSelecionada by remember { mutableStateOf<Classe?>(null) }
    var racaSelecionada by remember { mutableStateOf<Raca?>(null) }
    var nivelText by remember { mutableStateOf("") }

    // Estados para os menus expansíveis
    var cadastrarPersonagemExpanded by remember { mutableStateOf(false) }
    var buscarTodosExpanded by remember { mutableStateOf(false) }
    var buscarPorNomeExpanded by remember { mutableStateOf(false) }
    var buscarPorClasseExpanded by remember { mutableStateOf(false) }
    var buscarPorRacaExpanded by remember { mutableStateOf(false) }
    var buscarPorNivelExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Gerenciar Personagens", style = MaterialTheme.typography.titleLarge)

        // Menu para Cadastrar Personagem
        ExpandableMenu(
            title = "Cadastrar Personagem",
            expanded = cadastrarPersonagemExpanded,
            onExpandChange = { cadastrarPersonagemExpanded = it }
        ) {
            CadastroPersonagemMenu(
                personagemViewModel = personagemViewModel,
                nome = nome,
                setNome = { nome = it },
                classeSelecionada = classeSelecionada,
                setClasseSelecionada = { classeSelecionada = it },
                racaSelecionada = racaSelecionada,
                setRacaSelecionada = { racaSelecionada = it },
                nivelText = nivelText,
                setNivelText = { nivelText = it }
            )
        }

        // Menu para Buscar por Nome
        ExpandableMenu(
            title = "Buscar por Nome",
            expanded = buscarPorNomeExpanded,
            onExpandChange = { buscarPorNomeExpanded = it }
        ) {
            BuscaPorNomePersonagem(personagemViewModel = personagemViewModel)
        }

        // Menu para Buscar por Classe
        ExpandableMenu(
            title = "Buscar por Classe",
            expanded = buscarPorClasseExpanded,
            onExpandChange = { buscarPorClasseExpanded = it }
        ) {
            BuscaPorClasse(personagemViewModel = personagemViewModel)
        }

        // Menu para Buscar por Raça
        ExpandableMenu(
            title = "Buscar por Raça",
            expanded = buscarPorRacaExpanded,
            onExpandChange = { buscarPorRacaExpanded = it }
        ) {
            BuscaPorRaca(personagemViewModel = personagemViewModel)
        }

        // Menu para Buscar por Nível
        ExpandableMenu(
            title = "Buscar por Nível",
            expanded = buscarPorNivelExpanded,
            onExpandChange = { buscarPorNivelExpanded = it }
        ) {
            BuscaPorNivel(personagemViewModel = personagemViewModel)
        }

        // Menu para Mostrar Todos
        ExpandableMenu(
            title = "Mostrar Todos",
            expanded = buscarTodosExpanded,
            onExpandChange = { buscarTodosExpanded = it }
        ) {
            ListaPersonagens(personagemViewModel = personagemViewModel)
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
fun CadastroPersonagemMenu(
    personagemViewModel: PersonagemViewModel,
    nome: String,
    setNome: (String) -> Unit,
    classeSelecionada: Classe?,
    setClasseSelecionada: (Classe?) -> Unit,
    racaSelecionada: Raca?,
    setRacaSelecionada: (Raca?) -> Unit,
    nivelText: String,
    setNivelText: (String) -> Unit
) {
    val context = LocalContext.current
    val resultMessage by personagemViewModel.resultMessage

    val listaClasses by personagemViewModel.listaClasses
    val listaRacas by personagemViewModel.listaRacas

    // Estados para controlar o menu suspenso de classe
    var expandedClasse by remember { mutableStateOf(false) }

    // Estados para controlar o menu suspenso de raça
    var expandedRaca by remember { mutableStateOf(false) }

    LaunchedEffect(resultMessage) {
        resultMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            if (message == "Personagem salvo com sucesso!") {
                setNome("")
                setClasseSelecionada(null)
                setRacaSelecionada(null)
                setNivelText("")
            }
            personagemViewModel.resultMessage.value = null
        }
    }

    Column(modifier = Modifier.padding(8.dp)) {
        TextField(
            value = nome,
            onValueChange = setNome,
            label = { Text("Nome") },
            modifier = Modifier.fillMaxWidth()
        )

        // Menu suspenso para Classe
        ExposedDropdownMenuBox(
            expanded = expandedClasse,
            onExpandedChange = { expandedClasse = !expandedClasse }
        ) {
            TextField(
                value = classeSelecionada?.nome ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Classe") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedClasse) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
            ExposedDropdownMenu(
                expanded = expandedClasse,
                onDismissRequest = { expandedClasse = false }
            ) {
                listaClasses.forEach { classe ->
                    DropdownMenuItem(
                        text = { Text(classe.nome) },
                        onClick = {
                            setClasseSelecionada(classe)
                            expandedClasse = false
                        }
                    )
                }
            }
        }

        // Menu suspenso para Raça
        ExposedDropdownMenuBox(
            expanded = expandedRaca,
            onExpandedChange = { expandedRaca = !expandedRaca }
        ) {
            TextField(
                value = racaSelecionada?.nome ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Raça") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRaca) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
            ExposedDropdownMenu(
                expanded = expandedRaca,
                onDismissRequest = { expandedRaca = false }
            ) {
                listaRacas.forEach { raca ->
                    DropdownMenuItem(
                        text = { Text(raca.nome) },
                        onClick = {
                            setRacaSelecionada(raca)
                            expandedRaca = false
                        }
                    )
                }
            }
        }

        TextField(
            value = nivelText,
            onValueChange = setNivelText,
            label = { Text("Nível") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )
        Button(
            onClick = {
                val nivel = nivelText.toIntOrNull()
                if (nivel == null || nivel <= 0 || nome.isBlank() || classeSelecionada == null || racaSelecionada == null) {
                    Toast.makeText(context, "Preencha todos os campos corretamente!", Toast.LENGTH_SHORT).show()
                } else {
                    personagemViewModel.salvarPersonagem(
                        nome,
                        classeSelecionada.id,
                        racaSelecionada.id,
                        nivel
                    )
                }
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Criar")
        }
    }
    Divider(color = Color(0xFF8A2BE2), thickness = 1.dp)
}

@Composable
fun PersonagemItem(
    personagemComDetalhes: PersonagemComDetalhes,
    onEdit: (PersonagemComDetalhes) -> Unit,
    onDeleteRequest: (PersonagemComDetalhes) -> Unit
) {
    val personagem = personagemComDetalhes.personagem
    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Text(text = "Nome: ${personagem.nome}")
        Text(text = "Classe: ${personagemComDetalhes.nomeClasse}")
        Text(text = "Raça: ${personagemComDetalhes.nomeRaca}")
        Text(text = "Nível: ${personagem.nivel}")
        Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            Button(
                onClick = { onDeleteRequest(personagemComDetalhes) },
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Text("Excluir")
            }
            Button(
                onClick = { onEdit(personagemComDetalhes) }
            ) {
                Text("Editar")
            }
        }
    }
}

@Composable
fun PersonagemList(
    listaPersonagens: List<PersonagemComDetalhes>,
    onEdit: (PersonagemComDetalhes) -> Unit,
    onDelete: (PersonagemComDetalhes) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var personagemToDelete by remember { mutableStateOf<PersonagemComDetalhes?>(null) }

    if (listaPersonagens.isNotEmpty()) {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(top = 16.dp)) {
            items(listaPersonagens) { personagemComDetalhes ->
                PersonagemItem(
                    personagemComDetalhes = personagemComDetalhes,
                    onEdit = { onEdit(personagemComDetalhes) },
                    onDeleteRequest = {
                        personagemToDelete = it
                        showDialog = true
                    }
                )
            }
        }
    } else {
        Text("Nenhum personagem encontrado.", modifier = Modifier.padding(8.dp))
    }

    // Diálogo de confirmação
    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
                personagemToDelete = null
            },
            title = {
                Text(text = "Confirmar Exclusão")
            },
            text = {
                Text("Tem certeza de que deseja excluir este personagem?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        personagemToDelete?.let { onDelete(it) }
                        showDialog = false
                        personagemToDelete = null
                    }
                ) {
                    Text("Sim")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDialog = false
                        personagemToDelete = null
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
fun ListaPersonagens(personagemViewModel: PersonagemViewModel) {
    val context = LocalContext.current
    val listaPersonagens by personagemViewModel.listaPersonagensComDetalhes

    PersonagemList(
        listaPersonagens = listaPersonagens,
        onEdit = { personagemComDetalhes ->
            val personagem = personagemComDetalhes.personagem
            val intent = Intent(context, AtualizarPersonagemActivity::class.java).apply {
                putExtra("PERSONAGEM_ID", personagem.id)
                putExtra("PERSONAGEM_NOME", personagem.nome)
                putExtra("PERSONAGEM_CLASSE_ID", personagem.classe_id)
                putExtra("PERSONAGEM_RACA_ID", personagem.raca_id)
                putExtra("PERSONAGEM_NIVEL", personagem.nivel)
            }
            context.startActivity(intent)
        },
        onDelete = { personagemComDetalhes ->
            personagemViewModel.excluirPersonagem(personagemComDetalhes.personagem)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuscaPorNomePersonagem(personagemViewModel: PersonagemViewModel) {
    val context = LocalContext.current
    val buscaNome by personagemViewModel.buscaNome
    val setBuscaNome: (String) -> Unit = { personagemViewModel.setBuscaNome(it) }
    val listaPersonagensPorNome by personagemViewModel.listaPersonagensPorNome

    Column(modifier = Modifier.padding(8.dp)) {
        TextField(
            value = buscaNome,
            onValueChange = setBuscaNome,
            label = { Text("Nome") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                if (buscaNome.isBlank()) {
                    Toast.makeText(context, "Digite um nome para buscar", Toast.LENGTH_SHORT).show()
                    personagemViewModel.listaPersonagensPorNome.value = emptyList()
                } else {
                    personagemViewModel.buscarPorNome(buscaNome)
                }
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Buscar")
        }

        if (listaPersonagensPorNome.isEmpty()) {
            Text("Nenhum personagem encontrado.", modifier = Modifier.padding(8.dp))
        } else {
            PersonagemList(
                listaPersonagens = listaPersonagensPorNome,
                onEdit = { personagemComDetalhes ->
                    val personagem = personagemComDetalhes.personagem
                    val intent = Intent(context, AtualizarPersonagemActivity::class.java).apply {
                        putExtra("PERSONAGEM_ID", personagem.id)
                        putExtra("PERSONAGEM_NOME", personagem.nome)
                        putExtra("PERSONAGEM_CLASSE_ID", personagem.classe_id)
                        putExtra("PERSONAGEM_RACA_ID", personagem.raca_id)
                        putExtra("PERSONAGEM_NIVEL", personagem.nivel)
                    }
                    context.startActivity(intent)
                },
                onDelete = { personagemComDetalhes ->
                    personagemViewModel.excluirPersonagem(personagemComDetalhes.personagem)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuscaPorClasse(personagemViewModel: PersonagemViewModel) {
    val context = LocalContext.current
    val buscaClasse by personagemViewModel.buscaClasse
    val setBuscaClasse: (String) -> Unit = { personagemViewModel.setBuscaClasse(it) }
    val listaPersonagensPorClasse by personagemViewModel.listaPersonagensPorClasse

    Column(modifier = Modifier.padding(8.dp)) {
        TextField(
            value = buscaClasse,
            onValueChange = setBuscaClasse,
            label = { Text("Classe") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                if (buscaClasse.isBlank()) {
                    Toast.makeText(context, "Digite uma classe para buscar", Toast.LENGTH_SHORT).show()
                    personagemViewModel.listaPersonagensPorClasse.value = emptyList()
                } else {
                    personagemViewModel.buscarPorClasse(buscaClasse)
                }
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Buscar")
        }

        if (listaPersonagensPorClasse.isEmpty()) {
            Text("Nenhum personagem encontrado.", modifier = Modifier.padding(8.dp))
        } else {
            PersonagemList(
                listaPersonagens = listaPersonagensPorClasse,
                onEdit = { personagemComDetalhes ->
                    val personagem = personagemComDetalhes.personagem
                    val intent = Intent(context, AtualizarPersonagemActivity::class.java).apply {
                        putExtra("PERSONAGEM_ID", personagem.id)
                        putExtra("PERSONAGEM_NOME", personagem.nome)
                        putExtra("PERSONAGEM_CLASSE_ID", personagem.classe_id)
                        putExtra("PERSONAGEM_RACA_ID", personagem.raca_id)
                        putExtra("PERSONAGEM_NIVEL", personagem.nivel)
                    }
                    context.startActivity(intent)
                },
                onDelete = { personagemComDetalhes ->
                    personagemViewModel.excluirPersonagem(personagemComDetalhes.personagem)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuscaPorRaca(personagemViewModel: PersonagemViewModel) {
    val context = LocalContext.current
    val buscaRaca by personagemViewModel.buscaRaca
    val setBuscaRaca: (String) -> Unit = { personagemViewModel.setBuscaRaca(it) }
    val listaPersonagensPorRaca by personagemViewModel.listaPersonagensPorRaca

    Column(modifier = Modifier.padding(8.dp)) {
        TextField(
            value = buscaRaca,
            onValueChange = setBuscaRaca,
            label = { Text("Raça") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                if (buscaRaca.isBlank()) {
                    Toast.makeText(context, "Digite uma raça para buscar", Toast.LENGTH_SHORT).show()
                    personagemViewModel.listaPersonagensPorRaca.value = emptyList()
                } else {
                    personagemViewModel.buscarPorRaca(buscaRaca)
                }
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Buscar")
        }

        if (listaPersonagensPorRaca.isEmpty()) {
            Text("Nenhum personagem encontrado.", modifier = Modifier.padding(8.dp))
        } else {
            PersonagemList(
                listaPersonagens = listaPersonagensPorRaca,
                onEdit = { personagemComDetalhes ->
                    val personagem = personagemComDetalhes.personagem
                    val intent = Intent(context, AtualizarPersonagemActivity::class.java).apply {
                        putExtra("PERSONAGEM_ID", personagem.id)
                        putExtra("PERSONAGEM_NOME", personagem.nome)
                        putExtra("PERSONAGEM_CLASSE_ID", personagem.classe_id)
                        putExtra("PERSONAGEM_RACA_ID", personagem.raca_id)
                        putExtra("PERSONAGEM_NIVEL", personagem.nivel)
                    }
                    context.startActivity(intent)
                },
                onDelete = { personagemComDetalhes ->
                    personagemViewModel.excluirPersonagem(personagemComDetalhes.personagem)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuscaPorNivel(personagemViewModel: PersonagemViewModel) {
    val context = LocalContext.current
    val buscaNivel by personagemViewModel.buscaNivel
    val setBuscaNivel: (String) -> Unit = { personagemViewModel.setBuscaNivel(it) }
    val listaPersonagensPorNivel by personagemViewModel.listaPersonagensPorNivel

    Column(modifier = Modifier.padding(8.dp)) {
        TextField(
            value = buscaNivel,
            onValueChange = setBuscaNivel,
            label = { Text("Nível") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                val nivel = buscaNivel.toIntOrNull()
                if (buscaNivel.isBlank() || nivel == null) {
                    Toast.makeText(context, "Digite um nível válido", Toast.LENGTH_SHORT).show()
                    personagemViewModel.listaPersonagensPorNivel.value = emptyList()
                } else {
                    personagemViewModel.buscarPorNivel(nivel)
                }
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Buscar")
        }

        if (listaPersonagensPorNivel.isEmpty()) {
            Text("Nenhum personagem encontrado.", modifier = Modifier.padding(8.dp))
        } else {
            PersonagemList(
                listaPersonagens = listaPersonagensPorNivel,
                onEdit = { personagemComDetalhes ->
                    val personagem = personagemComDetalhes.personagem
                    val intent = Intent(context, AtualizarPersonagemActivity::class.java).apply {
                        putExtra("PERSONAGEM_ID", personagem.id)
                        putExtra("PERSONAGEM_NOME", personagem.nome)
                        putExtra("PERSONAGEM_CLASSE_ID", personagem.classe_id)
                        putExtra("PERSONAGEM_RACA_ID", personagem.raca_id)
                        putExtra("PERSONAGEM_NIVEL", personagem.nivel)
                    }
                    context.startActivity(intent)
                },
                onDelete = { personagemComDetalhes ->
                    personagemViewModel.excluirPersonagem(personagemComDetalhes.personagem)
                }
            )
        }
    }
}
