package cz.cvut.fel.zan.marketviewer.core.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.themeDataStore by preferencesDataStore(name = "theme_settings")

data class ThemeState(
    val isDarkMode: Boolean? = null,
    val useDynamicColor: Boolean = false
)

class ThemeSettingsManager(context: Context) {

    companion object {
        val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
        val DYNAMIC_COLOR_KEY = booleanPreferencesKey("dynamic_color")
    }

    private val dataStore = context.themeDataStore

    val themeFlow: Flow<ThemeState> = dataStore.data.map { preferences ->
        ThemeState(
            isDarkMode = preferences[DARK_MODE_KEY],
            useDynamicColor = preferences[DYNAMIC_COLOR_KEY] ?: false
        )
    }

    suspend fun saveDarkMode(isDark: Boolean?) {
        dataStore.edit { preferences ->
            if (isDark == null) {
                preferences.remove(DARK_MODE_KEY)
            } else {
                preferences[DARK_MODE_KEY] = isDark
            }
        }
    }

    suspend fun saveDynamicColor(useDynamic: Boolean) {
        dataStore.edit { preferences ->
            preferences[DYNAMIC_COLOR_KEY] = useDynamic
        }
    }
}