package com.example.storylogquest.adapter

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.storylogquest.databinding.ItemTopicBinding
import com.example.storylogquest.model.TopicModel
import java.util.*
import kotlin.collections.ArrayList

class TopicAdapter(private val mContext: Context, private val listTopic:ArrayList<TopicModel>):RecyclerView.Adapter<RecyclerView.ViewHolder>(),Filterable,CustomItemTouchHelper.CustomItemTouchHelperListener {

    private lateinit var listCopyTopic: ArrayList<TopicModel>
    lateinit var mListener:OnItemClick
    private var isFiltering = false

    fun setCopyList(listTopic:ArrayList<TopicModel>){
        listCopyTopic = ArrayList(listTopic)
        if(!isFiltering){
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemTopicViewHolder(ItemTopicBinding.inflate(LayoutInflater.from(mContext),parent,false))
    }

    override fun getItemCount(): Int {
        return listTopic.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as ItemTopicViewHolder
        val vh = holder.binding
        val item = listTopic[position]

        vh.tvTitle.text = item.title
        if(item.tag.isNotBlank()){
            when (item.tag){
                "Red" -> vh.tvTag.setBackgroundColor(ContextCompat.getColor(mContext,android.R.color.holo_red_light))
                "Blue" -> vh.tvTag.setBackgroundColor(ContextCompat.getColor(mContext,android.R.color.holo_blue_light))
                "Green" -> vh.tvTag.setBackgroundColor(ContextCompat.getColor(mContext,android.R.color.holo_green_light))
            }
            vh.tvTag.text = item.tag
            vh.tvTag.visibility = View.VISIBLE

        }else{
            vh.tvTag.visibility = View.GONE
        }

        if(item.imageUrl.isNotBlank()){
            Glide.with(mContext)
                .load(Uri.parse(item.imageUrl))
                .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.DATA))
                .into(vh.image)
            vh.image.visibility = View.VISIBLE
        }else{
            vh.image.visibility = View.GONE
        }

        vh.rootLayout.setOnLongClickListener {
            if(::mListener.isInitialized)
                mListener.onItemClick(item)
            true
        }

    }
    override fun getFilter(): Filter {

        return listFiltered
    }

    private val listFiltered = object :Filter(){
        override fun performFiltering(constaint: CharSequence?): FilterResults {
            val filterList:ArrayList<TopicModel> = arrayListOf()
            when (constaint){
                "All" ->{
                    filterList.addAll(listCopyTopic)
                    isFiltering = false
                }
                 else -> {
                     isFiltering = true
                    listCopyTopic.forEach {
                        if(it.tag == constaint){
                            filterList.add(it)
                        }
                    }
                }
            }

            val result = FilterResults()
            result.values = filterList

            return result
        }

        override fun publishResults(constaint: CharSequence?, result: FilterResults?) {
            listTopic.clear()
            try {
                listTopic.addAll(result?.values as ArrayList<TopicModel>)
                notifyDataSetChanged()
            }catch (e:ClassCastException){

            }
        }

    }

    inner class ItemTopicViewHolder(val binding:ItemTopicBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(){
            binding.executePendingBindings()
        }
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        Collections.swap(listTopic, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)

        return true
    }

    override fun onItemDismiss(position: Int) {
        val id = listTopic[position].id
        listTopic.removeAt(position)
        notifyItemRemoved(position)
        mListener.onItemDeleted(id)
    }

    interface OnItemClick{
        fun onItemClick(topic:TopicModel)
        fun onItemDeleted(id:Int)
    }


}