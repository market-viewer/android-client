package cz.cvut.fel.zan.marketviewer.feature.devices.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class DeviceNameAndHashDto (
    val name: String,
    val hash: String
) {
}