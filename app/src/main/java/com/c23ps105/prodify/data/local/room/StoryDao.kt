package com.c23ps105.prodify.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.c23ps105.prodify.data.local.entity.StoryEntity

@Dao
interface StoryDao {
    @Query("SELECT * FROM stories ORDER BY createdAt DESC")
    fun getStories(): LiveData<List<StoryEntity>>

    @Query("SELECT * FROM stories WHERE id = :id")
    fun getDetailStories(id: String): LiveData<List<StoryEntity>>

    @Insert
    fun insertStory(story: List<StoryEntity>)

    @Query("DELETE FROM stories")
    fun deleteAll()
}