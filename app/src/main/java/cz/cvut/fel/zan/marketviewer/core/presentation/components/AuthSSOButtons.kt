package cz.cvut.fel.zan.marketviewer.core.presentation.components

import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import cz.cvut.fel.zan.marketviewer.R
import cz.cvut.fel.zan.marketviewer.core.utils.backendSSOEndpoint
import androidx.core.net.toUri

@Composable
fun AuthSSOButtons(
) {
    val context = LocalContext.current

    HorizontalDivider(thickness = 2.dp, modifier = Modifier.padding(horizontal = 30.dp, vertical = 10.dp))

    IconButton(
        onClick = {
            val intent = CustomTabsIntent.Builder().build()
            intent.launchUrl(context, backendSSOEndpoint.toUri())
        },
    ) {
        Icon(
            painter = painterResource(id = R.drawable.github_logo),
            contentDescription = "Github sso icon",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.width(90.dp).height(90.dp)
        )
    }
}