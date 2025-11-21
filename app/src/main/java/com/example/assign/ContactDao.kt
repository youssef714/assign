package com.example.assignment

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ContactDao {

    @Insert
    suspend fun insert(contact: Contact)

    @Query("SELECT * FROM contacts")
    suspend fun getAllContacts(): List<Contact>

    @Query("SELECT DISTINCT category FROM contacts")
    suspend fun getCategories(): List<String>

    @Query("SELECT * FROM contacts WHERE category = :cat")
    suspend fun getContactsByCategory(cat: String): List<Contact>
}
