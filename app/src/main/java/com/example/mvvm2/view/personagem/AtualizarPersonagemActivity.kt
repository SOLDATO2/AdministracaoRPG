package com.example.mvvm2.view.personagem

import android.os.Bundle
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.mvvm2.model.database.AppDatabase
import com.example.mvvm2.model.entity.Classe
import com.example.mvvm2.model.entity.Personagem
import com.example.mvvm2.model.entity.Raca
import com.example.mvvm2.viewmodel.PersonagemViewModel
import com.example.mvvm2.viewmodel.factory.PersonagemViewModelFactory

class AtualizarPersonagemActivity : ComponentActivity() {
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

        // Recuperando dados passados pela intent
        val personagemId = intent.getIntExtra("PERSONAGEM_ID", -1)
        val personagemNome = intent.getStringExtra("PERSONAGEM_NOME") ?: ""
        val personagemClasseId = intent.getIntExtra("PERSONAGEM_CLASSE_ID", -1)
        val personagemRacaId = intent.getIntExtra("PERSONAGEM_RACA_ID", -1)
        val personagemNivel = intent.getIntExtra("PERSONAGEM_NIVEL", 1)

        setContent {
            AtualizarPersonagemScreen(
                viewModel = viewModel,
                personagemId = personagemId,
                personagemNome = personagemNome,
                personagemClasseId = personagemClasseId,
                personagemRacaId = personagemRacaId,
                personagemNivel = personagemNivel
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AtualizarPersonagemScreen(
    viewModel: PersonagemViewModel,
    personagemId: Int,
    personagemNome: String,
    personagemClasseId: Int,
    personagemRacaId: Int,
    personagemNivel: Int
) {
    var nome by remember { mutableStateOf(personagemNome) }
    var classeSelecionada by remember { mutableStateOf<Classe?>(null) }
    var racaSelecionada by remember { mutableStateOf<Raca?>(null) }
    var nivelText by remember { mutableStateOf(personagemNivel.toString()) }
    val context = LocalContext.current

    val listaClasses by viewModel.listaClasses
    val listaRacas by viewModel.listaRacas

    // Estados para controlar os menus suspensos
    var expandedClasse by remember { mutableStateOf(false) }
    var expandedRaca by remember { mutableStateOf(false) }

    // Inicializar as seleções com base nos IDs recebidos
    LaunchedEffect(Unit) {
        classeSelecionada = listaClasses.find { it.id == personagemClasseId }
        racaSelecionada = listaRacas.find { it.id == personagemRacaId }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = nome,
            onValueChange = { nome = it },
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
                            classeSelecionada = classe
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
                            racaSelecionada = raca
                            expandedRaca = false
                        }
                    )
                }
            }
        }

        TextField(
            value = nivelText,
            onValueChange = { nivelText = it },
            label = { Text("Nível") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )
        Button(
            onClick = {
                val nivel = nivelText.toIntOrNull()
                if (nivel != null && nivel > 0 && classeSelecionada != null && racaSelecionada != null && personagemId != -1) {
                    viewModel.atualizarPersonagem(
                        Personagem(
                            id = personagemId,
                            nome = nome,
                            classe_id = classeSelecionada!!.id,
                            raca_id = racaSelecionada!!.id,
                            nivel = nivel
                        )
                    )
                    Toast.makeText(context, "Personagem atualizado!", Toast.LENGTH_SHORT).show()
                    // Finalizar a atividade
                    (context as? ComponentActivity)?.finish()
                } else {
                    Toast.makeText(context, "Dados inválidos!", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Atualizar")
        }
    }
}