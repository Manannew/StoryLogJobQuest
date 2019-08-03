package com.example.storylogquest.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.storylogquest.Constant
import com.example.storylogquest.MainApplication
import com.example.storylogquest.viewmodel.MainViewModel
import com.example.storylogquest.R
import com.example.storylogquest.adapter.CustomItemTouchHelper
import com.example.storylogquest.adapter.TopicAdapter
import com.example.storylogquest.databinding.ActivityMainBinding
import com.example.storylogquest.model.TopicModel
import com.google.gson.Gson
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(),TopicAdapter.OnItemClick {
    private lateinit var binding: ActivityMainBinding
    private val listTopic = ArrayList<TopicModel>()
    private lateinit var topicAdapter: TopicAdapter
    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        initView()
    }

    private fun initView() {
        initRecyclerview()

        binding.rvTitle.adapter = topicAdapter
        binding.rvTitle.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        viewModel.getListTopic().observe(this,androidx.lifecycle.Observer {
            listTopic.clear()
            listTopic.addAll(it)
            topicAdapter.setCopyList(listTopic)
        })

        binding.fab.setOnClickListener {
            val intent = Intent(this, NewTopicActivity::class.java)
            startActivityForResult(intent, Constant.REQUEST_NEW_TOPIC)
        }

        binding.btnFilter.setOnClickListener {
            showFilterDialog()
        }


    }

    private fun initRecyclerview() {
        topicAdapter = TopicAdapter(this, listTopic)
        topicAdapter.mListener = this

        val callback = CustomItemTouchHelper(topicAdapter)
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.rvTitle)
    }

    private fun showFilterDialog() {
        val tags = arrayOf("Red", "Green", "Blue","None","All")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Filter Tag")
        builder.setItems(tags) { dialog, which ->
            val item = tags[which]
            if(item == "None"){
                topicAdapter.filter.filter("")
            }else{
                topicAdapter.filter.filter(item)

            }
            dialog.dismiss()
        }
        builder.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Constant.REQUEST_NEW_TOPIC -> {
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(MainApplication.applicationContext(),resources.getString(R.string.text_created),Toast.LENGTH_SHORT).show()
                    val title = data?.getStringExtra(Constant.TITLE)!!
                    val tag = data.getStringExtra(Constant.TAG)!!
                    val imgUrl = data.getStringExtra(Constant.IMG_URL)!!
                    viewModel.insert(TopicModel(title, tag, imgUrl))

                }
            }
            Constant.REQUEST_EDIT ->{
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(MainApplication.applicationContext(),resources.getString(R.string.text_updated),Toast.LENGTH_SHORT).show()
                    val id = data?.getIntExtra(Constant.ID,0)!!
                    val title = data.getStringExtra(Constant.TITLE)!!
                    val tag = data.getStringExtra(Constant.TAG)!!
                    val imgUrl = data.getStringExtra(Constant.IMG_URL)!!
                    val editTopic = TopicModel(title, tag, imgUrl)
                    editTopic.id = id
                    viewModel.upDateTopic(editTopic)

                }
            }

        }
    }

    override fun onItemClick(topic: TopicModel) {
        val jsonString = Gson().toJson(topic)
        val intent = Intent(this,NewTopicActivity::class.java)
        intent.action = Constant.ACTION_EDIT
        intent.putExtra(Constant.TOPIC_STRING,jsonString)
        startActivityForResult(intent,Constant.REQUEST_EDIT)
    }

    override fun onItemDeleted(id: Int) {
        viewModel.deleteTopic(id)
        Toast.makeText(MainApplication.applicationContext(),resources.getString(R.string.text_deleted),Toast.LENGTH_SHORT).show()
    }
}
