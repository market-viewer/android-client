package cz.cvut.fel.zan.marketviewer.core.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApiErrorDto(
    val timestamp: String? = null,
    val message: String,
    val path: String? = null
) {
}