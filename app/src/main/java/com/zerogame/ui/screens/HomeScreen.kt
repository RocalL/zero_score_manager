package com.zerogame.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zerogame.R
import com.zerogame.data.model.GameType
import com.zerogame.ui.theme.Lime
import com.zerogame.ui.theme.Pink
import com.zerogame.ui.theme.Purple
import com.zerogame.util.DatabaseExporter
import com.zerogame.util.DatabaseImporter
import com.zerogame.util.LocaleHelper
import com.zerogame.util.SampleData
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    onStartGame: (GameType) -> Unit,
    onManagePlayers: () -> Unit,
    onViewHistory: () -> Unit,
    onViewProfile: (Long) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let {
            scope.launch {
                try {
                    DatabaseExporter.exportToFile(context, it)
                    Toast.makeText(context, context.getString(R.string.export_success), Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(context, context.getString(R.string.export_error, e.message), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            scope.launch {
                try {
                    DatabaseImporter.importFromFile(context, it)
                    Toast.makeText(context, context.getString(R.string.import_success), Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(context, context.getString(R.string.import_error, e.message), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(64.dp))

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(colors = listOf(Lime, Pink))),
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
                text = "TABLE SCORE",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground,
                letterSpacing = 4.sp
            )
            Text(
                text = stringResource(R.string.home_score_manager),
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = stringResource(R.string.home_select_game),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            GameCard(
                title = "Zero",
                subtitle = stringResource(R.string.game_zero_desc),
                icon = Icons.Default.CreditCard,
                gradient = Brush.linearGradient(listOf(Lime, Color(0xFFA8E600))),
                onClick = { onStartGame(GameType.ZERO) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            GameCard(
                title = "Skyjo",
                subtitle = stringResource(R.string.game_skyjo_desc),
                icon = Icons.Default.GridView,
                gradient = Brush.linearGradient(listOf(Purple, Pink)),
                onClick = { onStartGame(GameType.SKYJO) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            GameCard(
                title = "Belote",
                subtitle = stringResource(R.string.game_belote_desc),
                icon = Icons.Default.Style,
                gradient = Brush.linearGradient(listOf(Pink, Purple.copy(alpha = 0.7f))),
                onClick = { },
                enabled = false
            )

            Spacer(modifier = Modifier.height(12.dp))

            GameCard(
                title = "Darts",
                subtitle = stringResource(R.string.game_darts_desc),
                icon = Icons.Default.CenterFocusStrong,
                gradient = Brush.linearGradient(listOf(Color.Gray, Color.DarkGray)),
                onClick = { },
                enabled = false
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActionChip(
                    icon = Icons.Default.People,
                    label = stringResource(R.string.home_players),
                    gradient = Brush.linearGradient(listOf(Purple, Pink)),
                    modifier = Modifier.weight(1f),
                    onClick = onManagePlayers
                )
                ActionChip(
                    icon = Icons.Default.History,
                    label = stringResource(R.string.home_history),
                    gradient = Brush.linearGradient(listOf(Pink, Purple)),
                    modifier = Modifier.weight(1f),
                    onClick = onViewHistory
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            LanguageToggle()

            Spacer(modifier = Modifier.height(16.dp))

            // Data management section
            Text(
                text = stringResource(R.string.data_management),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DataActionChip(
                    icon = Icons.Default.Upload,
                    label = stringResource(R.string.data_export),
                    modifier = Modifier.weight(1f),
                    onClick = { exportLauncher.launch("zero_score_backup.json") }
                )
                DataActionChip(
                    icon = Icons.Default.Download,
                    label = stringResource(R.string.data_import),
                    modifier = Modifier.weight(1f),
                    onClick = { importLauncher.launch(arrayOf("application/json")) }
                )
                DataActionChip(
                    icon = Icons.Default.Science,
                    label = stringResource(R.string.data_sample),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        scope.launch {
                            try {
                                SampleData.load(context)
                                Toast.makeText(context, context.getString(R.string.sample_loaded), Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {
                                Toast.makeText(context, context.getString(R.string.sample_error, e.message), Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.home_by_author),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun GameCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    gradient: Brush,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .then(if (enabled) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) MaterialTheme.colorScheme.surfaceVariant
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
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
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (enabled) gradient else Brush.linearGradient(listOf(Color.Gray, Color.DarkGray))),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.background,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
            if (enabled) {
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline
                )
            } else {
                Text(
                    stringResource(R.string.coming_soon),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Composable
fun ActionChip(
    icon: ImageVector,
    label: String,
    gradient: Brush,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(gradient),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.background,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun DataActionChip(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = Purple,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun LanguageToggle() {
    val context = LocalContext.current
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
