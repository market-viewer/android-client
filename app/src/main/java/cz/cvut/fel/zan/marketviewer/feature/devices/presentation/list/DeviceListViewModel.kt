package cz.cvut.fel.zan.marketviewer.feature.devices.presentation.list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.cvut.fel.zan.marketviewer.core.domain.ApiResult
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
) : ViewModel() {

    private val _uiState = MutableStateFlow(DeviceListScreenState())
    val uiState: StateFlow<DeviceListScreenState> = _uiState.asStateFlow()

    private val _uiEffect = Channel<DeviceListEffect>()
    val uiEffect = _uiEffect.receiveAsFlow()

    init {
        observeDatabase()
        syncWithServer()

        //navigate to the device if there is only one device
        val allDevices = _uiState.value.devices
        if (allDevices.size == 1) {
            viewModelScope.launch {
                _uiEffect.send(DeviceListEffect.NavigateToDeviceDetail(allDevices[0].id))
            }
        }
    }

    fun onEvent(event: DeviceListScreenEvent) {
        when (event) {
            is DeviceListScreenEvent.RefreshScreen -> {
                syncWithServer()
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
                viewModelScope.launch {
                    _uiEffect.send(DeviceListEffect.NavigateToDeviceDetail(event.deviceId))
                }
            }
        }
    }

    private fun observeDatabase() {
        viewModelScope.launch {
            deviceRepository.getDevicesAsFlow().collect { dbDevices ->
                _uiState.update { it.copy(devices = dbDevices, isLoading = false) }
            }
        }
    }

    private fun syncWithServer() {
        if (_uiState.value.devices.isEmpty()) {
            _uiState.update { it.copy(isLoading = true, errorMsg = null) }
        }

        viewModelScope.launch {
            when (val result = deviceRepository.syncDevices()) {
                is ApiResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, errorMsg = null) }
                }
                is ApiResult.Error -> {
                    //show remote error only when there are no devices
                    _uiEffect.send(DeviceListEffect.ShowSnackbar("Error syncing devices"))
                    if (_uiState.value.devices.isEmpty()) {
                        _uiState.update { it.copy(isLoading = false, errorMsg = result.message) }
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
                    _uiState.update { it.copy(showCreateDialog = false, newDeviceName = "") }

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
        data object RefreshScreen : DeviceListScreenEvent
        data object OpenCreateDialog : DeviceListScreenEvent
        data object CloseCreateDialog : DeviceListScreenEvent
        data class NewDeviceNameChanged(val name: String) : DeviceListScreenEvent
        data object CreateDeviceSubmit : DeviceListScreenEvent

        //on device item click
        data class DeviceDetailOpen(val deviceId: Int) : DeviceListScreenEvent
    }

    sealed interface DeviceListEffect {
        data class ShowSnackbar(val message: String) : DeviceListEffect
        data class NavigateToDeviceDetail(val deviceId: Int) : DeviceListEffect
    }
}