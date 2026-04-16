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
import cz.cvut.fel.zan.marketviewer.feature.screens.domain.model.ScreenType
import cz.cvut.fel.zan.marketviewer.feature.screens.domain.repository.ScreenRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DeviceDetailState(
    val deviceId: Int = 0,
    val deviceName: String? = null,
    val deviceHash: String? = null,
    val isLoading: Boolean = true,
    val errorMsg: String? = null,
    val isEditingName: Boolean = false,
    val showAddScreenDialog: Boolean = false,
    val nameChangeErrorMsg: String? = null,
    val screenAddErrorMsg: String? = null,
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
        listenLocalDb()
        syncScreensRemote()
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

            is DeviceDetailEvents.ChangeDeviceName -> {
                changeDeviceName(event.newName)
            }

            is DeviceDetailEvents.ToggleNameEdit -> {
                _uiState.update { it.copy(isEditingName = !it.isEditingName, nameChangeErrorMsg = null) }
            }

            is DeviceDetailEvents.ToggleScreenAddDialog -> {
                _uiState.update { it.copy(showAddScreenDialog = event.show, screenAddErrorMsg = null) }
            }

            is DeviceDetailEvents.AddScreenEvent -> {
                addNewScreen(event.screenType)
            }
        }
    }

    fun listenLocalDb() {
        viewModelScope.launch {
            screenRepository.getScreensForDevice(uiState.value.deviceId).collect { dbScreens ->
                _uiState.update { it.copy(screens = dbScreens, isLoading = false) }
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

    private fun syncScreensRemote() {
        _uiState.update { it.copy(isLoading = true, errorMsg = null) }

        viewModelScope.launch {
            when (val result = screenRepository.syncScreens(uiState.value.deviceId)) {
                is ApiResult.Error -> {

                    _uiState.update { currentState ->
                        currentState.copy(errorMsg = result.message, isLoading = false)
                    }
                }
                is ApiResult.Success -> {
                    Log.d("SyncSuccess", "Database updated form remote.")
                }
            }
        }
    }

    private fun deleteDevice() {
        viewModelScope.launch {
            when (deviceRepository.deleteDevice(uiState.value.deviceId)) {
                is ApiResult.Success -> {
                    //navigate back
                    _uiEffect.send(DeviceDetailEffect.GoBackToDeviceList)
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
                    syncScreensRemote() // when reorder fails, sync with server
                    Log.e("ReorderError", "Error reordering data")
                }
            }
        }
    }

    private fun changeDeviceName(newName: String) {
        if (newName.isBlank()) {
            _uiState.update { it.copy(nameChangeErrorMsg = "Name cannot be empty") }
        }

        _uiState.update { it.copy(nameChangeErrorMsg = null) }
        CoroutineScope(Dispatchers.IO).launch {
            when (val result = deviceRepository.changeDeviceName(uiState.value.deviceId, newName)) {
                is ApiResult.Success -> {
                    _uiState.update {
                        it.copy(
                            deviceName = newName,
                            isEditingName = false
                        )
                    }
                    Log.d("NameChange", "Successfully updated name")
                }
                is ApiResult.Error -> {
                    _uiState.update { it.copy(nameChangeErrorMsg = result.message) }
                    Log.e("NameChange", "Error changing name")
                }
            }
        }
    }

    private fun addNewScreen(screenType: ScreenType) {
        viewModelScope.launch {
            when (val result = screenRepository.createScreen(uiState.value.deviceId, screenType)) {
                is ApiResult.Success -> {
                    _uiState.update {
                        it.copy(showAddScreenDialog = false)
                    }
                    Log.d("ScreenAdd", "Successfully added new screen")
                }
                is ApiResult.Error -> {
                    _uiState.update { it.copy(screenAddErrorMsg = result.message) }
                    Log.e("ScreenAdd", "Error adding new screen")
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
        data class ChangeDeviceName(val newName: String) : DeviceDetailEvents
        object ToggleNameEdit : DeviceDetailEvents
        data class ToggleScreenAddDialog(val show: Boolean) : DeviceDetailEvents
        data class AddScreenEvent(val screenType: ScreenType) : DeviceDetailEvents

    }

    sealed interface DeviceDetailEffect {
        data object GoBackToDeviceList : DeviceDetailEffect
    }
}