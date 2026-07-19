package com.zerogame.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zerogame.R
import com.zerogame.ui.theme.Lime
import com.zerogame.ui.theme.Pink
import com.zerogame.ui.theme.Purple
import com.zerogame.viewmodel.GameViewModel

@Composable
fun LeaderboardScreen(
    viewModel: GameViewModel,
    onGoHome: () -> Unit,
    onStartNewGame: () -> Unit
) {
    val currentGamePlayers by viewModel.currentGamePlayers.collectAsState()
    val currentRoundScores by viewModel.currentRoundScores.collectAsState()
    val allPlayers by viewModel.allPlayers.collectAsState()
    val totalRounds by viewModel.totalRounds.collectAsState()

    val playerMap = remember(allPlayers) { allPlayers.associateBy { it.id } }
    val fallbackName = stringResource(R.string.player_fallback)

    val rankings = remember(currentGamePlayers, fallbackName) {
        currentGamePlayers
            .map { gp ->
                val name = playerMap[gp.playerId]?.name ?: fallbackName
                Triple(gp.playerId, name, gp.totalScore)
            }
            .sortedBy { it.third }
    }

    val winner = rankings.firstOrNull()
    val roundBreakdown = remember(currentRoundScores, fallbackName) {
        currentRoundScores
            .groupBy { it.roundNumber }
            .toSortedMap()
            .map { (round, scores) ->
                round to scores.map { rs ->
                    val name = playerMap[rs.playerId]?.name ?: fallbackName
                    Quadruple(rs.playerId, name, rs.score, rs.achievedZero)
                }.sortedBy { it.third }
            }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "trophy")
    val trophyScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "trophyPulse"
    )

    val fadeInAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowPulse"
    )

    LaunchedEffect(Unit) {
        viewModel.lastFinishedGameId.value
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.background,
                tonalElevation = 0.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            viewModel.resetAfterGameFinished()
                            onGoHome()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        border = ButtonDefaults.outlinedButtonBorder
                    ) {
                        Icon(Icons.Default.Home, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.home_button), fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            viewModel.resetAfterGameFinished()
                            onStartNewGame()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Lime,
                            contentColor = Color.Black
                        )
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.home_new_game), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 8.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(Purple.copy(alpha = 0.4f), Color.Transparent)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .scale(trophyScale)
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(Lime, Pink))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(40.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = stringResource(R.string.leaderboard_game_over),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Lime,
                            letterSpacing = 4.sp
                        )

                        if (winner != null) {
                            Text(
                                text = stringResource(R.string.leaderboard_wins, winner.second),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            Text(
                                text = stringResource(R.string.leaderboard_points, winner.third),
                                fontSize = 16.sp,
                                color = Lime.copy(alpha = fadeInAlpha),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = stringResource(R.string.leaderboard_final_standings),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }

            itemsIndexed(rankings) { index, (playerId, name, score) ->
                val isWinner = index == 0
                val isSecond = index == 1
                val zeros = currentGamePlayers.find { it.playerId == playerId }?.zerosAchieved ?: 0

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = when {
                            isWinner -> Lime.copy(alpha = 0.15f)
                            isSecond -> Purple.copy(alpha = 0.1f)
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isWinner -> Brush.linearGradient(listOf(Lime, Color(0xFFA8E600)))
                                        isSecond -> Brush.linearGradient(listOf(Purple, Pink))
                                        else -> Brush.linearGradient(listOf(Color.Gray, Color.DarkGray))
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isWinner) {
                                Icon(
                                    Icons.Default.EmojiEvents,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(26.dp)
                                )
                            } else {
                                Text(
                                    text = "${index + 1}",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp,
                                    color = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = if (isWinner) Lime else MaterialTheme.colorScheme.onSurface
                            )
                            if (zeros > 0) {
                                Text(
                                    text = stringResource(R.string.leaderboard_zero_x, zeros),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Lime,
                                    letterSpacing = 1.sp
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "$score",
                                fontWeight = FontWeight.Black,
                                fontSize = 24.sp,
                                color = if (isWinner) Lime else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = stringResource(R.string.pts),
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            if (roundBreakdown.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.leaderboard_round_by_round),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 12.dp)
                    )
                }

                itemsIndexed(roundBreakdown) { _, (round, players) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = stringResource(R.string.leaderboard_round, round),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Purple,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            players.forEach { (playerId, name, score, achievedZero) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = name,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        if (achievedZero) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "0",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Lime,
                                                modifier = Modifier
                                                    .background(
                                                        Lime.copy(alpha = 0.15f),
                                                        RoundedCornerShape(4.dp)
                                                    )
                                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "$score ${stringResource(R.string.pts)}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (achievedZero) Lime else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(8.dp)) }
            }
        }
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
