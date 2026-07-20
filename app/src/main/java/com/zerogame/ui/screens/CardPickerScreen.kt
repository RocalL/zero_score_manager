package com.zerogame.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
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
import com.zerogame.game.ZeroCard
import com.zerogame.game.ZeroCardColor
import com.zerogame.ui.theme.DarkBg
import com.zerogame.ui.theme.Lime
import com.zerogame.ui.theme.Pink
import com.zerogame.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardPickerScreen(
    viewModel: GameViewModel,
    playerId: Long,
    playerName: String,
    onConfirm: () -> Unit,
    onBack: () -> Unit
) {
    var selectedIds by remember { mutableStateOf(setOf<Int>()) }
    val (score, isZero) = remember(selectedIds) { ZeroCard.computeScore(selectedIds) }

    val groupedByColor = remember {
        ZeroCard.all.groupBy { it.color }.toSortedMap(compareBy { it.index })
    }

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            stringResource(R.string.card_picker_title, playerName),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            stringResource(R.string.card_picker_selected, selectedIds.size, 9),
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
                .padding(horizontal = 12.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isZero) Lime.copy(alpha = 0.12f)
                    else MaterialTheme.colorScheme.surfaceVariant
                ),
                border = if (isZero) BorderStroke(2.dp, Lime) else null
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.card_picker_score),
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
                    if (isZero) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Lime,
                            modifier = Modifier.shadow(8.dp, RoundedCornerShape(16.dp))
                        ) {
                            Text(
                                text = "ZERO!",
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                                fontWeight = FontWeight.Black,
                                fontSize = 20.sp,
                                color = Color.Black
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(8),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.weight(1f)
            ) {
                for ((color, cards) in groupedByColor) {
                    item(span = { GridItemSpan(8) }) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp, bottom = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(color.color)
                                    .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = color.label.uppercase(),
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp,
                                letterSpacing = 1.sp,
                                color = color.color
                            )
                            val count = cards.count { it.id in selectedIds }
                            if (count > 0) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = color.color.copy(alpha = 0.3f)
                                ) {
                                    Text(
                                        text = "$count",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = color.color
                                    )
                                }
                            }
                        }
                    }
                    items(cards) { card ->
                        val isSelected = card.id in selectedIds
                        val scale by animateFloatAsState(
                            targetValue = if (isSelected) 1.05f else 1f,
                            label = "scale"
                        )

                        Box(
                            modifier = Modifier
                                .aspectRatio(0.72f)
                                .shadow(
                                    elevation = if (isSelected) 8.dp else 2.dp,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    Brush.verticalGradient(
                                        colors = if (isSelected) {
                                            listOf(
                                                color.color,
                                                color.color.copy(alpha = 0.7f)
                                            )
                                        } else {
                                            listOf(
                                                color.color.copy(alpha = 0.25f),
                                                color.color.copy(alpha = 0.12f)
                                            )
                                        }
                                    )
                                )
                                .then(
                                    if (isSelected) Modifier.border(
                                        BorderStroke(2.5.dp, Color.White),
                                        RoundedCornerShape(10.dp)
                                    ) else Modifier
                                )
                                .clickable {
                                    selectedIds = when {
                                        isSelected -> selectedIds - card.id
                                        selectedIds.size < 9 -> selectedIds + card.id
                                        else -> selectedIds
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${card.value}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isSelected) Color.White else color.color.copy(alpha = 0.8f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    viewModel.setScoreFromCards(playerId, score, isZero)
                    onConfirm()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isZero) Pink else Lime,
                    contentColor = Color.Black
                ),
                enabled = selectedIds.size == 9
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    stringResource(R.string.card_picker_confirm, score),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
