package com.example.aplikasikotlin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class InvestorProfileActivity : AppCompatActivity() {

    private lateinit var ivProfile: ShapeableImageView
    private lateinit var tvFullName: TextView
    private lateinit var tvBusinessName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvPhoneNumber: TextView
    private lateinit var tvLocation: TextView
    private lateinit var tvDescription: TextView
    private lateinit var btnEditProfile: MaterialButton

    private val firestore = FirebaseFirestore.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_investor_profile)

        // Inisialisasi View
//        ivProfile = findViewById(R.id.imgProfilePicture)
        tvFullName = findViewById(R.id.tvFullName)
        tvBusinessName = findViewById(R.id.tvBusinessName)
        tvEmail = findViewById(R.id.tvEmail)
        tvPhoneNumber = findViewById(R.id.tvPhoneNumber)
        tvLocation = findViewById(R.id.tvLocation)
        tvDescription = findViewById(R.id.etDescription)
        btnEditProfile = findViewById(R.id.btnEditProfile)

        // Di dalam onCreate() ProfileActivity
        val btnSignOut = findViewById<Button>(R.id.btnSignOut)
        btnSignOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut() // Log out user dari Firebase
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Hapus back stack
            startActivity(intent)
            finish() // Akhiri aktivitas saat ini
        }

        // Ambil data dari Firebase
        loadUserProfile()

        // Event untuk tombol "Edit Profile"
        btnEditProfile.setOnClickListener {
            startActivity(Intent(this, InvestorEditProfileActivity::class.java))


        }
    }

    private fun loadUserProfile() {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            firestore.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        tvFullName.text = document.getString("fullname") ?: "Unknown"
                        tvBusinessName.text = document.getString("businessName") ?: "N/A"
                        tvEmail.text = document.getString("email") ?: "N/A"
                        tvPhoneNumber.text = document.getString("phone") ?: "N/A"
                        tvLocation.text = document.getString("location") ?: "N/A"
                        tvDescription.text = document.getString("description") ?: "No Description"
                    } else {
                        Toast.makeText(this, "Data tidak ditemukan", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Gagal memuat data", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "User tidak ditemukan. Silakan login kembali.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
