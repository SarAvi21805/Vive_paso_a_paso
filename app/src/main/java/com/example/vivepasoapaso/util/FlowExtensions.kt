package com.example.vivepasoapaso.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.EmptyCoroutineContext

@Composable
fun <T> Flow<T>.collectAsStateWithLifecycleFix(
    initial: T,
    lifecycle: Lifecycle = LocalLifecycleOwner.current.lifecycle,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED
): State<T> {
    val flow = remember(this, lifecycle) {
        this.flowWithLifecycle(lifecycle, minActiveState)
    }
    return flow.collectAsState(initial, EmptyCoroutineContext)
}

@Composable
fun <T> Flow<T>.collectAsStateWithLifecycleFix(
    lifecycle: Lifecycle = LocalLifecycleOwner.current.lifecycle,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED
): State<T?> {
    val flow = remember(this, lifecycle) {
        this.flowWithLifecycle(lifecycle, minActiveState)
    }
    return flow.collectAsState(null, EmptyCoroutineContext)
}