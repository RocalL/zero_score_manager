package com.zerogame.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zerogame.ui.theme.Lime
import com.zerogame.ui.theme.Pink
import com.zerogame.ui.theme.Purple
import com.zerogame.viewmodel.PlayerProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerProfileScreen(
    playerId: Long,
    viewModel: PlayerProfileViewModel,
    onBack: () -> Unit
) {
    val player by viewModel.player.collectAsState()
    val gamePlayers by viewModel.gamePlayers.collectAsState()
    val totalGames by viewModel.totalGames.collectAsState()
    val totalWins by viewModel.totalWins.collectAsState()
    val totalScore by viewModel.totalScore.collectAsState()
    val averageScore by viewModel.averageScore.collectAsState()
    val totalZeros by viewModel.totalZeros.collectAsState()

    LaunchedEffect(playerId) { viewModel.loadPlayer(playerId) }
    LaunchedEffect(gamePlayers) { viewModel.computeStats(gamePlayers) }

    val winRate = if (totalGames > 0) totalWins.toFloat() / totalGames else 0f
    val animatedWinRate by animateFloatAsState(targetValue = winRate, label = "winrate")
    val zeroRate = if (totalGames > 0) totalZeros.toFloat() / totalGames else 0f
    val animatedZeroRate by animateFloatAsState(targetValue = zeroRate, label = "zerorate")

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(player?.name ?: "Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
                HeroCard(player?.name ?: "")
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(Modifier.weight(1f), totalGames.toString(), "Games", Icons.Default.EmojiEvents, Brush.linearGradient(listOf(Purple, Purple.copy(alpha = 0.7f))))
                    StatCard(Modifier.weight(1f), totalWins.toString(), "Wins", Icons.Default.Star, Brush.linearGradient(listOf(Lime, Lime.copy(alpha = 0.7f))))
                }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(Modifier.weight(1f), String.format("%.1f", averageScore), "Avg Score", Icons.Default.TrendingDown, Brush.linearGradient(listOf(Pink, Pink.copy(alpha = 0.7f))))
                    StatCard(Modifier.weight(1f), totalScore.toString(), "Total Pts", Icons.Default.Score, Brush.linearGradient(listOf(Purple, Pink)))
                }
            }
            item {
                PerformanceCard(animatedWinRate, winRate, animatedZeroRate, zeroRate)
            }
            if (gamePlayers.isNotEmpty()) {
                item {
                    Text("Game History", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                items(gamePlayers.sortedBy { it.totalScore }) { gp ->
                    GameHistoryCard(gp)
                }
            }
        }
    }
}

@Composable
fun HeroCard(name: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(Purple, Pink, Lime.copy(alpha = 0.6f))))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = name.take(1).uppercase().ifEmpty { "?" },
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(name, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier,
    value: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    gradient: Brush
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(gradient)
                .padding(16.dp)
        ) {
            Column {
                Icon(icon, contentDescription = null, tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text(value, fontSize = 28.sp, fontWeight = FontWeight.Black, color = Color.White)
                Text(label, fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f))
            }
        }
    }
}

@Composable
fun PerformanceCard(
    animatedWinRate: Float,
    winRate: Float,
    animatedZeroRate: Float,
    zeroRate: Float
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Performance", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CircularStat(animatedWinRate, "Win Rate", "${(winRate * 100).toInt()}%", Lime)
                CircularStat(animatedZeroRate, "ZERO Rate", "${(zeroRate * 100).toInt()}%", Pink)
            }
        }
    }
}

@Composable
fun CircularStat(progress: Float, label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.size(100.dp), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = progress,
                modifier = Modifier.fillMaxSize(),
                color = color,
                trackColor = MaterialTheme.colorScheme.background,
                strokeWidth = 10.dp
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(value, fontSize = 22.sp, fontWeight = FontWeight.Black, color = color)
                Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun GameHistoryCard(gp: com.zerogame.data.model.GamePlayer) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(Purple, Pink))),
                contentAlignment = Alignment.Center
            ) {
                Text("${gp.roundsPlayed}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Game #${gp.gameId}", fontWeight = FontWeight.SemiBold)
                Text("${gp.roundsPlayed} rounds", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "${gp.totalScore} pts",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = if (gp.totalScore == 0) Lime else MaterialTheme.colorScheme.onSurface
                )
                if (gp.zerosAchieved > 0) {
                    Text("${gp.zerosAchieved}x ZERO", fontSize = 11.sp, color = Lime)
                }
            }
        }
    }
}
