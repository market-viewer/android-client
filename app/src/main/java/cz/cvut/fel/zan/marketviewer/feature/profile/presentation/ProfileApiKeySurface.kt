package cz.cvut.fel.zan.marketviewer.feature.profile.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import cz.cvut.fel.zan.marketviewer.R
import cz.cvut.fel.zan.marketviewer.feature.profile.domain.model.ApiKeyProvider

@Composable
fun ProfileAPIKeySurface(
    modifier: Modifier = Modifier,
    onSave: (ApiKeyProvider, String) -> Unit,
    onDelete: (ApiKeyProvider) -> Unit,
    savedProviders: Set<ApiKeyProvider>,
    isApiKeyVerifying: Boolean,
    ) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            //title header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(text = "Third-party API keys", color = MaterialTheme.colorScheme.secondary)
                InfoButton()
            }

            //loading bar
            Box(modifier = Modifier.fillMaxWidth().height(10.dp).padding(bottom = 5.dp)) {
                if (isApiKeyVerifying) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // list of keys
            Column (
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .padding(horizontal = 15.dp)
                    .verticalScroll(rememberScrollState())
            ) {

                //api keys blocks
                apiProviderData.forEach { providerData ->
                    ProfileApiKeyInput(
                        providerName = providerData.apiKeyProvider.displayName,
                        providerLogo = providerData.providerLogo,
                        isKeySet = savedProviders.contains(providerData.apiKeyProvider),
                        isInProgress = isApiKeyVerifying,
                        providerUrl = providerData.providerUrl,
                        onDelete = { onDelete(providerData.apiKeyProvider) },
                        onSave = { inputValue -> onSave(providerData.apiKeyProvider, inputValue) }
                    )
                }

            }
        }
    }
}

@Composable
fun InfoButton(
) {
    var showInfo by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { showInfo = true }) {
            Icon(
                painter = painterResource(id = R.drawable.outline_info_24),
                contentDescription = "Info about API key safety",
                tint = MaterialTheme.colorScheme.secondary
            )
        }

        DropdownMenu(
            expanded = showInfo,
            onDismissRequest = { showInfo = false }
        ) {
            Text(
                text = "Your API keys are stored encrypted on a server." +
                        " Both required Stock and Crypto API keys are 100% free to generate. If you don't trust us," +
                        " don't add your payed keys.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .widthIn(max = 250.dp)
            )
        }
    }
}