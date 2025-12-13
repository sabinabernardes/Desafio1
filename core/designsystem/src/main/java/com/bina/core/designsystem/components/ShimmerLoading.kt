package com.bina.core.designsystem.components

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bina.core.designsystem.colors.ColorBackground
import com.bina.core.designsystem.dimens.Dimens
import com.bina.core.designsystem.picpaytheme.PicPayTheme

@Composable
private fun createShimmerBrush(
    shimmerOffset: Float,
    targetValue: Float = Dimens.shimmerTargetValue,
    backgroundColor: Color = Color.LightGray.copy(alpha = Dimens.shimmerBackgroundColorAlpha),
    shimmerColor: Color = Color.White.copy(alpha = Dimens.shimmerHighlightColorAlpha)
): Brush {
    return Brush.linearGradient(
        colors = listOf(
            backgroundColor,
            shimmerColor,
            backgroundColor
        ),
        start = Offset(shimmerOffset - targetValue, Dimens.shimmerOffsetInitialValue),
        end = Offset(shimmerOffset, Dimens.shimmerOffsetInitialValue)
    )
}

@Composable
private fun rememberShimmerOffset(
    targetValue: Float = Dimens.shimmerTargetValue,
    durationMillis: Int = Dimens.shimmerAnimationDuration
): androidx.compose.runtime.State<Float> {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")

    return infiniteTransition.animateFloat(
        initialValue = Dimens.shimmerAnimationInitialValue,
        targetValue = targetValue,
        animationSpec = infiniteRepeatable(
            animation = androidx.compose.animation.core.tween(
                durationMillis = durationMillis,
                easing = FastOutLinearInEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_offset"
    )
}

@Composable
fun ShimmerLoadingItem(
    modifier: Modifier = Modifier,
    width: Float = Dimens.shimmerLoadingItemDefaultWidth,
    height: Float = Dimens.shimmerLoadingItemDefaultHeight,
    backgroundColor: Color = Color.LightGray.copy(alpha = Dimens.shimmerBackgroundColorAlpha),
    shimmerColor: Color = Color.White.copy(alpha = Dimens.shimmerHighlightColorAlpha)
) {
    val shimmerOffset = rememberShimmerOffset(targetValue = width)
    val shimmerBrush = createShimmerBrush(
        shimmerOffset = shimmerOffset.value,
        targetValue = width,
        backgroundColor = backgroundColor,
        shimmerColor = shimmerColor
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height.dp)
            .background(brush = shimmerBrush, shape = RoundedCornerShape(Dimens.shimmerCornerRadius))
    )
}

@Composable
fun ShimmerUserCardLoading(
    modifier: Modifier = Modifier
) {
    val shimmerOffset = rememberShimmerOffset()
    val shimmerBrush = createShimmerBrush(
        shimmerOffset = shimmerOffset.value
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(ColorBackground)
    ) {
        Row(
            modifier = Modifier
                .padding(
                    start = Dimens.spacing24,
                    end = Dimens.spacing24,
                    top = Dimens.spacing12,
                    bottom = Dimens.spacing12
                ),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(Dimens.spacing52)
                    .background(brush = shimmerBrush, shape = CircleShape)
            )

            Spacer(modifier = Modifier.width(Dimens.spacing16))

            Column(modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(Dimens.shimmerCardNameWidthFraction)
                        .height(Dimens.shimmerCardNameHeight)
                        .background(brush = shimmerBrush, shape = RoundedCornerShape(Dimens.shimmerCornerRadius))
                )
                Spacer(modifier = Modifier.height(Dimens.spacing8))
                Box(
                    modifier = Modifier
                        .fillMaxWidth(Dimens.shimmerCardUsernameWidthFraction)
                        .height(Dimens.shimmerCardUsernameHeight)
                        .background(brush = shimmerBrush, shape = RoundedCornerShape(Dimens.shimmerCornerRadius))
                )
            }
        }
    }
}

@Composable
fun ShimmerUserListLoading(
    modifier: Modifier = Modifier,
    itemCount: Int = Dimens.shimmerDefaultItemCount
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        repeat(itemCount) {
            ShimmerUserCardLoading()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShimmerUserCardLoadingPreview() {
    PicPayTheme {
        ShimmerUserCardLoading()
    }
}

@Preview(showBackground = true)
@Composable
fun ShimmerUserListLoadingPreview() {
    PicPayTheme {
        ShimmerUserListLoading(itemCount = Dimens.shimmerPreviewItemCount)
    }
}
