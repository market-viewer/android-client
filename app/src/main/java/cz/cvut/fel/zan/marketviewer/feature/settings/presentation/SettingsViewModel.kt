package cz.cvut.fel.zan.marketviewer.feature.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.cvut.fel.zan.marketviewer.core.data.local.ServerConfigManager
import cz.cvut.fel.zan.marketviewer.core.data.local.ThemeSettingsManager
import cz.cvut.fel.zan.marketviewer.core.data.local.TokenManager
import cz.cvut.fel.zan.marketviewer.core.utils.defaultBackendUrl
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsState(
    val serverUrl: String = "",
    val isDarkMode: Boolean? = null,
    val useDynamicColor: Boolean = false
)

class SettingsViewModel(
    private val themeSettingsManager: ThemeSettingsManager,
    private val serverConfigManager: ServerConfigManager,
    private val tokenManager: TokenManager
) : ViewModel() {

    // we dont need the private and public flow because the state is ONLY from datastore data
    val uiState: StateFlow<SettingsState> = combine(
        themeSettingsManager.themeFlow,
        serverConfigManager.serverUrlFlow
    ) { themeState, serverUrl ->
        SettingsState(
            serverUrl = serverUrl,
            isDarkMode = themeState.isDarkMode,
            useDynamicColor = themeState.useDynamicColor
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsState()
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
        //save new url and logout user
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

            tokenManager.forceLogout()
        }
    }



}