package cz.cvut.fel.zan.marketviewer.core.domain

sealed interface ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>
    data class Error(val message: String) : ApiResult<Nothing>
}