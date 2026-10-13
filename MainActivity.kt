package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Celebration
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.ProfileEntity
import com.example.ui.AddFolderDialog
import com.example.ui.AddMilestoneDialog
import com.example.ui.AddMomentDialog
import com.example.ui.EditProfileDialog
import com.example.ui.HeartShowerOverlay
import com.example.ui.HomeScreen
import com.example.ui.LoveCardDialog
import com.example.ui.LoveViewModel
import com.example.ui.MilestonesScreen
import com.example.ui.MomentsScreen
import com.example.ui.ProfilesScreen
import com.example.ui.SettingsScreen
import com.example.ui.theme.MyApplicationTheme

enum class ScreenTab {
    HOME, PROFILES, MILESTONES, MOMENTS, SETTINGS
}

class MainActivity : ComponentActivity() {
    private val loveViewModel: LoveViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkMode by loveViewModel.isDarkMode.collectAsStateWithLifecycle()
            MyApplicationTheme(darkTheme = isDarkMode) {
                LoveJourneyApp(viewModel = loveViewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoveJourneyApp(viewModel: LoveViewModel) {
    var currentTab by remember { mutableStateOf(ScreenTab.HOME) }
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showAddMilestoneDialog by remember { mutableStateOf(false) }
    var showAddMomentDialog by remember { mutableStateOf(false) }
    var showAddFolderDialog by remember { mutableStateOf(false) }
    var showLoveCardDialog by remember { mutableStateOf(false) }
    var targetFolderForAdd by remember { mutableStateOf<String?>(null) }

    val profile by viewModel.profile.collectAsStateWithLifecycle()
    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsStateWithLifecycle()
    val timeCounter by viewModel.timeCounter.collectAsStateWithLifecycle()
    val milestones by viewModel.milestones.collectAsStateWithLifecycle()
    val memories by viewModel.memories.collectAsStateWithLifecycle()
    val vaultFolders by viewModel.vaultFolders.collectAsStateWithLifecycle()
    val selectedFolder by viewModel.selectedFolder.collectAsStateWithLifecycle()
    val currentQuote by viewModel.currentQuote.collectAsStateWithLifecycle()

    val currentProfile = profile ?: ProfileEntity()

    // Handle system back button for folders or tab navigation
    if (selectedFolder != null) {
        BackHandler {
            viewModel.selectFolder(null)
        }
    } else if (currentTab != ScreenTab.HOME) {
        BackHandler {
            currentTab = ScreenTab.HOME
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .clickable {
                                    viewModel.triggerHeartBurst()
                                }
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Heart",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "Love Journey",
                                fontWeight = FontWeight.Bold,
                                fontSize = 19.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.primary
                    ),
                    actions = {
                        IconButton(
                            onClick = {
                                viewModel.triggerHeartBurst()
                            },
                            modifier = Modifier.testTag("appbar_heart_action")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Sparkle Love",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp,
                    modifier = Modifier.testTag("bottom_nav_bar")
                ) {
                    NavigationBarItem(
                        selected = currentTab == ScreenTab.HOME,
                        onClick = { currentTab = ScreenTab.HOME },
                        icon = {
                            Icon(
                                if (currentTab == ScreenTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                                contentDescription = "Home"
                            )
                        },
                        label = { Text("Home", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("nav_tab_home")
                    )

                    NavigationBarItem(
                        selected = currentTab == ScreenTab.PROFILES,
                        onClick = { currentTab = ScreenTab.PROFILES },
                        icon = {
                            Icon(
                                if (currentTab == ScreenTab.PROFILES) Icons.Filled.Person else Icons.Outlined.Person,
                                contentDescription = "Profiles"
                            )
                        },
                        label = { Text("Profiles", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("nav_tab_profiles")
                    )

                    NavigationBarItem(
                        selected = currentTab == ScreenTab.MILESTONES,
                        onClick = { currentTab = ScreenTab.MILESTONES },
                        icon = {
                            Icon(
                                if (currentTab == ScreenTab.MILESTONES) Icons.Filled.Celebration else Icons.Outlined.Celebration,
                                contentDescription = "Milestones"
                            )
                        },
                        label = { Text("Milestones", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("nav_tab_milestones")
                    )

                    NavigationBarItem(
                        selected = currentTab == ScreenTab.MOMENTS,
                        onClick = { currentTab = ScreenTab.MOMENTS },
                        icon = {
                            Icon(
                                if (currentTab == ScreenTab.MOMENTS) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Vault"
                            )
                        },
                        label = { Text("Vault", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("nav_tab_moments")
                    )

                    NavigationBarItem(
                        selected = currentTab == ScreenTab.SETTINGS,
                        onClick = { currentTab = ScreenTab.SETTINGS },
                        icon = {
                            Icon(
                                if (currentTab == ScreenTab.SETTINGS) Icons.Filled.Settings else Icons.Outlined.Settings,
                                contentDescription = "Settings"
                            )
                        },
                        label = { Text("Settings", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("nav_tab_settings")
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                AnimatedContent(
                    targetState = currentTab,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(180))
                    },
                    label = "tabSwitchAnimation"
                ) { tab ->
                    when (tab) {
                        ScreenTab.HOME -> {
                            HomeScreen(
                                profile = currentProfile,
                                timeCounter = timeCounter,
                                milestones = milestones,
                                currentQuote = currentQuote,
                                onEditProfile = { showEditProfileDialog = true },
                                onOpenShareCard = { showLoveCardDialog = true },
                                onOpenAddMilestone = { showAddMilestoneDialog = true },
                                onNavigateMilestones = { currentTab = ScreenTab.MILESTONES },
                                onNavigateProfiles = { currentTab = ScreenTab.PROFILES },
                                onNextQuote = { viewModel.nextQuote() },
                                onHeartTap = { viewModel.triggerHeartBurst() }
                            )
                        }

                        ScreenTab.PROFILES -> {
                            ProfilesScreen(
                                profile = currentProfile,
                                timeCounter = timeCounter,
                                onEditProfile = { showEditProfileDialog = true },
                                onOpenShareCard = { showLoveCardDialog = true },
                                onHeartTap = { viewModel.triggerHeartBurst() }
                            )
                        }

                        ScreenTab.MILESTONES -> {
                            MilestonesScreen(
                                milestones = milestones,
                                onOpenAddMilestone = { showAddMilestoneDialog = true },
                                onDeleteMilestone = { viewModel.deleteMilestone(it) }
                            )
                        }

                        ScreenTab.MOMENTS -> {
                            MomentsScreen(
                                folders = vaultFolders,
                                memories = memories,
                                selectedFolder = selectedFolder,
                                onSelectFolder = { viewModel.selectFolder(it) },
                                onOpenAddMoment = { folder ->
                                    targetFolderForAdd = folder
                                    showAddMomentDialog = true
                                },
                                onOpenAddFolder = { showAddFolderDialog = true },
                                onDeleteMoment = { viewModel.deleteMemory(it) },
                                onToggleFavorite = { id, current -> viewModel.toggleMemoryFavorite(id, current) }
                            )
                        }

                        ScreenTab.SETTINGS -> {
                            SettingsScreen(
                                profile = currentProfile,
                                isDarkMode = isDarkMode,
                                notificationsEnabled = notificationsEnabled,
                                onSaveProfileDetails = { p1, p2, addr ->
                                    viewModel.saveQuickProfileDetails(p1, p2, addr)
                                    viewModel.triggerHeartBurst()
                                },
                                onToggleDarkMode = { viewModel.toggleDarkMode(it) },
                                onToggleNotifications = { viewModel.toggleNotifications(it) }
                            )
                        }
                    }
                }
            }
        }

        // Heart Shower floating animation overlay
        HeartShowerOverlay(
            showerEvent = viewModel.heartShowerEvent,
            modifier = Modifier.fillMaxSize()
        )

        // Dialogs
        if (showEditProfileDialog) {
            EditProfileDialog(
                currentProfile = currentProfile,
                onDismiss = { showEditProfileDialog = false },
                onSave = { p1, p1Age, p1Birthday, p1Address, p2, p2Age, p2Birthday, p2Address, startMillis, status, note ->
                    viewModel.updateProfile(
                        partner1 = p1,
                        partner1Age = p1Age,
                        partner1Birthday = p1Birthday,
                        partner1Address = p1Address,
                        partner2 = p2,
                        partner2Age = p2Age,
                        partner2Birthday = p2Birthday,
                        partner2Address = p2Address,
                        startDateMillis = startMillis,
                        status = status,
                        note = note
                    )
                    viewModel.triggerHeartBurst()
                    showEditProfileDialog = false
                }
            )
        }

        if (showAddMilestoneDialog) {
            AddMilestoneDialog(
                onDismiss = { showAddMilestoneDialog = false },
                onAdd = { title, dateMillis, isAnnual, cat, notes, icon ->
                    viewModel.addMilestone(title, dateMillis, isAnnual, cat, notes, icon)
                    viewModel.triggerHeartBurst()
                    showAddMilestoneDialog = false
                }
            )
        }

        if (showAddMomentDialog) {
            val folderNames = vaultFolders.map { it.name }.ifEmpty { listOf("First Meet", "Propose", "Valentine", "Birthdays") }
            AddMomentDialog(
                initialFolderName = targetFolderForAdd,
                availableFolders = folderNames,
                onDismiss = { showAddMomentDialog = false },
                onAdd = { title, dateMillis, desc, folderName, tag, imageUri ->
                    viewModel.addMemory(
                        title = title,
                        dateMillis = dateMillis,
                        description = desc,
                        folderName = folderName,
                        tag = tag,
                        imageUri = imageUri
                    )
                    viewModel.triggerHeartBurst()
                    showAddMomentDialog = false
                }
            )
        }

        if (showAddFolderDialog) {
            AddFolderDialog(
                onDismiss = { showAddFolderDialog = false },
                onAdd = { name, subtitle ->
                    viewModel.addVaultFolder(name, subtitle)
                    viewModel.triggerHeartBurst()
                    showAddFolderDialog = false
                }
            )
        }

        if (showLoveCardDialog) {
            LoveCardDialog(
                profile = currentProfile,
                timeCounter = timeCounter,
                onDismiss = { showLoveCardDialog = false }
            )
        }
    }
}
