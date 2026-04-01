package cz.cvut.fel.zan.marketviewer.feature.auth.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.cvut.fel.zan.marketviewer.feature.auth.domain.model.LoginResult
import cz.cvut.fel.zan.marketviewer.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginScreenState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class LoginViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginScreenState())
    val uiState: StateFlow<LoginScreenState> = _uiState.asStateFlow()

    // one time effects like navigation
    private val _uiEffect = Channel<LoginEffect>()
    val uiEffect = _uiEffect.receiveAsFlow()

    fun onEvent(event: LoginScreenEvent) {
        when (event) {
            is LoginScreenEvent.LoginClick -> performLogin()
            is LoginScreenEvent.RegisterClick -> {
                viewModelScope.launch { _uiEffect.send(LoginEffect.NavigateToRegister) }
            }
            is LoginScreenEvent.UsernameChange -> {
                _uiState.update { it.copy(username = event.username, errorMessage = null) }
            }
            is LoginScreenEvent.PasswordChange -> {
                _uiState.update { it.copy(password = event.password, errorMessage = null) }
            }
        }
    }

    private fun performLogin() {
        val currentState = uiState.value

        //check if values are present
        if (currentState.username.isBlank() || currentState.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Username and password must not be empty") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }


        //check the username and password with server
        viewModelScope.launch {
        val result = repository.login(currentState.username, currentState.password)

            when (result) {
                is LoginResult.Success -> {
                    //redirect to next screen, save token, ...
                    _uiState.update { it.copy(isLoading = false) }
                    _uiEffect.send(LoginEffect.NavigateToDeviceListScreen)
                }
                is LoginResult.Error -> {
                    // show error in the ui
                    _uiState.update { it.copy(errorMessage = result.msg) }
                }
            }
        }
    }

    sealed interface LoginScreenEvent {
        data object LoginClick : LoginScreenEvent
        data object RegisterClick : LoginScreenEvent
        data class UsernameChange(val username: String) : LoginScreenEvent
        data class PasswordChange(val password: String) : LoginScreenEvent
    }

    sealed interface LoginEffect {
        data object NavigateToDeviceListScreen : LoginEffect
        data object NavigateToRegister : LoginEffect
    }

}