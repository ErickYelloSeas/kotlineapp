package com.example.assigment1

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun LoginScreen(viewModel: AuthViewModel, onLoginSuccess: () -> Unit, onRegisterClick: () -> Unit) {
    var email by remember { mutableStateOf("admin@streaming.com") }
    var password by remember { mutableStateOf("password123") }
    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onLoginSuccess()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary)
        Text("StreamHub", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
        Text("Your favorite content in one place", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        Spacer(modifier = Modifier.height(40.dp))

        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth(), leadingIcon = { Icon(Icons.Default.Email, null) }, singleLine = true)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth(), leadingIcon = { Icon(Icons.Default.Lock, null) }, singleLine = true)
        Spacer(modifier = Modifier.height(24.dp))

        if (authState is AuthState.Error) {
            ErrorBanner((authState as AuthState.Error).message)
            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(onClick = { viewModel.login(email, password) }, modifier = Modifier.fillMaxWidth().height(50.dp), enabled = authState !is AuthState.Loading, shape = MaterialTheme.shapes.medium) {
            if (authState is AuthState.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
            } else {
                Text("Sign In", fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Don't have an account? ", style = MaterialTheme.typography.bodyMedium)
            Text("Register", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { viewModel.resetState(); onRegisterClick() })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(viewModel: AuthViewModel, onBackToLogin: () -> Unit) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authState by viewModel.authState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Create Account", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(value = firstName, onValueChange = { firstName = it }, label = { Text("First Name") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = lastName, onValueChange = { lastName = it }, label = { Text("Last Name") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Username") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(24.dp))

        if (authState is AuthState.Error) {
            val isSuccess = (authState as AuthState.Error).message.contains("Account created")
            SuccessBanner(message = (authState as AuthState.Error).message, isSuccess = isSuccess)
            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(onClick = { viewModel.register(firstName, lastName, username, email, password) }, modifier = Modifier.fillMaxWidth().height(50.dp), enabled = authState !is AuthState.Loading) {
            if (authState is AuthState.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
            } else {
                Text("Register")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Already have an account? Sign In", modifier = Modifier.clickable { onBackToLogin() })
    }
}

@Composable
private fun ErrorBanner(message: String) {
    Surface(color = MaterialTheme.colorScheme.errorContainer, shape = MaterialTheme.shapes.small, modifier = Modifier.fillMaxWidth()) {
        Text(text = message, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
    }
}

@Composable
private fun SuccessBanner(message: String, isSuccess: Boolean) {
    Surface(color = if (isSuccess) Color(0xFFD7F9E9) else MaterialTheme.colorScheme.errorContainer, shape = MaterialTheme.shapes.small, modifier = Modifier.fillMaxWidth()) {
        Text(text = message, color = if (isSuccess) Color(0xFF006D3D) else MaterialTheme.colorScheme.error, modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
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
    val selectedCategoryId by contentViewModel.selectedCategoryId.collectAsState()
    val user by authViewModel.currentUser.collectAsState()

    // Ensure content is refreshed when the dashboard appears
    LaunchedEffect(Unit) {
        contentViewModel.refresh()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("StreamHub", fontWeight = FontWeight.Bold)
                        Text("Welcome, ${user?.username ?: "Guest"}", style = MaterialTheme.typography.bodySmall)
                    }
                },
                actions = { IconButton(onClick = onLogout) { Icon(Icons.Default.ExitToApp, "Logout") } }
            )
        },
        floatingActionButton = {
            if (user?.roleId == 1 || user?.roleId == 2) {
                FloatingActionButton(onClick = { /* Add Content Dialog */ }, containerColor = MaterialTheme.colorScheme.primaryContainer) {
                    Icon(Icons.Default.Add, contentDescription = "Add Content")
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            when (val state = categoriesState) {
                is UIState.Success -> {
                    LazyRow(modifier = Modifier.padding(vertical = 8.dp), contentPadding = PaddingValues(horizontal = 8.dp)) {
                        item {
                            FilterChip(
                                selected = selectedCategoryId == null,
                                onClick = { contentViewModel.selectCategory(null) },
                                label = { Text("All") },
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                        items(state.data) { category ->
                            FilterChip(
                                selected = selectedCategoryId == category.id,
                                onClick = { contentViewModel.selectCategory(category.id) },
                                label = { Text(category.name) },
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }
                }
                else -> {}
            }

            when (val state = contentState) {
                is UIState.Loading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                is UIState.Success -> {
                    if (state.data.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(48.dp), tint = Color.LightGray)
                                Text("No content found in this category", color = Color.Gray)
                            }
                        }
                    } else {
                        LazyColumn(contentPadding = PaddingValues(8.dp)) { 
                            items(state.data) { item -> ContentItem(item, onClick = { onContentClick(item.id) }) } 
                        }
                    }
                }
                is UIState.Error -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { 
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(state.message, color = Color.Red, textAlign = TextAlign.Center, modifier = Modifier.padding(16.dp))
                        Button(onClick = { contentViewModel.refresh() }) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ContentItem(content: Content, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column {
            Box {
                AsyncImage(
                    model = content.thumbnailUrl ?: "https://via.placeholder.com/400x225",
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp).align(Alignment.Center),
                    tint = Color.White.copy(alpha = 0.8f)
                )
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(content.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(content.categoryName ?: "General", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(end = 8.dp))
                    Text("• ${content.creatorName ?: "Unknown"}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(content.description, maxLines = 2, style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
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
            TopAppBar(title = { Text(content?.title ?: "Details") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } })
        }
    ) { padding ->
        if (content != null) {
            Column(modifier = Modifier.padding(padding).padding(16.dp)) {
                Card(shape = MaterialTheme.shapes.large, elevation = CardDefaults.cardElevation(8.dp)) {
                    AsyncImage(model = content.thumbnailUrl ?: "https://via.placeholder.com/600x337", contentDescription = null, modifier = Modifier.fillMaxWidth().height(250.dp), contentScale = androidx.compose.ui.layout.ContentScale.Crop)
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(content.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text("By ${content.creatorName ?: "N/A"}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))
                Surface(color = MaterialTheme.colorScheme.secondaryContainer, shape = MaterialTheme.shapes.extraSmall) {
                    Text(content.categoryName ?: "Category", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(content.description, style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
                Spacer(modifier = Modifier.weight(1f))
                Button(onClick = { /*TODO*/ }, modifier = Modifier.fillMaxWidth().height(56.dp), shape = MaterialTheme.shapes.large) {
                    Text("Watch Now", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        }
    }
}
