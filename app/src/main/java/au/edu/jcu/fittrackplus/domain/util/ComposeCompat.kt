package au.edu.jcu.fittrackplus.domain.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * Compose helpers for collecting [StateFlow] / [Flow] as [State] in a lifecycle-aware way.
 *
 * Why this exists:
 * - Some modules/projects may not consistently use `collectAsStateWithLifecycle(...)`.
 * - These extensions provide a single, searchable API surface for lifecycle-aware collection.
 *
 * Requirements:
 * - Ensure you have the lifecycle-compose runtime dependency:
 *   implementation("androidx.lifecycle:lifecycle-runtime-compose:<version>")
 *
 * Fallback:
 * - If you cannot use lifecycle-aware collection, replace these calls with `collectAsState(...)`
 *   (and provide an explicit initial value for `Flow`).
 */

/**
 * Collects a [StateFlow] into a Compose [State] that is automatically started/stopped
 * based on the current Lifecycle (e.g., STARTED/STOPPED).
 *
 * @param initial Initial value used by Compose before the first emission. Defaults to [StateFlow.value].
 */
@Composable
fun <T> StateFlow<T>.collectAsStateWithLifecycleCompat(
    initial: T = this.value
): State<T> {
    return this.collectAsStateWithLifecycle(initialValue = initial)
}

/**
 * Collects a [Flow] into a Compose [State] that is automatically started/stopped
 * based on the current Lifecycle (e.g., STARTED/STOPPED).
 *
 * Note:
 * - [Flow] does not have a synchronous `value`, so an explicit [initial] is required.
 *
 * @param initial Initial value used by Compose before the first emission.
 */
@Composable
fun <T> Flow<T>.collectAsStateWithLifecycleCompat(
    initial: T
): State<T> {
    return this.collectAsStateWithLifecycle(initialValue = initial)
}