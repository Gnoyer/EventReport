package com.lue.eventreport.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lue.eventreport.databinding.ItemEventBinding
import com.lue.eventreport.model.Event
import com.lue.eventreport.model.EventStatus
import java.text.SimpleDateFormat
import java.util.Locale

class EventAdapter : ListAdapter<Event, EventAdapter.EventViewHolder>(EventDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemEventBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class EventViewHolder(private val binding: ItemEventBinding) : RecyclerView.ViewHolder(binding.root) {

        private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        fun bind(event: Event) {
            binding.eventNameText.text = event.eventName
            binding.timestampText.text = dateFormat.format(event.timestamp)
            binding.statusText.text = when (event.status) {
                EventStatus.PENDING -> "⏳ 待发送"
                EventStatus.SENDING -> "📤 发送中"
                EventStatus.SUCCESS -> "✅ 已发送"
                EventStatus.FAILED -> "❌ 失败"
            }
            binding.retryCountText.text = "重试次数：${event.retryCount}"

            val propertiesText = if (event.properties.isNotEmpty()) {
                "属性：${event.properties.entries.joinToString { "${it.key}=${it.value}" }}"
            } else {
                "属性：无"
            }
            binding.propertiesText.text = propertiesText
        }
    }

    private class EventDiffCallback : DiffUtil.ItemCallback<Event>() {
        override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem == newItem
        }
    }
}
