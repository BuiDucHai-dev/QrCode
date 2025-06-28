package com.ecomobile.base.extension

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityOptionsCompat
import com.ecomobile.base.R

fun ActivityResultLauncher<Intent>.launchWithTransition(context: Context, intent: Intent?) {
    if (intent == null) {
        Log.d("ActivityResultLauncher", "Intent is null, cannot start activity")
        return
    }
    try {
        val option = ActivityOptionsCompat.makeCustomAnimation(context, R.anim.start_slide_enter, R.anim.start_slide_exit)
        launch(intent, option)
    } catch (e: Exception) {
        launch(intent)
    }
}