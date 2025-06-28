package com.ecomobile.base

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import com.ecomobile.base.storage_data.AppStates

open class BaseApplication: Application(), DefaultLifecycleObserver {

    override fun onCreate() {
        super<Application>.onCreate()
        AppStates.init(applicationContext)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }
}