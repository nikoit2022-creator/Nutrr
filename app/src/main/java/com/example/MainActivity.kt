package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.ArchitectureAdminScreen
import com.example.ui.screens.HealthProfileScreen
import com.example.ui.screens.ProductDetailScreen
import com.example.ui.screens.ScanHomeScreen
import com.example.ui.screens.ScanHistoryScreen
import com.example.ui.screens.ScientificLibraryScreen
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.NutriGuardTheme
import com.example.ui.viewmodel.MainViewModel

sealed class Screen(val route: String) {
    object ScanHome : Screen("scan_home")
    object ScanHistory : Screen("scan_history")
    object HealthProfiles : Screen("health_profiles")
    object ProductDetail : Screen("product_detail")
    object ScientificLibrary : Screen("scientific_library")
    object ArchitectureAdmin : Screen("architecture_admin")
}

data class BottomNavTab(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NutriGuardTheme {
                val viewModel: MainViewModel = viewModel(
                    factory = MainViewModel.Factory(applicationContext)
                )
                NutriGuardApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun NutriGuardApp(viewModel: MainViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val tabs = listOf(
        BottomNavTab(
            route = Screen.ScanHome.route,
            title = "Scan",
            selectedIcon = Icons.Filled.QrCodeScanner,
            unselectedIcon = Icons.Outlined.QrCodeScanner
        ),
        BottomNavTab(
            route = Screen.ScanHistory.route,
            title = "History",
            selectedIcon = Icons.Filled.History,
            unselectedIcon = Icons.Outlined.History
        ),
        BottomNavTab(
            route = Screen.HealthProfiles.route,
            title = "Profile",
            selectedIcon = Icons.Filled.Person,
            unselectedIcon = Icons.Outlined.Person
        )
    )

    val showBottomBar = currentRoute in listOf(
        Screen.ScanHome.route,
        Screen.ScanHistory.route,
        Screen.HealthProfiles.route
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 4.dp
                ) {
                    tabs.forEach { tab ->
                        val isSelected = currentRoute == tab.route
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                if (currentRoute != tab.route) {
                                    navController.navigate(tab.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                    contentDescription = tab.title
                                )
                            },
                            label = {
                                Text(
                                    text = tab.title,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = EmeraldPrimary,
                                selectedTextColor = EmeraldPrimary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.ScanHome.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.ScanHome.route) {
                ScanHomeScreen(
                    viewModel = viewModel,
                    onNavigateToResult = {
                        navController.navigate(Screen.ProductDetail.route)
                    },
                    onNavigateToLibrary = {
                        navController.navigate(Screen.ScientificLibrary.route)
                    }
                )
            }

            composable(Screen.ProductDetail.route) {
                ProductDetailScreen(
                    viewModel = viewModel,
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.ScientificLibrary.route) {
                ScientificLibraryScreen(
                    viewModel = viewModel,
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.HealthProfiles.route) {
                HealthProfileScreen(
                    viewModel = viewModel,
                    onNavigateToLibrary = {
                        navController.navigate(Screen.ScientificLibrary.route)
                    },
                    onNavigateToAdmin = {
                        navController.navigate(Screen.ArchitectureAdmin.route)
                    }
                )
            }

            composable(Screen.ScanHistory.route) {
                ScanHistoryScreen(
                    viewModel = viewModel,
                    onSelectHistoryItem = {
                        navController.navigate(Screen.ProductDetail.route)
                    },
                    onNavigateToScan = {
                        navController.navigate(Screen.ScanHome.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(Screen.ArchitectureAdmin.route) {
                ArchitectureAdminScreen(
                    viewModel = viewModel,
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

