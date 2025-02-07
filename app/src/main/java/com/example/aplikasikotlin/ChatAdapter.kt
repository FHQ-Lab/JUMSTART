package com.example.aplikasikotlin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatAdapter(private var messages: MutableList<ChatMessage>) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMessage: TextView = itemView.findViewById(R.id.tvMessage)
        val tvSender: TextView = itemView.findViewById(R.id.tvSender) // Pastikan ada di XML
        val tvTimestamp: TextView = itemView.findViewById(R.id.tvTimestamp) // Pastikan ada di XML
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_message, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message = messages[position]
        holder.tvMessage.text = message.message
        holder.tvSender.text = message.fullname // Bisa diganti dengan nama jika ada
        holder.tvTimestamp.text = message.timestamp.toString()
    }

    override fun getItemCount(): Int = messages.size

    // Tambahkan metode submitList untuk memperbarui data
    fun submitList(newMessages: List<ChatMessage>) {
        messages = newMessages.toMutableList() // Perbaiki inisialisasi list
        notifyDataSetChanged()
    }
}
