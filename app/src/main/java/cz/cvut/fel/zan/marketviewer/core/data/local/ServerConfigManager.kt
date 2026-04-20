package cz.cvut.fel.zan.marketviewer.core.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import cz.cvut.fel.zan.marketviewer.core.utils.backendBaseUrl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

// Creates the DataStore instance
private val Context.serverDataStore by preferencesDataStore(name = "server_settings")

class ServerConfigManager(context: Context) {

    companion object {
        val SERVER_URL_KEY = stringPreferencesKey("server_url")
        const val DEFAULT_URL = backendBaseUrl
    }

    private val dataStore = context.serverDataStore

    // in-memory cache for Ktor to read synchronously
    var currentBaseUrl: String = DEFAULT_URL
        private set

    // flow for the UI to observe changes
    val serverUrlFlow: Flow<String> = dataStore.data.map { preferences ->
        preferences[SERVER_URL_KEY] ?: DEFAULT_URL
    }

    init {
        CoroutineScope(Dispatchers.IO).launch {
            serverUrlFlow.collect { newUrl ->
                currentBaseUrl = if (newUrl.endsWith("/")) newUrl else "$newUrl/"
            }
        }
    }

    suspend fun saveServerUrl(newUrl: String) {
        if (newUrl.isEmpty()) return

        dataStore.edit { preferences ->
            preferences[SERVER_URL_KEY] = newUrl
        }
    }
}