package com.sabdev.froggym

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.sabdev.froggym.data.AppDatabase
import com.sabdev.froggym.data.entities.User
import com.sabdev.froggym.data.repository.*
import com.sabdev.froggym.ui.screens.*
import com.sabdev.froggym.ui.theme.FroggymTheme
import com.sabdev.froggym.viewmodel.*

class MainActivity : ComponentActivity() {
    private lateinit var routineViewModel: RoutineViewModel
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getInstance(applicationContext)
        val routineDao = database.routineDao()
        val exerciseDao = database.exerciseDao()
        val userDao = database.userDao()

        val routineRepository = RoutineRepository(routineDao)
        val exerciseRepository = ExerciseRepository(exerciseDao)
        val routineViewModelFactory = RoutineViewModelFactory(routineRepository, exerciseRepository)
        routineViewModel = viewModels<RoutineViewModel> { routineViewModelFactory }.value

        // Initialize authViewModel
        val authViewModelFactory = AuthViewModelFactory(userDao, application)
        authViewModel = viewModels<AuthViewModel> { authViewModelFactory }.value

        setContent {
            FroggymTheme {
                MainScreen(authViewModel, routineViewModel)
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
fun MainScreen(authViewModel: AuthViewModel, routineViewModel: RoutineViewModel) {
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
                composable(Screen.Home.route) { HomeScreen(authViewModel = authViewModel) }
                composable(Screen.Routines.route) {
                    RoutinesScreen(
                        viewModel = routineViewModel,
                        onCreateRoutine = {
                            navController.navigate(Screen.CreateRoutine.route)
                        },
                        onRoutineSelected = { routineId ->
                            // Navigate to routine details screen (you need to implement this)
                            navController.navigate("routine_details/$routineId")
                        }
                    )
                }
                composable(Screen.Profile.route) {
                    ProfileScreen(
                        user = currentUser,
                        onEditProfile = {
                            navController.navigate(Screen.EditProfile.route)
                        }
                    )
                }
                composable(Screen.EditProfile.route) {
                    val currentUser = currentUser ?: User("", "", 0f, 0f, "")
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
                composable(Screen.CreateRoutine.route) {
                    CreateRoutineScreen(
                        viewModel = routineViewModel,
                        onRoutineCreated = {
                            navController.popBackStack()
                        }
                    )
                }
                // Add a new composable for routine details if needed
                composable("routine_details/{routineId}") { backStackEntry ->
                    val routineId = backStackEntry.arguments?.getString("routineId")
                    if (routineId != null) {
                        RoutineDetailsScreen(
                            routineId = routineId.toLong(),
                            viewModel = routineViewModel
                        )
                    }
                }
            }
        }
    }
}

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Filled.Home)
    object Routines : Screen("routines", "Routines", Icons.Filled.List)
    object Profile : Screen("profile", "Profile", Icons.Filled.Person)
    object EditProfile : Screen("edit_profile", "Edit Profile", Icons.Filled.Edit)
    object CreateRoutine : Screen("create_routine", "Create Routine", Icons.Filled.Add)
}