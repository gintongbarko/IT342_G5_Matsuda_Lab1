package com.it342.timesheets

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.it342.timesheets.auth.AuthViewModel
import com.it342.timesheets.navigation.NavRoutes
import com.it342.timesheets.ui.components.AppHeader
import com.it342.timesheets.ui.screens.LoginScreen
import com.it342.timesheets.ui.screens.RegisterScreen
import com.it342.timesheets.ui.screens.TimesheetScreen
import com.it342.timesheets.timesheet.TimesheetViewModel
import com.it342.timesheets.ui.theme.TimesheetsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TimesheetsTheme {
                TimesheetsApp()
            }
        }
    }
}

@Composable
fun TimesheetsApp(authViewModel: AuthViewModel = viewModel()) {
    val timesheetViewModel: TimesheetViewModel = viewModel()
    val authState by authViewModel.state.collectAsState()
    val navController = rememberNavController()
    val currentUser = authState.currentUser
    val loading = authState.loading

    Column(modifier = Modifier.fillMaxSize()) {
        AppHeader(
            currentUser = currentUser,
            onLoginClick = { navController.navigate(NavRoutes.Login) },
            onRegisterClick = { navController.navigate(NavRoutes.Register) },
            onConfirmLogout = {
                authViewModel.logout()
                navController.navigate(NavRoutes.Login) {
                    popUpTo(NavRoutes.Timesheet) { inclusive = true }
                }
            }
        )

        if (loading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            NavHost(
                navController = navController,
                startDestination = when {
                    currentUser != null -> NavRoutes.Timesheet
                    else -> NavRoutes.Login
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 0.dp)
            ) {
                composable(NavRoutes.Timesheet) {
                    if (currentUser == null) {
                        LaunchedEffect(Unit) {
                            navController.navigate(NavRoutes.Login) {
                                popUpTo(NavRoutes.Timesheet) { inclusive = true }
                            }
                        }
                    } else {
                        TimesheetScreen(
                            currentUser = currentUser,
                            timesheetViewModel = timesheetViewModel
                        )
                    }
                }
                composable(NavRoutes.Login) {
                    if (currentUser != null) {
                        LaunchedEffect(currentUser) {
                            navController.navigate(NavRoutes.Timesheet) {
                                popUpTo(NavRoutes.Login) { inclusive = true }
                            }
                        }
                    } else {
                        LoginScreen(
                            error = authState.loginError,
                            onLogin = { u, p -> authViewModel.login(u, p) },
                            onNavigateToRegister = {
                                authViewModel.clearLoginError()
                                navController.navigate(NavRoutes.Register)
                            },
                            onClearError = { authViewModel.clearLoginError() }
                        )
                    }
                }
                composable(NavRoutes.Register) {
                    if (currentUser != null) {
                        LaunchedEffect(currentUser) {
                            navController.navigate(NavRoutes.Timesheet) {
                                popUpTo(NavRoutes.Register) { inclusive = true }
                            }
                        }
                    } else {
                        RegisterScreen(
                            error = authState.registerError,
                            employerOptions = authState.employerOptions,
                            loadingEmployers = authState.loadingEmployers,
                            onSearchEmployers = { authViewModel.searchEmployers(it) },
                            onRegister = { u, e, p, role, employerUsername ->
                                authViewModel.register(u, e, p, role, employerUsername)
                            },
                            onNavigateToLogin = {
                                authViewModel.clearRegisterError()
                                navController.navigate(NavRoutes.Login) {
                                    popUpTo(NavRoutes.Register) { inclusive = true }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
