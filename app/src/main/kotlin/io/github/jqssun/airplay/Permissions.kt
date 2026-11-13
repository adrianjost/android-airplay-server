package io.github.jqssun.airplay

import android.Manifest
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Process
import android.provider.Settings
import androidx.core.content.ContextCompat

fun Context.hasLocalNetwork() = Build.VERSION.SDK_INT < Build.VERSION_CODES.CINNAMON_BUN ||
    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_LOCAL_NETWORK) == PackageManager.PERMISSION_GRANTED

fun Context.appSettingsIntent() =
    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", packageName, null))

// android 13+ only honours ACQUIRE_CAUSES_WAKEUP with the turn screen on special app access
fun Context.canTurnScreenOn(): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
    val ops = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
    return try {
        when (ops.unsafeCheckOpNoThrow(OP_TURN_SCREEN_ON, Process.myUid(), packageName)) {
            AppOpsManager.MODE_ALLOWED -> true
            AppOpsManager.MODE_DEFAULT ->
                ContextCompat.checkSelfPermission(this, Manifest.permission.TURN_SCREEN_ON) == PackageManager.PERMISSION_GRANTED
            else -> false
        }
    } catch (_: Exception) {
        // op unknown to this build
        true
    }
}

// the dedicated screen may not exist on some builds
fun Context.openTurnScreenOnSettings() {
    val pkg = Uri.fromParts("package", packageName, null)
    val intents = listOf(Intent(ACTION_TURN_SCREEN_ON_SETTINGS, pkg), Intent(ACTION_TURN_SCREEN_ON_SETTINGS), appSettingsIntent())
    for (intent in intents) {
        try {
            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            return
        } catch (_: Exception) {
        }
    }
}

private const val OP_TURN_SCREEN_ON = "android:turn_screen_on"
private const val ACTION_TURN_SCREEN_ON_SETTINGS = "android.settings.TURN_SCREEN_ON_SETTINGS"
