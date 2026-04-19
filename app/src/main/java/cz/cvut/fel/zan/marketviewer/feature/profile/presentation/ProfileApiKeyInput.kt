package cz.cvut.fel.zan.marketviewer.feature.profile.presentation

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import cz.cvut.fel.zan.marketviewer.R

@Composable
fun ProfileApiKeyInput(
    providerName: String,
    providerLogo: Int,
    isKeySet: Boolean,
    providerUrl: String,
    isInProgress: Boolean,
    onSave: (String) -> Unit,
    onDelete: () -> Unit
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }

    var currentInput by rememberSaveable { mutableStateOf("") }

    val focusManager = LocalFocusManager.current
    val uriHandler = LocalUriHandler.current

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHighest
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { isExpanded = !isExpanded }
                    .padding(vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = providerLogo),
                        contentDescription = "$providerName Logo",
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = providerName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isKeySet) {
                        Icon(
                            painter = painterResource(id = R.drawable.outline_check_24),
                            contentDescription = "Key is saved",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Icon(
                            painter = painterResource(id = R.drawable.close_24px),
                            contentDescription = "Key is missing",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Icon(
                        painter = painterResource(
                            id = if (isExpanded) R.drawable.outline_arrow_drop_up_24 else R.drawable.outline_arrow_drop_down_24
                        ),
                        contentDescription = "Toggle Input",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = currentInput,
                    onValueChange = { currentInput = it },
                    label = { Text(if (isKeySet) "Overwrite saved" else "Enter API Key") },
                    shape = RoundedCornerShape(12.dp),
                    readOnly = isInProgress,
                    singleLine = true,
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (currentInput.isNotBlank()) {
                                onSave(currentInput)
                                currentInput = ""
                                isExpanded = false
                                focusManager.clearFocus()
                            }
                        }
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(
                                painter = painterResource(id = if (isPasswordVisible) R.drawable.visibility_off_24px else R.drawable.visibility_24px),
                                contentDescription = "Toggle visibility",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // 1. URL Link Button
                    IconButton(onClick = { uriHandler.openUri(providerUrl) }) {
                        Icon(
                            painter = painterResource(id = R.drawable.open_in_new_24px),
                            contentDescription = "Get API Key from website",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // 2. Dynamic Actions based on whether they are typing
                    if (currentInput.isNotBlank()) {
                        // User is typing -> Show Cancel and Save
                        TextButton(
                            onClick = {
                                currentInput = "" // Just clear the input field
                                focusManager.clearFocus()
                            },
                            enabled = !isInProgress,
                            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onSurfaceVariant)
                        ) {
                            Text("Cancel")
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                onSave(currentInput)
                                currentInput = ""
                                isExpanded = false
                                focusManager.clearFocus()
                            },
                            enabled = !isInProgress
                        ) {
                            Text("Save")
                        }

                    } else if (isKeySet) {
                        // User is NOT typing, but a key exists -> Show Delete
                        TextButton(
                            onClick = {
                                onDelete()
                                isExpanded = false // Auto-collapse after delete,
                            },
                            enabled = !isInProgress,
                            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.outline_delete_24), // Add a trash can icon!
                                contentDescription = "Delete Key",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Delete Key")
                        }
                    }
                }
            }
        }
    }
}