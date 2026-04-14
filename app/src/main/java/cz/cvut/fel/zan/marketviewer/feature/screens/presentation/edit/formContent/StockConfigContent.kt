package cz.cvut.fel.zan.marketviewer.feature.screens.presentation.edit.formContent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cz.cvut.fel.zan.marketviewer.R
import cz.cvut.fel.zan.marketviewer.feature.screens.domain.model.CryptoTimeframe
import cz.cvut.fel.zan.marketviewer.feature.screens.domain.model.GraphType
import cz.cvut.fel.zan.marketviewer.feature.screens.domain.model.StockScreen
import cz.cvut.fel.zan.marketviewer.feature.screens.domain.model.StockTimeframe
import cz.cvut.fel.zan.marketviewer.feature.screens.presentation.edit.DropdownPicker
import cz.cvut.fel.zan.marketviewer.feature.screens.presentation.edit.FetchIntervalField

@Composable
fun StockConfigContent(
    screen: StockScreen,
    onSave: (StockScreen) -> Unit,
) {
    var symbol by remember { mutableStateOf(screen.symbol) }

    var fetchIntervalStr by remember { mutableStateOf(screen.fetchInterval.toString()) }
    var showIntervalInfo by remember { mutableStateOf(false) }

    var timeFrame by remember { mutableStateOf(screen.timeFrame) }
    var graphType by remember { mutableStateOf(screen.graphType) }

    var displayGraph by remember { mutableStateOf(screen.displayGraph) }
    var simpleDisplay by remember { mutableStateOf(screen.simpleDisplay) }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // --- ASSET INFORMATION ---
        Text(
            text = "Stock Information",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = symbol,
                onValueChange = { symbol = it.uppercase() },
                label = { Text("Symbol (e.g. AAPL)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Characters,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.weight(1f)
            )

            FetchIntervalField(
                value = fetchIntervalStr,
                onValueChange = { fetchIntervalStr = it },
                modifier = Modifier.weight(1f)
            )
        }

        Text(
            text = "Display Settings",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 8.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Simple Mode (Hide extra details)")
            Switch(
                checked = simpleDisplay,
                onCheckedChange = { simpleDisplay = it }
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Display Price Chart")
            Switch(
                checked = displayGraph,
                onCheckedChange = { displayGraph = it }
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            DropdownPicker(
                label = "Timeframe",
                options = StockTimeframe.entries.map { it.label },
                selectedOption = timeFrame.label,
                onOptionSelected = { timeFrame = StockTimeframe.fromString(it) },
                modifier = Modifier.weight(1f)
            )

            DropdownPicker(
                label = "Graph Type",
                options = GraphType.entries.map { it.label },
                selectedOption = graphType.label,
                onOptionSelected = { graphType = GraphType.fromString(it) },
                modifier = Modifier.weight(1f),
                enabled = displayGraph
            )
        }

        Button(
            onClick = {
                val finalInterval = fetchIntervalStr.toIntOrNull() ?: 10

                val updatedScreen = screen.copy(
                    symbol = symbol,
                    timeFrame = timeFrame,
                    displayGraph = displayGraph,
                    graphType = graphType,
                    simpleDisplay = simpleDisplay,
                    fetchInterval = finalInterval
                )
                onSave(updatedScreen)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            // Prevent saving if the user forgot to type a symbol or interval
            enabled = symbol.isNotBlank() && fetchIntervalStr.isNotBlank()
        ) {
            Icon(
                painter = painterResource(id = R.drawable.save_24px),
                contentDescription = "Save icon"
            )
            Text("Save Stock Screen")
        }
    }
}