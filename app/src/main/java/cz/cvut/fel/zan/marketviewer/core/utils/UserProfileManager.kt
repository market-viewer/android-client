package cz.cvut.fel.zan.marketviewer.core.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import cz.cvut.fel.zan.marketviewer.feature.profile.domain.model.ApiKeyProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Notice the separate file name! It doesn't mix with auth_prefs.
val Context.profileDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_profile")

data class UserProfile(
    val username: String?,
    val userId: Int?,
    val savedProviders: Set<ApiKeyProvider>
)

class UserProfileManager(private val context: Context) {

    companion object {
        private val USERNAME_KEY = stringPreferencesKey("username")
        private val USER_ID_KEY = intPreferencesKey("user_id")
        private val SAVED_PROVIDERS_KEY = stringSetPreferencesKey("saved_providers")
    }

    val profileFlow: Flow<UserProfile> = context.profileDataStore.data.map { prefs ->
        val providerStrings = prefs[SAVED_PROVIDERS_KEY] ?: emptySet()

        // 2. Safely map them back to your Enums
        val providers = providerStrings.mapNotNull { stringValue ->
            ApiKeyProvider.entries.find { it.name == stringValue }
        }.toSet()

        UserProfile(
            username = prefs[USERNAME_KEY],
            userId = prefs[USER_ID_KEY],
            savedProviders = providers
        )
    }

    // Call this right after a successful Login
    suspend fun saveInitialProfile(username: String? = null, userId: Int, savedProviders: Set<String> = emptySet()) {
        context.profileDataStore.edit { prefs ->
            prefs[USERNAME_KEY] = username ?: ""
            prefs[USER_ID_KEY] = userId
            prefs[SAVED_PROVIDERS_KEY] = savedProviders
        }
    }

    // Call this when they hit "Save" on the username input!
    suspend fun updateUsername(newUsername: String) {
        context.profileDataStore.edit { prefs ->
            prefs[USERNAME_KEY] = newUsername
        }
    }

    suspend fun updateProviders(providers: Set<ApiKeyProvider>) {
        context.profileDataStore.edit { prefs ->
            prefs[SAVED_PROVIDERS_KEY] = providers.map { it.name }.toSet()
        }
    }

    // Call this when they hit "Save" or "Delete" on an API key!
    suspend fun updateKeyStatus(provider: ApiKeyProvider?, isSet: Boolean) {
        if (provider == null) return

        context.profileDataStore.edit { prefs ->
            val currentProviders = prefs[SAVED_PROVIDERS_KEY]?.toMutableSet() ?: mutableSetOf()

            if (isSet) {
                currentProviders.add(provider.name)
            } else {
                currentProviders.remove(provider.name)
            }

            prefs[SAVED_PROVIDERS_KEY] = currentProviders
        }
    }

    // Call this from your Logout event
    suspend fun clearProfile() {
        context.profileDataStore.edit { it.clear() }
    }
}