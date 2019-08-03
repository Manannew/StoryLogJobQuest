package com.example.storylogquest.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.storylogquest.model.TopicModel

@Dao
interface TopicDao {

    @Insert
    fun insert(topicModel : TopicModel)

    @Query("SELECT * FROM topic_table")
    fun getListTopic():LiveData<List<TopicModel>>

    @Query("UPDATE topic_table SET title=:title,tag=:tag,imageUrl=:imageUrl WHERE id=:id")
    fun updateTopic(id:Int,title:String,tag:String,imageUrl:String)

    @Query("DELETE FROM topic_table  WHERE id=:id")
    fun deleteTopic(id:Int)

}