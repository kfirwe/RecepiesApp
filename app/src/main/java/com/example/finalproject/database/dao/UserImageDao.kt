package com.example.finalproject.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.finalproject.database.entities.UserImage

@Dao
interface UserImageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUserImage(userImage: UserImage)

    @Query("SELECT * FROM user_images WHERE userKey = :userKey LIMIT 1")
    fun getUserImage(userKey: String): UserImage?
}


