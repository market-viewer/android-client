package cz.cvut.fel.zan.marketviewer.feature.profile.presentation

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.cvut.fel.zan.marketviewer.R
import cz.cvut.fel.zan.marketviewer.core.presentation.components.MarketViewerScaffold
import cz.cvut.fel.zan.marketviewer.core.presentation.components.MarketViewerTopAppBar
import cz.cvut.fel.zan.marketviewer.core.presentation.theme.MarketViewerTheme
import cz.cvut.fel.zan.marketviewer.feature.profile.domain.model.ApiKeyProvider
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onDrawerOpen: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }

    var showDeleteAccountDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is ProfileViewModel.ProfileEffects.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message)
                is ProfileViewModel.ProfileEffects.AccountDeleted -> {
                    showDeleteAccountDialog = false
                    onLogout()
                }
            }
        }
    }

    ProfileScreenContent(
        snackbarHostState = snackbarHostState,
        onDrawerOpen = onDrawerOpen,
        onLogoutButtonClick = onLogout,
        onUsernameSave = { viewModel.onEvent(ProfileViewModel.ProfileEvents.UsernameUpdate(it)) },
        username = state.username,
        savedProviders = state.savedProviders,
        onApiKeySave = { keyProvider, keyValue -> viewModel.onEvent(ProfileViewModel.ProfileEvents.SetApiKey(keyProvider, keyValue)) },
        onApiKeyDelete = { viewModel.onEvent(ProfileViewModel.ProfileEvents.DeleteApiKey(it)) },
        isApiKeyVerifying = state.isApiKeyVerifying,
        onDeleteAccount = { viewModel.onEvent(ProfileViewModel.ProfileEvents.DeleteAccount) },
        isDeletingAccount = state.isDeletingAccount,
        showDeleteAccountDialog = showDeleteAccountDialog,
        onDialogStateChnage = { showDeleteAccountDialog = it }
        )
}

@Composable
fun ProfileScreenContent(
    snackbarHostState: SnackbarHostState,
    onDrawerOpen: () -> Unit,
    onLogoutButtonClick: () -> Unit,
    onUsernameSave: (String) -> Unit,
    username: String?,
    savedProviders: Set<ApiKeyProvider>,
    onApiKeySave: (ApiKeyProvider, String) -> Unit,
    onApiKeyDelete: (ApiKeyProvider) -> Unit,
    isApiKeyVerifying: Boolean,
    onDeleteAccount: () -> Unit,
    isDeletingAccount: Boolean,
    showDeleteAccountDialog: Boolean,
    onDialogStateChnage: (Boolean) -> Unit
) {

    MarketViewerScaffold(
        topBar = {
            MarketViewerTopAppBar(
                title = "Profile",
                navigationIcon = R.drawable.outline_menu_24,
                onNavigationClick = onDrawerOpen,
                actions = {
                    TextButton(onClick = onLogoutButtonClick) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.outline_logout_24),
                                contentDescription = "Logout",
                            )
                            Text("Logout")
                        }
                    }
                }
            )
        },
        snackbarHostState = snackbarHostState
    ) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .padding(top = 20.dp)
        ) {
            //username and icon section
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.face_5_24px),
                    contentDescription = "Profile icon",
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            ProfileUsernameInput(
                savedUsername = username,
                onSave = { onUsernameSave(it) }
            )

            //api keys section
            ProfileAPIKeySurface(
                modifier = Modifier.fillMaxWidth().weight(1f),
                onSave = onApiKeySave,
                onDelete = onApiKeyDelete,
                savedProviders = savedProviders,
                isApiKeyVerifying = isApiKeyVerifying
            )

            Button(
                onClick = { onDialogStateChnage(true) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.textButtonColors(containerColor = MaterialTheme.colorScheme.error, contentColor = MaterialTheme.colorScheme.onError)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.outline_delete_forever_24),
                    contentDescription = "Delete icon"
                )
                Text("Delete account")
            }
        }
    }

    //show dialog
    if (showDeleteAccountDialog) {
        DeleteAccountDialog(
            onDismiss = { onDialogStateChnage(false) },
            onConfirm = onDeleteAccount,
            isDeleting = isDeletingAccount
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ProfileScreenPreview() {
    MarketViewerTheme {
        ProfileScreenContent(
            snackbarHostState = SnackbarHostState(),
            onDrawerOpen = {},
            onLogoutButtonClick = {},
            onUsernameSave = {},
            username = "Test user",
            savedProviders = emptySet(),
            onApiKeySave = { _, _ -> },
            onApiKeyDelete = {},
            isApiKeyVerifying = false,
            onDeleteAccount = {},
            isDeletingAccount = false,
            onDialogStateChnage = {},
            showDeleteAccountDialog = false
        )
    }
}