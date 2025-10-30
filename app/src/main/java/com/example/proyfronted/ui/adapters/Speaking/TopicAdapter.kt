package com.example.proyfronted.ui.adapters.Speaking

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyfronted.backend.Speaking.dto.TopicDto
import com.example.proyfronted.R

class TopicAdapter(
    private val topics: List<TopicDto>,
    private val onClick: (TopicDto) -> Unit
) : RecyclerView.Adapter<TopicAdapter.TopicViewHolder>() {

    inner class TopicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtTopicName: TextView = itemView.findViewById(R.id.txtTopicName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_topic, parent, false)
        return TopicViewHolder(view)
    }

    override fun getItemCount(): Int = topics.size

    override fun onBindViewHolder(holder: TopicViewHolder, position: Int) {
        val topic = topics[position]
        holder.txtTopicName.text = topic.name
        holder.itemView.setOnClickListener {
            onClick(topic)
        }
    }
}
