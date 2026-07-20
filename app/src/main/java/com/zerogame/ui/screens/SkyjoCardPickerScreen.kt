package com.zerogame.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zerogame.R
import com.zerogame.ui.theme.DarkBg
import com.zerogame.ui.theme.Lime
import com.zerogame.ui.theme.Pink
import com.zerogame.ui.theme.Purple
import com.zerogame.viewmodel.SkyjoGameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkyjoCardPickerScreen(
    viewModel: SkyjoGameViewModel,
    playerId: Long,
    playerName: String,
    onConfirm: () -> Unit,
    onBack: () -> Unit
) {
    var columns by remember { mutableIntStateOf(3) }
    var rows by remember { mutableIntStateOf(4) }
    val totalCards = columns * rows

    var cellValues by remember {
        mutableStateOf(MutableList(12) { 0 })
    }

    val activeValues = remember(cellValues, columns, rows) {
        cellValues.take(totalCards)
    }
    val score = remember(activeValues) { activeValues.sum() }
    var editingIndex by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            stringResource(R.string.skyjo_picker_title, playerName),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            stringResource(R.string.skyjo_picker_cards, totalCards),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBg,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.skyjo_picker_score),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "$score ${stringResource(R.string.pts)}",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(stringResource(R.string.skyjo_picker_cols), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { if (columns > 1) columns-- }, modifier = Modifier.size(28.dp)) {
                                    Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                                }
                                Text("$columns", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                IconButton(onClick = { if (columns < 3) columns++ }, modifier = Modifier.size(28.dp)) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(stringResource(R.string.skyjo_picker_rows), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { if (rows > 1) rows-- }, modifier = Modifier.size(28.dp)) {
                                    Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                                }
                                Text("$rows", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                IconButton(onClick = { if (rows < 4) rows++ }, modifier = Modifier.size(28.dp)) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            for (row in 0 until rows) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(vertical = 6.dp)
                ) {
                    for (col in 0 until columns) {
                        val idx = row * columns + col
                        val value = cellValues[idx]
                        val isSet = value != 0

                        val bgColor by animateColorAsState(
                            targetValue = when {
                                isSet && value < 0 -> Color(0xFFE53935).copy(alpha = 0.35f)
                                isSet && value == 0 -> Lime.copy(alpha = 0.4f)
                                isSet && value <= 5 -> Purple.copy(alpha = 0.4f)
                                isSet -> Pink.copy(alpha = 0.4f)
                                else -> Color.White.copy(alpha = 0.05f)
                            },
                            label = "bg"
                        )
                        val borderColor by animateColorAsState(
                            targetValue = when {
                                isSet && value < 0 -> Color(0xFFE53935).copy(alpha = 0.6f)
                                isSet && value == 0 -> Lime.copy(alpha = 0.7f)
                                isSet && value <= 5 -> Purple.copy(alpha = 0.6f)
                                isSet -> Pink.copy(alpha = 0.6f)
                                else -> Color.White.copy(alpha = 0.12f)
                            },
                            label = "border"
                        )
                        val textColor by animateColorAsState(
                            targetValue = when {
                                isSet && value < 0 -> Color(0xFFEF9A9A)
                                isSet && value == 0 -> Lime
                                else -> Color.White
                            },
                            label = "text"
                        )
                        val scale by animateFloatAsState(
                            targetValue = if (isSet) 1f else 0.9f,
                            label = "scale"
                        )

                        Box(
                            modifier = Modifier
                                .size(88.dp)
                                .shadow(
                                    elevation = if (isSet) 8.dp else 2.dp,
                                    shape = CircleShape
                                )
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        colors = if (isSet) listOf(bgColor, bgColor.copy(alpha = 0.5f))
                                        else listOf(Color.White.copy(alpha = 0.06f), Color.White.copy(alpha = 0.02f))
                                    )
                                )
                                .border(
                                    width = if (isSet) 2.5.dp else 1.5.dp,
                                    color = borderColor,
                                    shape = CircleShape
                                )
                                .clickable { editingIndex = idx },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = if (isSet) "$value" else "?",
                                    fontSize = 30.sp,
                                    fontWeight = FontWeight.Black,
                                    color = textColor,
                                    textAlign = TextAlign.Center
                                )
                                if (isSet) {
                                    Text(
                                        text = "row ${row + 1}",
                                        fontSize = 8.sp,
                                        color = textColor.copy(alpha = 0.5f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    viewModel.setScoreFromCards(playerId, activeValues)
                    onConfirm()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Pink,
                    contentColor = Color.Black
                )
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    stringResource(R.string.skyjo_picker_confirm, score),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    editingIndex?.let { idx ->
        ValuePickerDialog(
            currentValue = cellValues[idx],
            onValueSelected = { newValue ->
                val newList = cellValues.toMutableList()
                newList[idx] = newValue
                cellValues = newList
            },
            onDismiss = { editingIndex = null }
        )
    }
}
