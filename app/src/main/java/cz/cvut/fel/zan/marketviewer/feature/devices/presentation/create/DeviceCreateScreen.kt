package cz.cvut.fel.zan.marketviewer.feature.devices.presentation.create

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun DeviceCreateScreen(onDeviceCreate: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Device Create")
        Button(onClick = onDeviceCreate) {
            Text("Create")
        }
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun DeviceCreatePreview() {
    DeviceCreateScreen(
        onDeviceCreate = { }
    )
}