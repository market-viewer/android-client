package cz.cvut.fel.zan.marketviewer.feature.devices.presentation.list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.cvut.fel.zan.marketviewer.core.domain.ApiResult
import cz.cvut.fel.zan.marketviewer.core.utils.TokenManager
import cz.cvut.fel.zan.marketviewer.feature.devices.domain.model.MarketViewerDevice
import cz.cvut.fel.zan.marketviewer.feature.devices.domain.repository.DeviceRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DeviceListScreenState(
    val devices: List<MarketViewerDevice> = emptyList(),
    val isLoading: Boolean = false,
    val errorMsg: String? = null,
    val showCreateDialog: Boolean = false,
    val newDeviceName: String = "",
    val dialogErrorMsg: String? = null
)

class DeviceListViewModel(
    private val deviceRepository: DeviceRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(DeviceListScreenState())
    val uiState: StateFlow<DeviceListScreenState> = _uiState.asStateFlow()

    private val _uiEffect = Channel<DeviceListEffect>()
    val uiEffect = _uiEffect.receiveAsFlow()

    init {
        fetchDevices()
    }

    fun onEvent(event: DeviceListScreenEvent) {
        when (event) {
            is DeviceListScreenEvent.Logout -> {
                viewModelScope.launch {
                    tokenManager.forceLogout()
                }
            }
            is DeviceListScreenEvent.RefreshScreen -> {
                fetchDevices()
            }
            is DeviceListScreenEvent.OpenCreateDialog -> {
                _uiState.update { it.copy(showCreateDialog = true, newDeviceName = "", dialogErrorMsg = null) }
            }
            is DeviceListScreenEvent.CloseCreateDialog -> {
                _uiState.update { it.copy(showCreateDialog = false) }
            }
            is DeviceListScreenEvent.NewDeviceNameChanged -> {
                _uiState.update { it.copy(newDeviceName = event.name) }
            }
            is DeviceListScreenEvent.CreateDeviceSubmit -> {
                createDevice()
            }
            is DeviceListScreenEvent.DeviceDetailOpen -> {
                //
            }
        }
    }

    private fun fetchDevices() {
        _uiState.update { it.copy(isLoading = true, errorMsg = null) }

        viewModelScope.launch {
            when (val result = deviceRepository.listDevices()) {
                is ApiResult.Success -> {
                    _uiState.update {
                        it.copy(devices = result.data, isLoading = false)
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

    private fun createDevice() {
        val deviceName = _uiState.value.newDeviceName.trim()

        viewModelScope.launch {
            when (val result = deviceRepository.createDevice(deviceName)) {
                is ApiResult.Success -> {
                    //display the new device in the device list
                    val newDevice = MarketViewerDevice(id = result.data.deviceId, name = deviceName, screenCount = 0)
                    val newDeviceList = _uiState.value.devices + newDevice
                    _uiState.update { it.copy(showCreateDialog = false, newDeviceName = "", devices =  newDeviceList) }

                    //show success snackbar
                    _uiEffect.send(DeviceListEffect.ShowSnackbar("Device created successfully!"))
                }
                is ApiResult.Error -> {
                    _uiState.update {
                        it.copy(dialogErrorMsg = result.message)
                    }
                    Log.d("Device create error", result.message)
                }
            }
        }

    }

    sealed interface DeviceListScreenEvent {
        data object Logout : DeviceListScreenEvent
        data object RefreshScreen : DeviceListScreenEvent
        data object OpenCreateDialog : DeviceListScreenEvent
        data object CloseCreateDialog : DeviceListScreenEvent
        data class NewDeviceNameChanged(val name: String) : DeviceListScreenEvent
        data object CreateDeviceSubmit : DeviceListScreenEvent

        //on device item click
        data object DeviceDetailOpen : DeviceListScreenEvent
    }

    sealed interface DeviceListEffect {
        data class ShowSnackbar(val message: String) : DeviceListEffect
    }
}