package au.edu.jcu.fittrackplus.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

/**
 * UI-only design tokens used to keep spacing and shape consistent across screens.
 *
 * Notes:
 * - This object is intentionally kept in the UI layer.
 * - Adjusting these values should not affect any business logic.
 */
object FitTrackDimens {
    /** Default padding applied to most screens. */
    val ScreenPadding = 16.dp

    /** Spacing between major sections on a screen. */
    val SectionSpacing = 16.dp

    /** Spacing between items within a section (forms, lists, etc.). */
    val ItemSpacing = 12.dp

    /** Default corner radius for cards. */
    val CardRadius = 16.dp

    /** Smaller corner radius for buttons and compact elements. */
    val SmallRadius = 12.dp
}

/**
 * A consistent screen-level container.
 *
 * Recommended usage:
 * - Wrap the main content of each screen with [FitTrackScreen] to apply the same
 *   padding and default vertical spacing everywhere.
 */
@Composable
fun FitTrackScreen(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(FitTrackDimens.ScreenPadding),
        verticalArrangement = Arrangement.spacedBy(FitTrackDimens.ItemSpacing),
        content = content
    )
}

/**
 * A consistent section title style (e.g., for grouping settings or workout blocks).
 */
@Composable
fun FitTrackSectionTitle(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 2.dp)
    )
}

/**
 * A reusable card container for list rows, form blocks, or section panels.
 *
 * Behavior:
 * - If [onClick] is provided, the whole card becomes clickable without changing
 *   any underlying business logic.
 */
@Composable
fun FitTrackCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(FitTrackDimens.CardRadius)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) {
                    Modifier
                        .clip(shape)
                        .clickable { onClick() }
                } else Modifier
            ),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            content = content
        )
    }
}

/**
 * A consistent "click-to-select" outlined field for dropdowns, date pickers, and filters.
 *
 * Key points:
 * - `enabled = true` keeps the outlined border in the normal style (not greyed out).
 * - A transparent overlay captures clicks so the field remains read-only while still interactive.
 */
@Composable
fun ClickableOutlinedField(
    value: String,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    supportingText: String? = null
) {
    Box(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            enabled = true, // Keep the standard outlined style while preventing input edits.
            label = { Text(label) },
            supportingText = supportingText?.let { { Text(it) } },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        // Transparent overlay: clicking anywhere triggers the action (e.g., open menu/picker).
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { onClick() }
        )
    }
}

/**
 * A consistent primary action button (the main call-to-action on a screen).
 */
@Composable
fun FitTrackPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(FitTrackDimens.SmallRadius),
        modifier = modifier.fillMaxWidth()
    ) {
        Text(text)
    }
}

/**
 * A consistent secondary action button for non-destructive or less prominent actions.
 */
@Composable
fun FitTrackSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(FitTrackDimens.SmallRadius),
        modifier = modifier.fillMaxWidth()
    ) {
        Text(text)
    }
}

/**
 * A consistent "danger" button for destructive or irreversible actions (e.g., delete).
 */
@Composable
fun FitTrackDangerButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(FitTrackDimens.SmallRadius),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.error,
            contentColor = MaterialTheme.colorScheme.onError
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Text(text)
    }
}

/**
 * A standardized icon button size used across the app (e.g., plan actions, quick actions).
 */
@Composable
fun FitTrackIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.size(40.dp)
    ) {
        content()
    }
}