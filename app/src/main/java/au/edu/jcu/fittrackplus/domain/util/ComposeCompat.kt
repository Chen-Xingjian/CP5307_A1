package au.edu.jcu.fittrackplus.domain.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * Lifecycle-aware collecting for StateFlow.
 *
 * Requires dependency:
 * implementation("androidx.lifecycle:lifecycle-runtime-compose:<version>")
 *
 * If you don't have it, replace collectAsStateWithLifecycle(...) with collectAsState(...)
 * and add import: androidx.compose.runtime.collectAsState
 */
@Composable
fun <T> StateFlow<T>.collectAsStateWithLifecycleCompat(
    initial: T = this.value
): State<T> {
    return this.collectAsStateWithLifecycle(initialValue = initial)
}

/**
 * Lifecycle-aware collecting for Flow.
 *
 * If you don't have lifecycle-runtime-compose, switch to collectAsState(...)
 * and provide an explicit initial.
 */
@Composable
fun <T> Flow<T>.collectAsStateWithLifecycleCompat(
    initial: T
): State<T> {
    return this.collectAsStateWithLifecycle(initialValue = initial)
}