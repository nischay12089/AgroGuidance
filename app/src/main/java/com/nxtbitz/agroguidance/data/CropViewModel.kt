package com.nxtbitz.agroguidance.data

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CropViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: CropRepository
    val allCrops: Flow<List<Crop>>
    val cropsWithIssues: Flow<List<CropWithIssues>>

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing

    init {
        val cropDao = AppDatabase.getDatabase(application).cropDao()
        repository = CropRepository(cropDao)
        allCrops = repository.allCrops
        cropsWithIssues = repository.cropsWithIssues
    }

    fun syncFromFirebase(onComplete: (Boolean) -> Unit = {}) {
        if (_isSyncing.value) return
        
        _isSyncing.value = true
        val database = FirebaseDatabase.getInstance()
        val ref = database.reference
        
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                viewModelScope.launch {
                    try {
                        for (cropSnapshot in snapshot.children) {
                            val firebaseCropName = cropSnapshot.key ?: continue
                            val cropId = repository.insertCrop(Crop(name = firebaseCropName))
                            
                            for (diseaseSnapshot in cropSnapshot.children) {
                                val firebaseDiseaseName = diseaseSnapshot.key ?: continue
                                val firebaseSolution = diseaseSnapshot.getValue(String::class.java) ?: ""
                                repository.insertIssue(
                                    CropIssue(
                                        cropId = cropId.toInt(),
                                        issueName = firebaseDiseaseName,
                                        solution = firebaseSolution
                                    )
                                )
                            }
                        }
                        _isSyncing.value = false
                        onComplete(true)
                    } catch (e: Exception) {
                        Log.e("CropViewModel", "Sync failed", e)
                        _isSyncing.value = false
                        onComplete(false)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("CropViewModel", "Firebase Error: ${error.message}")
                _isSyncing.value = false
                onComplete(false)
            }
        })
    }

    fun addCrop(name: String) {
        viewModelScope.launch {
            repository.insertCrop(Crop(name = name))
        }
    }

    suspend fun addCropSync(name: String): Long {
        return repository.insertCrop(Crop(name = name))
    }

    fun addIssue(cropId: Int, issueName: String, solution: String) {
        viewModelScope.launch {
            repository.insertIssue(CropIssue(cropId = cropId, issueName = issueName, solution = solution))
        }
    }
}
