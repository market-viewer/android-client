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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import cz.cvut.fel.zan.marketviewer.R
import cz.cvut.fel.zan.marketviewer.feature.screens.domain.model.ClockScreen
import cz.cvut.fel.zan.marketviewer.feature.screens.domain.model.ClockTimeFormat
import cz.cvut.fel.zan.marketviewer.feature.screens.domain.model.ClockType
import cz.cvut.fel.zan.marketviewer.feature.screens.presentation.edit.DropdownPicker
import cz.cvut.fel.zan.marketviewer.feature.screens.presentation.edit.SearchableDropdownPicker
import java.time.ZoneId

@Composable
fun ClockConfigContent(
    screen: ClockScreen,
    onSave: (ClockScreen) -> Unit,
) {
    var timezone by remember { mutableStateOf(screen.timezone) }

    var clockType by remember { mutableStateOf(screen.clockType) }
    var timeFormat by remember { mutableStateOf(screen.timeFormat) }

    val allTimezones = getStandardTimezones()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Region Settings",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            SearchableDropdownPicker(
                label = "Timezone",
                options = allTimezones,
                selectedOption = timezone,
                onOptionSelected = { timezone = it },
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
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            DropdownPicker(
                label = "Clock Type",
                options = ClockType.entries.map { it.name },
                selectedOption = clockType.name,
                onOptionSelected = { clockType = ClockType.fromString(it) },
                modifier = Modifier.weight(1f)
            )

            DropdownPicker(
                label = "Format",
                options = ClockTimeFormat.entries.map { it.label },
                selectedOption = timeFormat.label,
                onOptionSelected = { timeFormat = ClockTimeFormat.fromString(it) },
                modifier = Modifier.weight(1f),
            )
        }

        Button(
            onClick = {
                val updatedScreen = screen.copy(
                    timezone = timezone,
                    clockType = clockType,
                    timeFormat = timeFormat
                )
                onSave(updatedScreen)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            enabled = timezone.isNotBlank()
        ) {
            Icon(
                painter = painterResource(id = R.drawable.save_24px),
                contentDescription = "Save icon"
            )
            Text("Save Clock Screen")
        }
    }
}

fun getStandardTimezones(): List<String> {
    val validRegions = setOf(
        "Africa", "America", "Antarctica", "Arctic", "Asia",
        "Atlantic", "Australia", "Europe", "Indian", "Pacific"
    )

    return ZoneId.getAvailableZoneIds()
        .filter { zoneId ->
            val parts = zoneId.split("/")

            parts.size >= 2 && validRegions.contains(parts[0])
        }
        .sorted()
}