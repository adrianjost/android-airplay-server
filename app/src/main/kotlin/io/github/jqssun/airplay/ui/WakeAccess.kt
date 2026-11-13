package io.github.jqssun.airplay.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import io.github.jqssun.airplay.canSleepDevice
import io.github.jqssun.airplay.canTurnScreenOn

// re-checked on resume, these are granted in system settings
@Composable
fun rememberOnResume(check: Context.() -> Boolean): Boolean {
    val ctx = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var allowed by remember { mutableStateOf(ctx.check()) }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) allowed = ctx.check()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    return allowed
}

@Composable
fun rememberCanTurnScreenOn(): Boolean = rememberOnResume { canTurnScreenOn() }

@Composable
fun rememberCanSleepDevice(): Boolean = rememberOnResume { canSleepDevice() }
