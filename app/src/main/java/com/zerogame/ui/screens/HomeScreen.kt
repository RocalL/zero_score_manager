package com.zerogame.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zerogame.R
import com.zerogame.ui.theme.Lime
import com.zerogame.ui.theme.Pink
import com.zerogame.ui.theme.Purple
import com.zerogame.util.LocaleHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onStartGame: () -> Unit,
    onManagePlayers: () -> Unit,
    onViewHistory: () -> Unit,
    onViewProfile: (Long) -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(64.dp))

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(colors = listOf(Lime, Pink))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "0",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.background
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "ZERO",
                fontSize = 40.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground,
                letterSpacing = 8.sp
            )
            Text(
                text = stringResource(R.string.home_score_manager),
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            HomeButton(
                icon = Icons.Default.PlayArrow,
                label = stringResource(R.string.home_new_game),
                gradient = Brush.linearGradient(colors = listOf(Lime, Color(0xFFA8E600))),
                onClick = onStartGame
            )

            Spacer(modifier = Modifier.height(16.dp))

            HomeButton(
                icon = Icons.Default.People,
                label = stringResource(R.string.home_players),
                gradient = Brush.linearGradient(colors = listOf(Purple, Pink)),
                onClick = onManagePlayers
            )

            Spacer(modifier = Modifier.height(16.dp))

            HomeButton(
                icon = Icons.Default.History,
                label = stringResource(R.string.home_history),
                gradient = Brush.linearGradient(colors = listOf(Pink, Purple)),
                onClick = onViewHistory
            )

            Spacer(modifier = Modifier.weight(1f))

            LanguageToggle()

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.home_by_author),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun LanguageToggle() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val currentCode = remember { mutableStateOf(LocaleHelper.getSavedLocale(context)) }
    val isFrench = currentCode.value == "fr"

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        LanguageChip(
            label = "FR",
            selected = isFrench,
            onClick = {
                LocaleHelper.setLocale(context, "fr")
                currentCode.value = "fr"
            }
        )
        LanguageChip(
            label = "EN",
            selected = !isFrench,
            onClick = {
                LocaleHelper.setLocale(context, "en")
                currentCode.value = "en"
            }
        )
    }
}

@Composable
private fun LanguageChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) Lime else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun HomeButton(
    icon: ImageVector,
    label: String,
    gradient: Brush,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(gradient),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.background,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline
            )
        }
    }
}
