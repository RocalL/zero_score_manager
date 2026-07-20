package com.zerogame.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip

import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.zerogame.ui.theme.DarkBg
import com.zerogame.ui.theme.Lime
import com.zerogame.ui.theme.Pink
import com.zerogame.ui.theme.Purple
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ValuePickerDialog(
    currentValue: Int,
    onValueSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val values = (-2..12).toList()
    val count = values.size
    val radius = 130f

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkBg)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Select Value",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .size(320.dp)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .shadow(12.dp, CircleShape)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Purple.copy(alpha = 0.6f),
                                        Purple.copy(alpha = 0.2f)
                                    )
                                )
                            )
                            .border(2.dp, Purple.copy(alpha = 0.8f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$currentValue",
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = "pts",
                                fontSize = 10.sp,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        }
                    }

                    for ((i, v) in values.withIndex()) {
                        val angle = (i.toDouble() / count) * 2 * Math.PI - Math.PI / 2
                        val x = (radius * cos(angle)).toFloat()
                        val y = (radius * sin(angle)).toFloat()
                        val isSelected = v == currentValue

                        val bgColor by animateColorAsState(
                            targetValue = when {
                                isSelected && v < 0 -> Color(0xFFE53935)
                                isSelected && v == 0 -> Lime
                                isSelected && v <= 5 -> Purple
                                isSelected -> Pink
                                v < 0 -> Color(0xFFE53935).copy(alpha = 0.25f)
                                v == 0 -> Lime.copy(alpha = 0.2f)
                                v <= 5 -> Purple.copy(alpha = 0.2f)
                                else -> Pink.copy(alpha = 0.2f)
                            },
                            label = "bg"
                        )
                        val textColor by animateColorAsState(
                            targetValue = when {
                                isSelected -> Color.Black
                                v < 0 -> Color(0xFFEF9A9A)
                                v == 0 -> Lime
                                else -> Color.White.copy(alpha = 0.7f)
                            },
                            label = "text"
                        )
                        Box(
                            modifier = Modifier
                                .offset(x = x.dp, y = y.dp)
                                .size(48.dp)
                                .shadow(
                                    elevation = if (isSelected) 8.dp else 2.dp,
                                    shape = CircleShape
                                )
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        colors = if (isSelected) listOf(bgColor, bgColor.copy(alpha = 0.7f))
                                        else listOf(bgColor, bgColor.copy(alpha = 0.4f))
                                    )
                                )
                                .then(
                                    if (isSelected) Modifier.border(2.5.dp, Color.White, CircleShape)
                                    else Modifier.border(1.dp, Color.White.copy(alpha = 0.12f), CircleShape)
                                )
                                .clickable {
                                    onValueSelected(v)
                                    onDismiss()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$v",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColor,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(onClick = onDismiss) {
                    Text("Cancel", color = Color.White.copy(alpha = 0.5f))
                }
            }
        }
    }
}
