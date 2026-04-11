package cz.cvut.fel.zan.marketviewer.feature.auth.presentation.login

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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
fun LoginScreen(
    ssoToken: String?,
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit,
    showRegistrationSnackbar: Boolean,
    onSnackBarShown: () -> Unit,
    viewModel: LoginViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }

    //when token is received from github callback -> login success
    LaunchedEffect(ssoToken) {
        if (ssoToken != null) {
            Log.d("JWT token", ssoToken)
            viewModel.onEvent(LoginViewModel.LoginScreenEvent.SSOTokenReceived(ssoToken))
        }
    }

    //listen for navigation effect
    LaunchedEffect(Unit) {
        //show registration successful snackbar when needed
        if (showRegistrationSnackbar) {
            snackBarHostState.showSnackbar("Registration Successful!")
            onSnackBarShown()
        }

        //navigate to different screens
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is LoginViewModel.LoginEffect.NavigateToDeviceListScreen -> onLoginSuccess()
                is LoginViewModel.LoginEffect.NavigateToRegister -> onRegisterClick()
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            LoginContent(
                username = state.username,
                password = state.password,
                isLoading = state.isLoading,
                errorMsg = state.errorMessage,

                onUsernameChange = { viewModel.onEvent(LoginViewModel.LoginScreenEvent.UsernameChange(it)) },
                onPasswordChange = { viewModel.onEvent(LoginViewModel.LoginScreenEvent.PasswordChange(it)) },
                onLoginClick = { viewModel.onEvent(LoginViewModel.LoginScreenEvent.LoginClick) },
                onRegisterClick = { viewModel.onEvent(LoginViewModel.LoginScreenEvent.RegisterClick) },
            )
        }
    }
}

@Composable
fun LoginContent(
    username: String,
    password: String,
    isLoading: Boolean,
    errorMsg: String?,

    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
) {
    val buttonWidth = 150.dp

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
        
        Text(text = "Login", style = MaterialTheme.typography.headlineSmall)

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

        TextButton(onClick = onRegisterClick) {
            Text("Recover account", color = MaterialTheme.colorScheme.secondary)
        }

//        Spacer(modifier = Modifier.height(16.dp))

        // React to the UI State
        if (isLoading) {
            CircularProgressIndicator()
        } else if (errorMsg != null) {
            Text(text = errorMsg, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
        } else {
            Spacer(modifier = Modifier.height(10.dp))

        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onLoginClick,
            enabled = !isLoading, // Prevent double-clicks
            modifier = Modifier.width(buttonWidth)
        ) {
            Text("Login")
        }

        Spacer(modifier = Modifier.height(10.dp))

        TextButton(onClick = onRegisterClick, modifier = Modifier.width(buttonWidth)) {
            Text("Register")
        }

        AuthSSOButtons(
            onButtonClick = {}
        )
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
        username = "TestUser",
        password = "Password123",
        isLoading = false,
        errorMsg = null,
        onUsernameChange = {},
        onPasswordChange = {},
        onLoginClick = {},
        onRegisterClick = {},
    )
}
