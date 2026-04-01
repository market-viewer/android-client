package cz.cvut.fel.zan.marketviewer.feature.auth.presentation.register

import android.content.ClipData
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.toClipEntry
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.cvut.fel.zan.marketviewer.R
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun RegisterScreen(
    onBackToLogin: () -> Unit,
    viewModel: RegisterViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    //listen for navigation effect
    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is RegisterViewModel.RegisterEffect.NavigateToLoginScreen -> onBackToLogin()
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
    if (recoveryCodes != null) {
        RecoveryCodesDialog(
            recoveryCodes,
            onRecoveryOkClick
        )
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Register",
            style = MaterialTheme.typography.headlineSmall,
        )

        Spacer(modifier = Modifier.height(50.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            RegisterInputField(
                value = username,
                onValueChange = onUsernameChange,
                labelString = stringResource(R.string.label_username),
                isPassword = false
            )

            RegisterInputField(
                value = password,
                onValueChange = onPasswordChange,
                labelString = stringResource(R.string.label_password),
                isPassword = true
            )

            RegisterInputField(
                value = passwordConfirm,
                onValueChange = onPasswordConfirmChange,
                labelString = stringResource(R.string.label_passwordConfirm),
                isPassword = true
            )

        }


        Spacer(modifier = Modifier.height(16.dp))

        // React to the UI State
        if (isLoading) {
            CircularProgressIndicator()
        } else if (errorMsg != null) {
            Text(text = errorMsg, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
        } else {
            Spacer(modifier = Modifier.height(20.dp))

        }
        Spacer(modifier = Modifier.height(10.dp))

        Button(onClick = onRegisterClick) {
            Text("Register")
        }

        Spacer(modifier = Modifier.height(10.dp))

        TextButton(onClick = onBackToLoginClick) {
            Text("Back to Login")
        }
    }
}

@Composable
private fun RegisterInputField(
    value: String,
    onValueChange: (String) -> Unit,
    labelString: String,
    isPassword: Boolean
) {
    TextField(
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
            },
        modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp)
    )

}

@Composable
private fun RecoveryCodesDialog(
    recoveryCodes: List<String>,
    onRecoveryOkClick: () -> Unit
) {
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = {
            // on click outside the dialog
        },
        title = { Text(text = "Registration Successful") },
        text = {
            Column {
                Text(
                    text = buildAnnotatedString {
                        append("Please save these ")

                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)) {
                          append("recovery keys ")
                        }
                        append("securely. You will not be shown these again.")
                    }

                )
                Spacer(modifier = Modifier.height(16.dp))
                recoveryCodes.forEach { code ->
                    Text(
                        text = code,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    HorizontalDivider(thickness = 2.dp)
                }
            }
        },
        confirmButton = {
            Button(onClick = onRecoveryOkClick) {
                Text("OK")
            }
        },
        //button for copy all data to clipboard
        dismissButton = {
            TextButton(
                onClick = {
                    val formattedCodes: String = recoveryCodes.joinToString(separator = "\n")
                    val clipData = ClipData.newPlainText("Recovery Codes", formattedCodes)
                    scope.launch {
                        clipboard.setClipEntry(clipData.toClipEntry())
                    }
                }
            ) {
                Text("Copy")
            }
        }
//        modifier = Modifier.fillMaxSize().fillMaxWidth()
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
        recoveryCodes = listOf("ALPHA-1234-ABCD", "ALPHA-1234-sdf5", "ALPHA-1234-ABCDsd", "ALPHA-1234-ABCD", "ALPHA-1234-sdf5", "ALPHA-1234-ABCDsd"),
//        recoveryCodes = null,
        onUsernameChange = {},
        onPasswordChange = {},
        onPasswordConfirmChange = {},
        onRegisterClick = {},
        onBackToLoginClick = {},
        onRecoveryOkClick = {}
    )
}