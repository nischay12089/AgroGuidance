package com.nxtbitz.agroguidance

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nxtbitz.agroguidance.data.MongoRepository
import com.nxtbitz.agroguidance.data.User
import com.nxtbitz.agroguidance.ui.theme.*
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val repository = MongoRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AgroGuidanceTheme {
                var currentScreen by remember { mutableStateOf("startup") }
                
                Surface(modifier = Modifier.fillMaxSize(), color = PureBlackBg) {
                    Crossfade(targetState = currentScreen, label = "screen_transition") { screen ->
                        when (screen) {
                            "startup" -> StartupScreen(onGetStarted = { currentScreen = "login" })
                            "login" -> LoginPage(repository)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StartupScreen(onGetStarted: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                drawRect(
                    Brush.radialGradient(
                        colors = listOf(DarkGreenBg, DeepBlackBg, PureBlackBg),
                        center = Offset(size.width * 0.5f, size.height * 0.3f),
                        radius = size.maxDimension * 1.2f
                    )
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Agriculture,
                contentDescription = null,
                modifier = Modifier.size(120.dp),
                tint = PrimaryGreen
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = stringResource(R.string.app_name),
                style = TextStyle(
                    fontSize = 42.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = 1.sp
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = stringResource(R.string.smart_farming_tagline),
                style = TextStyle(
                    fontSize = 18.sp,
                    color = Color.LightGray,
                    textAlign = TextAlign.Center,
                    lineHeight = 26.sp
                )
            )
            
            Spacer(modifier = Modifier.height(80.dp))
            
            Button(
                onClick = onGetStarted,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                shape = RoundedCornerShape(30.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 10.dp)
            ) {
                Text(
                    text = stringResource(R.string.explore_now),
                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                )
            }
        }
    }
}

@Composable
fun LoginPage(repository: MongoRepository) {
    var isLoginTab by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                drawRect(
                    Brush.radialGradient(
                        colors = listOf(DarkGreenBg, DeepBlackBg, PureBlackBg),
                        center = Offset(size.width * 0.2f, size.height * 0.2f),
                        radius = size.maxDimension * 1.5f
                    )
                )
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))
            
            Text(
                text = stringResource(if (isLoginTab) R.string.welcome_back else R.string.join_us),
                style = TextStyle(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
            
            Text(
                text = stringResource(if (isLoginTab) R.string.sign_in_to_account else R.string.start_farming_journey),
                style = TextStyle(fontSize = 16.sp, color = Color.Gray),
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Custom Tab Switcher
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(TextFieldBg.copy(alpha = 0.5f))
                    .padding(4.dp)
            ) {
                TabButton(
                    label = stringResource(R.string.login),
                    isActive = isLoginTab,
                    onClick = { isLoginTab = true },
                    modifier = Modifier.weight(1f)
                )
                TabButton(
                    label = stringResource(R.string.register),
                    isActive = !isLoginTab,
                    onClick = { isLoginTab = false },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            AuthTextField(
                value = email,
                onValueChange = { email = it },
                hint = stringResource(R.string.email_address),
                icon = Icons.Outlined.Email
            )

            Spacer(modifier = Modifier.height(16.dp))

            AuthTextField(
                value = password,
                onValueChange = { password = it },
                hint = stringResource(R.string.password),
                icon = Icons.Outlined.Lock,
                isPass = true
            )

            if (isLoginTab) {
                Text(
                    text = stringResource(R.string.forgot_password),
                    color = PrimaryGreen,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 12.dp)
                        .clickable { /* Handle Forgot Password */ },
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            SubmitButton(
                isLoginTab = isLoginTab,
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                        return@SubmitButton
                    }
                    
                    scope.launch {
                        if (isLoginTab) {
                            val user = repository.loginUser(email, password)
                            if (user != null) {
                                Toast.makeText(context, "Login Successful!", Toast.LENGTH_SHORT).show()
                                // Navigate to home or dashboard
                            } else {
                                Toast.makeText(context, "Invalid credentials", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            val success = repository.registerUser(User(email = email, password = password))
                            if (success) {
                                Toast.makeText(context, "Registration Successful!", Toast.LENGTH_SHORT).show()
                                isLoginTab = true
                            } else {
                                Toast.makeText(context, "User already exists or error occurred", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = stringResource(R.string.terms_service),
                style = TextStyle(fontSize = 12.sp, color = Color.DarkGray, textAlign = TextAlign.Center)
            )
        }
    }
}

@Composable
fun TabButton(label: String, isActive: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isActive) Brush.linearGradient(listOf(PrimaryGreen, SecondaryGreen))
                else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
            )
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = TextStyle(
                color = if (isActive) Color.White else Color.Gray,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isPass: Boolean = false
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        placeholder = { Text(hint, color = HintText) },
        leadingIcon = { Icon(icon, contentDescription = null, tint = PrimaryGreen.copy(alpha = 0.6f), modifier = Modifier.size(20.dp)) },
        visualTransformation = if (isPass) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        keyboardOptions = if (isPass) KeyboardOptions(keyboardType = KeyboardType.Password) else KeyboardOptions.Default,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = TextFieldBg,
            unfocusedContainerColor = TextFieldBg,
            focusedIndicatorColor = PrimaryGreen,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = PrimaryGreen,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
        ),
        singleLine = true
    )
}

@Composable
fun SubmitButton(isLoginTab: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
    ) {
        Text(
            text = stringResource(if (isLoginTab) R.string.sign_in else R.string.create_account),
            style = TextStyle(fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
        )
    }
}
