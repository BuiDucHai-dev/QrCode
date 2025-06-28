package com.eco.ads.ads

sealed class AdStatus {
    data object None : AdStatus()
    data object Loading : AdStatus()
    data object Loaded : AdStatus()
    data class Failed(val error: String?) : AdStatus()
}