package cz.cvut.fel.zan.marketviewer.feature.devices.presentation.detail

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun ScreenDeleteDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    selectedCount: Int
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Screens") },
        text = { Text("Are you sure you want to delete $selectedCount selected screen(s)?") },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = MaterialTheme.colorScheme.secondary) }
        }
    )
}