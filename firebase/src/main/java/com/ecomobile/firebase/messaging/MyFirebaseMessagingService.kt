package com.ecomobile.firebase.messaging

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService: FirebaseMessagingService() {

    companion object {
        private const val URL = "url"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.e("FCM_TOKEN", token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val title = remoteMessage.notification?.title
        val body = remoteMessage.notification?.body

        if (remoteMessage.data.isNotEmpty()) {
            val url = remoteMessage.data[URL]
            showNotification(title, body, url)
        } else {
            showNotification(title, body)
        }
    }

    private fun showNotification(title: String?, message: String?) {
        Log.e("HAI", "showNotification: $title - $message")
    }

    private fun showNotification(title: String?, message: String?, url: String?) {
        Log.e("HAI", "showNotification: $title - $message - $url")
    }
}