package com.example.storylogquest.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "topic_table")
data class TopicModel (
    val title:String,
    val tag:String,
    val imageUrl:String
){
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}