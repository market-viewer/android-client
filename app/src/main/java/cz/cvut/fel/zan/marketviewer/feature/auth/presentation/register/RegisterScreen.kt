package cz.cvut.fel.zan.marketviewer.feature.auth.presentation.register

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.cvut.fel.zan.marketviewer.R
import cz.cvut.fel.zan.marketviewer.core.presentation.components.AuthSSOButtons
import org.koin.androidx.compose.koinViewModel

@Composable
fun RegisterScreen(
    onBackToLogin: () -> Unit,
    onRegistrationSuccessful: () -> Unit,
    viewModel: RegisterViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    //listen for navigation effect
    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is RegisterViewModel.RegisterEffect.NavigateToLoginScreen -> onBackToLogin()
                is RegisterViewModel.RegisterEffect.NavigateToLoginWithSuccess -> onRegistrationSuccessful()
            }
        }
    }

    RegisterScreenContent(
        username = state.username,
        password = state.password,
        passwordConfirm = state.passwordConfirm,
        isLoading = state.isLoading,
        errorMsg = state.errorMessage,
        recoveryCodes = state.recoveryCodes,

        onUsernameChange = { viewModel.onEvent(RegisterViewModel.RegisterScreenEvent.UsernameChange(it)) },
        onPasswordChange = { viewModel.onEvent(RegisterViewModel.RegisterScreenEvent.PasswordChange(it)) },
        onPasswordConfirmChange = { viewModel.onEvent(RegisterViewModel.RegisterScreenEvent.PasswordConfirmChange(it)) },
        onRegisterClick = { viewModel.onEvent(RegisterViewModel.RegisterScreenEvent.RegisterClick) },
        onBackToLoginClick = { viewModel.onEvent(RegisterViewModel.RegisterScreenEvent.CancelRegisterClick) },
        onRecoveryOkClick = { viewModel.onEvent(RegisterViewModel.RegisterScreenEvent.ConfirmRecoveryCodeDialog) }
    )
}

@Composable
fun RegisterScreenContent(
    username: String,
    password: String,
    passwordConfirm: String,
    isLoading: Boolean,
    errorMsg: String?,
    recoveryCodes: List<String>?,

    onRegisterClick: () -> Unit,
    onBackToLoginClick: () -> Unit,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onPasswordConfirmChange: (String) -> Unit,
    onRecoveryOkClick: () -> Unit
) {
    val componentWidth = 0.85f

    if (recoveryCodes != null) {
        RecoveryCodesDialog(
            recoveryCodes,
            onRecoveryOkClick
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
                    painter = painterResource(id = R.drawable.person_add_48px),
                    contentDescription = "App Logo",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(text = "Create an account", style = MaterialTheme.typography.titleLarge)

            Spacer(modifier = Modifier.height(24.dp))

            RegisterInputField(
                value = username,
                onValueChange = onUsernameChange,
                labelString = stringResource(R.string.label_username),
                isPassword = false,
                isLastInput = false,
                modifier = Modifier.fillMaxWidth(componentWidth)
            )

            Spacer(modifier = Modifier.height(12.dp))

            RegisterInputField(
                value = password,
                onValueChange = onPasswordChange,
                labelString = stringResource(R.string.label_password),
                isPassword = true,
                isLastInput = false,
                modifier = Modifier.fillMaxWidth(componentWidth)
            )

            Spacer(modifier = Modifier.height(12.dp))

            RegisterInputField(
                value = passwordConfirm,
                onValueChange = onPasswordConfirmChange,
                labelString = stringResource(R.string.label_passwordConfirm),
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
                onClick = onRegisterClick,
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth(componentWidth)
                    .height(50.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.person_48px),
                    contentDescription = "Register",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Register", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = onBackToLoginClick,
                modifier = Modifier.fillMaxWidth(componentWidth)
            ) {
                Text("Already have an account? Login")
            }

            Spacer(modifier = Modifier.height(24.dp))

            AuthSSOButtons()
        }
    }
}

@Composable
private fun RegisterInputField(
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
@Preview(showBackground = true, showSystemUi = true)
fun RegisterScreenPreview() {
    RegisterScreenContent(
        username = "TestUser",
        password = "Password123",
        passwordConfirm = "Password132",
        isLoading = false,
        errorMsg = null,
        recoveryCodes = null,
        onUsernameChange = {},
        onPasswordChange = {},
        onPasswordConfirmChange = {},
        onRegisterClick = {},
        onBackToLoginClick = {},
        onRecoveryOkClick = {}
    )
}