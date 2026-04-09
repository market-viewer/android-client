package cz.cvut.fel.zan.marketviewer.feature.devices.presentation.detail

import android.content.ClipData
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import cz.cvut.fel.zan.marketviewer.R
import kotlinx.coroutines.launch

@Composable
fun DeviceHashDisplay(
    deviceHash: String?
) {
    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }

    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
    ) {
        OutlinedTextField(
            value = deviceHash ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Device hash") },
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions( keyboardType = KeyboardType.Password),
            modifier = Modifier.weight(1f).padding(end = 10.dp),
            trailingIcon = {
                IconButton(onClick = {isPasswordVisible = !isPasswordVisible}) {
                    Icon(
                        painter = painterResource(id = if (isPasswordVisible) R.drawable.visibility_off_24px else R.drawable.visibility_24px),
                        contentDescription = "visibility toggle"
                    )
                }
            }
        )

        CopyButtonWithTooltip {
            // Only copy if the hash actually exists
            deviceHash?.let { hash ->
                scope.launch {
                    val clipData = ClipData.newPlainText("Device Hash", hash)
                    // B: Wrap it in the Compose ClipEntry and set it
                    clipboard.setClipEntry(ClipEntry(clipData))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CopyButtonWithTooltip(
    onCopyClick: () -> Unit
)
{
    TooltipBox(
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
            TooltipAnchorPosition.Above,
            10.dp
        ),
        tooltip = {
            PlainTooltip { Text("Paste into the device wifi portal.") }
        },
        state = rememberTooltipState()
    ) {
        IconButton(onClick = onCopyClick) {
            Icon(
                painter = painterResource(id = R.drawable.content_copy_24px),
                contentDescription = "Copy hash to clipboard",
            )
        }
    }
}