package com.example.assigment1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
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

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate("dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        composable("dashboard") {
            DashboardScreen(
                contentViewModel = contentViewModel,
                authViewModel = authViewModel,
                onLogout = {
                    authViewModel.logout()
                    navController.navigate("login") {
                        popUpTo("dashboard") { inclusive = true }
                    }
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
