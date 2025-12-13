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
fun ShimmerLoadingItem(
    modifier: Modifier = Modifier,
    width: Float = 200f,
    height: Float = 20f,
    backgroundColor: Color = Color.LightGray.copy(alpha = 0.3f),
    shimmerColor: Color = Color.White.copy(alpha = 0.5f)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")

    val shimmerOffset = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = width,
        animationSpec = infiniteRepeatable(
            animation = androidx.compose.animation.core.tween(
                durationMillis = 1200,
                easing = FastOutLinearInEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_offset"
    )

    val shimmerBrush = Brush.linearGradient(
        colors = listOf(
            backgroundColor,
            shimmerColor,
            backgroundColor
        ),
        start = Offset(shimmerOffset.value - width, 0f),
        end = Offset(shimmerOffset.value, 0f)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height.dp)
            .background(brush = shimmerBrush, shape = RoundedCornerShape(4.dp))
    )
}

@Composable
fun ShimmerUserCardLoading(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")

    val shimmerOffset = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 400f,
        animationSpec = infiniteRepeatable(
            animation = androidx.compose.animation.core.tween(
                durationMillis = 1200,
                easing = FastOutLinearInEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_offset"
    )

    val shimmerBrush = Brush.linearGradient(
        colors = listOf(
            Color.LightGray.copy(alpha = 0.3f),
            Color.White.copy(alpha = 0.5f),
            Color.LightGray.copy(alpha = 0.3f)
        ),
        start = Offset(shimmerOffset.value - 400f, 0f),
        end = Offset(shimmerOffset.value, 0f)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(ColorBackground)
            .padding(vertical = Dimens.spacing12)
    ) {
        Row(
            modifier = Modifier.padding(start = Dimens.spacing24),
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
                        .fillMaxWidth(0.7f)
                        .height(12.dp)
                        .background(brush = shimmerBrush, shape = RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.height(Dimens.spacing8))
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(14.dp)
                        .background(brush = shimmerBrush, shape = RoundedCornerShape(4.dp))
                )
            }
        }
    }
}

@Composable
fun ShimmerUserListLoading(
    modifier: Modifier = Modifier,
    itemCount: Int = 5
) {
    Column(modifier = modifier) {
        repeat(itemCount) {
            ShimmerUserCardLoading()
            Spacer(modifier = Modifier.height(Dimens.spacing8))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShimmerLoadingItemPreview() {
    PicPayTheme {
        ShimmerLoadingItem()
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
        ShimmerUserListLoading(itemCount = 3)
    }
}

