package com.nxtbitz.agroguidance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nxtbitz.agroguidance.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AgroGuidanceTheme {
                LoginPage()
            }
        }
    }
}

@Composable
fun LoginPage() {
    var isLoginTab by remember { mutableStateOf(true) }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

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
            HeroSection()
            Spacer(modifier = Modifier.height(40.dp))
            AuthCard(
                isLoginTab = isLoginTab,
                onTabChange = { isLoginTab = it },
                username = username,
                onUsernameChange = { username = it },
                password = password,
                onPasswordChange = { password = it }
            )
            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = "🌿 Agro Guidance · Empowering Farmers",
                style = TextStyle(color = Color.White.copy(alpha = 0.24f), fontSize = 12.sp)
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun HeroSection() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "🌱",
            style = TextStyle(
                fontSize = 60.sp,
                shadow = Shadow(
                    color = Color(0x8C4ADE80),
                    blurRadius = 18f
                )
            )
        )
        Text(
            text = "Agro Guidance",
            style = TextStyle(
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                brush = Brush.linearGradient(
                    colors = listOf(LightGreen, AccentGreen, SoftGreen)
                )
            )
        )
        Text(
            text = "Your intelligent farming companion",
            style = TextStyle(color = Color.Gray, fontSize = 14.sp)
        )
    }
}

@Composable
fun AuthCard(
    isLoginTab: Boolean,
    onTabChange: (Boolean) -> Unit,
    username: String,
    onUsernameChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 50.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = Color.Black.copy(alpha = 0.5f)
            ),
        color = CardBg,
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Green.copy(alpha = 0.18f))
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TabSwitcher(isLoginTab, onTabChange)
            Spacer(modifier = Modifier.height(25.dp))
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = if (isLoginTab) "SIGN IN TO YOUR ACCOUNT" else "CREATE A NEW ACCOUNT",
                    style = TextStyle(
                        color = Color.Gray,
                        fontSize = 10.sp,
                        letterSpacing = 1.2.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(15.dp))
                AuthTextField(
                    value = username,
                    onValueChange = onUsernameChange,
                    hint = "Username",
                    icon = Icons.Outlined.Email
                )
                Spacer(modifier = Modifier.height(15.dp))
                AuthTextField(
                    value = password,
                    onValueChange = onPasswordChange,
                    hint = "Password",
                    icon = Icons.Outlined.Lock,
                    isPass = true
                )
            }
            Spacer(modifier = Modifier.height(30.dp))
            SubmitButton(isLoginTab)
        }
    }
}

@Composable
fun TabSwitcher(isLoginActive: Boolean, onTabChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Black.copy(alpha = 0.38f))
            .padding(4.dp)
    ) {
        TabButton(
            label = "🔑 Sign In",
            isActive = isLoginActive,
            onClick = { onTabChange(true) },
            modifier = Modifier.weight(1f)
        )
        TabButton(
            label = "✨ Sign Up",
            isActive = !isLoginActive,
            onClick = { onTabChange(false) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun TabButton(label: String, isActive: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val bgColor by animateColorAsState(
        targetValue = if (isActive) Color.Transparent else Color.Transparent,
        label = "tabColor"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(9.dp))
            .background(
                if (isActive) Brush.linearGradient(listOf(PrimaryGreen, SecondaryGreen))
                else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
            )
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = TextStyle(
                color = if (isActive) Color.White else Color.Gray,
                fontWeight = FontWeight.SemiBold,
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
            .clip(RoundedCornerShape(10.dp)),
        placeholder = { Text(hint, color = HintText) },
        leadingIcon = { Icon(icon, contentDescription = null, tint = Color.Green.copy(alpha = 0.5f), modifier = Modifier.size(20.dp)) },
        visualTransformation = if (isPass) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        keyboardOptions = if (isPass) KeyboardOptions(keyboardType = KeyboardType.Password) else KeyboardOptions.Default,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = TextFieldBg,
            unfocusedContainerColor = TextFieldBg,
            focusedIndicatorColor = PrimaryGreen,
            unfocusedIndicatorColor = Color.Green.copy(alpha = 0.2f),
            cursorColor = PrimaryGreen,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
        ),
        singleLine = true
    )
}

@Composable
fun SubmitButton(isLoginTab: Boolean) {
    Button(
        onClick = { /* Handle Auth */ },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
    ) {
        Text(
            text = if (isLoginTab) "Sign In →" else "Create Account →",
            style = TextStyle(fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPagePreview() {
    AgroGuidanceTheme {
        LoginPage()
    }
}
