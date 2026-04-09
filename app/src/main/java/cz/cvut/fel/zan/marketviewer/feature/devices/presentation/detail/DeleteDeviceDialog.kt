package cz.cvut.fel.zan.marketviewer.feature.devices.presentation.detail

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import cz.cvut.fel.zan.marketviewer.R

@Composable
fun DeleteDeviceDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,

) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
          Icon(
              painter = painterResource(id = R.drawable.outline_delete_forever_24),
              contentDescription = null,
          )
        },
        title = {
            Text(text = "Delete Device")
        },
        text = {
            Text(text = "Are you sure you want to delete this device? This action cannot be undone.")
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            ) {
                Text("Delete")
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