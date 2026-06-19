package com.example.ui

import android.annotation.SuppressLint
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.DependencyManager
import com.example.data.Project
import com.example.data.ProjectFile
import com.example.ui.editor.CodeHighlighter
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IdeMainScreen(viewModel: IdeViewModel) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val projects by viewModel.projects.collectAsStateWithLifecycle()
    val currentProject by viewModel.currentProject.collectAsStateWithLifecycle()
    val projectFiles by viewModel.projectFiles.collectAsStateWithLifecycle()
    val selectedFile by viewModel.selectedFile.collectAsStateWithLifecycle()
    val editorContent by viewModel.editorContent.collectAsStateWithLifecycle()
    val openTabs by viewModel.openTabs.collectAsStateWithLifecycle()
    val consoleLogs by viewModel.consoleLogs.collectAsStateWithLifecycle()
    val aiLoading by viewModel.aiLoading.collectAsStateWithLifecycle()
    val aiResponse by viewModel.aiResponse.collectAsStateWithLifecycle()
    val isPremium by viewModel.isPremium.collectAsStateWithLifecycle()
    val teamActivity by viewModel.teamActivity.collectAsStateWithLifecycle()
    val onlineCollaborators by viewModel.onlineCollaborators.collectAsStateWithLifecycle()

    // Screen navigation state (EDITOR, PREVIEW, PACKAGES, AI_ASSISTANT, TEAM_SYNC)
    var currentTabState by remember { mutableStateOf("EDITOR") }

    // Floating creators trigger
    var showCreditsDialog by remember { mutableStateOf(false) }
    var showCreateProjectDialog by remember { mutableStateOf(false) }
    var showCreateFileDialog by remember { mutableStateOf(false) }

    // Editor settings
    var wordWrapEnabled by remember { mutableStateOf(true) }
    var zoomFontSize by remember { mutableStateOf(14) }

    // Deep Luxury Cyber Dark colors
    val luxuryBackground = Color(0xFF090B10)
    val luxurySurface = Color(0xFF111420)
    val luxuryBorder = Color(0xFF1E2336)
    val neonBlue = Color(0xFF38BDF8)
    val neonEmerald = Color(0xFF34D399)
    val neonAmber = Color(0xFFF59E0B)
    val neonPink = Color(0xFFF472B6)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = luxurySurface,
                modifier = Modifier.width(310.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Header
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 20.dp)
                    ) {
                        Text(
                            text = "⚡ HYPER CODE",
                            style = TextStyle(
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = neonBlue,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                        if (isPremium) {
                            Badge(
                                containerColor = neonAmber,
                                contentColor = Color.Black
                            ) {
                                Text("PLUS", fontWeight = FontWeight.Bold, fontSize = 10.sp)
                            }
                        }
                    }

                    HorizontalDivider(color = luxuryBorder)

                    // Project selection row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ACTIVE PROJECTS",
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray,
                                letterSpacing = 1.sp
                            )
                        )
                        IconButton(
                            onClick = { showCreateProjectDialog = true },
                            modifier = Modifier.testTag("new_project_btn")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "New Project", tint = neonBlue)
                        }
                    }

                    // Projects list
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(projects) { project ->
                            val isActive = currentProject?.id == project.id
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isActive) luxuryBorder else Color.Transparent)
                                    .clickable {
                                        viewModel.selectProject(project)
                                        scope.launch { drawerState.close() }
                                    }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = project.name,
                                        style = TextStyle(
                                            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isActive) neonBlue else Color.White,
                                            fontSize = 15.sp
                                        )
                                    )
                                    Text(
                                        text = project.templateType + " Template",
                                        style = TextStyle(color = Color.Gray, fontSize = 11.sp)
                                    )
                                }
                                IconButton(onClick = { viewModel.deleteProject(project) }) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = Color.Red.copy(0.7f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    HorizontalDivider(color = luxuryBorder, modifier = Modifier.padding(vertical = 12.dp))

                    // Collaborative Team detail footer
                    Card(
                        colors = CardDefaults.cardColors(containerColor = luxuryBackground),
                        border = borderStroke(luxuryBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "OWNER CREDENTIALS",
                                style = TextStyle(fontSize = 10.sp, color = neonBlue, fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tucci Cyber Nation",
                                style = TextStyle(fontSize = 13.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                            )
                            Text(
                                text = "Kawooya Raymond",
                                style = TextStyle(fontSize = 12.sp, color = Color.Gray)
                            )
                        }
                    }
                }
            }
        },
        content = {
            Scaffold(
                containerColor = luxuryBackground,
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = luxurySurface,
                            titleContentColor = Color.White
                        ),
                        title = {
                            Column {
                                Text(
                                    text = currentProject?.name ?: "Hyper Code IDE",
                                    style = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.Bold, color = neonBlue)
                                )
                                Text(
                                    text = currentProject?.templateLabel ?: "No Workspace Active",
                                    style = TextStyle(fontSize = 12.sp, color = Color.Gray)
                                )
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "MenuToggle", tint = Color.White)
                            }
                        },
                        actions = {
                            // Premium trigger
                            IconButton(onClick = { viewModel.togglePremium() }) {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = "Premium Mode",
                                    tint = if (isPremium) neonAmber else Color.Gray
                                )
                            }
                            IconButton(onClick = { showCreditsDialog = true }) {
                                Icon(Icons.Default.Info, contentDescription = "Credits", tint = neonBlue)
                            }
                        }
                    )
                },
                bottomBar = {
                    NavigationBar(
                        containerColor = luxurySurface,
                        contentColor = Color.White
                    ) {
                        NavigationBarItem(
                            selected = currentTabState == "EDITOR",
                            onClick = { currentTabState = "EDITOR" },
                            icon = { Icon(Icons.Default.Edit, contentDescription = "Lines editor") },
                            label = { Text("Code Editor", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = neonBlue,
                                selectedTextColor = neonBlue,
                                indicatorColor = luxuryBorder
                            )
                        )
                        NavigationBarItem(
                            selected = currentTabState == "PREVIEW",
                            onClick = { currentTabState = "PREVIEW" },
                            icon = { Icon(Icons.Default.PlayArrow, contentDescription = "Webview runner") },
                            label = { Text("Live Preview", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = neonEmerald,
                                selectedTextColor = neonEmerald,
                                indicatorColor = luxuryBorder
                            )
                        )
                        NavigationBarItem(
                            selected = currentTabState == "PACKAGES",
                            onClick = { currentTabState = "PACKAGES" },
                            icon = { Icon(Icons.Default.Build, contentDescription = "Package selector") },
                            label = { Text("Packages", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = neonAmber,
                                selectedTextColor = neonAmber,
                                indicatorColor = luxuryBorder
                            )
                        )
                        NavigationBarItem(
                            selected = currentTabState == "AI_ASSISTANT",
                            onClick = { currentTabState = "AI_ASSISTANT" },
                            icon = { Icon(Icons.Default.Refresh, contentDescription = "Hyper AI Helper") },
                            label = { Text("Hyper AI", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = neonPink,
                                selectedTextColor = neonPink,
                                indicatorColor = luxuryBorder
                            )
                        )
                    }
                }
            ) { innerPadding ->
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    // Responsive Split: on wide screens, show file explorer, otherwise it drawer-triggered.
                    // For typical phone layout, we load full core view:
                    Column(modifier = Modifier.weight(1f)) {
                        when (currentTabState) {
                            "EDITOR" -> CodeEditorTab(
                                viewModel = viewModel,
                                selectedFile = selectedFile,
                                editorContent = editorContent,
                                openTabs = openTabs,
                                wordWrapEnabled = wordWrapEnabled,
                                onWordWrapToggle = { wordWrapEnabled = !wordWrapEnabled },
                                zoomFontSize = zoomFontSize,
                                onZoomIn = { if (zoomFontSize < 32) zoomFontSize += 2 },
                                onZoomOut = { if (zoomFontSize > 10) zoomFontSize -= 2 },
                                onAddFileClick = { showCreateFileDialog = true },
                                luxurySurface = luxurySurface,
                                luxuryBorder = luxuryBorder,
                                neonBlue = neonBlue
                            )
                            "PREVIEW" -> WebSandboxPreview(
                                viewModel = viewModel,
                                consoleLogs = consoleLogs,
                                luxurySurface = luxurySurface,
                                luxuryBorder = luxuryBorder,
                                neonBlue = neonBlue,
                                neonEmerald = neonEmerald
                            )
                            "PACKAGES" -> PackageManagerTab(
                                viewModel = viewModel,
                                luxurySurface = luxurySurface,
                                luxuryBorder = luxuryBorder,
                                neonAmber = neonAmber
                            )
                            "AI_ASSISTANT" -> HyperAiTab(
                                viewModel = viewModel,
                                aiLoading = aiLoading,
                                aiResponse = aiResponse,
                                luxurySurface = luxurySurface,
                                luxuryBorder = luxuryBorder,
                                neonPink = neonPink
                            )
                        }
                    }
                }
            }

            // --- ALL DIALOGS ---

            if (showCreditsDialog) {
                AlertDialog(
                    onDismissRequest = { showCreditsDialog = false },
                    title = { Text("🚀 Hyper Code IDE", fontWeight = FontWeight.Bold, color = neonBlue) },
                    text = {
                        Column {
                            Text("Full-stack mobile programming environment configured of HTML, CSS, JavaScript, TypeScript, and React with Babel dynamic rendering engine.", color = Color.White, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Developed with absolute precision to support premium sandboxed coding client-side across Android platform devices.", color = Color.LightGray, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(14.dp))
                            Text("CREDENTIALS", fontWeight = FontWeight.Bold, color = neonAmber, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Studio: Tucci Cyber Nation", fontSize = 13.sp, color = Color.White)
                            Text("Architect: Kawooya Raymond", fontSize = 13.sp, color = Color.White)
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showCreditsDialog = false }) {
                            Text("Dismiss", color = neonBlue)
                        }
                    },
                    containerColor = luxurySurface
                )
            }

            if (showCreateProjectDialog) {
                var nameVal by remember { mutableStateOf("") }
                var descVal by remember { mutableStateOf("") }
                var templateVal by remember { mutableStateOf("HTML") }

                AlertDialog(
                    onDismissRequest = { showCreateProjectDialog = false },
                    title = { Text("Create Workspace", fontWeight = FontWeight.Bold, color = Color.White) },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = nameVal,
                                onValueChange = { nameVal = it },
                                label = { Text("Project Name") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = neonBlue,
                                    focusedLabelColor = neonBlue
                                ),
                                modifier = Modifier.testTag("project_name_input")
                            )
                            OutlinedTextField(
                                value = descVal,
                                onValueChange = { descVal = it },
                                label = { Text("Short Description") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = neonBlue,
                                    focusedLabelColor = neonBlue
                                )
                            )

                            Text("Select Framework Template:", color = neonBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            
                            val templatesList = listOf(
                                "HTML" to "HTML5 Website",
                                "REACT" to "React JS (JSX)",
                                "TS" to "TypeScript Console",
                                "REACT_TS" to "React TS (TSX)",
                                "LANDING" to "Landing Page",
                                "PORTFOLIO" to "Portfolio Website",
                                "BLOG" to "Blog Website",
                                "DASHBOARD" to "Admin Dashboard",
                                "TODO_APP" to "Todo Webapp",
                                "CALCULATOR" to "Calculator App"
                            )

                            LazyColumn(modifier = Modifier.height(150.dp)) {
                                items(templatesList) { (key, label) ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(if (templateVal == key) luxuryBorder else Color.Transparent)
                                            .clickable { templateVal = key }
                                            .padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = templateVal == key,
                                            onClick = { templateVal = key }
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(label, color = Color.White, fontSize = 13.sp)
                                    }
                                }
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                if (nameVal.isNotEmpty()) {
                                    viewModel.createProject(nameVal, descVal, templateVal)
                                    showCreateProjectDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = neonBlue),
                            modifier = Modifier.testTag("project_create_confirm")
                        ) {
                            Text("Create", color = Color.Black)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showCreateProjectDialog = false }) {
                            Text("Cancel", color = Color.Gray)
                        }
                    },
                    containerColor = luxurySurface
                )
            }

            if (showCreateFileDialog) {
                var filePathVal by remember { mutableStateOf("") }
                AlertDialog(
                    onDismissRequest = { showCreateFileDialog = false },
                    title = { Text("New Project File", color = Color.White) },
                    text = {
                        Column {
                            Text("Enter paths relative to project root (e.g. src/utils.ts, index.html):", color = Color.Gray, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = filePathVal,
                                onValueChange = { filePathVal = it },
                                singleLine = true,
                                label = { Text("File Name / Path") },
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = neonBlue),
                                modifier = Modifier.testTag("file_name_input")
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                if (filePathVal.isNotEmpty()) {
                                    viewModel.createNewFileInProject(filePathVal, false)
                                    showCreateFileDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = neonBlue),
                            modifier = Modifier.testTag("file_create_confirm")
                        ) {
                            Text("Verify & Create", color = Color.Black)
                        }
                    },
                    containerColor = luxurySurface
                )
            }
        }
    )
}

// --- SUB-SCREEN COMPONENTS ---

@Composable
fun CodeEditorTab(
    viewModel: IdeViewModel,
    selectedFile: ProjectFile?,
    editorContent: String,
    openTabs: List<ProjectFile>,
    wordWrapEnabled: Boolean,
    onWordWrapToggle: () -> Unit,
    zoomFontSize: Int,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    onAddFileClick: () -> Unit,
    textColor: Color = Color.White,
    luxurySurface: Color,
    luxuryBorder: Color,
    neonBlue: Color
) {
    val projectFiles by viewModel.projectFiles.collectAsStateWithLifecycle()
    var searchOpen by remember { mutableStateOf(false) }
    var searchVal by remember { mutableStateOf("") }
    var replaceVal by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        // Project explorer toolbar overlay and open tabs row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(luxurySurface)
                .border(1.dp, luxuryBorder)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onAddFileClick) {
                Icon(Icons.Default.Add, contentDescription = "Add File in hierarchy", tint = neonBlue)
            }

            // Project Selector hierarchy
            Row(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(androidx.compose.foundation.rememberScrollState())
                    .padding(horizontal = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // File directory badges
                for (file in projectFiles) {
                    val isCurrent = selectedFile?.id == file.id
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isCurrent) neonBlue.copy(0.15f) else Color.Transparent)
                            .border(1.dp, if (isCurrent) neonBlue else luxuryBorder)
                            .clickable { viewModel.selectFile(file) }
                            .padding(6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = file.filePath,
                                style = TextStyle(
                                    color = if (isCurrent) neonBlue else Color.LightGray,
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                            if (file.filePath != "index.html") {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = Color.Gray,
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clickable { viewModel.deleteFileInProject(file) }
                                )
                            }
                        }
                    }
                }
            }

            IconButton(onClick = { searchOpen = !searchOpen }) {
                Icon(Icons.Default.Search, contentDescription = "Search symbols", tint = Color.LightGray)
            }
        }

        // Search & Replace Panel
        if (searchOpen) {
            Card(
                colors = CardDefaults.cardColors(containerColor = luxurySurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                border = borderStroke(luxuryBorder)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = searchVal,
                            onValueChange = {
                                searchVal = it
                                viewModel.updateSearchAndReplace(it, replaceVal)
                            },
                            label = { Text("Search text") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = neonBlue)
                        )
                        OutlinedTextField(
                            value = replaceVal,
                            onValueChange = {
                                replaceVal = it
                                viewModel.updateSearchAndReplace(searchVal, it)
                            },
                            label = { Text("Replace") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = neonBlue)
                        )
                    }
                    Button(
                        onClick = { viewModel.executeReplaceInEditor() },
                        colors = ButtonDefaults.buttonColors(containerColor = neonBlue),
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Replace All", color = Color.Black)
                    }
                }
            }
        }

        // Monaco Editor Options bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // Undo / Redo Actions
                IconButton(onClick = { viewModel.triggerUndo() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Undo", tint = Color.LightGray)
                }
                IconButton(onClick = { viewModel.triggerRedo() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Redo", tint = Color.LightGray)
                }
                // Word wrap badge indicator
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (wordWrapEnabled) neonBlue.copy(0.2f) else Color.Transparent)
                        .clickable { onWordWrapToggle() }
                        .padding(6.dp)
                ) {
                    Text("Word Wrap: ${if (wordWrapEnabled) "ON" else "OFF"}", color = if (wordWrapEnabled) neonBlue else Color.Gray, fontSize = 11.sp)
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Zoom:", fontSize = 11.sp, color = Color.Gray)
                Text("-", modifier = Modifier.clickable { onZoomOut() }.padding(4.dp), color = Color.White, fontWeight = FontWeight.Bold)
                Text("${zoomFontSize}pt", color = neonBlue, fontSize = 11.sp)
                Text("+", modifier = Modifier.clickable { onZoomIn() }.padding(4.dp), color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        // Gutter lines count layout + Code Text Field
        if (selectedFile != null) {
            Row(modifier = Modifier.weight(1f)) {
                // Gutter Column for Line Numbers
                val linesCount = editorContent.lines().size
                Column(
                    modifier = Modifier
                        .width(42.dp)
                        .fillMaxHeight()
                        .background(luxurySurface)
                        .border(1.dp, luxuryBorder)
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    for (i in 1..linesCount) {
                        Text(
                            text = "$i",
                            style = TextStyle(
                                fontSize = zoomFontSize.sp,
                                fontFamily = FontFamily.Monospace,
                                color = Color.Gray.copy(0.6f)
                            ),
                            modifier = Modifier.padding(end = 8.dp, bottom = 2.dp)
                        )
                    }
                }

                // Editor Text Field
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState())
                        .horizontalScroll(rememberScrollState())
                        .padding(12.dp)
                ) {
                    BasicTextField(
                        value = editorContent,
                        onValueChange = { viewModel.updateEditorContent(it) },
                        textStyle = TextStyle(
                            color = textColor,
                            fontSize = zoomFontSize.sp,
                            fontFamily = FontFamily.Monospace
                        ),
                        cursorBrush = SolidColor(neonBlue),
                        visualTransformation = CodeHighlighter(selectedFile.fileExtension),
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("code_editor_field")
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("No file selected", color = Color.Gray, fontSize = 16.sp)
                    Text("Select a file from the toolbar tabs above to start writing code.", color = Color.Gray.copy(0.7f), fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun WebSandboxPreview(
    viewModel: IdeViewModel,
    consoleLogs: List<ConsoleLogItem>,
    luxurySurface: Color,
    luxuryBorder: Color,
    neonBlue: Color,
    neonEmerald: Color
) {
    var compiledHtml by remember { mutableStateOf("") }
    var compilingInProgress by remember { mutableStateOf(false) }
    var currentDeviceMode by remember { mutableStateOf("MOBILE") } // MOBILE, TABLET, DESKTOP
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = Unit) {
        compilingInProgress = true
        compiledHtml = viewModel.compileBundleHtml()
        compilingInProgress = false
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Tool bar action triggers
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(luxurySurface)
                .border(1.dp, luxuryBorder)
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        compilingInProgress = true
                        scope.launch {
                            compiledHtml = viewModel.compileBundleHtml()
                            compilingInProgress = false
                            viewModel.addLog("info", "Live Sandbox bundle recompiled successfully.")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = neonEmerald),
                    modifier = Modifier.testTag("run_preview_btn")
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Run", tint = Color.Black, modifier = Modifier.size(16.dp))
                        Text("Run App", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }

                // Compile loader state
                if (compilingInProgress) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = neonBlue)
                }
            }

            // Dimension switcher triggers
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                arrayOf("MOBILE", "TABLET", "DESKTOP").forEach { mode ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (currentDeviceMode == mode) neonBlue.copy(0.2f) else Color.Transparent)
                            .border(1.dp, if (currentDeviceMode == mode) neonBlue else luxuryBorder)
                            .clickable { currentDeviceMode = mode }
                            .padding(6.dp)
                    ) {
                        Text(mode, fontSize = 10.sp, color = if (currentDeviceMode == mode) neonBlue else Color.LightGray)
                    }
                }
            }
        }

        // Adaptive WebView Preview Container
        Box(
            modifier = Modifier
                .weight(1.3f)
                .fillMaxWidth()
                .background(Color.DarkGray.copy(0.3f))
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            val previewWidth = when (currentDeviceMode) {
                "MOBILE" -> 340.dp
                "TABLET" -> 460.dp
                else -> Modifier.fillMaxWidth() // DESKTOP fills all
            }

            Box(
                modifier = Modifier
                    .run {
                        if (currentDeviceMode == "DESKTOP") fillMaxSize() else width(previewWidth as androidx.compose.ui.unit.Dp).fillMaxHeight()
                    }
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .border(3.dp, luxuryBorder)
            ) {
                if (compiledHtml.isNotEmpty() && !compilingInProgress) {
                    LivePreviewWebView(
                        htmlContent = compiledHtml,
                        onConsoleLog = { type, message -> viewModel.addLog(type, message) },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Web Simulator Ready. Hit Run to Compile.", color = Color.Black, fontSize = 13.sp)
                    }
                }
            }
        }

        // Live Debug Console Drawers
        Column(
            modifier = Modifier
                .weight(0.7f)
                .fillMaxWidth()
                .background(luxurySurface)
                .border(1.dp, luxuryBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("DEBUGGER TERMINAL", color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 1.sp)
                TextButton(onClick = { viewModel.clearConsole() }) {
                    Text("Clear Logs", color = Color.Red.copy(0.7f), fontSize = 11.sp)
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(consoleLogs) { log ->
                    val color = when (log.type) {
                        "error" -> Color(0xFFEF4444)
                        "warn" -> Color(0xFFF59E0B)
                        "success" -> Color(0xFF10B981)
                        else -> Color(0xFF38BDF8)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "[${log.type.uppercase()}]",
                            color = color,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = log.message,
                            color = Color.LightGray,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PackageManagerTab(
    viewModel: IdeViewModel,
    luxurySurface: Color,
    luxuryBorder: Color,
    neonAmber: Color
) {
    val installed = viewModel.getInstalledNpmPackages()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            Text("NPM PACKAGE MANAGER", color = neonAmber, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text("Install dependencies dynamically from public registries straight into your reactive index.html playground.", color = Color.Gray, fontSize = 12.sp)
        }

        // Active deps Card
        Card(
            colors = CardDefaults.cardColors(containerColor = luxurySurface),
            border = borderStroke(luxuryBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("DEPENDENCY VIEWER", color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                if (installed.isEmpty()) {
                    Text("No packages declared, project relies on native files.", color = Color.LightGray, fontSize = 13.sp)
                } else {
                    for (pkg in installed) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(pkg.name, color = neonAmber, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Version: ${pkg.version}", color = Color.Gray, fontSize = 11.sp)
                            }
                            IconButton(onClick = { viewModel.uninstallNpmPackage(pkg.name) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Uninstall", tint = Color.Red.copy(0.7f))
                            }
                        }
                    }
                }
            }
        }

        Text("AVAILABLE PACKAGES REGISTRY:", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)

        // Install registry list
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val registry = DependencyManager.AVAILABLE_PACKAGES
            items(registry) { pkg ->
                val isPresent = installed.any { it.name == pkg.name }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(luxurySurface)
                        .border(1.dp, luxuryBorder)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(pkg.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(pkg.description, color = Color.LightGray, fontSize = 11.sp)
                        Text("Stable: ${pkg.version}", color = Color.Gray, fontSize = 11.sp)
                    }
                    Button(
                        onClick = {
                            if (!isPresent) viewModel.installNpmPackage(pkg.name)
                            else viewModel.uninstallNpmPackage(pkg.name)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isPresent) Color.Gray else neonAmber
                        ),
                        modifier = Modifier.testTag("pkg_btn_${pkg.name}")
                    ) {
                        Text(if (isPresent) "Uninstall" else "Install", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun HyperAiTab(
    viewModel: IdeViewModel,
    aiLoading: Boolean,
    aiResponse: String,
    luxurySurface: Color,
    luxuryBorder: Color,
    neonPink: Color
) {
    var promptInput by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            Text("🔮 HYPER AI COMPANION", color = neonPink, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text("Automate bug fixing, components compilation, and full source code translations securely inside context scope.", color = Color.Gray, fontSize = 12.sp)
        }

        // Code assist quick buttons
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { viewModel.triggerAiInstruction("EXPLAIN") },
                colors = ButtonDefaults.buttonColors(containerColor = neonPink.copy(0.15f)),
                border = borderStroke(neonPink),
                modifier = Modifier.testTag("ai_explain_btn")
            ) {
                Text("Explain Code", color = neonPink, fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = { viewModel.triggerAiInstruction("OPTIMIZE") },
                colors = ButtonDefaults.buttonColors(containerColor = neonPink.copy(0.15f)),
                border = borderStroke(neonPink),
                modifier = Modifier.testTag("ai_optimize_btn")
            ) {
                Text("Optimize File", color = neonPink, fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = { viewModel.triggerAiInstruction("FIX_BUGS") },
                colors = ButtonDefaults.buttonColors(containerColor = neonPink.copy(0.15f)),
                border = borderStroke(neonPink)
            ) {
                Text("Auto-Fix Logs", color = neonPink, fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = { viewModel.triggerAiInstruction("JS_TO_TS") },
                colors = ButtonDefaults.buttonColors(containerColor = neonPink.copy(0.15f)),
                border = borderStroke(neonPink)
            ) {
                Text("JS -> TSX", color = neonPink, fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = { viewModel.triggerAiInstruction("HTML_TO_REACT") },
                colors = ButtonDefaults.buttonColors(containerColor = neonPink.copy(0.15f)),
                border = borderStroke(neonPink)
            ) {
                Text("HTML -> React App", color = neonPink, fontWeight = FontWeight.Bold)
            }
        }

        // Prompt input area
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = promptInput,
                onValueChange = { promptInput = it },
                label = { Text("Describe custom component to generate...") },
                modifier = Modifier.weight(1f).testTag("ai_chat_input"),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = neonPink)
            )
            IconButton(
                onClick = {
                    if (promptInput.isNotEmpty()) {
                        viewModel.triggerAiInstruction("GENERATE_COMPONENT", promptInput)
                        promptInput = ""
                    }
                },
                modifier = Modifier.testTag("ai_send_btn")
            ) {
                Icon(Icons.Default.Share, contentDescription = "Send prompt", tint = neonPink)
            }
        }

        // AI Response panel containing final output block
        Card(
            colors = CardDefaults.cardColors(containerColor = luxurySurface),
            border = borderStroke(luxuryBorder),
            modifier = Modifier.weight(1f).fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp)
            ) {
                if (aiLoading) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = neonPink)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Querying Hyper AI Engine...", color = Color.Gray, fontSize = 12.sp)
                    }
                } else {
                    Box(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        Text(
                            text = aiResponse.ifEmpty { "Hyper AI Consultation node awake. Choose an assistance routine above or type a prompt." },
                            style = TextStyle(
                                color = if (aiResponse.isEmpty()) Color.Gray else Color.White,
                                fontSize = 13.sp,
                                fontFamily = if (aiResponse.isEmpty()) FontFamily.SansSerif else FontFamily.Monospace
                            )
                        )
                    }
                }
            }
        }
    }
}

// --- STANDARD STYLES HELPER ---
private fun borderStroke(color: Color) = androidx.compose.foundation.BorderStroke(1.dp, color)

@SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
@Composable
fun LivePreviewWebView(
    htmlContent: String,
    onConsoleLog: (type: String, message: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            WebView(ctx).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
                settings.allowFileAccess = true

                val bridge = object {
                    @JavascriptInterface
                    fun log(msg: String) {
                        onConsoleLog("info", msg)
                    }
                    @JavascriptInterface
                    fun error(msg: String) {
                        onConsoleLog("error", msg)
                    }
                    @JavascriptInterface
                    fun warn(msg: String) {
                        onConsoleLog("warn", msg)
                    }
                }
                addJavascriptInterface(bridge, "AndroidBridge")
                webChromeClient = WebChromeClient()
            }
        },
        update = { webView ->
            webView.loadDataWithBaseURL(
                "https://localhost/",
                htmlContent,
                "text/html",
                "utf-8",
                null
            )
        }
    )
}
