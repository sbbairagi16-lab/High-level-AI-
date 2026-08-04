package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.DarkBorderColor
import com.example.ui.theme.DarkBorderHighlight
import com.example.ui.theme.DarkCardSurface
import com.example.ui.theme.ElectricIndigo

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    borderColor: Color = DarkBorderColor,
    glowingBorder: Boolean = false,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val animatedBorderColor by animateColorAsState(
        targetValue = if (glowingBorder) ElectricIndigo else borderColor,
        label = "borderColorAnimation"
    )

    val shape = RoundedCornerShape(cornerRadius)
    val borderBrush = if (glowingBorder) {
        Brush.linearGradient(
            colors = listOf(
                ElectricIndigo,
                Color(0xFF38BDF8),
                Color(0xFF8B5CF6)
            )
        )
    } else {
        Brush.linearGradient(
            colors = listOf(animatedBorderColor, DarkBorderHighlight)
        )
    }

    Box(
        modifier = modifier
            .clip(shape)
            .background(DarkCardSurface, shape)
            .border(1.dp, borderBrush, shape)
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
            )
            .padding(16.dp)
    ) {
        content()
    }
}
