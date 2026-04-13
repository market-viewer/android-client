package cz.cvut.fel.zan.marketviewer.feature.screens.presentation.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.cvut.fel.zan.marketviewer.core.domain.ApiResult
import cz.cvut.fel.zan.marketviewer.feature.screens.data.remote.dto.toDto
import cz.cvut.fel.zan.marketviewer.feature.screens.domain.model.MarketViewerScreen
import cz.cvut.fel.zan.marketviewer.feature.screens.domain.repository.ScreenRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ScreenConfigState(
    val saveErrorMsg: String? = null,
    val isLoading: Boolean = false,
)

class ScreenConfigViewModel(
    val screenRepository: ScreenRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScreenConfigState())
    val uiState: StateFlow<ScreenConfigState> = _uiState.asStateFlow()

    private val _uiEffect = Channel<ScreenConfigEffect>()
    val uiEffect = _uiEffect.receiveAsFlow()

    fun onEvent(event: ScreenConfigEvents) {
        when (event) {
            is ScreenConfigEvents.SaveScreenConfig -> {
                saveScreenConfig(event.deviceId, event.updatedScreen)
            }
        }
    }

    private fun saveScreenConfig(deviceId: Int, updatedScreen: MarketViewerScreen) {
        _uiState.update { it.copy(isLoading = true, saveErrorMsg = null) }

        viewModelScope.launch {
            when (val result = screenRepository.updateScreen(deviceId, updatedScreen)) {
                is ApiResult.Success -> {
                    _uiState.update {
                        it.copy(isLoading = false)
                    }
                    _uiEffect.send(ScreenConfigEffect.SaveSuccess(result.data))
                }
                is ApiResult.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, saveErrorMsg = result.message)
                    }
                }
            }
        }
    }


    sealed interface ScreenConfigEvents {
        data class SaveScreenConfig(val deviceId: Int, val updatedScreen: MarketViewerScreen) : ScreenConfigEvents
    }

    sealed interface ScreenConfigEffect {
        data class SaveSuccess(val updatedScreen: MarketViewerScreen) : ScreenConfigEffect
    }
}