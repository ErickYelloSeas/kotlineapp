package com.example.assigment1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.assigment1.ui.theme.Assigment1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Assigment1Theme {
                StreamingApp()
            }
        }
    }
}

@Composable
fun StreamingApp() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val contentViewModel: ContentViewModel = viewModel()
    
    val currentUser by authViewModel.currentUser.collectAsState()
    
    // Global navigation observer
    LaunchedEffect(Unit) {
        authViewModel.navigationEvent.collect { route ->
            navController.navigate(route) {
                if (route == "dashboard") {
                    popUpTo("login") { inclusive = true }
                } else if (route == "login") {
                    popUpTo("dashboard") { inclusive = true }
                }
            }
        }
    }
    
    val startDestination = if (currentUser != null) "dashboard" else "login"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    // Handled by navigationEvent, but keeping for safety
                    navController.navigate("dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onRegisterClick = {
                    navController.navigate("register")
                }
            )
        }
        composable("register") {
            RegisterScreen(
                viewModel = authViewModel,
                onBackToLogin = {
                    navController.popBackStack()
                }
            )
        }
        composable("dashboard") {
            DashboardScreen(
                contentViewModel = contentViewModel,
                authViewModel = authViewModel,
                onLogout = {
                    authViewModel.logout()
                },
                onContentClick = { contentId ->
                    navController.navigate("detail/$contentId")
                }
            )
        }
        composable("detail/{contentId}") { backStackEntry ->
            val contentId = backStackEntry.arguments?.getString("contentId")?.toIntOrNull() ?: 0
            ContentDetailScreen(
                contentId = contentId,
                viewModel = contentViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
