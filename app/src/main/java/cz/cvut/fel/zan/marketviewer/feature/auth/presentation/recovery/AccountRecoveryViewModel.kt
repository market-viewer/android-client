package cz.cvut.fel.zan.marketviewer.feature.auth.presentation.recovery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.cvut.fel.zan.marketviewer.core.domain.ApiResult
import cz.cvut.fel.zan.marketviewer.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RecoveryScreenState(
    val username: String = "",
    val recoveryCode: String = "",
    val newPassword: String = "",
    val passwordConfirm: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class AccountRecoverViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecoveryScreenState())
    val uiState = _uiState.asStateFlow()

    private val _uiEffect = Channel<RecoveryEffect>()
    val uiEffect = _uiEffect.receiveAsFlow()

    fun onEvent(event: RecoveryScreenEvent) {
        when (event) {
            is RecoveryScreenEvent.SubmitRecoveryClick -> performRecovery()
            is RecoveryScreenEvent.CancelRecoveryClick -> {
                viewModelScope.launch { _uiEffect.send(RecoveryEffect.NavigateToLoginScreen) }
            }
            is RecoveryScreenEvent.UsernameChange -> {
                _uiState.update { it.copy(username = event.username) }
            }
            is RecoveryScreenEvent.RecoveryCodeChange -> {
                _uiState.update { it.copy(recoveryCode = event.code) }
            }
            is RecoveryScreenEvent.NewPasswordChange -> {
                _uiState.update { it.copy(newPassword = event.password) }
            }
            is RecoveryScreenEvent.PasswordConfirmChange -> {
                _uiState.update { it.copy(passwordConfirm = event.passwordConfirm) }
            }
        }
    }

    private fun performRecovery() {
        val currentState = uiState.value

        if (currentState.username.isBlank() ||
            currentState.recoveryCode.isBlank() ||
            currentState.newPassword.isBlank() ||
            currentState.passwordConfirm.isBlank()
        ) {
            _uiState.update { it.copy(errorMessage = "Please fill in all fields") }
            return
        }

        if (currentState.newPassword != currentState.passwordConfirm) {
            _uiState.update { it.copy(errorMessage = "Passwords do not match") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = repository.recoverAccount(
                username = currentState.username.trim(),
                recoveryCode = currentState.recoveryCode.trim(),
                password = currentState.newPassword,
                passwordRepeat = currentState.passwordConfirm,

            )

            when (result) {
                is ApiResult.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _uiEffect.send(RecoveryEffect.ShowSuccessDialog)
                }
                is ApiResult.Error -> {
                    // Show error in the UI
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
            }
        }
    }

    sealed interface RecoveryScreenEvent {
        data object SubmitRecoveryClick : RecoveryScreenEvent
        data object CancelRecoveryClick : RecoveryScreenEvent
        data class UsernameChange(val username: String) : RecoveryScreenEvent
        data class RecoveryCodeChange(val code: String) : RecoveryScreenEvent
        data class NewPasswordChange(val password: String) : RecoveryScreenEvent
        data class PasswordConfirmChange(val passwordConfirm: String) : RecoveryScreenEvent
    }

    sealed interface RecoveryEffect {
        data object NavigateToLoginScreen : RecoveryEffect
        data object ShowSuccessDialog : RecoveryEffect
    }
}