package cz.cvut.fel.zan.marketviewer.core.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.authProvider
import io.ktor.client.plugins.auth.authProviders
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map

// init datastore
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

class TokenManager(
    private val context: Context
) {

    private val _loggedOutEvent = MutableSharedFlow<Unit>()
    val loggedOutEvent = _loggedOutEvent.asSharedFlow()

    companion object {
        private val JWT_TOKEN_KEY = stringPreferencesKey("jwt_token")
    }

    val tokenFlow: Flow<String?> = context.dataStore.data
        .map {
            preferences -> preferences[JWT_TOKEN_KEY]
        }

    suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[JWT_TOKEN_KEY] = token
        }
    }

    private suspend fun clearToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(JWT_TOKEN_KEY)
        }
    }

    suspend fun forceLogout() {
        clearToken()
        _loggedOutEvent.emit(Unit)
    }

}