package io.github.jqssun.airplay

import android.content.Context
import android.provider.Settings

private const val FALLBACK_SERVER_NAME = "Android AirPlay"

// name the user gave this device in system settings
fun Context.deviceName(): String? = try {
    Settings.Global.getString(contentResolver, Settings.Global.DEVICE_NAME)?.trim()?.takeIf { it.isNotEmpty() }
} catch (_: Exception) {
    null
}

// an empty server name follows the device name
fun Context.effectiveServerName(name: String): String =
    name.ifBlank { deviceName() ?: FALLBACK_SERVER_NAME }
