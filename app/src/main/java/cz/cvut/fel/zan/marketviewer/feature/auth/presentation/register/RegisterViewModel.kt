package cz.cvut.fel.zan.marketviewer.feature.auth.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.cvut.fel.zan.marketviewer.feature.auth.domain.model.RegisterResult
import cz.cvut.fel.zan.marketviewer.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RegisterScreenState(
    val username: String = "",
    val password: String = "",
    val passwordConfirm: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val recoveryCodes: List<String>? = null
)

class RegisterViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterScreenState())
    val uiState = _uiState.asStateFlow()


    // one time effects like navigation
    private val _uiEffect = Channel<RegisterEffect>()
    val uiEffect = _uiEffect.receiveAsFlow()

    fun onEvent(event: RegisterScreenEvent) {
        when (event) {
            is RegisterScreenEvent.RegisterClick -> performRegister()
            is RegisterScreenEvent.CancelRegisterClick -> {
                viewModelScope.launch { _uiEffect.send(RegisterEffect.NavigateToLoginScreen) }
            }
            is RegisterScreenEvent.UsernameChange -> {
                _uiState.update { it.copy(username = event.username) }
            }
            is RegisterScreenEvent.PasswordChange -> {
                _uiState.update { it.copy(password = event.password) }
            }
            is RegisterScreenEvent.PasswordConfirmChange -> {
                _uiState.update { it.copy(passwordConfirm = event.passwordConfirm) }
            }
            is RegisterScreenEvent.ConfirmRecoveryCodeDialog -> {
                _uiState.update { it.copy(recoveryCodes = null) }
                viewModelScope.launch { _uiEffect.send(RegisterEffect.NavigateToLoginWithSuccess) }
            }
        }
    }

    private fun performRegister() {
        val currentState = uiState.value

        //check if values are present
        if (currentState.username.isBlank() || currentState.password.isBlank() || currentState.passwordConfirm.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please fill in all fields") }
            return
        }

        //check if password and password confirm match
        if (currentState.password != currentState.passwordConfirm) {
            _uiState.update { it.copy(errorMessage = "Passwords do not match") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        //check the username and password with server
        viewModelScope.launch {
            val result = repository.register(currentState.username, currentState.password, currentState.passwordConfirm)

            when (result) {
                is RegisterResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, recoveryCodes = result.recoveryCodes) }
                }
                is RegisterResult.Error -> {
                    // show error in the ui
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.msg) }
                }
            }
        }
    }

    sealed interface RegisterScreenEvent {
        data object RegisterClick : RegisterScreenEvent
        data object CancelRegisterClick : RegisterScreenEvent
        data object ConfirmRecoveryCodeDialog : RegisterScreenEvent
        data class UsernameChange(val username: String) : RegisterScreenEvent
        data class PasswordChange(val password: String) : RegisterScreenEvent
        data class PasswordConfirmChange(val passwordConfirm: String) : RegisterScreenEvent
    }

    sealed interface RegisterEffect {
        data object NavigateToLoginScreen : RegisterEffect
        data object NavigateToLoginWithSuccess : RegisterEffect
    }

}