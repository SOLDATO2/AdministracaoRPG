package com.example.mvvm2.view.classe

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.mvvm2.model.entity.Classe
import com.example.mvvm2.viewmodel.ClasseViewModel
import com.example.mvvm2.model.database.AppDatabase
import com.example.mvvm2.viewmodel.factory.ClasseViewModelFactory

class AtualizarClasseActivity : ComponentActivity() {
    private val viewModel: ClasseViewModel by viewModels {
        val database = AppDatabase.getDatabase(applicationContext)
        ClasseViewModelFactory(database.classeDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Recuperando dados passados pela intent
        val classeId = intent.getIntExtra("CLASSE_ID", -1)
        val classeNome = intent.getStringExtra("CLASSE_NOME") ?: ""
        val classeVariante = intent.getStringExtra("CLASSE_VARIANTE") ?: ""

        setContent {
            AtualizarClasseScreen(
                viewModel = viewModel,
                classeId = classeId,
                classeNome = classeNome,
                classeVariante = classeVariante
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AtualizarClasseScreen(viewModel: ClasseViewModel, classeId: Int, classeNome: String, classeVariante: String) {
    var nome by remember { mutableStateOf(classeNome) }
    var variante by remember { mutableStateOf(classeVariante) }
    val context = LocalContext.current

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text("Nome") },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = variante,
            onValueChange = { variante = it },
            label = { Text("Variante") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )
        Button(
            onClick = {
                if (classeId != -1) {
                    viewModel.atualizarClasse(Classe(id = classeId, nome = nome, variante = variante))
                    Toast.makeText(context, "Classe atualizada!", Toast.LENGTH_SHORT).show()
                    // Opcional: finalizar a atividade para retornar à tela anterior
                    // (context as? Activity)?.finish()
                }
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Atualizar")
        }
    }
}