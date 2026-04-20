package cz.cvut.fel.zan.marketviewer.feature.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.cvut.fel.zan.marketviewer.core.domain.ApiResult
import cz.cvut.fel.zan.marketviewer.core.data.local.UserProfileManager
import cz.cvut.fel.zan.marketviewer.feature.profile.domain.model.ApiKeyProvider
import cz.cvut.fel.zan.marketviewer.feature.profile.domain.repository.ProfileRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileScreenState(
    val username: String? = null,
    val savedProviders: Set<ApiKeyProvider> = emptySet(),
    val isLoading: Boolean = true,
    val isApiKeyVerifying: Boolean = false,
    val isDeletingAccount: Boolean = false
)

class ProfileViewModel(
    private val profileRepository: ProfileRepository,
    private val userProfileManager: UserProfileManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileScreenState())
    val uiState: StateFlow<ProfileScreenState> = _uiState.asStateFlow()

    private val _uiEffect = Channel<ProfileEffects>()
    val uiEffect = _uiEffect.receiveAsFlow()

    //get the user data
    init {
        viewModelScope.launch {
            //update the data to the ui state
            userProfileManager.profileFlow.collect { profile ->
                _uiState.update { currentState ->
                    currentState.copy(
                        username = profile.username,
                        savedProviders = profile.savedProviders,
                        isLoading = false
                    )
                }
            }
        }
        viewModelScope.launch {
            //fetch the current data form remote
            getProfileData()
        }
    }

    fun onEvent(event: ProfileEvents) {
        when (event) {
            is ProfileEvents.UsernameUpdate -> {
                updateUsername(event.newUsername)
            }
            is ProfileEvents.SetApiKey -> {
                updateApiKey(event.apiKeyProvider, event.keyValue)
            }
            is ProfileEvents.DeleteApiKey -> {
                deleteApiKey(event.apiKeyProvider)
            }
            is ProfileEvents.DeleteAccount -> {
                deleteUserAccount()
            }
        }
    }

    private suspend fun getProfileData() {
        when (val result = profileRepository.getUsernameAndApiKeyInfo()) {
            is ApiResult.Success -> {
                //update username
                val username = result.data.username
                userProfileManager.updateUsername(username)

                //update api keys data
                val apiKeyProviders = result.data.apiKeyProviders.mapNotNull { stringValue ->
                    ApiKeyProvider.entries.find { it.name == stringValue }
                }.toSet()

                userProfileManager.updateProviders(apiKeyProviders)
            }
            is ApiResult.Error -> {
                //show the snackbar error
                _uiEffect.send(ProfileEffects.ShowSnackbar(result.message))
            }
        }
    }

    private fun updateUsername(newUsername: String) {
        viewModelScope.launch {
            when (val result = profileRepository.updateUsername(newUsername)) {
                is ApiResult.Success -> {
                    userProfileManager.updateUsername(result.data)
                }
                is ApiResult.Error -> {
                    //show the snackbar error
                    _uiEffect.send(ProfileEffects.ShowSnackbar(result.message))
                }
            }
        }
    }

    private fun updateApiKey(keyProvider: ApiKeyProvider, keyValue: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isApiKeyVerifying = true) }

            when (val result = profileRepository.updateApiKey(keyProvider, keyValue)) {
                is ApiResult.Success -> {

                    userProfileManager.updateKeyStatus(keyProvider, true)
                }
                is ApiResult.Error -> {
                    //show the snackbar error
                    _uiEffect.send(ProfileEffects.ShowSnackbar(result.message))
                }
            }
            _uiState.update { it.copy(isApiKeyVerifying = false) }
        }
    }

    private fun deleteApiKey(apiKeyProvider: ApiKeyProvider) {
        viewModelScope.launch {
            when (val result = profileRepository.deleteApiKey(apiKeyProvider)) {
                is ApiResult.Success -> {
                    userProfileManager.updateKeyStatus(apiKeyProvider, false)
                }
                is ApiResult.Error -> {
                    //show the snackbar error
                    _uiEffect.send(ProfileEffects.ShowSnackbar(result.message))
                }
            }
        }
    }

    private fun deleteUserAccount() {
        if (_uiState.value.isDeletingAccount) return

        viewModelScope.launch {
            _uiState.update { it.copy(isDeletingAccount = true) }

            when (val result = profileRepository.deleteAccount()) {
                is ApiResult.Success -> {
                    _uiEffect.send(ProfileEffects.AccountDeleted)
                }
                is ApiResult.Error -> {
                    //show the snackbar error
                    _uiEffect.send(ProfileEffects.ShowSnackbar(result.message))
                    _uiState.update { it.copy(isDeletingAccount = false) }
                }
            }

        }
    }


    sealed interface ProfileEvents {
        data class UsernameUpdate(val newUsername: String) : ProfileEvents
        data class SetApiKey(val apiKeyProvider: ApiKeyProvider, val keyValue: String) : ProfileEvents
        data class DeleteApiKey(val apiKeyProvider: ApiKeyProvider) : ProfileEvents
        data object DeleteAccount : ProfileEvents
    }

    sealed interface ProfileEffects {
        data class ShowSnackbar(val message: String) : ProfileEffects
        data object AccountDeleted : ProfileEffects
    }
}