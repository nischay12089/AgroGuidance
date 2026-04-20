package com.nxtbitz.agroguidance

import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.nxtbitz.agroguidance.data.*
import com.nxtbitz.agroguidance.ui.theme.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.lifecycle.viewmodel.compose.viewModel


class MainActivity : AppCompatActivity() {

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val locales = AppCompatDelegate.getApplicationLocales()
        if (!locales.isEmpty) {
            val configuration = Configuration(newConfig)
            configuration.setLocales(locales.get(0)?.let { android.os.LocaleList(it) })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AgroGuidanceTheme {
                var currentScreen by rememberSaveable { mutableStateOf("startup") }
                var userName by rememberSaveable { mutableStateOf("") }
                
                Surface(modifier = Modifier.fillMaxSize(), color = PureBlackBg) {
                    Column {
                        Box(modifier = Modifier.weight(1f)) {
                            Crossfade(targetState = currentScreen, label = "screen_transition") { screen ->
                                when (screen) {
                                    "startup" -> StartupScreen(onGetStarted = { currentScreen = "user_setup" })
                                    "user_setup" -> UserPage(onNameSubmitted = { name ->
                                        userName = name
                                        currentScreen = "home"
                                    })
                                    "home" -> HomePage(userName)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LanguageSwitcher() {
    var expanded by remember { mutableStateOf(false) }
    val languages = listOf(
        "English" to "en",
        "हिन्दी" to "hi",
        "বাংলা" to "bn",
        "मराठी" to "mr",
        "తెలుగు" to "te",
        "தமிழ்" to "ta",
        "ગુજરાતી" to "gu",
        "ಕನ್ನಡ" to "kn",
        "മലയാളം" to "ml",
        "ਪੰਜਾਬੀ" to "pa"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.TopEnd
    ) {
        OutlinedButton(
            onClick = { expanded = true },
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(20.dp)
        ) {
            Icon(Icons.Default.Translate, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.language_preferences).split(" ")[0], fontSize = 12.sp)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(TextFieldBg)
        ) {
            languages.forEach { (name, code) ->
                DropdownMenuItem(
                    text = { Text(name, color = Color.White) },
                    onClick = {
                        val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(code)
                        AppCompatDelegate.setApplicationLocales(appLocale)
                        expanded = false
                    }
                )
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
            }
    ) {
        LanguageSwitcher()

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(32.dp)
        ) {
            Text(
                text = "🌱",
                style = TextStyle(fontSize = 100.sp),
                modifier = Modifier.size(120.dp),
                textAlign = TextAlign.Center
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
fun UserPage(onNameSubmitted: (String) -> Unit) {
    var name by remember { mutableStateOf("") }
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
                .padding(horizontal = 24.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            Text(
                text = stringResource(R.string.personalize_profile),
                style = TextStyle(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
                textAlign = TextAlign.Center
            )
            
            Text(
                text = stringResource(R.string.enter_name_desc),
                style = TextStyle(fontSize = 16.sp, color = Color.Gray),
                modifier = Modifier.padding(top = 8.dp),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.weight(1f))

            AuthTextField(
                value = name,
                onValueChange = { name = it },
                hint = stringResource(R.string.your_full_name),
                leadingIcon = { 
                    Text(
                        text = "🌱",
                        modifier = Modifier.padding(start = 12.dp),
                        fontSize = 18.sp
                    )
                }
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = {
                    if (name.isBlank()) {
                        Toast.makeText(context, context.getString(R.string.enter_name_error), Toast.LENGTH_SHORT).show()
                    } else {
                        onNameSubmitted(name)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                Text(
                    text = stringResource(R.string.continue_btn),
                    style = TextStyle(fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = stringResource(R.string.terms_service),
                style = TextStyle(fontSize = 12.sp, color = Color.DarkGray, textAlign = TextAlign.Center)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(userName: String, viewModel: CropViewModel = viewModel()) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var showSettings by rememberSaveable { mutableStateOf(false) }
    val tabs = listOf("Search", "Library", "AI Advisor", "Update Crops")
    
    val cropsWithIssues by viewModel.cropsWithIssues.collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PureBlackBg)
            .statusBarsPadding()
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Column {
                Text(
                    text = "${stringResource(R.string.welcome)}, $userName!",
                    style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold, color = PrimaryGreen)
                )
                Text(
                    text = stringResource(R.string.dashboard_subtitle),
                    style = TextStyle(fontSize = 14.sp, color = Color.Gray)
                )
            }
            
            IconButton(
                onClick = { showSettings = true },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .background(TextFieldBg, RoundedCornerShape(12.dp))
            ) {
                Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White)
            }
        }

        // Stat Cards
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatCard(
                value = "${cropsWithIssues.size}",
                label = stringResource(R.string.crops),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                value = "${cropsWithIssues.sumOf { it.issues.size }}",
                label = stringResource(R.string.solutions),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                value = stringResource(R.string.ready),
                label = stringResource(R.string.offline_ai),
                icon = Icons.Default.CheckCircle,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = PrimaryGreen,
            edgePadding = 16.dp,
            indicator = { tabPositions ->
                if (selectedTab < tabPositions.size) {
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = PrimaryGreen
                    )
                }
            },
            divider = {}
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = when(index) {
                                0 -> stringResource(R.string.search)
                                1 -> stringResource(R.string.library)
                                2 -> stringResource(R.string.ai_advisor)
                                3 -> "Database"
                                else -> title
                            },
                            style = TextStyle(
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 14.sp
                            )
                        )
                    }
                )
            }
        }

        // Tab Content
        Box(modifier = Modifier.weight(1f)) {
            when (selectedTab) {
                0 -> SearchTab(cropsWithIssues)
                1 -> LibraryTab(cropsWithIssues, viewModel)
                2 -> AiAdvisorTab()
                3 -> DatabaseTab(viewModel)
            }
        }
    }

    if (showSettings) {
        ModalBottomSheet(
            onDismissRequest = { showSettings = false },
            containerColor = PureBlackBg,
            dragHandle = { BottomSheetDefaults.DragHandle(color = Color.Gray) }
        ) {
            SettingsContent()
        }
    }
}

@Composable
fun DatabaseTab(viewModel: CropViewModel) {
    var cropName by remember { mutableStateOf("") }
    var selectedCropId by remember { mutableStateOf<Int?>(null) }
    var issueName by remember { mutableStateOf("") }
    var solution by remember { mutableStateOf("") }
    
    val crops by viewModel.allCrops.collectAsState(initial = emptyList())
    val isSyncing by viewModel.isSyncing.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "➕ Add New Crop",
                style = TextStyle(color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            )
            
            IconButton(
                onClick = {
                    viewModel.syncFromFirebase { success ->
                        if (success) {
                            Toast.makeText(context, "Sync Completed", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Sync Failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                enabled = !isSyncing,
                modifier = Modifier.background(TextFieldBg, RoundedCornerShape(12.dp))
            ) {
                if (isSyncing) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = PrimaryGreen, strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.Sync, contentDescription = "Sync from Firebase", tint = Color.White)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        AuthTextField(
            value = cropName,
            onValueChange = { cropName = it },
            hint = "Crop Name (e.g. Rice, Wheat)"
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                if (cropName.isNotBlank()) {
                    viewModel.addCrop(cropName)
                    cropName = ""
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
        ) {
            Text("Add Crop")
        }

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider(color = Color.Gray.copy(alpha = 0.3f))
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "➕ Add Issue & Solution",
            style = TextStyle(color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        Text("Select Crop:", color = Color.Gray, fontSize = 12.sp)
        Box(modifier = Modifier.fillMaxWidth().heightIn(max = 150.dp).background(TextFieldBg, RoundedCornerShape(12.dp)).padding(4.dp)) {
            LazyColumn {
                items(crops) { crop ->
                    Text(
                        text = crop.name,
                        color = if (selectedCropId == crop.id) PrimaryGreen else Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedCropId = crop.id }
                            .padding(8.dp)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        AuthTextField(
            value = issueName,
            onValueChange = { issueName = it },
            hint = "Issue Name (e.g. Blast Disease)"
        )
        Spacer(modifier = Modifier.height(8.dp))
        AuthTextField(
            value = solution,
            onValueChange = { solution = it },
            hint = "Solution Details"
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = {
                val cropId = selectedCropId
                if (cropId != null && issueName.isNotBlank() && solution.isNotBlank()) {
                    viewModel.addIssue(cropId, issueName, solution)
                    issueName = ""
                    solution = ""
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
            enabled = selectedCropId != null,
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                "Submit Issue & Solution",
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp)
            )
        }
    }
}

@Composable
fun SettingsContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .navigationBarsPadding()
    ) {
        Text(
            text = "⚙️ ${stringResource(R.string.app_settings)}",
            style = TextStyle(color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = stringResource(R.string.language_preferences),
            style = TextStyle(color = PrimaryGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        )
        Text(
            text = stringResource(R.string.select_language_desc),
            style = TextStyle(color = Color.Gray, fontSize = 12.sp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LanguageDropdown()
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(TextFieldBg, RoundedCornerShape(12.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Info, contentDescription = null, tint = PrimaryGreen)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = stringResource(R.string.about_app), color = Color.White, fontSize = 14.sp)
                Text(text = "${stringResource(R.string.version)} 1.0.0", color = Color.Gray, fontSize = 12.sp)
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun LanguageDropdown() {
    var expanded by remember { mutableStateOf(false) }
    val languages = listOf(
        "English" to "en",
        "हिन्दी" to "hi",
        "বাংলা" to "bn",
        "मराठी" to "mr",
        "తెలుగు" to "te",
        "தமிழ்" to "ta",
        "ગુજરાતી" to "gu",
        "ಕನ್ನಡ" to "kn",
        "മലയാളം" to "ml",
        "ਪੰਜਾਬੀ" to "pa"
    )
    
    val currentLocale = AppCompatDelegate.getApplicationLocales().toLanguageTags()
    val currentLangName = languages.find { it.second == currentLocale }?.first ?: "English"

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Translate, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(currentLangName)
                }
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .background(TextFieldBg)
        ) {
            languages.forEach { (name, code) ->
                DropdownMenuItem(
                    text = { Text(name, color = Color.White) },
                    onClick = {
                        val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(code)
                        AppCompatDelegate.setApplicationLocales(appLocale)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun StatCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = TextFieldBg),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) {
                    Icon(icon, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Text(
                    text = value,
                    style = TextStyle(color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp),
                    maxLines = 1
                )
            }
            Text(
                text = label,
                style = TextStyle(color = Color.Gray, fontSize = 10.sp)
            )
        }
    }
}

@Composable
fun SearchTab(cropsWithIssues: List<CropWithIssues>) {
    var query by remember { mutableStateOf("") }
    val results = cropsWithIssues.filter { it.crop.name.contains(query, ignoreCase = true) }

    Column(modifier = Modifier.padding(16.dp)) {
        AuthTextField(
            value = query,
            onValueChange = { query = it },
            hint = stringResource(R.string.search_crop_hint),
            leadingIcon = { 
                Icon(
                    Icons.Default.Search, 
                    contentDescription = null, 
                    tint = PrimaryGreen.copy(alpha = 0.6f),
                    modifier = Modifier.size(20.dp)
                ) 
            }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(results) { item ->
                Column {
                    Text(
                        text = "📍 ${item.crop.name}",
                        style = TextStyle(color = PrimaryGreen, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    )
                    item.issues.forEach { issue ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkGreenBg.copy(alpha = 0.3f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "⚠️ ${issue.issueName}",
                                    style = TextStyle(color = Color.White, fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = issue.solution,
                                    style = TextStyle(color = Color(0xFFD1FAE5), fontSize = 14.sp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LibraryTab(cropsWithIssues: List<CropWithIssues>, viewModel: CropViewModel) {
    val isSyncing by viewModel.isSyncing.collectAsState()
    val context = LocalContext.current

    if (cropsWithIssues.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
                Icon(Icons.AutoMirrored.Filled.LibraryBooks, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(64.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text("Your library is empty", color = Color.Gray, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        viewModel.syncFromFirebase { success ->
                            if (success) {
                                Toast.makeText(context, "Data fetched successfully", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Failed to fetch data", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    enabled = !isSyncing,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isSyncing) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Fetching...")
                    } else {
                        Text("Fetch Data from Database")
                    }
                }
            }
        }
    } else {
        val items = cropsWithIssues.sortedBy { it.crop.name }
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(items) { item ->
                var expanded by remember { mutableStateOf(false) }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = !expanded }
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "🌿 ${item.crop.name}",
                            style = TextStyle(color = Color.White, fontSize = 16.sp),
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    }
                    AnimatedVisibility(visible = expanded) {
                        Column(modifier = Modifier.padding(top = 8.dp)) {
                            item.issues.forEach { issue ->
                                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                                    Text(
                                        text = "⚠️ ${issue.issueName}",
                                        style = TextStyle(color = Color.White, fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = issue.solution,
                                        style = TextStyle(color = Color(0xFFA7F3D0), fontSize = 14.sp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AiAdvisorTab() {
    var prompt by remember { mutableStateOf("") }
    var response by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "🤖 ${stringResource(R.string.ai_consultant_title)}",
            style = TextStyle(color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        )
        Text(
            text = stringResource(R.string.ai_consultant_desc),
            style = TextStyle(color = Color.Gray, fontSize = 13.sp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        TextField(
            value = prompt,
            onValueChange = { prompt = it },
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)),
            placeholder = { Text(stringResource(R.string.ai_prompt_hint), color = HintText) },
            trailingIcon = {
                IconButton(onClick = {
                    if (prompt.isNotBlank()) {
                        isLoading = true
                        response = ""
                        scope.launch {
                            delay(2000)
                            response = "Based on Indian agricultural practices, for '$prompt', you should prioritize soil testing and use organic neem cake. Ensure moisture levels are consistent and use crop rotation with legumes to naturally enrich nitrogen."
                            isLoading = false
                        }
                    }
                }) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = PrimaryGreen)
                }
            },
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
        
        Spacer(modifier = Modifier.height(24.dp))
        
        if (isLoading) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryGreen)
            }
        }
        
        if (response.isNotBlank()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = TextFieldBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "${stringResource(R.string.ai_suggestion_label)}:",
                        style = TextStyle(color = PrimaryGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = response,
                        style = TextStyle(color = Color.White, lineHeight = 22.sp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    leadingIcon: @Composable (() -> Unit)? = null,
    isPass: Boolean = false
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        placeholder = { Text(hint, color = HintText) },
        leadingIcon = leadingIcon,
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
