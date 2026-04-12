package cz.cvut.fel.zan.marketviewer.feature.screens.presentation.edit

import androidx.lifecycle.ViewModel

class ScreenConfigViewModel() : ViewModel() {

    sealed interface ScreenConfigEvents {
        object EditShit : ScreenConfigEvents
    }
}