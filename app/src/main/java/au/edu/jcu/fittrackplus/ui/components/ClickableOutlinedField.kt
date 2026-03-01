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
 * ===== Design tokens (统一风格) =====
 * 只在 UI 层用，不影响任何业务逻辑
 */
object FitTrackDimens {
    val ScreenPadding = 16.dp
    val SectionSpacing = 16.dp
    val ItemSpacing = 12.dp
    val CardRadius = 16.dp
    val SmallRadius = 12.dp
}

/**
 * 统一页面外层容器：所有 Screen 建议都用它包一层
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
 * 统一 Section 标题（更像健身 App 的分区结构）
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
 * 统一 Card 容器（列表条目、表单块、分区容器都可以用）
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
 * 统一“可点击选择框”（用于 dropdown / date picker / filter）
 *
 * - 关键点：enabled = true，保证黑边框
 * - 用 overlay 捕获点击，不改你原有交互
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
            enabled = true, // ✅ 永远正常边框（不灰）
            label = { Text(label) },
            supportingText = supportingText?.let { { Text(it) } },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        // 覆盖层：点任意位置触发
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { onClick() }
        )
    }
}

/**
 * 统一 Primary 按钮（更像 App 主按钮）
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
 * 统一 Secondary 按钮（次操作）
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
 * 统一 Danger 按钮（删除/不可逆操作）
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
 * 统一小图标按钮（Schedule/History/Plan actions 常用）
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