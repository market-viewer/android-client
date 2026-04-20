package cz.cvut.fel.zan.marketviewer.feature.devices.presentation.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.SemanticsProperties.ImeAction
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cz.cvut.fel.zan.marketviewer.R

@Composable
fun DeviceNameTitle(
    deviceName: String?,
    isEditing: Boolean,
    nameChangeErrorMsg: String?,
    onNameChangeSave: (String) -> Unit,
    onToggleEdit: () -> Unit,
    isEnabled: Boolean
) {
    var newName by remember(deviceName, isEditing) { mutableStateOf(deviceName ?: "") }
    val focusManager = LocalFocusManager.current

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Device name:",
                    color = MaterialTheme.colorScheme.secondary
                )

                Row(verticalAlignment = Alignment.CenterVertically) {

                    // show revert button when editing
                    if (isEditing) {
                        IconButton(
                            onClick = {
                                onToggleEdit()
                                focusManager.clearFocus()
                            }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.outline_arrow_back_24),
                                contentDescription = "Cancel edit",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            if (isEditing) {
                                onNameChangeSave(newName)
                                focusManager.clearFocus()
                            } else {
                                onToggleEdit()
                            }
                        },
                        enabled = if (isEditing) newName.isNotBlank() else isEnabled
                    ) {
                        Icon(
                            painter = painterResource(
                                id =  if (isEditing) R.drawable.outline_check_24 else R.drawable.outline_edit_24
                            ),
                            contentDescription = "Toggle edit and save button"
                        )
                    }
                }

            }

            //display either input field or text filed (depending on editing state)
            if (isEditing) {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    isError = !nameChangeErrorMsg.isNullOrEmpty(),
                    keyboardOptions = KeyboardOptions(imeAction = androidx.compose.ui.text.input.ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            onNameChangeSave(newName)
                            focusManager.clearFocus()
                        }
                    )
                )
                if (!nameChangeErrorMsg.isNullOrEmpty()) {
                    Text(
                        text = nameChangeErrorMsg,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                Text(
                    text = deviceName ?: "Unknown name",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

        }

    }
}