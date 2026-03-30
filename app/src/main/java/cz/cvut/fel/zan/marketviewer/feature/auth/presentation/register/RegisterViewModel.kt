package cz.cvut.fel.zan.marketviewer.feature.auth.presentation.register

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.cvut.fel.zan.marketviewer.feature.auth.domain.model.LoginResult
import cz.cvut.fel.zan.marketviewer.feature.auth.domain.model.RegisterResult
import cz.cvut.fel.zan.marketviewer.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    var uiState by mutableStateOf<RegisterUiState>(RegisterUiState.Initial)
        private set

    var username by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var passwordConfirm by mutableStateOf("")
        private set

    // one time effects like navigation
    private val _uiEffect = Channel<RegisterEffect>()
    val uiEffect = _uiEffect.receiveAsFlow()

    fun onEvent(event: RegisterScreenEvent) {
        when (event) {
            is RegisterScreenEvent.RegisterClick -> performRegister()
            is RegisterScreenEvent.CancelRegisterClick -> {
                viewModelScope.launch { _uiEffect.send(RegisterEffect.NavigateToLoginScreen) }
            }
        }
    }

    private fun performRegister() {
        //check if values are present
        if (username.isBlank() || password.isBlank() || passwordConfirm.isBlank()) {
            uiState = RegisterUiState.Error("Please fill in all fields")
            return
        }

        uiState = RegisterUiState.Loading

        //check the username and password with server
        viewModelScope.launch {
            val result = repository.register(username, password, passwordConfirm)

            when (result) {
                is RegisterResult.Success -> {
                    uiState = RegisterUiState.Initial
                    _uiEffect.send(RegisterEffect.NavigateToLoginScreen)
                    //dont navigate the the login screen just now, display some dialog iwth the recovery keys
                }
                is RegisterResult.Error -> {
                    // show error in the ui
                    uiState = RegisterUiState.Error(result.msg)
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

    fun updatePasswordConfirm(input: String) {
        passwordConfirm = input
    }

    sealed interface RegisterUiState {
        data object Loading : RegisterUiState
        data class Error(val message: String) : RegisterUiState
        data object Initial: RegisterUiState
    }

    sealed interface RegisterScreenEvent {
        data object RegisterClick : RegisterScreenEvent
        data object CancelRegisterClick : RegisterScreenEvent
    }

    sealed interface RegisterEffect {
        data object NavigateToLoginScreen : RegisterEffect
    }

}