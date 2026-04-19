package cz.cvut.fel.zan.marketviewer.feature.auth.presentation.register

import android.content.ClipData
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.toClipEntry
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun RecoveryCodesDialog(
    recoveryCodes: List<String>,
    onRecoveryOkClick: () -> Unit
) {
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = {
            // on click outside the dialog
        },
        title = { Text(text = "Registration Successful") },
        text = {
            Column {
                Text(
                    text = buildAnnotatedString {
                        append("Please save these ")

                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)) {
                            append("recovery keys ")
                        }
                        append("securely. You will not be shown these again.\n\n")

                        append("This will be the ONLY way to recover your account.")
                    },

                    )
                Spacer(modifier = Modifier.height(16.dp))
                recoveryCodes.forEach { code ->
                    Text(
                        text = code,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    HorizontalDivider(thickness = 2.dp)
                }
            }
        },
        confirmButton = {
            Button(onClick = onRecoveryOkClick) {
                Text("OK")
            }
        },
        //button for copy all data to clipboard
        dismissButton = {
            TextButton(
                onClick = {
                    val formattedCodes: String = recoveryCodes.joinToString(separator = "\n")
                    val clipData = ClipData.newPlainText("Recovery Codes", formattedCodes)
                    scope.launch {
                        clipboard.setClipEntry(clipData.toClipEntry())
                    }
                }
            ) {
                Text("Copy")
            }
        }
//        modifier = Modifier.fillMaxSize().fillMaxWidth()
    )
}