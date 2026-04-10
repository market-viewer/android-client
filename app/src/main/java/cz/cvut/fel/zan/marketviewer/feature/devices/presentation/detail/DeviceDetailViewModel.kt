package cz.cvut.fel.zan.marketviewer.feature.devices.presentation.detail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import cz.cvut.fel.zan.marketviewer.core.domain.ApiResult
import cz.cvut.fel.zan.marketviewer.core.navigation.Route
import cz.cvut.fel.zan.marketviewer.feature.devices.domain.repository.DeviceRepository
import cz.cvut.fel.zan.marketviewer.feature.screens.domain.model.MarketViewerScreen
import cz.cvut.fel.zan.marketviewer.feature.screens.domain.repository.ScreenRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DeviceDetailState(
    val deviceId: Int = 0,
    val deviceName: String? = null,
    val deviceHash: String? = null,
    val isLoading: Boolean = true,
    val errorMsg: String? = null,
    val screens: List<MarketViewerScreen>? = null
)

class DeviceDetailViewModel(
    val deviceRepository: DeviceRepository,
    val screenRepository: ScreenRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(DeviceDetailState(
        deviceId = savedStateHandle.toRoute<Route.DeviceDetail>().deviceId
    ))
    val uiState: StateFlow<DeviceDetailState> = _uiState.asStateFlow()

    private val _uiEffect = Channel<DeviceDetailEffect>()
    val uiEffect = _uiEffect.receiveAsFlow()

    init {
        fetchDeviceNameAndHash()
        fetchDeviceScreens()
    }

    fun onEvent(event: DeviceDetailEvents) {
        when (event) {
            is DeviceDetailEvents.DeleteDeviceClick -> {
                deleteDevice()
            }
            is DeviceDetailEvents.DeleteScreenLocally -> {
                val currentScreens = _uiState.value.screens?.toMutableList() ?: return
                currentScreens.remove(event.screenToDelete)
                _uiState.update { it.copy(screens = currentScreens) }
            }

            is DeviceDetailEvents.UndoDeleteScreen -> {
                val currentScreens = _uiState.value.screens?.toMutableList() ?: return

                if (event.index in 0..currentScreens.size) {
                    currentScreens.add(event.index, event.screenToDelete)
                } else {
                    currentScreens.add(event.screenToDelete)
                }

                _uiState.update { it.copy(screens = currentScreens) }
            }

            is DeviceDetailEvents.ConfirmDeleteScreen -> {
                deleteScreenRemote(event.screenToDelete, event.originalIndex)
            }

            is DeviceDetailEvents.ReorderScreenLocally -> {
                val currentList = _uiState.value.screens?.toMutableList() ?: return

                val moveItem = currentList.removeAt(event.fromIndex)
                currentList.add(event.toIndex, moveItem)

                _uiState.update { it.copy(screens = currentList) }
            }

            is DeviceDetailEvents.ConfirmReorderScreens -> {
                reorderScreensRemote()
            }
        }
    }

    private fun fetchDeviceNameAndHash() {
        _uiState.update { it.copy(isLoading = true, errorMsg = null) }


        viewModelScope.launch {
            when (val result = deviceRepository.getDeviceNameAndHash(uiState.value.deviceId)) {
                is ApiResult.Success -> {
                    _uiState.update {
                        it.copy(deviceName = result.data.name, deviceHash = result.data.hash, isLoading = false)
                    }
                }
                is ApiResult.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, errorMsg = result.message)
                    }
                }
            }
        }
    }

    private fun fetchDeviceScreens() {
        _uiState.update { it.copy(isLoading = true, errorMsg = null) }


        viewModelScope.launch {
            when (val result = screenRepository.getScreensForDevice(uiState.value.deviceId)) {
                is ApiResult.Success -> {
                    val fetchedScreenList = result.data

                    _uiState.update { currentState ->
                        currentState.copy(screens = fetchedScreenList.sortedBy{ it.position }, isLoading = false)
                    }
                }
                is ApiResult.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, errorMsg = result.message)
                    }
                }
            }
        }
    }

    private fun deleteDevice() {
        viewModelScope.launch {
            when (deviceRepository.deleteDevice(uiState.value.deviceId)) {
                is ApiResult.Success -> {
                    //navigate back
                    _uiEffect.send(DeviceDetailEffect.GoBackWithDeleteResult(uiState.value.deviceId))
                }
                is ApiResult.Error -> {
                    //display the error msg
                    Log.e("Error deleting", "Error while deleting device")
                }
            }
        }
    }

    private fun deleteScreenRemote(screenToDelete: MarketViewerScreen, originalIndex: Int) {
        // don't run it in the viewmodel scope, because we need to delete the screen even on back button press
        CoroutineScope(Dispatchers.IO).launch {
            when (screenRepository.deleteScreen(screenToDelete.id, uiState.value.deviceId)) {
                is ApiResult.Success -> {
                    //refetch the screens
                    Log.d("DeleteSuccess", "Screen deleted successfully from remote")
                }
                is ApiResult.Error -> {
                    //put it back when the request fails
                    onEvent(DeviceDetailEvents.UndoDeleteScreen(screenToDelete, originalIndex))
                    Log.e("Error deleting", "Error while deleting device")
                }
            }
        }
    }

    private fun reorderScreensRemote() {
        //get screen ids in order
        val currentScreens = _uiState.value.screens?.toMutableList() ?: return
        val screenIdList: List<Int> = currentScreens.map { it.id }

        CoroutineScope(Dispatchers.IO).launch {
            when (screenRepository.reorderScreens(screenIdList, uiState.value.deviceId)) {
                is ApiResult.Success -> {
                    //refetch the screens
                    Log.d("ReorderSuccess", "Screens reordered successfully on remote")
                }
                is ApiResult.Error -> {
                    Log.e("ReorderError", "Error reordering data")
                }
            }
        }
    }

    sealed interface DeviceDetailEvents {
        object DeleteDeviceClick : DeviceDetailEvents
        data class DeleteScreenLocally(val screenToDelete: MarketViewerScreen) : DeviceDetailEvents
        data class UndoDeleteScreen(val screenToDelete: MarketViewerScreen, val index: Int) : DeviceDetailEvents
        data class ConfirmDeleteScreen(val screenToDelete: MarketViewerScreen, val originalIndex: Int) : DeviceDetailEvents
        data class ReorderScreenLocally(val fromIndex: Int, val toIndex: Int) : DeviceDetailEvents
        object ConfirmReorderScreens : DeviceDetailEvents

    }

    sealed interface DeviceDetailEffect {
        data object GoBackToDeviceList : DeviceDetailEffect
        data class GoBackWithDeleteResult(val deviceId: Int) : DeviceDetailEffect
    }
}