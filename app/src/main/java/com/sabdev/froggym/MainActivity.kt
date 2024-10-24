package com.sabdev.froggym

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sabdev.froggym.data.AppDatabase
import com.sabdev.froggym.data.entities.User
import com.sabdev.froggym.data.repository.ExerciseRepository
import com.sabdev.froggym.data.repository.RoutineRepository
import com.sabdev.froggym.ui.SetupNavigation
import com.sabdev.froggym.ui.screens.AddExercisesScreen
import com.sabdev.froggym.ui.screens.CreateRoutineScreen
import com.sabdev.froggym.ui.screens.EditProfileScreen
import com.sabdev.froggym.ui.screens.HomeScreen
import com.sabdev.froggym.ui.screens.ProfileScreen
import com.sabdev.froggym.ui.screens.RegisterScreen
import com.sabdev.froggym.ui.screens.RoutineDetailsScreen
import com.sabdev.froggym.ui.screens.RoutinesScreen
import com.sabdev.froggym.ui.theme.FroggymTheme
import com.sabdev.froggym.viewmodel.AuthViewModel
import com.sabdev.froggym.viewmodel.AuthViewModelFactory
import com.sabdev.froggym.viewmodel.RoutineViewModel
import com.sabdev.froggym.viewmodel.RoutineViewModelFactory
import kotlinx.coroutines.launch

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

        val authViewModelFactory = AuthViewModelFactory(userDao, application)
        authViewModel = viewModels<AuthViewModel> { authViewModelFactory }.value

        lifecycleScope.launch {
            routineRepository.initializePredefinedRoutines()
        }

        setContent {
            FroggymTheme {
                val navController = rememberNavController()
                SetupNavigation(navController, routineViewModel)
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
            navController.navigate("registrarse")
        } else {
            isRegistered = true
        }
    }

    if (!isRegistered) {
        NavHost(navController = navController, startDestination = "registrarse") {
            composable("registrarse") {
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
                            icon = {
                                Icon(
                                    painter = painterResource(id = screen.icon),
                                    contentDescription = null,
                                    modifier = Modifier.size(screen.iconSize.dp)
                                )
                            },
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
                composable("routine_details/{routineId}") { backStackEntry ->
                    val routineId = backStackEntry.arguments?.getString("routineId")
                    if (routineId != null) {
                        RoutineDetailsScreen(
                            routineId = routineId.toLong(),
                            viewModel = routineViewModel,
                            onNavigateToAddExercises = { routineId ->
                                navController.navigate("add_exercises/$routineId")
                            }
                        )
                    }
                }
                composable("add_exercises/{routineId}") { backStackEntry ->
                    val routineId = backStackEntry.arguments?.getString("routineId")
                    if (routineId != null) {
                        val routineIdLong = routineId.toLong()
                        val routine by routineViewModel.getRoutineById(routineIdLong)
                            .collectAsState(initial = null)
                        routine?.let {
                            AddExercisesScreen(
                                routineId = routineIdLong,
                                routineType = it.type,
                                viewModel = routineViewModel,
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

sealed class Screen(
    val route: String,
    val title: String,
    @DrawableRes val icon: Int,
    val iconSize: Int = 25
) {
    object Home : Screen("home", "Inicio", R.drawable.ic_home)
    object Routines : Screen("routines", "Rutinas", R.drawable.ic_routines)
    object Profile : Screen("profile", "Perfil", R.drawable.ic_profile)
    object EditProfile : Screen("edit_profile", "Editar perfil", R.drawable.ic_launcher_foreground)
    object CreateRoutine :
        Screen("create_routine", "Crear rutinas", R.drawable.ic_launcher_background)
}