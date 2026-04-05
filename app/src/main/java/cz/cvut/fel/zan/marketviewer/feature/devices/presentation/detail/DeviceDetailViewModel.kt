package cz.cvut.fel.zan.marketviewer.feature.devices.presentation.detail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import cz.cvut.fel.zan.marketviewer.core.domain.ApiResult
import cz.cvut.fel.zan.marketviewer.core.navigation.Route
import cz.cvut.fel.zan.marketviewer.feature.devices.domain.repository.DeviceRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

data class DeviceDetailState(
    val deviceId: Int = 0
)

class DeviceDetailViewModel(
    val deviceRepository: DeviceRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(DeviceDetailState(
        deviceId = savedStateHandle.toRoute<Route.DeviceDetail>().deviceId
    ))
    val uiState: StateFlow<DeviceDetailState> = _uiState.asStateFlow()

    private val _uiEffect = Channel<DeviceDetailEffect>()
    val uiEffect = _uiEffect.receiveAsFlow()


    fun onEvent(event: DeviceDetailEvents) {
        when (event) {
            is DeviceDetailEvents.DeleteDeviceClick -> {
                deleteDevice()
            }
        }
    }

    private fun deleteDevice() {
        viewModelScope.launch {
            when (val result = deviceRepository.deleteDevice(uiState.value.deviceId)) {
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

    sealed interface DeviceDetailEvents {
        data object DeleteDeviceClick : DeviceDetailEvents
    }

    sealed interface DeviceDetailEffect {
        data object GoBackToDeviceList : DeviceDetailEffect
    }
}