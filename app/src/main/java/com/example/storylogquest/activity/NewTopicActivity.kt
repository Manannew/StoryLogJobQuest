package com.example.storylogquest.activity

import android.app.Activity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.storylogquest.R
import com.example.storylogquest.databinding.ActivityNewTopicBinding
import android.content.Intent
import android.net.Uri
import com.bumptech.glide.Glide
import com.example.storylogquest.Constant
import androidx.appcompat.app.AlertDialog
import com.example.storylogquest.model.TopicModel
import com.google.gson.Gson


class NewTopicActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewTopicBinding
    private var title = ""
    private var tag = ""
    private var imageUrl = ""
    private var id = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_topic)
        val actionbarTitle = if (intent.action == Constant.ACTION_EDIT) resources.getString(R.string.Edit_add_topic)
                else resources.getString(R.string.title_add_topic)

        supportActionBar?.title = actionbarTitle

        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if(savedInstanceState != null){
            tag = savedInstanceState.getString(Constant.TAG)!!
            imageUrl = savedInstanceState.getString(Constant.IMG_URL)!!
            Glide.with(this).load(imageUrl).into(binding.image)
        }

        if(intent.action == Constant.ACTION_EDIT){
            val jsonString = intent.getStringExtra(Constant.TOPIC_STRING)
            val editTopic = Gson().fromJson(jsonString,TopicModel::class.java)
            initViewEdit(editTopic)
        }

        initView()

    }


    private fun initViewEdit(editTopic: TopicModel) {
        binding.btnCreate.text = resources.getString(R.string.btn_update)
        binding.edTitle.setText(editTopic.title)
        binding.edTag.setText(editTopic.tag)
        Glide.with(this).load(Uri.parse(editTopic.imageUrl)).into(binding.image)
        id = editTopic.id
    }



    private fun initView() {
        binding.edTag.setOnClickListener {
            showDialogChooser()
        }
        binding.btnAddImage.setOnClickListener {
            openGallery()
        }

        binding.btnDeleteImage.setOnClickListener {
            deleteImage()
        }

        binding.btnCreate.setOnClickListener {
            createTitle()
        }
    }

    private fun showDialogChooser() {

        val tags = arrayOf("Red", "Green", "Blue","None")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Tag")
        builder.setItems(tags) { dialog, which ->
            val item = tags[which]
            tag = if(item != "None") item else ""
            binding.edTag.setText(tag)
            dialog.dismiss()
        }
        builder.show()

    }

    private fun deleteImage() {
        imageUrl = ""
        Glide.with(this).load("").into(binding.image)
    }

    private fun openGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "select a picture"), Constant.REQUEST_GALLERY)
    }

    private fun createTitle() {
        title = binding.edTitle.text.toString()

        if(title.isBlank()){
            binding.edTitle.error = resources.getString(R.string.error_no_title)
        }else{
            binding.edTitle.error = null
            val result = Intent()
            result.putExtra(Constant.TITLE,title)
            result.putExtra(Constant.TAG,tag)
            result.putExtra(Constant.IMG_URL,imageUrl)
            if(intent.action == Constant.ACTION_EDIT)
                result.putExtra(Constant.ID,id)
            setResult(Activity.RESULT_OK,result)
            finish()
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Constant.REQUEST_GALLERY -> {
                if (resultCode == Activity.RESULT_OK) {
                    imageUrl = data?.data.toString()
                    Glide.with(this).load(Uri.parse(imageUrl)).into(binding.image)
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(Constant.TAG,tag)
        outState.putString(Constant.IMG_URL,imageUrl)
    }
}