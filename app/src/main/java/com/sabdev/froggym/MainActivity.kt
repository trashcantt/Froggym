package com.sabdev.froggym

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sabdev.froggym.data.AppDatabase
import com.sabdev.froggym.data.entities.*
import com.sabdev.froggym.ui.screens.*
import com.sabdev.froggym.ui.theme.FroggymTheme
import com.sabdev.froggym.viewmodel.AuthViewModel
import com.sabdev.froggym.viewmodel.AuthViewModelFactory

class MainActivity : ComponentActivity() {
    private lateinit var authViewModel: AuthViewModel

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userDao = AppDatabase.getInstance(applicationContext).userDao()
        val authViewModelFactory = AuthViewModelFactory(userDao, application)
        authViewModel = viewModels<AuthViewModel> { authViewModelFactory }.value

        setContent {
            FroggymTheme {
                MainScreen(authViewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        authViewModel.loadCurrentUser()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(authViewModel: AuthViewModel) {
    val navController = rememberNavController()
    var isRegistered by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        if (authViewModel.isFirstLaunch()) {
            navController.navigate("register")
        } else {
            isRegistered = true
        }
    }

    if (!isRegistered) {
        NavHost(navController = navController, startDestination = "register") {
            composable("register") {
                RegisterScreen(
                    authViewModel = authViewModel,
                    onRegistrationComplete = {
                        isRegistered = true
                    }
                )
            }
        }
    } else {
        val currentUser by authViewModel.currentUser.collectAsState()

        Scaffold(
            bottomBar = {
                NavigationBar {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination
                    listOf(
                        Screen.Home,
                        Screen.Routines,
                        Screen.Profile
                    ).forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = null) },
                            label = { Text(screen.title) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screen.Home.route) { HomeScreen() }
                composable(Screen.Routines.route) { RoutinesScreen() }
                composable(Screen.Profile.route) {
                    ProfileScreen(
                        user = currentUser,
                        onEditProfile = {
                            navController.navigate(Screen.EditProfile.route)
                        }
                    )
                }
                composable(Screen.EditProfile.route) {
                    val currentUser = currentUser ?: User("", "", 0f, 0f, "") // Proporciona un User con valores por defecto si currentUser es null
                    EditProfileScreen(
                        user = currentUser,
                        onSaveChanges = { updatedUser ->
                            authViewModel.updateUser(updatedUser)
                            navController.popBackStack()
                        },
                        onNavigateBack = {
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SplashScreen(navController: NavController, authViewModel: AuthViewModel) {
    // Implementa tu pantalla de splash aquí
}

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Filled.Home)
    object Routines : Screen("routines", "Routines", Icons.Filled.List)
    object Profile : Screen("profile", "Profile", Icons.Filled.Person)
    object EditProfile : Screen("edit_profile", "Edit Profile", Icons.Filled.Person)
}