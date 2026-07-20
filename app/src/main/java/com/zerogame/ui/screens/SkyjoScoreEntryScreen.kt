package com.zerogame.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zerogame.R
import com.zerogame.ui.theme.Lime
import com.zerogame.ui.theme.Pink
import com.zerogame.ui.theme.Purple
import com.zerogame.viewmodel.SkyjoGameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkyjoScoreEntryScreen(
    viewModel: SkyjoGameViewModel,
    onGameFinished: () -> Unit,
    onPickCards: (Long) -> Unit,
    onBack: () -> Unit
) {
    val currentGamePlayers by viewModel.currentGamePlayers.collectAsState()
    val currentRound by viewModel.currentRound.collectAsState()
    val scores by viewModel.scores.collectAsState()
    val allPlayers by viewModel.allPlayers.collectAsState()
    val currentRoundScores by viewModel.currentRoundScores.collectAsState()
    val gameFinished by viewModel.gameFinished.collectAsState()
    val triggerPlayerId by viewModel.triggerPlayerId.collectAsState()
    val columnsEliminated by viewModel.columnsEliminated.collectAsState()
    var showEndGameDialog by remember { mutableStateOf(false) }

    LaunchedEffect(gameFinished) {
        if (gameFinished) onGameFinished()
    }

    val playerMap = remember(allPlayers) { allPlayers.associateBy { it.id } }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(stringResource(R.string.score_round, currentRound), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(
                            stringResource(R.string.skyjo_cumulative_hint),
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
                actions = {
                    IconButton(onClick = { showEndGameDialog = true }) {
                        Icon(Icons.Default.Flag, contentDescription = stringResource(R.string.score_end_game))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                    actionIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            if (currentRoundScores.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(R.string.skyjo_cumulative_scores),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Lime
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        currentRoundScores
                            .groupBy { it.playerId }
                            .map { (playerId, roundScores) ->
                                val name = playerMap[playerId]?.name ?: stringResource(R.string.player_fallback)
                                val totalScore = roundScores.sumOf { it.score }
                                playerId to (name to totalScore)
                            }
                            .sortedBy { it.second.second }
                            .forEachIndexed { index, (playerId, nameScore) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "${index + 1}. ${nameScore.first}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (index == 0) Lime else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "${nameScore.second} ${stringResource(R.string.pts)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = if (index == 0) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            Text(
                text = stringResource(R.string.skyjo_enter_round_scores),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(R.string.skyjo_enter_round_scores_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(currentGamePlayers) { gamePlayer ->
                    val playerName = playerMap[gamePlayer.playerId]?.name ?: stringResource(R.string.player_fallback)
                    val isTrigger = gamePlayer.playerId == triggerPlayerId
                    val cols = columnsEliminated[gamePlayer.playerId] ?: 0

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isTrigger) {
                                Purple.copy(alpha = 0.15f)
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            }
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isTrigger) Brush.linearGradient(listOf(Purple, Pink))
                                            else Brush.linearGradient(listOf(Purple.copy(alpha = 0.7f), Pink.copy(alpha = 0.7f)))
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isTrigger) {
                                        Icon(
                                            Icons.Default.Flag,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    } else {
                                        Text(
                                            text = playerName.take(1).uppercase(),
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = playerName,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 15.sp
                                    )
                                    val cumulativeScore = currentRoundScores
                                        .filter { it.playerId == gamePlayer.playerId }
                                        .sumOf { it.score }
                                    Text(
                                        text = stringResource(R.string.skyjo_cumulative, cumulativeScore),
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                OutlinedTextField(
                                    value = scores[gamePlayer.playerId] ?: "0",
                                    onValueChange = { viewModel.updateScore(gamePlayer.playerId, it) },
                                    modifier = Modifier.width(72.dp),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    textStyle = LocalTextStyle.current.copy(
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    ),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Purple,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                    )
                                )

                                Spacer(modifier = Modifier.width(6.dp))

                                FilledTonalButton(
                                    onClick = { onPickCards(gamePlayer.playerId) },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.height(36.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.filledTonalButtonColors(
                                        containerColor = Purple.copy(alpha = 0.2f),
                                        contentColor = Purple
                                    )
                                ) {
                                    Text(
                                        text = stringResource(R.string.skyjo_picker_button),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .then(
                                            if (isTrigger) Modifier.border(2.dp, Purple, RoundedCornerShape(10.dp))
                                            else Modifier
                                        )
                                        .clickable { viewModel.setTriggerPlayer(gamePlayer.playerId) }
                                        .padding(8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.Flag,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp),
                                            tint = if (isTrigger) Purple else MaterialTheme.colorScheme.outline
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            stringResource(R.string.skyjo_trigger),
                                            fontSize = 11.sp,
                                            color = if (isTrigger) Purple else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = {
                                            if (cols > 0) viewModel.updateColumnsEliminated(gamePlayer.playerId, cols - 1)
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(16.dp))
                                    }
                                    Text(
                                        "$cols",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp)
                                    )
                                    IconButton(
                                        onClick = {
                                            if (cols < 4) viewModel.updateColumnsEliminated(gamePlayer.playerId, cols + 1)
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    }
                                    Text(
                                        stringResource(R.string.skyjo_cols),
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.submitRound() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Pink,
                    contentColor = Color.Black
                )
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    stringResource(R.string.skyjo_submit_round),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showEndGameDialog) {
        AlertDialog(
            onDismissRequest = { showEndGameDialog = false },
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            title = { Text(stringResource(R.string.score_end_title)) },
            text = { Text(stringResource(R.string.score_end_confirm)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.endGame()
                    showEndGameDialog = false
                }) {
                    Text(stringResource(R.string.end), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEndGameDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}
