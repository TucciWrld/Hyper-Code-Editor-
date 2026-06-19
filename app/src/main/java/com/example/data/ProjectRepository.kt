package com.example.data

import kotlinx.coroutines.flow.Flow
import java.util.UUID

class ProjectRepository(private val projectDao: ProjectDao) {

    val allProjects: Flow<List<Project>> = projectDao.getAllProjects()

    fun getProjectByIdFlow(projectId: Long): Flow<Project?> {
        return projectDao.getProjectByIdFlow(projectId)
    }

    suspend fun getProjectById(projectId: Long): Project? {
        return projectDao.getProjectById(projectId)
    }

    fun getFilesByProject(projectId: Long): Flow<List<ProjectFile>> {
        return projectDao.getFilesByProject(projectId)
    }

    suspend fun getFilesByProjectDirect(projectId: Long): List<ProjectFile> {
        return projectDao.getFilesByProjectDirect(projectId)
    }

    suspend fun getFileById(id: String): ProjectFile? {
        return projectDao.getFileById(id)
    }

    suspend fun createProject(name: String, description: String, templateType: String): Long {
        val project = Project(
            name = name,
            description = description,
            templateType = templateType
        )
        val projectId = projectDao.insertProject(project)

        // Prepopulate default files for templates
        val defaultFiles = TemplateProvider.getDefaultFiles(projectId, templateType)
        for (file in defaultFiles) {
            projectDao.insertFile(file)
        }

        return projectId
    }

    suspend fun updateProject(project: Project) {
        projectDao.updateProject(project)
    }

    suspend fun deleteProject(project: Project) {
        projectDao.deleteProject(project)
    }

    suspend fun insertFile(file: ProjectFile) {
        projectDao.insertFile(file)
    }

    suspend fun updateFileContent(projectId: Long, filePath: String, newContent: String) {
        val fileId = "$projectId:$filePath"
        val existingFile = projectDao.getFileById(fileId)
        if (existingFile != null) {
            projectDao.insertFile(existingFile.copy(content = newContent, updatedAt = System.currentTimeMillis()))
        } else {
            val extension = filePath.substringAfterLast('.', "")
            val fileObj = ProjectFile(
                id = fileId,
                projectId = projectId,
                filePath = filePath,
                content = newContent,
                isFolder = false,
                language = extension,
                updatedAt = System.currentTimeMillis()
            )
            projectDao.insertFile(fileObj)
        }
    }

    suspend fun createNewFile(projectId: Long, filePath: String, isFolder: Boolean) {
        val cleanPath = filePath.trimStart('/')
        val fileId = "$projectId:$cleanPath"
        val extension = cleanPath.substringAfterLast('.', "")
        val defaultContent = when (extension) {
            "html" -> "<!DOCTYPE html>\n<html>\n<head>\n  <title>New File</title>\n</head>\n<body>\n\n</body>\n</html>"
            "css" -> "/* Style Sheet */\n"
            "js", "jsx" -> "// JavaScript File\n"
            "ts", "tsx" -> "// TypeScript File\n"
            "json" -> "{\n  \n}"
            "md" -> "# New Document\n"
            else -> ""
        }
        val fileObj = ProjectFile(
            id = fileId,
            projectId = projectId,
            filePath = cleanPath,
            content = if (isFolder) "" else defaultContent,
            isFolder = isFolder,
            language = extension,
            updatedAt = System.currentTimeMillis()
        )
        projectDao.insertFile(fileObj)
    }

    suspend fun deleteFileById(id: String) {
        projectDao.deleteFileById(id)
    }
}
