package com.example.mvvm2.view



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
import com.example.mvvm2.model.database.AppDatabase
import com.example.mvvm2.model.entity.Raca
import com.example.mvvm2.viewmodel.RacaViewModel
import com.example.mvvm2.viewmodel.factory.RacaViewModelFactory


class AtualizarRacaActivity : ComponentActivity() {
    private val viewModel: RacaViewModel by viewModels {
        val database = AppDatabase.getDatabase(applicationContext)
        RacaViewModelFactory(database.racaDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //pega os dados do intent
        val racaId = intent.getIntExtra("RACA_ID", -1)
        val racaNome = intent.getStringExtra("RACA_NOME") ?: ""
        val racaHabilidade = intent.getStringExtra("RACA_HABILIDADE") ?: ""

        setContent {
            AtualizarRacaScreen(
                viewModel = viewModel,
                racaId = racaId,
                racaNome = racaNome,
                racaHabilidade = racaHabilidade
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AtualizarRacaScreen(viewModel: RacaViewModel, racaId: Int, racaNome: String, racaHabilidade: String) {
    var nome by remember { mutableStateOf(racaNome) }
    var habilidadeEspecifica by remember { mutableStateOf(racaHabilidade) }
    val context = LocalContext.current

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text("Nome") },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = habilidadeEspecifica,
            onValueChange = { habilidadeEspecifica = it },
            label = { Text("Habilidade Específica") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )
        Button(
            onClick = {
                if (racaId != -1) {
                    viewModel.atualizarRaca(Raca(id = racaId, nome = nome, habilidadeEspecifica = habilidadeEspecifica))
                    Toast.makeText(context, "Raça atualizada!", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Atualizar")
        }
    }
}