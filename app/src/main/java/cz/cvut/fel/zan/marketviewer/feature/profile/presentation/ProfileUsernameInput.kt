package cz.cvut.fel.zan.marketviewer.feature.profile.presentation

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cz.cvut.fel.zan.marketviewer.R

@Composable
fun ProfileUsernameInput(
    savedUsername: String?,
    onSave: (String) -> Unit
) {
    var currentInput by remember(savedUsername) { mutableStateOf(savedUsername) }

    val isModified = currentInput?.trim() != savedUsername && currentInput != null
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = currentInput ?: "Unknown name",
        onValueChange = { currentInput = it },
        label = { Text("Username") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        readOnly = currentInput == null,
        shape = RoundedCornerShape(12.dp),
        keyboardActions = KeyboardActions(
            onDone = {
                if (isModified && currentInput != null) {
                    onSave(currentInput!!)
                    focusManager.clearFocus()
                }
            }
        ),
        textStyle = MaterialTheme.typography.headlineSmall.copy(
            fontWeight = FontWeight.Bold,
//            textAlign = TextAlign.Center
        ),
        trailingIcon = {
            if (isModified && currentInput != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {

                    //revert name
                    IconButton(onClick = {
                        currentInput = savedUsername
                        focusManager.clearFocus()
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.outline_arrow_back_24),
                            contentDescription = "Revert changes",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    //save name
                    if (!currentInput.isNullOrBlank()) {
                        IconButton(
                            onClick = {
                                onSave(currentInput!!)
                                focusManager.clearFocus()
                            },
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.outline_check_24),
                                contentDescription = "Confirm name edit",
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                }
            }
        }
    )
}