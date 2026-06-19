package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class Project(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    val templateType: String, // HTML, REACT, TS, REACT_TS, LANDING, PORTFOLIO, BLOG, DASHBOARD, TODO_APP, CALCULATOR
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isPlus: Boolean = false,
    val author: String = "Tucci Cyber Nation & Kawooya Raymond"
) {
    val templateLabel: String
        get() = when (templateType) {
            "HTML" -> "HTML5 Website"
            "REACT" -> "React App (JSX)"
            "TS" -> "TypeScript Console"
            "REACT_TS" -> "React TypeScript (TSX)"
            "LANDING" -> "Landing Page Template"
            "PORTFOLIO" -> "Portfolio Site"
            "BLOG" -> "Blog Website"
            "DASHBOARD" -> "Admin Dashboard"
            "TODO_APP" -> "Todo Web Application"
            "CALCULATOR" -> "Calculator Web Application"
            else -> "Web Project"
        }
}
