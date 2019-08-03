package com.example.storylogquest.viewmodel

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.storylogquest.db.TopicDao
import com.example.storylogquest.db.TopicRoom
import com.example.storylogquest.model.TopicModel

class MainViewModel(application:Application):AndroidViewModel(application) {

    private var topicDao: TopicDao

    private var listTopic:LiveData<List<TopicModel>>

    init {
        val database:TopicRoom  = TopicRoom.getDatabase(
            application.applicationContext
        )!!
        topicDao = database.topicDao()
        listTopic = topicDao.getListTopic()
    }

    fun insert(topic:TopicModel){
        InsertTopic(topicDao).execute(topic)
    }

    fun getListTopic():LiveData<List<TopicModel>>{
        return listTopic
    }

    fun upDateTopic(topic:TopicModel){
        UpdateTopic(topicDao).execute(topic)
    }

    fun deleteTopic(id:Int){
        DeleteTopic(topicDao).execute(id)
    }


    class InsertTopic(private val topicDao:TopicDao):AsyncTask<TopicModel,Void,Void>(){
        override fun doInBackground(vararg p0: TopicModel?): Void? {
            topicDao.insert(p0[0]!!)
            return null
        }

    }

    class UpdateTopic(private val topicDao:TopicDao):AsyncTask<TopicModel,Void,Void>(){
        override fun doInBackground(vararg p0: TopicModel?): Void? {
            val item = p0[0]!!
            topicDao.updateTopic(item.id,item.title,item.tag,item.imageUrl)
            return null
        }

    }

    class DeleteTopic(private val topicDao:TopicDao):AsyncTask<Int,Void,Void>(){
        override fun doInBackground(vararg p0: Int?): Void? {
            val id = p0[0]!!
            topicDao.deleteTopic(id)
            return null
        }

    }
}