package com.nxtbitz.agroguidance.data

import kotlinx.coroutines.flow.Flow

class CropRepository(private val cropDao: CropDao) {
    val allCrops: Flow<List<Crop>> = cropDao.getAllCrops()
    val cropsWithIssues: Flow<List<CropWithIssues>> = cropDao.getCropsWithIssues()

    suspend fun insertCrop(crop: Crop): Long {
        return cropDao.insertCrop(crop)
    }

    suspend fun insertIssue(issue: CropIssue) {
        cropDao.insertIssue(issue)
    }

    fun getIssuesForCrop(cropId: Int): Flow<List<CropIssue>> {
        return cropDao.getIssuesForCrop(cropId)
    }
}
