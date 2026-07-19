package com.zerogame.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zerogame.R
import com.zerogame.data.model.GameType
import com.zerogame.data.model.PlayerGameKpi
import com.zerogame.ui.theme.Lime
import com.zerogame.ui.theme.Pink
import com.zerogame.ui.theme.Purple
import com.zerogame.viewmodel.GameFilter
import com.zerogame.viewmodel.PlayerProfileViewModel
import com.zerogame.viewmodel.StatsPeriod

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerProfileScreen(
    playerId: Long,
    viewModel: PlayerProfileViewModel,
    onBack: () -> Unit
) {
    val player by viewModel.player.collectAsState()
    val allGamePlayers by viewModel.allGamePlayers.collectAsState()
    val allGames by viewModel.allGames.collectAsState()
    val selectedGameFilter by viewModel.selectedGameFilter.collectAsState()
    val selectedPeriod by viewModel.selectedPeriod.collectAsState()

    LaunchedEffect(playerId) { viewModel.loadPlayer(playerId) }

    val playerIdSafe = player?.id ?: playerId

    val mergedKpi = remember(allGamePlayers, allGames, selectedGameFilter, selectedPeriod) {
        viewModel.computeKpis(allGamePlayers, allGames, selectedGameFilter, selectedPeriod, playerIdSafe)
    }

    val perGameKpis = remember(allGamePlayers, allGames, selectedPeriod) {
        viewModel.computePerGameKpis(allGamePlayers, allGames, selectedPeriod, playerIdSafe)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(player?.name ?: stringResource(R.string.profile_title), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
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
                GameFilterSelector(selectedGameFilter = selectedGameFilter, onGameFilterSelected = { viewModel.setGameFilter(it) })
            }

            item {
                PeriodSelector(selectedPeriod = selectedPeriod, onPeriodSelected = { viewModel.setPeriod(it) })
            }

            if (mergedKpi != null) {
                item {
                    KpiOverviewCard(mergedKpi, selectedGameFilter)
                }

                if (selectedGameFilter == GameFilter.ALL) {
                    val zeroKpi = perGameKpis[GameType.ZERO]
                    if (zeroKpi != null) {
                        item {
                            ZeroKpiCard(zeroKpi)
                        }
                    }

                    val skyjoKpi = perGameKpis[GameType.SKYJO]
                    if (skyjoKpi != null) {
                        item {
                            SkyjoKpiCard(skyjoKpi)
                        }
                    }
                }

                if (selectedGameFilter == GameFilter.ZERO) {
                    val zeroKpi = perGameKpis[GameType.ZERO]
                    if (zeroKpi != null) {
                        item {
                            ZeroKpiCard(zeroKpi)
                        }
                    }
                }

                if (selectedGameFilter == GameFilter.SKYJO) {
                    val skyjoKpi = perGameKpis[GameType.SKYJO]
                    if (skyjoKpi != null) {
                        item {
                            SkyjoKpiCard(skyjoKpi)
                        }
                    }
                }
            } else {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            stringResource(R.string.profile_no_games),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GameFilterSelector(
    selectedGameFilter: GameFilter,
    onGameFilterSelected: (GameFilter) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        GameFilter.values().forEach { filter ->
            val isSelected = filter == selectedGameFilter
            val (bgBrush, textColor) = when {
                isSelected && filter == GameFilter.ZERO -> Brush.linearGradient(listOf(Purple, Purple.copy(alpha = 0.8f))) to Color.White
                isSelected && filter == GameFilter.SKYJO -> Brush.linearGradient(listOf(Pink, Pink.copy(alpha = 0.8f))) to Color.Black
                isSelected -> Brush.linearGradient(listOf(Lime, Lime.copy(alpha = 0.8f))) to Color.Black
                else -> Brush.linearGradient(listOf(Color.Transparent, Color.Transparent)) to MaterialTheme.colorScheme.onSurfaceVariant
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(bgBrush)
                    .clickable { onGameFilterSelected(filter) }
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(
                    text = stringResource(filter.labelResId),
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = textColor
                )
            }
        }
    }
}

@Composable
fun PeriodSelector(
    selectedPeriod: StatsPeriod,
    onPeriodSelected: (StatsPeriod) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatsPeriod.values().forEach { period ->
            val isSelected = period == selectedPeriod
            val bgColor = if (isSelected) Lime else Color.Transparent
            val textColor = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(bgColor)
                    .clickable { onPeriodSelected(period) }
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(
                    text = stringResource(period.labelResId),
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = textColor
                )
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
fun KpiOverviewCard(kpi: PlayerGameKpi, filter: GameFilter) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            val title = when (filter) {
                GameFilter.ALL -> stringResource(R.string.kpi_overview_all)
                GameFilter.ZERO -> stringResource(R.string.kpi_overview_zero)
                GameFilter.SKYJO -> stringResource(R.string.kpi_overview_skyjo)
            }
            Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Lime)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MiniStat(stringResource(R.string.kpi_games), kpi.totalGames.toString(), Purple)
                MiniStat(stringResource(R.string.kpi_wins), kpi.totalWins.toString(), Lime)
                MiniStat(stringResource(R.string.kpi_winrate), "${(kpi.winRate * 100).toInt()}%", Pink)
                MiniStat(stringResource(R.string.kpi_streak), kpi.maxWinStreak.toString(), Purple)
            }
        }
    }
}

@Composable
fun ZeroKpiCard(kpi: PlayerGameKpi) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(stringResource(R.string.kpi_zero_stats), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Purple)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MiniStat(stringResource(R.string.kpi_games), kpi.totalGames.toString(), Purple)
                MiniStat(stringResource(R.string.kpi_wins), kpi.totalWins.toString(), Lime)
                MiniStat(stringResource(R.string.kpi_winrate), "${(kpi.winRate * 100).toInt()}%", Pink)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MiniStat(stringResource(R.string.kpi_zeros), kpi.totalZeros.toString(), Lime)
                MiniStat(stringResource(R.string.kpi_avg_pts), String.format("%.1f", kpi.averagePoints), Pink)
                MiniStat(stringResource(R.string.kpi_total_pts), kpi.totalPoints.toString(), Purple)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                MiniStat(stringResource(R.string.kpi_max_streak), kpi.maxWinStreak.toString(), Purple)
            }
        }
    }
}

@Composable
fun SkyjoKpiCard(kpi: PlayerGameKpi) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(stringResource(R.string.kpi_skyjo_stats), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Pink)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MiniStat(stringResource(R.string.kpi_games), kpi.totalGames.toString(), Purple)
                MiniStat(stringResource(R.string.kpi_wins), kpi.totalWins.toString(), Lime)
                MiniStat(stringResource(R.string.kpi_winrate), "${(kpi.winRate * 100).toInt()}%", Pink)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MiniStat(stringResource(R.string.kpi_triggers), kpi.triggerCount.toString(), Pink)
                MiniStat(stringResource(R.string.kpi_avg_pts), String.format("%.1f", kpi.averagePoints), Purple)
                MiniStat(stringResource(R.string.kpi_total_pts), kpi.totalPoints.toString(), Lime)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                MiniStat(stringResource(R.string.kpi_max_streak), kpi.maxWinStreak.toString(), Purple)
            }
        }
    }
}

@Composable
fun MiniStat(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 22.sp, fontWeight = FontWeight.Black, color = color)
        Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun GameHistoryCard(gp: com.zerogame.data.model.GamePlayer, allGames: List<com.zerogame.data.model.Game>) {
    val game = remember(gp.gameId, allGames) { allGames.find { it.id == gp.gameId } }
    val gameType = game?.gameType

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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(R.string.profile_game_n, gp.gameId.toInt()), fontWeight = FontWeight.SemiBold)
                    if (gameType != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        GameTypeBadge(gameType)
                    }
                }
                Text(stringResource(R.string.profile_rounds, gp.roundsPlayed), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "${gp.totalScore} ${stringResource(R.string.pts)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = if (gp.totalScore == 0) Lime else MaterialTheme.colorScheme.onSurface
                )
                if ((gp.extras["zerosAchieved"]?.toIntOrNull() ?: 0) > 0) {
                    Text(stringResource(R.string.profile_x_zero, gp.extras["zerosAchieved"]?.toIntOrNull() ?: 0), fontSize = 11.sp, color = Lime)
                }
            }
        }
    }
}
