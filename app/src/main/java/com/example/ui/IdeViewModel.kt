package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

data class ConsoleLogItem(
    val type: String, // info, error, warn, success
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class TeamActivity(
    val author: String,
    val action: String,
    val timeAgo: String
)

class IdeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ProjectRepository
    private val aiService = AiService()

    // Workspace Project State
    private val _projects = MutableStateFlow<List<Project>>(emptyList())
    val projects: StateFlow<List<Project>> = _projects.asStateFlow()

    private val _currentProject = MutableStateFlow<Project?>(null)
    val currentProject: StateFlow<Project?> = _currentProject.asStateFlow()

    private val _projectFiles = MutableStateFlow<List<ProjectFile>>(emptyList())
    val projectFiles: StateFlow<List<ProjectFile>> = _projectFiles.asStateFlow()

    private val _selectedFile = MutableStateFlow<ProjectFile?>(null)
    val selectedFile: StateFlow<ProjectFile?> = _selectedFile.asStateFlow()

    private val _editorContent = MutableStateFlow("")
    val editorContent: StateFlow<String> = _editorContent.asStateFlow()

    private val _openTabs = MutableStateFlow<List<ProjectFile>>(emptyList())
    val openTabs: StateFlow<List<ProjectFile>> = _openTabs.asStateFlow()

    // Undo / Redo Buffer
    private val undoStack = mutableListOf<String>()
    private val redoStack = mutableListOf<String>()

    // Console Logging State
    private val _consoleLogs = MutableStateFlow<List<ConsoleLogItem>>(emptyList())
    val consoleLogs: StateFlow<List<ConsoleLogItem>> = _consoleLogs.asStateFlow()

    // Search and Replace
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _replaceQuery = MutableStateFlow("")
    val replaceQuery: StateFlow<String> = _replaceQuery.asStateFlow()

    // AI Companion State
    private val _aiLoading = MutableStateFlow(false)
    val aiLoading: StateFlow<Boolean> = _aiLoading.asStateFlow()

    private val _aiResponse = MutableStateFlow("")
    val aiResponse: StateFlow<String> = _aiResponse.asStateFlow()

    // Premium Subscription State ("Hyper Code Plus")
    private val _isPremium = MutableStateFlow(false)
    val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()

    // Real-time Collaboration Simulation Data
    private val _teamActivity = MutableStateFlow<List<TeamActivity>>(
        listOf(
            TeamActivity("Kawooya Raymond", "Configured SQLite DB triggers & Room schema", "2 mins ago"),
            TeamActivity("Tucci Cyber Bot", "Verified secure WebView container sandboxing", "10 mins ago"),
            TeamActivity("Tucci Partner", "Pushed React Babel dynamic preset updates", "1 hr ago")
        )
    )
    val teamActivity: StateFlow<List<TeamActivity>> = _teamActivity.asStateFlow()

    // Active Collaborators virtual online presence
    private val _onlineCollaborators = MutableStateFlow(listOf("Kawooya Raymond", "Tucci Dev 01"))
    val onlineCollaborators: StateFlow<List<String>> = _onlineCollaborators.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = ProjectRepository(database.projectDao())

        // Fetch projects on startup
        viewModelScope.launch {
            repository.allProjects.collect { list ->
                _projects.value = list
                // If there are no projects, automatically seed a demo one so the app opens with data!
                if (list.isEmpty()) {
                    createProject("Hyper Sandbox", "Default HTML5 and CSS playground.", "HTML")
                    createProject("React Tech Stack", "Modern React counter sandbox.", "REACT")
                } else if (_currentProject.value == null) {
                    selectProject(list.first())
                }
            }
        }
    }

    // --- Project Operations ---
    fun createProject(name: String, description: String, templateType: String) {
        viewModelScope.launch {
            val projectId = repository.createProject(name, description, templateType)
            val updatedList = repository.getFilesByProjectDirect(projectId)
            val proj = repository.getProjectById(projectId)
            if (proj != null) {
                selectProject(proj)
            }
        }
    }

    fun deleteProject(project: Project) {
        viewModelScope.launch {
            repository.deleteProject(project)
            if (_currentProject.value?.id == project.id) {
                _currentProject.value = null
                _projectFiles.value = emptyList()
                _selectedFile.value = null
                _openTabs.value = emptyList()
            }
        }
    }

    fun selectProject(project: Project) {
        _currentProject.value = project
        _openTabs.value = emptyList()
        _selectedFile.value = null
        _editorContent.value = ""
        clearConsole()

        viewModelScope.launch {
            repository.getFilesByProject(project.id).collect { files ->
                _projectFiles.value = files
                // Automatically open files that are default entry points (index.html, App.jsx, App.tsx, etc.)
                if (_selectedFile.value == null && files.isNotEmpty()) {
                    val entryPoint = files.firstOrNull { it.filePath == "index.html" } 
                        ?: files.firstOrNull { !it.isFolder }
                    if (entryPoint != null) {
                        selectFile(entryPoint)
                    }
                }
            }
        }
    }

    fun togglePremium() {
        _isPremium.value = !_isPremium.value
        addLog("success", "Premium Mode ${if (_isPremium.value) "ENABLED" else "DISABLED"}! Welcome back to Hyper Code Plus.")
    }

    // --- File Operations ---
    fun selectFile(file: ProjectFile) {
        if (file.isFolder) return
        _selectedFile.value = file
        _editorContent.value = file.content
        
        // Add to tabs if not already open
        val currentTabs = _openTabs.value.toMutableList()
        if (!currentTabs.any { it.id == file.id }) {
            currentTabs.add(file)
            _openTabs.value = currentTabs
        }
        
        // Clear history for undo/redo on tab switch
        undoStack.clear()
        redoStack.clear()
    }

    fun updateSearchAndReplace(search: String, replace: String) {
        _searchQuery.value = search
        _replaceQuery.value = replace
    }

    fun executeReplaceInEditor() {
        val search = _searchQuery.value
        val replace = _replaceQuery.value
        if (search.isEmpty()) return
        val current = _editorContent.value
        if (current.contains(search)) {
            val updated = current.replace(search, replace)
            updateEditorContent(updated)
            addLog("info", "Replaced '$search' with '$replace' in active editor.")
        }
    }

    fun closeTab(file: ProjectFile) {
        val currentTabs = _openTabs.value.toMutableList()
        currentTabs.removeAll { it.id == file.id }
        _openTabs.value = currentTabs

        if (_selectedFile.value?.id == file.id) {
            if (currentTabs.isNotEmpty()) {
                selectFile(currentTabs.last())
            } else {
                _selectedFile.value = null
                _editorContent.value = ""
            }
        }
    }

    fun updateEditorContent(newContent: String) {
        // Push current content to undo stack
        if (undoStack.isEmpty() || undoStack.last() != _editorContent.value) {
            undoStack.add(_editorContent.value)
            if (undoStack.size > 50) {
                undoStack.removeAt(0)
            }
        }
        _editorContent.value = newContent
        redoStack.clear()
        
        // Auto-save: update active file buffer internally
        val active = _selectedFile.value
        val proj = _currentProject.value
        if (active != null && proj != null) {
            viewModelScope.launch {
                repository.updateFileContent(proj.id, active.filePath, newContent)
            }
        }
    }

    fun triggerUndo() {
        if (undoStack.isNotEmpty()) {
            val prev = undoStack.removeAt(undoStack.size - 1)
            redoStack.add(_editorContent.value)
            _editorContent.value = prev
            
            val active = _selectedFile.value
            val proj = _currentProject.value
            if (active != null && proj != null) {
                viewModelScope.launch {
                    repository.updateFileContent(proj.id, active.filePath, prev)
                }
            }
        }
    }

    fun triggerRedo() {
        if (redoStack.isNotEmpty()) {
            val next = redoStack.removeAt(redoStack.size - 1)
            undoStack.add(_editorContent.value)
            _editorContent.value = next

            val active = _selectedFile.value
            val proj = _currentProject.value
            if (active != null && proj != null) {
                viewModelScope.launch {
                    repository.updateFileContent(proj.id, active.filePath, next)
                }
            }
        }
    }

    fun createNewFileInProject(path: String, isFolder: Boolean) {
        val proj = _currentProject.value ?: return
        viewModelScope.launch {
            repository.createNewFile(proj.id, path, isFolder)
            addLog("info", "Created new file: $path")
        }
    }

    fun deleteFileInProject(file: ProjectFile) {
        viewModelScope.launch {
            repository.deleteFileById(file.id)
            closeTab(file)
            addLog("warn", "Deleted file: ${file.filePath}")
        }
    }

    // --- Package Manager Operations (npm Simulation) ---
    fun installNpmPackage(packageName: String) {
        val proj = _currentProject.value ?: return
        viewModelScope.launch {
            // Locate or create package.json in the current project
            val packageJson = _projectFiles.value.firstOrNull { it.filePath == "package.json" }
            val existingContent = packageJson?.content ?: """{
  "name": "${proj.name.lowercase().replace(" ", "-")}",
  "dependencies": {}
}"""
            val updatedContent = DependencyManager.addDependency(existingContent, packageName)
            repository.updateFileContent(proj.id, "package.json", updatedContent)
            addLog("success", "Successfully installed npm package: $packageName")
        }
    }

    fun uninstallNpmPackage(packageName: String) {
        val proj = _currentProject.value ?: return
        viewModelScope.launch {
            val packageJson = _projectFiles.value.firstOrNull { it.filePath == "package.json" } ?: return@launch
            val updatedContent = DependencyManager.removeDependency(packageJson.content, packageName)
            repository.updateFileContent(proj.id, "package.json", updatedContent)
            addLog("warn", "Uninstalled npm package: $packageName")
        }
    }

    fun getInstalledNpmPackages(): List<DependencyManager.PackageInfo> {
        val packageJson = _projectFiles.value.firstOrNull { it.filePath == "package.json" } ?: return emptyList()
        return DependencyManager.getInstalledDependencies(packageJson.content)
    }

    // --- Live Webview Engine Pre-Compilation Builder ---
    suspend fun compileBundleHtml(): String = withContext(Dispatchers.IO) {
        val files = _projectFiles.value
        val indexHtml = files.firstOrNull { it.filePath == "index.html" }?.content 
            ?: """<!DOCTYPE html><html><body><h3>Entrypoint index.html not found!</h3></body></html>"""
        
        val cssContent = files.firstOrNull { it.filePath == "style.css" }?.content ?: ""
        val jsContent = files.firstOrNull { it.filePath == "script.js" }?.content ?: ""

        val packageJson = files.firstOrNull { it.filePath == "package.json" }?.content ?: ""
        val packageScriptInjections = DependencyManager.generateCdnInjections(packageJson)

        var buildHtml = indexHtml

        // 1. Intercept console inside HTML with a Javascript bridge
        val consoleLoggerInjection = """
        <script>
            (function() {
                const _log = console.log;
                const _error = console.error;
                const _warn = console.warn;
                
                console.log = function() {
                    _log.apply(console, arguments);
                    const msg = Array.from(arguments).map(v => typeof v === 'object' ? JSON.stringify(v) : v).join(' ');
                    if (window.AndroidBridge) {
                        window.AndroidBridge.log(msg);
                    }
                };
                console.error = function() {
                    _error.apply(console, arguments);
                    const msg = Array.from(arguments).map(v => typeof v === 'object' ? JSON.stringify(v) : v).join(' ');
                    if (window.AndroidBridge) {
                        window.AndroidBridge.error(msg);
                    }
                };
                console.warn = function() {
                    _warn.apply(console, arguments);
                    const msg = Array.from(arguments).map(v => typeof v === 'object' ? JSON.stringify(v) : v).join(' ');
                    if (window.AndroidBridge) {
                        window.AndroidBridge.warn(msg);
                    }
                };
                
                window.onerror = function(message, source, lineno, colno, error) {
                    if (window.AndroidBridge) {
                        window.AndroidBridge.error(message + " (Line " + lineno + ")");
                    }
                    return false;
                };
            })();
        </script>
        """.trimIndent()

        // Insert console logger immediately inside head or body
        buildHtml = if (buildHtml.contains("<head>")) {
            buildHtml.replace("<head>", "<head>\n$consoleLoggerInjection\n$packageScriptInjections")
        } else {
            "<html><head>\n$consoleLoggerInjection\n$packageScriptInjections</head>\n$buildHtml"
        }

        // 2. Inline local style.css
        if (buildHtml.contains("<link rel=\"stylesheet\" href=\"style.css\">")) {
            buildHtml = buildHtml.replace(
                "<link rel=\"stylesheet\" href=\"style.css\">",
                "<style>\n$cssContent\n</style>"
            )
        } else if (cssContent.isNotEmpty() && !buildHtml.contains("style.css")) {
            buildHtml = buildHtml.replace("</body>", "<style>\n$cssContent\n</style>\n</body>")
        }

        // 3. Handle Script compilation bindings
        // Strip imports and exports from standard reactive classes to allow pure BabelStandalone global scripts execution
        fun cleanModuleImports(raw: String): String {
            return raw.lines().filter { line ->
                val trimmed = line.trim()
                !trimmed.startsWith("import ") && !trimmed.startsWith("export default") && !trimmed.startsWith("export ")
            }.joinToString("\n")
        }

        val reactComponentsInlined = StringBuilder()
        for (f in files) {
            if (f.filePath.endsWith(".jsx") || f.filePath.endsWith(".tsx")) {
                reactComponentsInlined.append("// --- File: ${f.filePath} ---\n")
                reactComponentsInlined.append(cleanModuleImports(f.content))
                reactComponentsInlined.append("\n\n")
            }
        }

        val typeScriptInlined = StringBuilder()
        for (f in files) {
            if (f.filePath.endsWith(".ts") && !f.filePath.endsWith(".d.ts")) {
                typeScriptInlined.append("// --- TypeScript Module: ${f.filePath} ---\n")
                typeScriptInlined.append(cleanModuleImports(f.content))
                typeScriptInlined.append("\n\n")
            }
        }

        // Inject compiled React codes if the template requires it, replacing the placeholder
        if (buildHtml.contains("/*FILE_INJECTION_PLACEHOLDER*/")) {
            val allInlines = reactComponentsInlined.toString() + "\n" + typeScriptInlined.toString()
            buildHtml = buildHtml.replace("/*FILE_INJECTION_PLACEHOLDER*/", allInlines)
        }

        // 4. Inline local script.js
        if (buildHtml.contains("<script src=\"script.js\"></script>")) {
            buildHtml = buildHtml.replace(
                "<script src=\"script.js\"></script>",
                "<script>\n$jsContent\n</script>"
            )
        } else if (jsContent.isNotEmpty()) {
            buildHtml = buildHtml.replace("</body>", "<script>\n$jsContent\n</script>\n</body>")
        }

        buildHtml
    }

    // --- Console Logger API callbacks ---
    fun addLog(type: String, message: String) {
        val current = _consoleLogs.value.toMutableList()
        current.add(ConsoleLogItem(type, message))
        if (current.size > 100) {
            current.removeAt(0)
        }
        _consoleLogs.value = current
    }

    fun clearConsole() {
        _consoleLogs.value = emptyList()
    }

    // --- AI Companion Services (Hyper AI) ---
    fun triggerAiInstruction(instructionType: String, arg: String = "") {
        val activeFile = _selectedFile.value ?: return
        val code = _editorContent.value

        _aiLoading.value = true
        _aiResponse.value = ""

        viewModelScope.launch {
            val response = when (instructionType) {
                "EXPLAIN" -> aiService.explainCode(code, activeFile.filePath)
                "OPTIMIZE" -> {
                    val optimized = aiService.writeCode("Optimize this source code for latency and rendering smoothness", code, activeFile.language)
                    _aiResponse.value = "Code Optimized Successfully! Appending..."
                    updateEditorContent(optimized)
                    "Optimized version generated and loaded into the active editor tab."
                }
                "FIX_BUGS" -> {
                    val errorsCombined = _consoleLogs.value.filter { it.type == "error" }.joinToString("\n") { it.message }
                    val fixed = aiService.checkErrorsAndFix(code, activeFile.filePath, errorsCombined.ifEmpty { "General debugging review" })
                    _aiResponse.value = "Bugs debugged and file updated!"
                    updateEditorContent(fixed)
                    "Review completed. Generated dynamic fixes and synchronized repository."
                }
                "GENERATE_COMPONENT" -> {
                    val component = aiService.writeCode("Generate a beautiful, responsive visual CSS/HTML component or React structure for: $arg", "", activeFile.language)
                    val before = _editorContent.value
                    updateEditorContent(before + "\n\n" + component)
                    "New component successfully appended to the active file buffer."
                }
                "JS_TO_TS" -> {
                    if (activeFile.fileExtension == "js" || activeFile.fileExtension == "jsx") {
                        val tsCode = aiService.convertJsToTs(code)
                        val newPath = activeFile.filePath.replace(".js", ".ts").replace(".jsx", ".tsx")
                        createNewFileInProject(newPath, false)
                        viewModelScope.launch {
                            repository.updateFileContent(_currentProject.value!!.id, newPath, tsCode)
                        }
                        "Converted JavaScript to TypeScript and saved as: $newPath"
                    } else {
                        "File is already in TypeScript or not applicable."
                    }
                }
                "HTML_TO_REACT" -> {
                    if (activeFile.fileExtension == "html") {
                        val reactCode = aiService.convertHtmlToReact(code)
                        val newPath = "src/App.jsx"
                        createNewFileInProject(newPath, false)
                        viewModelScope.launch {
                            repository.updateFileContent(_currentProject.value!!.id, newPath, reactCode)
                        }
                        "Successfully transpiled Static HTML into stateful JSX component at: $newPath"
                    } else {
                        "Active target in focus must be an HTML file to execute transpilation hooks."
                    }
                }
                "CHAT" -> {
                    aiService.writeCode(arg, code, activeFile.language)
                }
                else -> "Selection method context not recognized."
            }

            _aiResponse.value = response
            _aiLoading.value = false
            addLog("info", "Hyper AI Engine completed execution.")
        }
    }
}
