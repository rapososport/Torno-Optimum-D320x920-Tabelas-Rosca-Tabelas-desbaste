package pt.optimum.d320x920

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.Locale
import kotlin.math.roundToInt


private data class FeedValue(val longitudinal: Double, val transversal: Double)
private val feedRows = listOf(
    "A/D" to listOf(
        FeedValue(1.392, 0.475), FeedValue(1.301, 0.445), FeedValue(1.044, 0.356), FeedValue(0.833, 0.285),
        FeedValue(0.696, 0.239), FeedValue(0.650, 0.221), FeedValue(0.521, 0.178), FeedValue(0.417, 0.142)
    ),
    "B/D" to listOf(
        FeedValue(0.696, 0.239), FeedValue(0.650, 0.221), FeedValue(0.521, 0.178), FeedValue(0.417, 0.142),
        FeedValue(0.348, 0.119), FeedValue(0.325, 0.112), FeedValue(0.259, 0.089), FeedValue(0.208, 0.071)
    ),
    "A/C" to listOf(
        FeedValue(0.348, 0.119), FeedValue(0.325, 0.112), FeedValue(0.259, 0.089), FeedValue(0.208, 0.071),
        FeedValue(0.175, 0.061), FeedValue(0.163, 0.056), FeedValue(0.130, 0.043), FeedValue(0.104, 0.036)
    ),
    "B/C" to listOf(
        FeedValue(0.175, 0.061), FeedValue(0.163, 0.056), FeedValue(0.130, 0.043), FeedValue(0.104, 0.036),
        FeedValue(0.086, 0.031), FeedValue(0.079, 0.028), FeedValue(0.064, 0.023), FeedValue(0.051, 0.018)
    )
)
private val feedColumns = listOf("60T-T", "60T-S", "60T-R", "60T-V", "30T-T", "30T-S", "30T-R", "30T-V")

private val spindle = mapOf(
    "I-A" to listOf(240, 1150, 730),
    "I-B" to listOf(65, 330, 200),
    "I-C" to listOf(190, 920, 560),
    "II-A" to listOf(380, 1800, 1100),
    "II-B" to listOf(100, 500, 320),
    "II-C" to listOf(290, 1400, 860)
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { LatheApp() } }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LatheApp() {
    var module by remember { mutableStateOf("Desbaste") }
    var range by remember { mutableStateOf("I") }
    var leverABC by remember { mutableStateOf("A") }
    var lever123 by remember { mutableStateOf("1") }
    var hz by remember { mutableStateOf("50") }
    var diameter by remember { mutableStateOf("40") }
    var cuttingSpeed by remember { mutableStateOf("100") }
    var threadType by remember { mutableStateOf("Métrica ISO") }
    var pitch by remember { mutableStateOf("1.5") }

    val baseRpm = spindle["$range-$leverABC"]!![lever123.toInt() - 1]
    val frequency = hz.toDoubleOrNull()?.coerceIn(20.0, 70.0) ?: 50.0
    val actualRpm = baseRpm * frequency / 50.0
    val d = diameter.toDoubleOrNull() ?: 0.0
    val vc = cuttingSpeed.toDoubleOrNull() ?: 0.0
    val recommendedRpm = if (d > 0) (1000.0 * vc / (Math.PI * d)).roundToInt() else 0

    Scaffold(topBar = {
        TopAppBar(title = { Text("Optimum D320 × 920") })
    }) { padding ->
        Column(Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
            Text("Calculadora e tabelas da máquina", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = module == "Desbaste", onClick = { module = "Desbaste" }, label = { Text("Desbaste") })
                FilterChip(selected = module == "Rosqueamento", onClick = { module = "Rosqueamento" }, label = { Text("Rosqueamento") })
            }
            Spacer(Modifier.height(16.dp))
            Section("Quadro A — Velocidade do fuso")
            Text("Três seletores mecânicos + ajuste VFD")
            ChoiceRow("Gama", listOf("I", "II"), range) { range = it }
            ChoiceRow("Alavanca A/B/C", listOf("A", "B", "C"), leverABC) { leverABC = it }
            ChoiceRow("Alavanca 1/2/3", listOf("1", "2", "3"), lever123) { lever123 = it }
            Text("Rotação de tabela a 50 Hz: $baseRpm rpm")
            OutlinedTextField(value = hz, onValueChange = { hz = it }, label = { Text("Frequência VFD (20–70 Hz)") }, singleLine = true)
            Text("Rotação calculada: ${fmt(actualRpm)} rpm", style = MaterialTheme.typography.titleLarge)
            Text("Fórmula: RPM real = RPM da tabela × Hz / 50")
            Spacer(Modifier.height(18.dp))

            if (module == "Desbaste") {
                Section("Desbaste de material")
                OutlinedTextField(value = diameter, onValueChange = { diameter = it }, label = { Text("Diâmetro da peça (mm)") }, singleLine = true)
                OutlinedTextField(value = cuttingSpeed, onValueChange = { cuttingSpeed = it }, label = { Text("Velocidade de corte pretendida (m/min)") }, singleLine = true)
                Text("Rotação teórica: $recommendedRpm rpm", style = MaterialTheme.typography.titleLarge)
                Text("Escolha na máquina a combinação de alavancas cuja rotação real seja a mais próxima, sem ultrapassar o limite recomendado para a ferramenta e a peça.")
                Section("Quadro B — Avanços Norton")
                Text("Valores em mm/rotação. Em cada combinação: longitudinal / transversal.")
                feedRows.forEach { (lever, values) ->
                    Text("Alavancas $lever", style = MaterialTheme.typography.titleSmall)
                    values.forEachIndexed { index, value ->
                        Text("${feedColumns[index]}: ${fmt3(value.longitudinal)} / ${fmt3(value.transversal)} mm/rot")
                    }
                    HorizontalDivider(Modifier.padding(vertical = 6.dp))
                }
            } else {
                Section("Rosqueamento")
                ChoiceRow("Sistema de rosca", listOf("Métrica ISO", "Inglesa Whitworth"), threadType) { threadType = it }
                OutlinedTextField(value = pitch, onValueChange = { pitch = it }, label = {
                    Text(if (threadType == "Métrica ISO") "Passo (mm)" else "Passo (TPI)")
                }, singleLine = true)
                Text(if (threadType == "Métrica ISO") "Perfil ISO: 60°" else "Perfil Whitworth: 55°")
                Section("Quadro B — Caixa Norton / roscas")
                Text("A seleção final da combinação de engrenagens e alavancas será apresentada depois de confirmar os dados da placa e a correspondência exata do passo.")
            }
            Spacer(Modifier.height(16.dp))
            Section("Aviso técnico")
            Text("A tabela de rotações é considerada calibrada a 50 Hz. A relação linear com a frequência é uma estimativa: confirmar as rotações com tacómetro. Não assumir que 70 Hz é seguro sem verificar motor, transmissão, rolamentos e limites do fabricante.")
        }
    }
}

@Composable
private fun Section(title: String) {
    Text(title, style = MaterialTheme.typography.titleMedium)
    HorizontalDivider(Modifier.padding(vertical = 8.dp))
}

@Composable
private fun ChoiceRow(label: String, options: List<String>, selected: String, onSelect: (String) -> Unit) {
    Text(label, style = MaterialTheme.typography.labelLarge)
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        options.forEach { option ->
            FilterChip(selected = selected == option, onClick = { onSelect(option) }, label = { Text(option) })
        }
    }
    Spacer(Modifier.height(8.dp))
}

private fun fmt(value: Double): String = String.format(Locale("pt", "PT"), "%.0f", value)
private fun fmt3(value: Double): String = String.format(Locale("pt", "PT"), "%.3f", value)
