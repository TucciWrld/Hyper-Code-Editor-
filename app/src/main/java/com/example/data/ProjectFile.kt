package com.example.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "project_files",
    foreignKeys = [
        ForeignKey(
            entity = Project::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["projectId"])]
)
data class ProjectFile(
    @PrimaryKey val id: String, // Format: "$projectId:$filePath"
    val projectId: Long,
    val filePath: String, // E.g., "index.html", "style.css", "script.js", "src/App.tsx"
    val content: String,
    val isFolder: Boolean = false,
    val language: String = "", // html, css, javascript, typescript, json, markdown
    val updatedAt: Long = System.currentTimeMillis()
) {
    val fileName: String
        get() = filePath.substringAfterLast('/')

    val fileExtension: String
        get() = filePath.substringAfterLast('.', "")
}
