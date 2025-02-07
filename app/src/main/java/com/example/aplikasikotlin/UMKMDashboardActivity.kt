package com.example.aplikasikotlin

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.example.aplikasikotlin.R.id.tv_greeting
import com.example.aplikasikotlin.R.id.tv_user_name
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UMKMDashboardActivity : AppCompatActivity() {

    // Inisialisasi TextView untuk Business Card
    private lateinit var tvBusinessName: TextView
    private lateinit var tvUserName: TextView
    private lateinit var tvCategory: TextView
    private lateinit var tvLocation: TextView
    private lateinit var tvWelcome: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard_umkm)

        // Hubungkan TextView dengan ID di layout
        tvBusinessName = findViewById(R.id.tv_business_name)
        tvUserName = findViewById(tv_user_name)
        tvCategory = findViewById(R.id.tv_category)
        tvLocation = findViewById(R.id.tv_location)
        tvWelcome = findViewById(tv_greeting)

        // Ambil UID user yang sedang login
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid

        if (currentUserUid != null) {
            fetchUserData(currentUserUid)
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }

        // 1️⃣ SearchView - Menampilkan pencarian
        val searchView = findViewById<SearchView>(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Toast.makeText(this@UMKMDashboardActivity, "Searching: $query", Toast.LENGTH_SHORT).show()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Bisa tambahkan filter pencarian di sini
                return false
            }
        })

        // 2️⃣ Chips Category - Event click untuk filter kategori
        val chipCulinary = findViewById<Chip>(R.id.chipCulinary)
        val chipFashion = findViewById<Chip>(R.id.chipFashion)
        val chipServices = findViewById<Chip>(R.id.chipServices)

        chipCulinary.setOnClickListener { Toast.makeText(this, "Culinary Selected", Toast.LENGTH_SHORT).show() }
        chipFashion.setOnClickListener { Toast.makeText(this, "Fashion Selected", Toast.LENGTH_SHORT).show() }
        chipServices.setOnClickListener { Toast.makeText(this, "Services Selected", Toast.LENGTH_SHORT).show() }

        // 3️⃣ Bottom Navigation - Navigasi antar halaman
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    Toast.makeText(this, "Home Selected", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_chat -> {
                    startActivity(Intent(this, ChatListActivity::class.java))
                    true
                }
                R.id.nav_notification -> {
                    startActivity(Intent(this, NotificationActivity::class.java))
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    // Fungsi untuk mengambil data user dari Firestore
    private fun fetchUserData(uid: String) {
        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    // Ambil data dari Firestore
                    val fullname = document.getString("fullname") ?: "Unknown"
                    val email = document.getString("email") ?: "Unknown"
                    val phone = document.getString("phone") ?: "Unknown"
                    val role = document.getString("role") ?: "Unknown"
                    val businessName = document.getString("businessName") ?: "Unknown"
                    val location = document.getString("location") ?: "Unknown"
                    val category = document.getString("category") ?: "Unknown"

                    // Tampilkan data di "Business Card"
                    tvBusinessName.text = "$fullname"
                    tvUserName.text = "$businessName"
                    tvCategory.text = "$category"
                    tvLocation.text = "$location"
                    tvWelcome.text = "Hi, $fullname"
                } else {
                    Toast.makeText(this, "No user data found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to fetch data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
