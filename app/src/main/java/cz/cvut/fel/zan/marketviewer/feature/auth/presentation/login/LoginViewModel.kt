package cz.cvut.fel.zan.marketviewer.feature.auth.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.cvut.fel.zan.marketviewer.core.utils.JwtDecoder
import cz.cvut.fel.zan.marketviewer.core.utils.TokenManager
import cz.cvut.fel.zan.marketviewer.core.utils.UserProfileManager
import cz.cvut.fel.zan.marketviewer.feature.auth.domain.model.LoginResult
import cz.cvut.fel.zan.marketviewer.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginScreenState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

class LoginViewModel(
    private val repository: AuthRepository,
    private val tokenManager: TokenManager,
    private val userProfileManager: UserProfileManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginScreenState())
    val uiState: StateFlow<LoginScreenState> = _uiState.asStateFlow()

    // one time effects like navigation
    private val _uiEffect = Channel<LoginEffect>()
    val uiEffect = _uiEffect.receiveAsFlow()

    init {
        checkExistingToken()
    }

    //check if user have saved token
    private fun checkExistingToken() {
        viewModelScope.launch {
            val token = tokenManager.tokenFlow.first()
            if (!token.isNullOrBlank()) {
                //token found, navigate to device list screen
                _uiEffect.send(LoginEffect.NavigateToDeviceListScreen)
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onEvent(event: LoginScreenEvent) {
        when (event) {
            is LoginScreenEvent.LoginClick -> performLogin()
            is LoginScreenEvent.RegisterClick -> {
                _uiState.update { it.copy(username = "", password = "", errorMessage = null) }
                viewModelScope.launch { _uiEffect.send(LoginEffect.NavigateToRegister) }
            }
            is LoginScreenEvent.RecoveryClick -> {
                _uiState.update { it.copy(username = "", password = "", errorMessage = null) }
                viewModelScope.launch { _uiEffect.send(LoginEffect.NavigateToRecovery) }
            }
            is LoginScreenEvent.UsernameChange -> {
                _uiState.update { it.copy(username = event.username, errorMessage = null) }
            }
            is LoginScreenEvent.PasswordChange -> {
                _uiState.update { it.copy(password = event.password, errorMessage = null) }
            }
            is LoginScreenEvent.SSOTokenReceived -> {
                viewModelScope.launch {
                    saveDataAndNavigateToApp(event.token)
                }
            }
        }
    }

    private fun performLogin() {
        val currentState = uiState.value

        //check if values are present
        if (currentState.username.isBlank() || currentState.password.isBlank()) {
            viewModelScope.launch {
                _uiEffect.send(LoginEffect.ShowSnackbar("Username and password must not be empty!"))
            }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        //check the username and password with server
        viewModelScope.launch {
            val result = repository.login(currentState.username.trim(), currentState.password)

            when (result) {
                is LoginResult.Success -> {
                    //redirect to next screen, save token, ...
                    _uiState.update { it.copy(isLoading = false) }
                    saveDataAndNavigateToApp(result.token)
                }
                is LoginResult.Error -> {
                    _uiEffect.send(LoginEffect.ShowSnackbar(result.msg))
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    private suspend fun saveDataAndNavigateToApp(token: String) {
        val userId = JwtDecoder.getUserId(token)?.toInt()

        tokenManager.saveToken(token)
        userProfileManager.saveInitialProfile(userId = userId!!)
        _uiEffect.send(LoginEffect.NavigateToDeviceListScreen)
    }


    sealed interface LoginScreenEvent {
        data object LoginClick : LoginScreenEvent
        data object RegisterClick : LoginScreenEvent
        data object RecoveryClick : LoginScreenEvent
        data class SSOTokenReceived(val token : String) : LoginScreenEvent
        data class UsernameChange(val username: String) : LoginScreenEvent
        data class PasswordChange(val password: String) : LoginScreenEvent
    }

    sealed interface LoginEffect {
        data object NavigateToDeviceListScreen : LoginEffect
        data object NavigateToRegister : LoginEffect
        data object NavigateToRecovery : LoginEffect
        data class ShowSnackbar(val message: String) : LoginEffect
    }

}