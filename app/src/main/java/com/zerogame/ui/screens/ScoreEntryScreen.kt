package com.zerogame.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zerogame.ui.theme.Lime
import com.zerogame.ui.theme.Pink
import com.zerogame.ui.theme.Purple
import com.zerogame.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScoreEntryScreen(
    viewModel: GameViewModel,
    onGameFinished: () -> Unit,
    onBack: () -> Unit
) {
    val currentGamePlayers by viewModel.currentGamePlayers.collectAsState()
    val currentRound by viewModel.currentRound.collectAsState()
    val totalRounds by viewModel.totalRounds.collectAsState()
    val scores by viewModel.scores.collectAsState()
    val zeros by viewModel.zeros.collectAsState()
    val allPlayers by viewModel.allPlayers.collectAsState()
    val currentRoundScores by viewModel.currentRoundScores.collectAsState()
    val gameFinished by viewModel.gameFinished.collectAsState()
    var showEndGameDialog by remember { mutableStateOf(false) }

    LaunchedEffect(gameFinished) {
        if (gameFinished) onGameFinished()
    }

    val playerMap = remember(allPlayers) { allPlayers.associateBy { it.id } }
    val progress = if (totalRounds > 0) (currentRound - 1).toFloat() / totalRounds else 0f
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "progress")

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Round $currentRound", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(
                            "$currentRound of $totalRounds",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showEndGameDialog = true }) {
                        Icon(Icons.Default.Flag, contentDescription = "End Game")
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
            LinearProgressIndicator(
                progress = animatedProgress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = Lime,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )

            Spacer(modifier = Modifier.height(12.dp))

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
                            text = "Standings",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Lime
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        currentRoundScores
                            .groupBy { it.playerId }
                            .map { (playerId, roundScores) ->
                                val playerName = playerMap[playerId]?.name ?: "Player"
                                val totalScore = roundScores.sumOf { it.score }
                                playerName to totalScore
                            }
                            .sortedBy { it.second }
                            .forEachIndexed { index, (name, score) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "${index + 1}. $name",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (index == 0) Lime else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "$score pts",
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
                text = "Enter scores",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(currentGamePlayers) { gamePlayer ->
                    val playerName = playerMap[gamePlayer.playerId]?.name ?: "Player"
                    val isZero = gamePlayer.playerId in zeros

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isZero) {
                                Lime.copy(alpha = 0.15f)
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            }
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isZero) Brush.linearGradient(listOf(Lime, Pink))
                                        else Brush.linearGradient(listOf(Purple, Pink))
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = playerName.take(1).uppercase(),
                                    color = if (isZero) Color.Black else Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = playerName,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "Total: ${gamePlayer.totalScore} pts",
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
                                    focusedBorderColor = if (isZero) Lime else Purple,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                )
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Checkbox(
                                    checked = isZero,
                                    onCheckedChange = { viewModel.toggleZero(gamePlayer.playerId) },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = Lime,
                                        uncheckedColor = MaterialTheme.colorScheme.outline
                                    )
                                )
                                Text(
                                    text = "0",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isZero) Lime else MaterialTheme.colorScheme.outline
                                )
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
                    containerColor = if (currentRound >= totalRounds) Pink else Lime,
                    contentColor = Color.Black
                )
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    if (currentRound >= totalRounds) "Final Round" else "Submit Round",
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
            title = { Text("End Game?") },
            text = { Text("Are you sure you want to end the current game?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.endGame()
                    showEndGameDialog = false
                    onGameFinished()
                }) {
                    Text("End", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEndGameDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
