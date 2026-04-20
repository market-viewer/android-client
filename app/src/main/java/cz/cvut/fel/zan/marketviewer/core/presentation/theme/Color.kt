package cz.cvut.fel.zan.marketviewer.core.presentation.theme

import androidx.compose.ui.graphics.Color

val PrimaryDark = Color(0xFFE5C05C)
val OnPrimaryDark = Color(0xFF3B2F00)
val SecondaryDark = Color(0xFFD0C5B4)
val OnSecondaryDark = Color(0xFF363022)
val TertiaryDark = Color(0xFFA8D0A6)
val BackgroundDark = Color(0xFF141311)
val SurfaceDark = Color(0xFF1D1C1A)
val OnBackgroundDark = Color(0xFFE8E2D9)
val OnSurfaceDark = Color(0xFFE8E2D9)
val ErrorDark = Color(0xFFFF6856)
val OnErrorDark = Color(0xFF690005)
val PrimaryContainerDark = Color(0xFFE59C1C)
val OnPrimaryContainerDark = Color(0xFF18150B)
val SurfaceContainerDark = Color(0xFF1A1A17)
val SecondaryContainerDark = Color(0xFF4E4639)
val OnSecondaryContainerDark = Color(0xFFEBE0CE)





// --- LIGHT THEME COLORS ---
val PrimaryLight = Color(0xFFD9730D)
val OnPrimaryLight = Color(0xFFFFFFFF)
val PrimaryContainerLight = Color(0xFFFFDDBA) // Soft peach for highlighted/active states
val OnPrimaryContainerLight = Color(0xFF2B1600) // Very dark brown for text on containers

// 2. Secondary: A refined warm taupe (gray-brown), removing the muddy khaki tones.
val SecondaryLight = Color(0xFF735943)
val OnSecondaryLight = Color(0xFFFFFFFF)
val SecondaryContainerLight = Color(0xFFFFDCBE) // Warm sand
val OnSecondaryContainerLight = Color(0xFF2A1706)

// 3. Tertiary: A muted, professional sage/olive green.
val TertiaryLight = Color(0xFF516646)
val OnTertiaryLight = Color(0xFFFFFFFF)

// 4. Background & Surface: Unified!
// Both are now a perfectly clean, warm off-white. No more clashing gray.
val BackgroundLight = Color(0xFFFCF8F4)
val SurfaceLight = Color(0xFFFCF8F4)
val OnBackgroundLight = Color(0xFF201A15) // A warm, soft black (much easier on the eyes than pure #000000)
val OnSurfaceLight = Color(0xFF201A15)

// 5. Variants & Containers: These use the exact same color math as the background,
// just slightly darkened to create depth for your cards and text fields.
val SurfaceVariantLight = Color(0xFFEAE1D9) // Clean beige for text field borders/unselected cards
val OnSurfaceVariantLight = Color(0xFF4C453F)
val SurfaceContainerLight = Color(0xFFF4EBE3) // Slightly elevated for standard cards

// 6. Error (Kept standard Material 3 Red for clear UX)
val ErrorLight = Color(0xFFBA1A1A)
val OnErrorLight = Color(0xFFFFFFFF)