package com.example.assigment1

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun LoginScreen(viewModel: AuthViewModel, onLoginSuccess: () -> Unit) {
    var email by remember { mutableStateOf("admin@streaming.com") }
    var password by remember { mutableStateOf("password123") }
    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onLoginSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Streaming Platform", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(32.dp))
        
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        if (authState is AuthState.Error) {
            Text(text = (authState as AuthState.Error).message, color = Color.Red)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = { viewModel.login(email, password) },
            modifier = Modifier.fillMaxWidth(),
            enabled = authState !is AuthState.Loading
        ) {
            if (authState is AuthState.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
            } else {
                Text("Login")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    contentViewModel: ContentViewModel,
    authViewModel: AuthViewModel,
    onLogout: () -> Unit,
    onContentClick: (Int) -> Unit
) {
    val contentState by contentViewModel.contentState.collectAsState()
    val categoriesState by contentViewModel.categoriesState.collectAsState()
    val user by authViewModel.currentUser.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Welcome, ${user?.username ?: "User"}") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            when (val state = categoriesState) {
                is UIState.Success -> {
                    LazyRow(modifier = Modifier.padding(8.dp)) {
                        items(state.data) { category ->
                            FilterChip(
                                selected = false,
                                onClick = { },
                                label = { Text(category.name) },
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }
                }
                else -> {}
            }

            when (val state = contentState) {
                is UIState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is UIState.Success -> {
                    LazyColumn {
                        items(state.data) { item ->
                            ContentItem(item, onClick = { onContentClick(item.id) })
                        }
                    }
                }
                is UIState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = state.message, color = Color.Red)
                    }
                }
            }
        }
    }
}

@Composable
fun ContentItem(content: Content, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            AsyncImage(
                model = content.thumbnailUrl ?: "https://via.placeholder.com/400x225",
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = content.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(text = content.categoryName ?: "Category", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = content.description, maxLines = 2, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentDetailScreen(contentId: Int, viewModel: ContentViewModel, onBack: () -> Unit) {
    val contentState by viewModel.contentState.collectAsState()
    val content = (contentState as? UIState.Success)?.data?.find { it.id == contentId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(content?.title ?: "Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (content != null) {
            Column(modifier = Modifier.padding(padding).padding(16.dp)) {
                AsyncImage(
                    model = content.thumbnailUrl ?: "https://via.placeholder.com/600x337",
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(250.dp),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = content.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text(text = "Created by: ${content.creatorName ?: "N/A"}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.secondary)
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = content.description, style = MaterialTheme.typography.bodyLarge)
                
                Spacer(modifier = Modifier.weight(1f))
                
                Button(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Watch Content")
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}
