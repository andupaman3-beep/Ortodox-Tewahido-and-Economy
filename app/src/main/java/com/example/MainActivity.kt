package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AppBottomNav
import com.example.ui.components.AppNavigationRail
import com.example.ui.components.AppTopBar
import com.example.ui.screens.*
import com.example.ui.state.AppScreen
import com.example.ui.state.AppViewModel
import com.example.ui.theme.TewahidoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val appViewModel: AppViewModel = viewModel()
            val isDark by appViewModel.isDarkMode.collectAsState()

            TewahidoTheme(darkTheme = isDark) {
                val currentScreen by appViewModel.currentScreen.collectAsState()
                val snackbarMessage by appViewModel.snackbarMessage.collectAsState()
                val snackbarHostState = remember { SnackbarHostState() }

                LaunchedEffect(snackbarMessage) {
                    snackbarMessage?.let { msg ->
                        snackbarHostState.showSnackbar(msg)
                        appViewModel.clearSnackbar()
                    }
                }

                // Global BackHandler for nested sub-screens
                BackHandler(enabled = currentScreen != AppScreen.LANDING_HOME) {
                    appViewModel.navigateBack()
                }

                val showBars = currentScreen !in listOf(AppScreen.CHAT, AppScreen.AUTH)

                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val isExpandedScreen = maxWidth >= 600.dp

                    if (isExpandedScreen && showBars) {
                        // Responsive Layout for Wide/Tablet Screens: NavigationRail on the side
                        Row(modifier = Modifier.fillMaxSize()) {
                            AppNavigationRail(viewModel = appViewModel)

                            Scaffold(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(),
                                contentWindowInsets = WindowInsets.safeDrawing,
                                topBar = {
                                    AppTopBar(viewModel = appViewModel)
                                },
                                snackbarHost = { SnackbarHost(snackbarHostState) }
                            ) { innerPadding ->
                                val contentModifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding)

                                AppNavHost(
                                    currentScreen = currentScreen,
                                    appViewModel = appViewModel,
                                    contentModifier = contentModifier
                                )
                            }
                        }
                    } else {
                        // Standard Modern Mobile Layout: Top-level Scaffold with BottomNavigation
                        Scaffold(
                            modifier = Modifier.fillMaxSize(),
                            contentWindowInsets = WindowInsets.safeDrawing,
                            topBar = {
                                if (showBars) {
                                    AppTopBar(viewModel = appViewModel)
                                }
                            },
                            bottomBar = {
                                if (showBars) {
                                    AppBottomNav(viewModel = appViewModel)
                                }
                            },
                            snackbarHost = { SnackbarHost(snackbarHostState) }
                        ) { innerPadding ->
                            val contentModifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)

                            AppNavHost(
                                currentScreen = currentScreen,
                                appViewModel = appViewModel,
                                contentModifier = contentModifier
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AppNavHost(
    currentScreen: AppScreen,
    appViewModel: AppViewModel,
    contentModifier: Modifier
) {
    when (currentScreen) {
        AppScreen.LANDING_HOME -> LandingHomeScreen(viewModel = appViewModel, modifier = contentModifier)
        AppScreen.JOBS -> JobsScreen(viewModel = appViewModel, modifier = contentModifier)
        AppScreen.JOB_DETAIL -> JobDetailScreen(viewModel = appViewModel, modifier = contentModifier)
        AppScreen.MARKETPLACE -> MarketplaceScreen(viewModel = appViewModel, modifier = contentModifier)
        AppScreen.PRODUCT_DETAIL -> ProductDetailScreen(viewModel = appViewModel, modifier = contentModifier)
        AppScreen.B2B -> B2BScreen(viewModel = appViewModel, modifier = contentModifier)
        AppScreen.B2B_DETAIL -> B2BDetailScreen(viewModel = appViewModel, modifier = contentModifier)
        AppScreen.SERVICES -> ServicesScreen(viewModel = appViewModel, modifier = contentModifier)
        AppScreen.SERVICE_DETAIL -> ServiceDetailScreen(viewModel = appViewModel, modifier = contentModifier)
        AppScreen.MESSAGES -> MessagesScreen(viewModel = appViewModel, modifier = contentModifier)
        AppScreen.CHAT -> ChatScreen(viewModel = appViewModel, modifier = Modifier.fillMaxSize())
        AppScreen.NOTIFICATIONS -> NotificationsScreen(viewModel = appViewModel, modifier = contentModifier)
        AppScreen.PROFILE -> ProfileScreen(viewModel = appViewModel, modifier = contentModifier)
        AppScreen.ADMIN -> AdminDashboardScreen(viewModel = appViewModel, modifier = contentModifier)
        AppScreen.AI_ASSISTANT -> AiAssistantScreen(viewModel = appViewModel, modifier = contentModifier)
        AppScreen.GLOBAL_SEARCH -> SearchScreen(viewModel = appViewModel, modifier = contentModifier)
        AppScreen.AUTH -> AuthScreen(viewModel = appViewModel, modifier = contentModifier)
        AppScreen.CV_BUILDER -> CVManagementScreen(viewModel = appViewModel, modifier = contentModifier)
        AppScreen.DOCUMENTS -> DocumentManagementScreen(viewModel = appViewModel, modifier = contentModifier)
        AppScreen.SELLER_DASHBOARD -> SellerDashboardScreen(viewModel = appViewModel, modifier = contentModifier)
        AppScreen.FAVORITES -> FavoritesScreen(viewModel = appViewModel, modifier = contentModifier)
    }
}
