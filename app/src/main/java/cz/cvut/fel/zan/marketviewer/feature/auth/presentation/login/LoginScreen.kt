package cz.cvut.fel.zan.marketviewer.feature.auth.presentation.login

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.currentComposer
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
import cz.cvut.fel.zan.marketviewer.core.presentation.components.MarketViewerScaffold
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(
    ssoToken: String?,
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit,
    onRecoveryClick: () -> Unit,
    showRegistrationSnackbar: Boolean,
    onSnackBarShown: () -> Unit,
    viewModel: LoginViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

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
            snackbarHostState.showSnackbar("Registration Successful!")
            onSnackBarShown()
        }

        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is LoginViewModel.LoginEffect.NavigateToDeviceListScreen -> onLoginSuccess()
                is LoginViewModel.LoginEffect.NavigateToRegister -> onRegisterClick()
                is LoginViewModel.LoginEffect.NavigateToRecovery -> onRecoveryClick()
                is LoginViewModel.LoginEffect.ShowSnackbar ->
                    launch {snackbarHostState.showSnackbar(effect.message)}
            }
        }
    }

    MarketViewerScaffold(
        snackbarHostState = snackbarHostState
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
                onRecoveryClick = { viewModel.onEvent(LoginViewModel.LoginScreenEvent.RecoveryClick) },

                currentServerUrl = state.currentServerUrl,
                onServerUrlSave = { viewModel.onEvent(LoginViewModel.LoginScreenEvent.SaveServerUrl(it)) },
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
    onRecoveryClick: () -> Unit,

    currentServerUrl: String,
    onServerUrlSave: (String) -> Unit
) {
    val componentWidth = 0.85f
    var showServerDialog by remember { mutableStateOf(false) }

    if (showServerDialog) {
        CustomServerDialog(
            currentUrl = currentServerUrl,
            onDismiss = { showServerDialog = false },
            onSave = { newUrl ->
                onServerUrlSave(newUrl)
                showServerDialog = false
            }
        )
    }

    Box(modifier = Modifier.fillMaxWidth()) {

        //custom server icon
        IconButton(
            onClick = { showServerDialog = true },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(24.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.hard_drive_24px),
                contentDescription = "Server Settings",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(35.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.market_viewer_logo),
                    contentDescription = "App Logo",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(75.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Market Viewer",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(40.dp))

            Text(text = "Welcome back", style = MaterialTheme.typography.titleLarge)

            Spacer(modifier = Modifier.height(24.dp))

            LoginInputField(
                value = username,
                onValueChange = onUsernameChange,
                labelString = stringResource(R.string.label_username),
                isPassword = false,
                modifier = Modifier.fillMaxWidth(componentWidth)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier.fillMaxWidth(componentWidth),
                horizontalAlignment = Alignment.End
            ) {
                LoginInputField(
                    value = password,
                    onValueChange = onPasswordChange,
                    labelString = stringResource(R.string.label_password),
                    isPassword = true,
                    modifier = Modifier.fillMaxWidth()
                )

                TextButton(
                    onClick = onRecoveryClick,
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        "Recover account?",
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            Box(
                modifier = Modifier.height(48.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else if (errorMsg != null) {
                    Text(
                        text = errorMsg,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            //login button
            Button(
                onClick = onLoginClick,
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth(componentWidth)
                    .height(50.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.login_24px),
                    contentDescription = "Login",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Login", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(modifier = Modifier.height(12.dp))

            //register button
            TextButton(
                onClick = onRegisterClick,
                modifier = Modifier.fillMaxWidth(componentWidth)
            ) {
                Text("Don't have an account? Register")
            }

            Spacer(modifier = Modifier.height(24.dp))

            AuthSSOButtons(backendUrl = currentServerUrl)
        }
    }
}

@Composable
private fun LoginInputField(
    value: String,
    onValueChange: (String) -> Unit,
    labelString: String,
    isPassword: Boolean,
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

        keyboardOptions = if (isPassword) {
            KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done)
        } else {
            KeyboardOptions(imeAction = ImeAction.Next)
        },

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
        onRecoveryClick = {},

        currentServerUrl = "",
        onServerUrlSave = {}
    )
}
