package com.example.aplikasikotlin

import ChatListAdapter
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikasikotlin.ChatActivity
import com.example.aplikasikotlin.R
import com.example.aplikasikotlin.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ChatListActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var chatListAdapter: ChatListAdapter
    private val userList = mutableListOf<User>()
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_list)

        recyclerView = findViewById(R.id.recyclerViewChatList)
        recyclerView.layoutManager = LinearLayoutManager(this)

        chatListAdapter = ChatListAdapter(userList) { user ->
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("USER_ID", user.uid)
            intent.putExtra("USER_NAME", user.fullname) // Menambahkan nama user ke intent
            startActivity(intent)
        }
        recyclerView.adapter = chatListAdapter

        fetchChatUsers()
    }

    private fun fetchChatUsers() {
        val currentUser = auth.currentUser?.uid ?: return
        db.collection("users")
            .whereNotEqualTo("uid", currentUser)
            .get()
            .addOnSuccessListener { documents ->
                userList.clear()
                for (doc in documents) {
                    val user = doc.toObject(User::class.java)
                    userList.add(user.copy(fullname = doc.getString("fullname") ?: "")) // Pastikan fullname diambil
                }
                chatListAdapter.notifyDataSetChanged()
            }
    }
}
