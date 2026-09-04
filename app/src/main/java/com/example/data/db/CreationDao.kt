package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CreationItem
import com.example.data.model.MediaType
import kotlinx.coroutines.flow.Flow

@Dao
interface CreationDao {
    @Query("SELECT * FROM creations ORDER BY timestamp DESC")
    fun getAllCreations(): Flow<List<CreationItem>>

    @Query("SELECT * FROM creations WHERE mediaType = :type ORDER BY timestamp DESC")
    fun getCreationsByType(type: MediaType): Flow<List<CreationItem>>

    @Query("SELECT * FROM creations WHERE id = :id LIMIT 1")
    suspend fun getCreationById(id: Long): CreationItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCreation(creation: CreationItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(creations: List<CreationItem>)

    @Update
    suspend fun updateCreation(creation: CreationItem)

    @Query("DELETE FROM creations WHERE id = :id")
    suspend fun deleteCreationById(id: Long)

    @Query("SELECT COUNT(*) FROM creations")
    suspend fun getCreationCount(): Int
}
