package cz.cvut.fel.zan.marketviewer.feature.auth.presentation.login

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cz.cvut.fel.zan.marketviewer.R
import cz.cvut.fel.zan.marketviewer.feature.auth.presentation.login.LoginContent
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit,
    viewModel: LoginViewModel = koinViewModel()
) {
    //listen for navigation effect
    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is LoginViewModel.LoginEffect.NavigateToDeviceListScreen -> onLoginSuccess()
                is LoginViewModel.LoginEffect.NavigateToRegister -> onRegisterClick()
            }
        }
    }

    LoginContent(
        uiState = viewModel.uiState,
        username = viewModel.username,
        password = viewModel.password,
        onUsernameChange = { viewModel.updateUsername(it) },
        onPasswordChange = { viewModel.updatePassword(it) },
        onLoginClick = { viewModel.onEvent(LoginViewModel.LoginScreenEvent.LoginClick) },
        onRegisterClick = { viewModel.onEvent(LoginViewModel.LoginScreenEvent.RegisterClick) },
    )
}

@Composable
fun LoginContent(
    uiState: LoginViewModel.LoginUiState,
    username: String,
    password: String,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Market Viewer",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(50.dp))
        
        Text(text = "Login", style = MaterialTheme.typography.bodyLarge)

        Spacer(modifier = Modifier.height(16.dp))

        LoginInputField(
            value = username,
            onValueChange = onUsernameChange,
            labelString = stringResource(R.string.label_username),
            isPassword = false
        )

        Spacer(modifier = Modifier.height(8.dp))

        LoginInputField(
            value = password,
            onValueChange = onPasswordChange,
            labelString = stringResource(R.string.label_password),
            isPassword = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // React to the UI State
        when (uiState) {
            is LoginViewModel.LoginUiState.Loading -> {
                CircularProgressIndicator()
            }
            is LoginViewModel.LoginUiState.Error -> {
                Text(text = uiState.message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelLarge)
            }
            is LoginViewModel.LoginUiState.Initial -> {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onLoginClick,
            enabled = uiState !is LoginViewModel.LoginUiState.Loading // Prevent double-clicks
        ) {
            Text("Login")
        }

        TextButton(onClick = onRegisterClick) {
            Text("Register")
        }
    }
}

@Composable
private fun LoginInputField(
    value: String,
    onValueChange: (String) -> Unit,
    labelString: String,
    isPassword: Boolean
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(labelString) },
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions =
            if (isPassword) {
                KeyboardOptions( keyboardType = KeyboardType.Password, imeAction = ImeAction.Done)
            } else {
                KeyboardOptions(imeAction = ImeAction.Next)
            }
    )

}

//@SuppressLint("ViewModelConstructorInComposable")
@Composable
@Preview(showBackground = true, showSystemUi = true)
fun LoginScreenPreview() {
    LoginContent (
        uiState = LoginViewModel.LoginUiState.Initial,
        username = "TestUser",
        password = "Password123",
        onUsernameChange = {},
        onPasswordChange = {},
        onLoginClick = {},
        onRegisterClick = {},
    )
}
