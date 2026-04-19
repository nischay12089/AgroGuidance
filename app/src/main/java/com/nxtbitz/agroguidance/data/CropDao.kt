package com.nxtbitz.agroguidance.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CropDao {
    @Query("SELECT * FROM crops")
    fun getAllCrops(): Flow<List<Crop>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrop(crop: Crop): Long

    @Query("SELECT * FROM crop_issues WHERE cropId = :cropId")
    fun getIssuesForCrop(cropId: Int): Flow<List<CropIssue>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIssue(issue: CropIssue): Long

    @Transaction
    @Query("SELECT * FROM crops")
    fun getCropsWithIssues(): Flow<List<CropWithIssues>>
}

data class CropWithIssues(
    @Embedded val crop: Crop,
    @Relation(
        parentColumn = "id",
        entityColumn = "cropId"
    )
    val issues: List<CropIssue>
)
