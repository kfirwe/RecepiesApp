package com.example.finalproject.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.finalproject.database.entities.UserEntity

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: UserEntity)

    @Query("SELECT * FROM user WHERE id = :userId")
    fun getUserById(userId: String): UserEntity?

    @Query("DELETE FROM user")
    fun clearUsers()

    @Query("DELETE FROM user WHERE id = :userId")
    fun deleteUserById(userId: String)

    @Query("SELECT * FROM user WHERE email = :email")
    fun getUserByEmail(email: String): UserEntity?
}



