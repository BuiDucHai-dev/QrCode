package com.ecomobile.qrcode.startup

import android.content.Context
import android.util.Log
import androidx.startup.Initializer
import com.ecomobile.qrcode.BuildConfig
import com.ecomobile.qrcode.koin.moduleList
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.fragment.koin.fragmentFactory
import org.koin.core.context.startKoin

class WorkManagerInitializer : Initializer<String> {

    override fun create(context: Context): String {
        if (BuildConfig.DEBUG) {
            //Test Ads
//            TestAds.generateDeviceId(context)
        }
        startKoin {
            fragmentFactory()
            androidContext(context.applicationContext)
            modules(moduleList)
        }
        FirebaseApp.initializeApp(context.applicationContext)
        FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = !BuildConfig.DEBUG
        return ""
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}
