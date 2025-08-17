package com.example.firebasechatapp.ui.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.firebasechatapp.data.model.Message
import com.example.firebasechatapp.databinding.ListItemMessageReceivedBinding
import com.example.firebasechatapp.databinding.ListItemMessageSentBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatAdapter(private val currentUid: String) :
    ListAdapter<Message, RecyclerView.ViewHolder>(DiffCallback()) {

    private val TYPE_SENT = 1
    private val TYPE_RECEIVED = 2

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).senderId == currentUid) TYPE_SENT else TYPE_RECEIVED
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_SENT) {
            SentViewHolder(ListItemMessageSentBinding.inflate(inflater, parent, false))
        } else {
            ReceivedViewHolder(ListItemMessageReceivedBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val msg = getItem(position)
        when (holder) {
            is SentViewHolder -> holder.bind(msg)
            is ReceivedViewHolder -> holder.bind(msg)
        }
    }

    inner class SentViewHolder(private val binding: ListItemMessageSentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(msg: Message) {
            binding.messageText.text = msg.text
            binding.timeStamp.text = msg.timeStamp.toChatTime()
        }
    }

    inner class ReceivedViewHolder(private val binding: ListItemMessageReceivedBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(msg: Message) {
            binding.messageText.text = msg.text
            binding.timeStamp.text = msg.timeStamp.toChatTime()
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message) = oldItem.timeStamp == newItem.timeStamp
        override fun areContentsTheSame(oldItem: Message, newItem: Message) = oldItem == newItem
    }

    fun Long.toChatTime(): String {
        val sdf = SimpleDateFormat("d MMM, yyyy h:mm a", Locale.getDefault())
        return sdf.format(Date(this))
    }
}
