package cz.cvut.fel.zan.marketviewer.feature.screens.presentation.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cz.cvut.fel.zan.marketviewer.R
import cz.cvut.fel.zan.marketviewer.core.presentation.theme.MarketViewerTheme

@Composable
fun ScreenListCard(
    position: Int,
    screenType: String,
    additionalInfo: String,
    icon: Int,
    onDeleteClick: () -> Unit,
    onEditClick: () -> Unit
) {
    Card(
        onClick = onEditClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Icon(
//                    painter = painterResource(id = R.drawable.outline_drag_handle_24),
                    painter = painterResource(id = R.drawable.outline_drag_indicator_24),
                    contentDescription = "Drag indicator",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
//                    modifier = Modifier.rotate(90f)
                )

                Text(
                    text = "${position+1}.",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = "Screen icon",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = screenType,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
                Text(
                    text = additionalInfo,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            //delete screen button
            TextButton(
                onClick = onDeleteClick
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.outline_delete_24),
                    contentDescription = "delete icon",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScreenCardPreview() {
    MarketViewerTheme {
        ScreenListCard(
            position = 1,
            screenType = "Crypto",
            additionalInfo = "Asset name: Bitcoin",
            icon = R.drawable.currency_bitcoin_40px,
            onDeleteClick = {},
            onEditClick = {}
        )
    }
}