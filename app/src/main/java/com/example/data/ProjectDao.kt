package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects ORDER BY updatedAt DESC")
    fun getAllProjects(): Flow<List<Project>>

    @Query("SELECT * FROM projects WHERE id = :id")
    suspend fun getProjectById(id: Long): Project?

    @Query("SELECT * FROM projects WHERE id = :id")
    fun getProjectByIdFlow(id: Long): Flow<Project?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: Project): Long

    @Update
    suspend fun updateProject(project: Project)

    @Delete
    suspend fun deleteProject(project: Project)

    // Files operations
    @Query("SELECT * FROM project_files WHERE projectId = :projectId ORDER BY isFolder DESC, filePath ASC")
    fun getFilesByProject(projectId: Long): Flow<List<ProjectFile>>

    @Query("SELECT * FROM project_files WHERE projectId = :projectId ORDER BY isFolder DESC, filePath ASC")
    suspend fun getFilesByProjectDirect(projectId: Long): List<ProjectFile>

    @Query("SELECT * FROM project_files WHERE id = :id")
    suspend fun getFileById(id: String): ProjectFile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFile(file: ProjectFile)

    @Update
    suspend fun updateFile(file: ProjectFile)

    @Delete
    suspend fun deleteFile(file: ProjectFile)

    @Query("DELETE FROM project_files WHERE id = :id")
    suspend fun deleteFileById(id: String)
}
