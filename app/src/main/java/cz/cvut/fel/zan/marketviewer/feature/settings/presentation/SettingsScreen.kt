package cz.cvut.fel.zan.marketviewer.feature.settings.presentation

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import kotlin.Boolean
import kotlin.String

// --- New internal enum for UI handling ---
private enum class UIThemeMode {
    LIGHT, DARK, SYSTEM
}

// --- Mapper from Boolean? to UIThemeMode ---
private fun Boolean?.toUIThemeMode(): UIThemeMode {
    return when (this) {
        true -> UIThemeMode.DARK
        false -> UIThemeMode.LIGHT
        null -> UIThemeMode.SYSTEM
    }
}

// --- Mapper from UIThemeMode back to Boolean? ---
private fun UIThemeMode.toPreferenceValue(): Boolean? {
    return when (this) {
        UIThemeMode.DARK -> true
        UIThemeMode.LIGHT -> false
        UIThemeMode.SYSTEM -> null
    }
}

@Composable
fun SettingsScreen(
    onDrawerOpen: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SettingsScreenContent(
        uiState = uiState,
        onDrawerOpen = onDrawerOpen,
        snackbarHostState = snackbarHostState,
        onThemeChange = viewModel::saveThemePreference,
        onDynamicColorChange = viewModel::saveDynamicColorPreference,
        onServerUrlSave = viewModel::saveServerUrl
    )
}

@Composable
fun SettingsScreenContent(
    uiState: SettingsUiState,
    onDrawerOpen: () -> Unit,
    snackbarHostState: SnackbarHostState,
    onThemeChange: (Boolean?) -> Unit,
    onDynamicColorChange: (Boolean) -> Unit,
    onServerUrlSave: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()

    // Local state for the text field so it doesn't jump while typing
    var serverUrlInput by remember(uiState.serverUrl) { mutableStateOf(uiState.serverUrl) }

    val currentUIThemeMode = uiState.isDarkMode.toUIThemeMode()

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

            // --- APPEARANCE SECTION WITH ICONS ---
            SettingsSectionTitle(
                title = "Appearance",
                icon = painterResource(id = R.drawable.brightness_7_24px)
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
                    mode = UIThemeMode.LIGHT,
                    selected = currentUIThemeMode == UIThemeMode.LIGHT,
                    onClick = { onThemeChange(UIThemeMode.LIGHT.toPreferenceValue()) },
                    modifier = Modifier.weight(1f)
                )

                // System Default
                ThemeCardButton(
                    mode = UIThemeMode.SYSTEM,
                    selected = currentUIThemeMode == UIThemeMode.SYSTEM,
                    onClick = { onThemeChange(UIThemeMode.SYSTEM.toPreferenceValue()) },
                    modifier = Modifier.weight(1f)
                )

                // Dark Mode
                ThemeCardButton(
                    mode = UIThemeMode.DARK,
                    selected = currentUIThemeMode == UIThemeMode.DARK,
                    onClick = { onThemeChange(UIThemeMode.DARK.toPreferenceValue()) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dynamic Color Toggle WITH ICONS
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onDynamicColorChange(!uiState.useDynamicColor) }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.android_24px),
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
                    checked = uiState.useDynamicColor,
                    onCheckedChange = null // Handled by the Row click
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

            // --- NETWORK SECTION WITH ICONS ---
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
                Button(
                    onClick = {
                        onServerUrlSave(serverUrlInput)
                        focusManager.clearFocus()
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Server URL saved")
                        }
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Save URL")
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
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

// --- New ThemeCardButton composable ---
@Composable
private fun ThemeCardButton(
    mode: UIThemeMode,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = if (selected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surface
    }

    val contentColor = if (selected) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    val border = if (selected) {
        BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
    } else {
        BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
    }

    val icon = when (mode) {
        UIThemeMode.LIGHT -> painterResource(id = R.drawable.brightness_7_24px)
        UIThemeMode.DARK -> painterResource(id = R.drawable.brightness_empty_24px)
        UIThemeMode.SYSTEM -> painterResource(id = R.drawable.android_24px)
    }

    val label = when (mode) {
        UIThemeMode.LIGHT -> "Light Mode"
        UIThemeMode.DARK -> "Dark Mode"
        UIThemeMode.SYSTEM -> "System Default"
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        border = border,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = contentColor
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                color = contentColor
            )
        }
    }
}

@Preview
@Composable
fun SettingsScreenPreview() {
    MarketViewerTheme {
        SettingsScreenContent(
            uiState = SettingsUiState(
                serverUrl = "",
                isDarkMode = null,
                useDynamicColor = false
            ),
            onDrawerOpen = {},
            snackbarHostState = SnackbarHostState(),
            onThemeChange = {},
            onDynamicColorChange = {},
            onServerUrlSave = {}
        )
    }
}