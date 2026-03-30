package cz.cvut.fel.zan.marketviewer.feature.auth.presentation.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.cvut.fel.zan.marketviewer.feature.auth.domain.model.LoginResult
import cz.cvut.fel.zan.marketviewer.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    var uiState by mutableStateOf<LoginUiState>(LoginUiState.Initial)
        private set

    var username by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    // one time effects like navigation
    private val _uiEffect = Channel<LoginEffect>()
    val uiEffect = _uiEffect.receiveAsFlow()

    fun onEvent(event: LoginScreenEvent) {
        when (event) {
            is LoginScreenEvent.LoginClick -> performLogin()
            is LoginScreenEvent.RegisterClick -> {
                viewModelScope.launch { _uiEffect.send(LoginEffect.NavigateToRegister) }
            }
        }
    }

    private fun performLogin() {
        //check if values are present
        if (username.isBlank() || password.isBlank()) {
            uiState = LoginUiState.Error("Username and password must not be empty")
            return
        }

        uiState = LoginUiState.Loading

        //check the username and password with server
        viewModelScope.launch {
        val result = repository.login(username, password)

            when (result) {
                is LoginResult.Success -> {
                    //redirect to next screen, save token, ...
                    uiState = LoginUiState.Initial
                    _uiEffect.send(LoginEffect.NavigateToDeviceListScreen)
                }
                is LoginResult.Error -> {
                    // show error in the ui
                    uiState = LoginUiState.Error(result.msg)
                }
            }
        }
    }

    fun updateUsername(input: String) {
        username = input
    }

    fun updatePassword(input: String) {
        password = input
    }

    sealed interface LoginUiState {
        data object Loading : LoginUiState
        data class Error(val message: String) : LoginUiState
        data object Initial: LoginUiState
    }

    sealed interface LoginScreenEvent {
        data object LoginClick : LoginScreenEvent
        data object RegisterClick : LoginScreenEvent
    }

    sealed interface LoginEffect {
        data object NavigateToDeviceListScreen : LoginEffect
        data object NavigateToRegister : LoginEffect
    }

}