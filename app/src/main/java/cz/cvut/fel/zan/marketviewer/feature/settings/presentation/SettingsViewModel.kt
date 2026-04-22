package cz.cvut.fel.zan.marketviewer.feature.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.cvut.fel.zan.marketviewer.core.data.local.ServerConfigManager
import cz.cvut.fel.zan.marketviewer.core.data.local.ThemeSettingsManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsUiState(
    val serverUrl: String = "",
    val isDarkMode: Boolean? = null,
    val useDynamicColor: Boolean = false
)

class SettingsViewModel(
    private val themeSettingsManager: ThemeSettingsManager,
    private val serverConfigManager: ServerConfigManager
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        themeSettingsManager.themeFlow,
        serverConfigManager.serverUrlFlow
    ) { themeState, serverUrl ->
        SettingsUiState(
            serverUrl = serverUrl,
            isDarkMode = themeState.isDarkMode,
            useDynamicColor = themeState.useDynamicColor
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    fun saveThemePreference(isDark: Boolean?) {
        viewModelScope.launch {
            themeSettingsManager.saveDarkMode(isDark)
        }
    }

    fun saveDynamicColorPreference(useDynamic: Boolean) {
        viewModelScope.launch {
            themeSettingsManager.saveDynamicColor(useDynamic)
        }
    }

    fun saveServerUrl(url: String) {
        viewModelScope.launch {
            // Basic sanitization
            var safeUrl = url.trim()
            if (!safeUrl.startsWith("http://") && !safeUrl.startsWith("https://")) {
                safeUrl = "http://$safeUrl"
            }
            if (!safeUrl.endsWith("/")) {
                safeUrl = "$safeUrl/"
            }
            serverConfigManager.saveServerUrl(safeUrl)
        }
    }


}