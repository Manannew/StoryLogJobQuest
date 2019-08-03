package com.example.storylogquest.db

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.storylogquest.model.TopicModel
import androidx.room.Room


@Database(entities = [TopicModel::class],version = 1)
abstract class TopicRoom:RoomDatabase() {

    abstract fun topicDao(): TopicDao

    companion object{
        private var topicRoomInstance: TopicRoom? = null

        fun getDatabase(context: Context): TopicRoom? {
            if (topicRoomInstance == null) {
                synchronized(TopicRoom::class.java) {
                    if (topicRoomInstance == null) {
                        topicRoomInstance = Room.databaseBuilder(
                            context.applicationContext,
                            TopicRoom::class.java, "topic_database")
                            .build()
                    }
                }
            }
            return topicRoomInstance
        }
    }

}