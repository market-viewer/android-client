package cz.cvut.fel.zan.marketviewer.core.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.flagging.Flags
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import cz.cvut.fel.zan.marketviewer.MainActivity
import cz.cvut.fel.zan.marketviewer.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.TimeUnit

class ServerHealthWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    //check if the backend server is offline
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        //get the server url passed into the worker
        val serverUrl = inputData.getString("SERVER_URL") ?: return@withContext Result.failure()

        //perform ping to the server
        try {
            val url = URL(serverUrl+ "ping")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000

            val responseCode = connection.responseCode

            if (responseCode !in 200..299) {
                showServerOfflineNotification(serverUrl)
            }

            connection.disconnect()
            Result.success()

        } catch (e: Exception) {
            //network timeout, or server totally unreachable
            showServerOfflineNotification(serverUrl)
            Result.success()
        }

    }

    private fun showServerOfflineNotification(serverUrl: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "server_health_channel"

        val channel = NotificationChannel(
            channelId,
            "Server Health Alerts",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Notifies you when the saved backend server is offline."
        }

        //open the app on notification click
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        notificationManager.createNotificationChannel(channel)
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.market_viewer_logo)
            .setContentTitle("Server is offline")
            .setContentText("Cannot reach server: $serverUrl")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        //send the notification always with same id -> dont spam new notifcations
        notificationManager.notify(1, notification)
    }
}

//schedule the worker
fun scheduleServerHealthCheck(context: Context, currentServerUrl: String) {
    val inputData = workDataOf("SERVER_URL" to currentServerUrl)

    //run the check only if the user is connected
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    val healthCheckWorkRequest = PeriodicWorkRequestBuilder<ServerHealthWorker>(8, TimeUnit.HOURS)
        .setConstraints(constraints)
        .setInputData(inputData)
        .build()

    //ensues when the url gets updated the worker gets also updated
    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "ServerHealthCheckWork",
        ExistingPeriodicWorkPolicy.UPDATE,
        healthCheckWorkRequest
    )
}