package com.zerogame.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        if (gameFinished) {
            onGameFinished()
        }
    }

    val playerMap = remember(allPlayers) {
        allPlayers.associateBy { it.id }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Round $currentRound / $totalRounds") },
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
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (currentRoundScores.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Previous Rounds",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        currentRoundScores
                            .groupBy { it.playerId }
                            .forEach { (playerId, roundScores) ->
                                val playerName = playerMap[playerId]?.name ?: "Player"
                                val totalScore = roundScores.sumOf { it.score }
                                Text(
                                    text = "$playerName: $totalScore pts (${roundScores.size} rounds)",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            Text(
                text = "Enter scores for Round $currentRound of $totalRounds",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(currentGamePlayers) { gamePlayer ->
                    val playerName = playerMap[gamePlayer.playerId]?.name ?: "Player"
                    val isZero = gamePlayer.playerId in zeros

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isZero) {
                                MaterialTheme.colorScheme.tertiaryContainer
                            } else {
                                MaterialTheme.colorScheme.surface
                            }
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = playerName,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Total: ${gamePlayer.totalScore} pts",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                OutlinedTextField(
                                    value = scores[gamePlayer.playerId] ?: "0",
                                    onValueChange = { newValue ->
                                        viewModel.updateScore(gamePlayer.playerId, newValue)
                                    },
                                    modifier = Modifier.width(80.dp),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    label = { Text("Score") }
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Checkbox(
                                        checked = isZero,
                                        onCheckedChange = {
                                            viewModel.toggleZero(gamePlayer.playerId)
                                        }
                                    )
                                    Text(
                                        text = "ZERO",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
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
                    .height(56.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    if (currentRound >= totalRounds) "Submit Final Round"
                    else "Submit Round $currentRound",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }

    if (showEndGameDialog) {
        AlertDialog(
            onDismissRequest = { showEndGameDialog = false },
            title = { Text("End Game?") },
            text = { Text("Are you sure you want to end the current game? This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.endGame()
                    showEndGameDialog = false
                    onGameFinished()
                }) {
                    Text("End Game")
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
