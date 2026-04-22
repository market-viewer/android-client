package cz.cvut.fel.zan.marketviewer.feature.settings.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.cvut.fel.zan.marketviewer.R
import cz.cvut.fel.zan.marketviewer.core.presentation.components.MarketViewerScaffold
import cz.cvut.fel.zan.marketviewer.core.presentation.components.MarketViewerTopAppBar
import cz.cvut.fel.zan.marketviewer.core.presentation.theme.MarketViewerTheme
import cz.cvut.fel.zan.marketviewer.core.utils.defaultBackendUrl
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import kotlin.Boolean
import kotlin.String

@Composable
fun SettingsScreen(
    onDrawerOpen: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    SettingsScreenContent(
        onDrawerOpen = onDrawerOpen,
        snackbarHostState = snackbarHostState,
        onThemeChange = viewModel::saveThemePreference,
        onDynamicColorChange = viewModel::saveDynamicColorPreference,
        onServerUrlSave = viewModel::saveServerUrl,
        serverUrl = state.serverUrl,
        isDarkMode = state.isDarkMode,
        useDynamicColor = state.useDynamicColor
    )
}

@Composable
fun SettingsScreenContent(
    onDrawerOpen: () -> Unit,
    snackbarHostState: SnackbarHostState,
    serverUrl: String,
    isDarkMode: Boolean?,
    useDynamicColor: Boolean,
    onThemeChange: (Boolean?) -> Unit,
    onDynamicColorChange: (Boolean) -> Unit,
    onServerUrlSave: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()

    var showSaveUrlDialog by remember { mutableStateOf(false) }

    var serverUrlInput by remember(serverUrl) { mutableStateOf(serverUrl) }

    MarketViewerScaffold(
        topBar = {
            MarketViewerTopAppBar(
                title = "Settings",
                navigationIcon = R.drawable.outline_menu_24,
                onNavigationClick = onDrawerOpen,
            )
        },
        snackbarHostState = snackbarHostState
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            SettingsSectionTitle(
                title = "Appearance",
                icon = painterResource(id = R.drawable.routine_24px)
            )

            // New Theme Card buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Light Mode
                ThemeCardButton(
                    themeMode = ThemeMode.LIGHT,
                    selected = isDarkMode == false,
                    onClick = { onThemeChange(false) },
                    modifier = Modifier.weight(1f)
                )

                // System Default
                ThemeCardButton(
                    themeMode = ThemeMode.SYSTEM,
                    selected = isDarkMode == null,
                    onClick = { onThemeChange(null) },
                    modifier = Modifier.weight(1f)
                )

                // Dark Mode
                ThemeCardButton(
                    themeMode = ThemeMode.DARK,
                    selected = isDarkMode == true,
                    onClick = { onThemeChange(true) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onDynamicColorChange(!useDynamicColor) }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.palette_24px),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Dynamic Color",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Use wallpaper colors for the app theme (Android 12+)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Switch(
                    checked = useDynamicColor,
                    onCheckedChange = null
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            SettingsSectionTitle(
                title = "Network",
                icon = painterResource(id = R.drawable.hard_drive_24px)
            )

            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text(
                    text = "Backend Server URL",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "The address of your self-hosted instance.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = serverUrlInput,
                    onValueChange = { serverUrlInput = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Uri,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            onServerUrlSave(serverUrlInput)
                            focusManager.clearFocus()
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Server URL saved")
                            }
                        }
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(
                        onClick = {
                            serverUrlInput = defaultBackendUrl
                            focusManager.clearFocus()
                        },
                    ) {
                        Text("Reset to default")
                    }

                    Button(
                        onClick = {
                            showSaveUrlDialog = true
                            focusManager.clearFocus()
                        },
                        enabled = serverUrl != serverUrlInput
                    ) {
                        Text("Save URL")
                    }
                }
            }
        }
    }

    if (showSaveUrlDialog) {
        SaveUrlDialog(
            onDismiss = {showSaveUrlDialog = false},
            onConfirm = {
                onServerUrlSave(serverUrlInput)
                showSaveUrlDialog = false
            }
        )
    }
}

@Composable
private fun SettingsSectionTitle(
    title: String,
    icon: Painter? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        if (icon != null) {
            Icon(
                painter = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
        }
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
    }
}



@Preview
@Composable
fun SettingsScreenPreview() {
    MarketViewerTheme {
        SettingsScreenContent(
            onDrawerOpen = {},
            snackbarHostState = SnackbarHostState(),
            onThemeChange = {},
            onDynamicColorChange = {},
            onServerUrlSave = {},
            useDynamicColor = false,
            isDarkMode = false,
            serverUrl = ""
        )
    }
}