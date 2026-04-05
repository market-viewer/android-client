package cz.cvut.fel.zan.marketviewer.core.network

import android.util.Log
import io.ktor.utils.io.CancellationException
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException

inline fun <T> safeApiCall(
    onError: (errorMessage: String) -> T,
    apiCall: () -> T
) : T {
    return try {
        apiCall()
    } catch (e: IOException) {
        e.message?.let { Log.d("Network error", it) }
        onError("No internet connection.")
    } catch (_: SerializationException) {
        onError("Failed to process server response")
    } catch (e: CancellationException) {
        throw e //throw the exception so coroutines can cancel properly
    } catch (e: Exception) {
        onError("Unknown error: ${e.message}")
    }
}