package com.ecomobile.base.storage_data

import android.content.Context
import android.content.SharedPreferences

object AppStates {

    private const val PREFS = "PREFS"
    private const val IS_PURCHASE = "IS_PURCHASE"
    private const val WAS_RATE = "WAS_RATE"
    private const val OPEN_APP_SESSION = "OPEN_APP_SESSION"
    private const val SCAN_WITH_VIBRATE = "SCAN_WITH_VIBRATE"
    private const val SCAN_WITH_SOUND = "SCAN_WITH_SOUND"

    private lateinit var preference: SharedPreferences

    fun init(context: Context) {
        preference = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    }

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return preference.getBoolean(key, defaultValue)
    }

    fun putBoolean(key: String, value: Boolean) {
        preference.edit().putBoolean(key, value).apply()
    }

    fun getInt(key: String): Int {
        return preference.getInt(key, 0)
    }

    fun putInt(key: String, value: Int) {
        preference.edit().putInt(key, value).apply()
    }

    fun getLong(key: String): Long = preference.getLong(key, 0L)

    fun putLong(key: String, value: Long) = preference.edit().putLong(key, value).apply()

    var isPurchase: Boolean = false
        get() = preference.getBoolean(IS_PURCHASE, false)
        set(value) {
            field = value
            preference.edit().putBoolean(IS_PURCHASE, value).apply()
        }
    var wasRate: Boolean = false
        get() = preference.getBoolean(WAS_RATE, false)
        set(value) {
            field = value
            preference.edit().putBoolean(WAS_RATE, value).apply()
        }
    var openAppSession: Int = 0
        get() = preference.getInt(OPEN_APP_SESSION, 0)
        set(value) {
            field = value
            preference.edit().putInt(OPEN_APP_SESSION, value).apply()
        }
    var scanWithVibrate: Boolean = true
        get() = preference.getBoolean(SCAN_WITH_VIBRATE, true)
        set(value) {
            field = value
            preference.edit().putBoolean(SCAN_WITH_VIBRATE, value).apply()
        }
    var scanWithSound: Boolean = true
        get() = preference.getBoolean(SCAN_WITH_SOUND, true)
        set(value) {
            field = value
            preference.edit().putBoolean(SCAN_WITH_SOUND, value).apply()
        }
}

