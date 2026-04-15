package cz.cvut.fel.zan.marketviewer.feature.screens.presentation.edit.formContent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cz.cvut.fel.zan.marketviewer.R
import cz.cvut.fel.zan.marketviewer.feature.screens.domain.model.AITextScreen

@Composable
fun AiTextConfigContent(
    screen: AITextScreen,
    onSave: (AITextScreen) -> Unit,
) {
    var prompt by remember { mutableStateOf(screen.prompt) }
    var fetchIntervalStr by remember { mutableStateOf(screen.fetchIntervalHours.toString()) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "AI Generation Settings",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )

        OutlinedTextField(
            value = prompt,
            onValueChange = { prompt = it },
            label = { Text("Prompt (What should the AI say?)") },
            placeholder = { Text("e.g., Give me today's news on stock market.") },
            minLines = 3,
            maxLines = 8,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Default
            ),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = fetchIntervalStr,
            onValueChange = { newValue ->
                if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                    fetchIntervalStr = newValue
                }
            },
            label = { Text("Refresh Interval (hours)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth(),
            supportingText = {
                Text(
                    text = "AI requests can be expensive. We recommend setting this to 4 or 10 hours.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        )

        Button(
            onClick = {
                val finalInterval = fetchIntervalStr.toIntOrNull() ?: 5

                val updatedScreen = screen.copy(
                    prompt = prompt,
                    fetchIntervalHours = finalInterval
                )
                onSave(updatedScreen)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            enabled = prompt.isNotBlank() && fetchIntervalStr.isNotBlank() && fetchIntervalStr != "0"
        ) {
            Icon(
                painter = painterResource(id = R.drawable.save_24px),
                contentDescription = "Save icon"
            )
            Text("Save AI Text Screen")
        }
    }
}