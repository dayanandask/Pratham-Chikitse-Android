package com.example.health.ui.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.health.R
import com.example.health.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onCategoryClick: (String) -> Unit,
    onSOSClick: () -> Unit,
    onAssistantClick: () -> Unit,
    onHospitalsClick: () -> Unit,
    onViewAllCategories: () -> Unit,
    onProfileClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    if (uiState.isLoading) { ShimmerEffect(); return }
    if (uiState.error != null) { ErrorState(message = uiState.error ?: "Unknown error"); return }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // Immersive Medical Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF006064), Color(0xFF00838F), MaterialTheme.colorScheme.background)
                        )
                    )
                    .padding(top = 24.dp, bottom = 40.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color.White.copy(0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(0.3f)),
                            modifier = Modifier.size(54.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Filled.Bloodtype, null, Modifier.size(32.dp), Color(0xFFFF5252))
                            }
                        }
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text(
                                text = stringResource(R.string.app_name),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = (-0.5).sp
                            )
                            Text(
                                "Saving Lives • Offline First Aid",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White.copy(0.8f),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Profile Icon at top right
                    IconButton(onProfileClick) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(0.3f)),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Filled.AccountCircle, null, tint = Color.White)
                            }
                        }
                    }
                }
            }
        }

        // Floating Search Bar (Offset to overlap header)
        item {
            Box(Modifier.padding(horizontal = 20.dp).offset(y = (-20).dp)) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 8.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.search(it) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Search 15+ Emergency Guides...", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.6f)) },
                        leadingIcon = { Icon(Icons.Filled.Search, "Search", tint = Color(0xFF00838F)) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) IconButton({ viewModel.search("") }) { Icon(Icons.Filled.Clear, "Clear") }
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = Color.Transparent
                        )
                    )
                }
            }
        }

        // Emergency Banner
        item {
            EmergencyBanner(
                onClick = onSOSClick, 
                modifier = Modifier.padding(horizontal = 20.dp).offset(y = (-10).dp)
            )
            Spacer(Modifier.height(8.dp))
        }

        // Quick Actions
        item {
            // AI Assist Pulse Animation
            val infiniteTransition = rememberInfiniteTransition()
            val pulseScale by infiniteTransition.animateFloat(
                initialValue = 1f, targetValue = 1.05f,
                animationSpec = infiniteRepeatable(tween(1000, easing = androidx.compose.animation.core.LinearOutSlowInEasing), RepeatMode.Reverse)
            )

            SectionHeader(title = stringResource(R.string.quick_actions))
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickActionCard(Icons.Filled.Phone, stringResource(R.string.call_108), onSOSClick, Modifier.weight(1f), containerColor = Color(0xFFFFEBEE), contentColor = Color(0xFFC62828))
                QuickActionCard(
                    Icons.Filled.SmartToy, stringResource(R.string.ai_assist), onAssistantClick, 
                    Modifier.weight(1f).scale(pulseScale), 
                    containerColor = Color(0xFFE3F2FD), contentColor = Color(0xFF1565C0)
                )
                QuickActionCard(Icons.Filled.LocalHospital, stringResource(R.string.hospitals), onHospitalsClick, Modifier.weight(1f), containerColor = Color(0xFFE8F5E9), contentColor = Color(0xFF2E7D32))
            }
            Spacer(Modifier.height(20.dp))
        }

        // Search results or normal content
        if (searchQuery.isNotEmpty()) {
            val filtered = viewModel.getFilteredCategories()
            item { SectionHeader(title = stringResource(R.string.search_results) + " (${filtered.size})") }
            items(filtered, key = { it.id }) { cat ->
                EmergencyCard(
                    category = cat,
                    isBookmarked = uiState.bookmarkedIds.contains(cat.id),
                    onClick = { onCategoryClick(cat.id) },
                    onBookmark = { viewModel.toggleBookmark(cat.id) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp).animateContentSize()
                )
            }
        } else {
            // Recent
            if (uiState.recentCategories.isNotEmpty()) {
                item {
                    SectionHeader(title = stringResource(R.string.recently_viewed))
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp), 
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.recentCategories, key = { "recent_${it.id}" }) { cat ->
                            var isVisible by remember { mutableStateOf(false) }
                            LaunchedEffect(Unit) { isVisible = true }
                            
                            AnimatedVisibility(
                                visible = isVisible,
                                enter = fadeIn() + scaleIn(initialScale = 0.8f)
                            ) {
                                Card(
                                    onClick = { onCategoryClick(cat.id) },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainerLow),
                                    modifier = Modifier.width(160.dp)
                                ) {
                                    Column(Modifier.padding(16.dp)) {
                                        Text(cat.icon, fontSize = 28.sp)
                                        Spacer(Modifier.height(8.dp))
                                        Text(cat.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, maxLines = 1)
                                    }
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(20.dp))
                }
            }

            // Top / All Categories
            item { SectionHeader(title = stringResource(R.string.all_emergencies), action = stringResource(R.string.view_all), onAction = onViewAllCategories) }
            items(uiState.categories, key = { it.id }) { cat ->
                EmergencyCard(
                    category = cat,
                    isBookmarked = uiState.bookmarkedIds.contains(cat.id),
                    onClick = { onCategoryClick(cat.id) },
                    onBookmark = { viewModel.toggleBookmark(cat.id) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp).animateContentSize()
                )
            }
        }
    }
}
