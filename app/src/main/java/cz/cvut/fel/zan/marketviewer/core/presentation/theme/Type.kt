package cz.cvut.fel.zan.marketviewer.core.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import cz.cvut.fel.zan.marketviewer.R

val JetBrainsMonoFont = FontFamily(
    Font(R.font.jetbrains_mono_regular, FontWeight.Normal),
    Font(R.font.jetbrains_mono_bold, FontWeight.Bold),
    Font(R.font.jetbrains_mono_light, FontWeight.Light),
)

val Typography = Typography(
    headlineMedium = TextStyle(
        fontFamily = JetBrainsMonoFont,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = JetBrainsMonoFont,
        fontWeight = FontWeight.Normal,
        fontSize = 23.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = JetBrainsMonoFont,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = JetBrainsMonoFont,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = JetBrainsMonoFont,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
    ),
    labelLarge = TextStyle( // Used for Buttons
        fontFamily = JetBrainsMonoFont,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    )
)