package com.example.aplikasikotlin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View

import com.example.aplikasikotlin.R


class NotificationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewNotifications)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Sementara hanya menggunakan list statis
        val notifications = listOf(
            "Notifikasi 1",
            "Notifikasi 2",
            "Notifikasi 3"
        )

        val adapter = NotificationAdapter(notifications)
        recyclerView.adapter = adapter
    }
}


