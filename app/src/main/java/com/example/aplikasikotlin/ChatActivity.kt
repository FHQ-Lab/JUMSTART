package com.example.aplikasikotlin

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.example.aplikasikotlin.ChatMessage

class ChatActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var messageInput: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var chatTitle: TextView
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var chatId: String = "default_chat"
    private var receiverId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        recyclerView = findViewById(R.id.recyclerViewChat)
        messageInput = findViewById(R.id.etMessageInput)
        sendButton = findViewById(R.id.btnSendMessage)
        chatTitle = findViewById(R.id.chatTitle)

        chatAdapter = ChatAdapter(mutableListOf())
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@ChatActivity)
            adapter = chatAdapter
        }

        receiverId = intent.getStringExtra("USER_ID")
        chatId = generateChatId(auth.currentUser?.uid, receiverId)

        fetchReceiverName()
        sendButton.setOnClickListener {
            sendMessage()
        }

        listenForMessages()
    }

    private fun fetchReceiverName() {
        receiverId?.let { id ->
            db.collection("users").document(id).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val fullname = document.getString("fullname") ?: "Unknown"
                        chatTitle.text = fullname
                    }
                }
        }
    }

    private fun sendMessage() {
        val messageText = messageInput.text.toString().trim()
        if (messageText.isEmpty()) return

        val userId = auth.currentUser?.uid ?: return
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                val fullname = document.getString("fullname") ?: "Unknown"
                val message = ChatMessage(
                    fullname = fullname,
                    message = messageText,
                    timestamp = System.currentTimeMillis()
                )

                db.collection("chats").document(chatId).collection("messages")
                    .add(message)
                    .addOnSuccessListener {
                        messageInput.text.clear()
                    }
            }
    }

    private fun listenForMessages() {
        db.collection("chats").document(chatId).collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshots, _ ->
                snapshots?.let {
                    val messages = snapshots.toObjects(ChatMessage::class.java)
                    runOnUiThread { chatAdapter.submitList(messages) }
                }
            }
    }

    private fun generateChatId(user1: String?, user2: String?): String {
        return if (user1 != null && user2 != null) {
            listOf(user1, user2).sorted().joinToString("_")
        } else {
            "default_chat"
        }
    }
}
