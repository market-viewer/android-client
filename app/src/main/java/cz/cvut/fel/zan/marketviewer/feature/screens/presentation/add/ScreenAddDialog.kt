package cz.cvut.fel.zan.marketviewer.feature.screens.presentation.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import cz.cvut.fel.zan.marketviewer.R
import cz.cvut.fel.zan.marketviewer.feature.screens.domain.model.ScreenType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenAddDialog(
    onDismiss: () -> Unit,
    onConfirm: (ScreenType) -> Unit,
    errorMsg: String?
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf(ScreenType.entries.first()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                painter = painterResource(id = R.drawable.outline_fit_screen_24),
                contentDescription = null,
            )
        },
        title = {
            Text(text = "Add screen")
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Select the type of screen you want to add to this device.")

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                        readOnly = true,
                        value = selectedType.displayName,
                        onValueChange = {},
                        label = { Text("Screen Type") },
                        leadingIcon = { InputIcon(selectedType) },
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        ScreenType.entries.forEach { selectionOption ->
                            DropdownMenuItem(
                                leadingIcon = { InputIcon(selectionOption) },
                                text = { Text(selectionOption.displayName) },
                                onClick = {
                                    selectedType = selectionOption
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                if (errorMsg != null) {
                    Text(text = errorMsg, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedType) },
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel", color = MaterialTheme.colorScheme.secondary)
            }
        }
    )
}

@Composable
fun InputIcon(screenType: ScreenType) {
    Icon(
        painter = painterResource(id = getIconForScreenType(screenType)),
        contentDescription = "Item icon",
        modifier = Modifier.size(24.dp),
        tint = MaterialTheme.colorScheme.secondary
    )
}

private fun getIconForScreenType(screenType: ScreenType): Int {
    return when (screenType) {
        ScreenType.CRYPTO -> R.drawable.currency_bitcoin_40px
        ScreenType.STOCK -> R.drawable.finance_mode_40px
        ScreenType.TIMER -> R.drawable.timer_40px
        ScreenType.CLOCK -> R.drawable.nest_clock_farsight_analog_40px
        ScreenType.AI_TEXT -> R.drawable.network_intel_node_40px
    }
}