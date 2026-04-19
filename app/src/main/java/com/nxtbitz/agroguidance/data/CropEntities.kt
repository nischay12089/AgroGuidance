package com.nxtbitz.agroguidance.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(tableName = "crops")
data class Crop(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String
)

@Entity(
    tableName = "crop_issues",
    foreignKeys = [
        ForeignKey(
            entity = Crop::class,
            parentColumns = ["id"],
            childColumns = ["cropId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("cropId")]
)
data class CropIssue(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val cropId: Int,
    val issueName: String,
    val solution: String
)
