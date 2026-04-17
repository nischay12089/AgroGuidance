package com.nxtbitz.agroguidance.data

import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoClient
import kotlinx.coroutines.flow.firstOrNull

class MongoRepository {
    private val connectionString = "mongodb+srv://tanishmhalsekar999_db_user:60FFpomMZXEOy7Dj@cluster0.ffgfoyu.mongodb.net/?appName=Cluster0"
    private val client = MongoClient.create(connectionString)
    private val database = client.getDatabase("agroguidance")
    private val usersCollection = database.getCollection<User>("users")

    suspend fun registerUser(user: User): Boolean {
        return try {
            val existingUser = usersCollection.find(Filters.eq("email", user.email)).firstOrNull()
            if (existingUser != null) return false
            
            usersCollection.insertOne(user)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun loginUser(email: String, password: String): User? {
        return try {
            usersCollection.find(
                Filters.and(
                    Filters.eq("email", email),
                    Filters.eq("password", password)
                )
            ).firstOrNull()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
