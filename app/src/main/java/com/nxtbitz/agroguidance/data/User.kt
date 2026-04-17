package com.nxtbitz.agroguidance.data

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class User(
    @BsonId
    val id: ObjectId = ObjectId(),
    val email: String = "",
    val password: String = "" // In a real app, always hash passwords!
)
