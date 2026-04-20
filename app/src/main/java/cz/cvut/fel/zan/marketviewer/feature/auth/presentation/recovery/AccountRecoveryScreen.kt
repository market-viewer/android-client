package cz.cvut.fel.zan.marketviewer.feature.auth.presentation.recovery

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.cvut.fel.zan.marketviewer.R
import cz.cvut.fel.zan.marketviewer.core.presentation.theme.MarketViewerTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun RecoveryScreen(
    onBackToLogin: () -> Unit,
    viewModel: AccountRecoverViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    var showSuccessDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is AccountRecoverViewModel.RecoveryEffect.NavigateToLoginScreen -> onBackToLogin()
                is AccountRecoverViewModel.RecoveryEffect.ShowSuccessDialog -> {
                    showSuccessDialog = true
                }
            }
        }
    }

    RecoveryScreenContent(
        username = state.username,
        recoveryCode = state.recoveryCode,
        newPassword = state.newPassword,
        passwordConfirm = state.passwordConfirm,
        isLoading = state.isLoading,
        errorMsg = state.errorMessage,
        showSuccessDialog = showSuccessDialog,

        onUsernameChange = { viewModel.onEvent(AccountRecoverViewModel.RecoveryScreenEvent.UsernameChange(it)) },
        onRecoveryCodeChange = { viewModel.onEvent(AccountRecoverViewModel.RecoveryScreenEvent.RecoveryCodeChange(it)) },
        onNewPasswordChange = { viewModel.onEvent(AccountRecoverViewModel.RecoveryScreenEvent.NewPasswordChange(it)) },
        onPasswordConfirmChange = { viewModel.onEvent(AccountRecoverViewModel.RecoveryScreenEvent.PasswordConfirmChange(it)) },

        onRecoverClick = { viewModel.onEvent(AccountRecoverViewModel.RecoveryScreenEvent.SubmitRecoveryClick) },
        onBackToLoginClick = { viewModel.onEvent(AccountRecoverViewModel.RecoveryScreenEvent.CancelRecoveryClick) },

        onDialogOkClick = { viewModel.onEvent(AccountRecoverViewModel.RecoveryScreenEvent.CancelRecoveryClick) },
    )
}

@Composable
fun RecoveryScreenContent(
    username: String,
    recoveryCode: String,
    newPassword: String,
    passwordConfirm: String,
    isLoading: Boolean,
    errorMsg: String?,
    showSuccessDialog: Boolean,

    onUsernameChange: (String) -> Unit,
    onRecoveryCodeChange: (String) -> Unit,
    onNewPasswordChange: (String) -> Unit,
    onPasswordConfirmChange: (String) -> Unit,

    onRecoverClick: () -> Unit,
    onBackToLoginClick: () -> Unit,
    onDialogOkClick: () -> Unit
) {
    val componentWidth = 0.85f

    // Success Dialog
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Recovery Successful") },
            text = { Text("Your account has been recovered and your new password is set. You can now access your account.") },
            confirmButton = {
                Button(onClick = onDialogOkClick) {
                    Text("OK")
                }
            }
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.healing_48px),
                    contentDescription = "Recover Logo",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(text = "Recover Account", style = MaterialTheme.typography.titleLarge)

            Spacer(modifier = Modifier.height(24.dp))

            RecoveryInputField(
                value = username,
                onValueChange = onUsernameChange,
                labelString = "Username",
                isPassword = false,
                isLastInput = false,
                modifier = Modifier.fillMaxWidth(componentWidth)
            )

            Spacer(modifier = Modifier.height(12.dp))

            RecoveryInputField(
                value = recoveryCode,
                onValueChange = onRecoveryCodeChange,
                labelString = "Recovery Code",
                isPassword = false,
                isLastInput = false,
                modifier = Modifier.fillMaxWidth(componentWidth)
            )

            Spacer(modifier = Modifier.height(12.dp))

            RecoveryInputField(
                value = newPassword,
                onValueChange = onNewPasswordChange,
                labelString = "New Password",
                isPassword = true,
                isLastInput = false,
                modifier = Modifier.fillMaxWidth(componentWidth)
            )

            Spacer(modifier = Modifier.height(12.dp))

            RecoveryInputField(
                value = passwordConfirm,
                onValueChange = onPasswordConfirmChange,
                labelString = "Confirm New Password",
                isPassword = true,
                isLastInput = true,
                modifier = Modifier.fillMaxWidth(componentWidth)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier.height(48.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else if (errorMsg != null) {
                    Text(text = errorMsg, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
                }
            }

            Button(
                onClick = onRecoverClick,
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth(componentWidth)
                    .height(50.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.key_24px),
                    contentDescription = "key icon",
                    modifier = Modifier.padding(end = 10.dp)
                )
                Text("Reset Password", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = onBackToLoginClick,
                modifier = Modifier.fillMaxWidth(componentWidth)
            ) {
                Text("Back to Login")
            }
        }
    }
}

@Composable
private fun RecoveryInputField(
    value: String,
    onValueChange: (String) -> Unit,
    labelString: String,
    isPassword: Boolean,
    isLastInput: Boolean,
    modifier: Modifier = Modifier
) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(labelString) },
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        modifier = modifier,

        visualTransformation = if (isPassword && !passwordVisible) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },

        keyboardOptions = KeyboardOptions(
            keyboardType = if (isPassword) KeyboardType.Password else KeyboardType.Text,
            imeAction = if (isLastInput) ImeAction.Done else ImeAction.Next
        ),

        trailingIcon = {
            if (isPassword) {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        painter = painterResource(
                            id = if (passwordVisible) R.drawable.visibility_off_24px else R.drawable.visibility_24px
                        ),
                        contentDescription = if (passwordVisible) "Hide password" else "Show password"
                    )
                }
            }
        }
    )
}

@Composable
@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
fun RecoveryScreenPreview() {
    MarketViewerTheme {
        RecoveryScreenContent(
            username = "TestUser",
            recoveryCode = "ALPHA-1234",
            newPassword = "NewPassword123",
            passwordConfirm = "NewPassword123",
            isLoading = false,
            errorMsg = null,
            showSuccessDialog = false,
            onUsernameChange = {},
            onRecoveryCodeChange = {},
            onNewPasswordChange = {},
            onPasswordConfirmChange = {},
            onRecoverClick = {},
            onBackToLoginClick = {},
            onDialogOkClick = {}
        )
    }
}