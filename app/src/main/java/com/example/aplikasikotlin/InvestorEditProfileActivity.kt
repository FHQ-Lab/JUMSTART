package com.example.aplikasikotlin

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class InvestorEditProfileActivity : AppCompatActivity() {

    private lateinit var ivProfilePicture: ShapeableImageView
    private lateinit var etFullName: TextInputEditText
    private lateinit var etBidangInvest: TextInputEditText
    private lateinit var etPhoneNumber: TextInputEditText
    private lateinit var etLocation: TextInputEditText
    private lateinit var etDescription: TextInputEditText
    private lateinit var btnSave: MaterialButton

    private val firestore = FirebaseFirestore.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_investor_profile)

        // Inisialisasi View
        ivProfilePicture = findViewById(R.id.ivProfilePicture)
        etFullName = findViewById(R.id.etFullName)
        etBidangInvest = findViewById(R.id.etBidangInvest)
        etPhoneNumber = findViewById(R.id.etPhoneNumber)
        etLocation = findViewById(R.id.etLocation)
        etDescription = findViewById(R.id.etDescription)
        btnSave = findViewById(R.id.btnSave)

        // Load data dari Firebase
        loadUserData()

        // Tombol Save
        btnSave.setOnClickListener {
            saveUserData()
        }
    }

    private fun loadUserData() {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            firestore.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        etFullName.setText(document.getString("fullname"))
                        etBidangInvest.setText(document.getString("businessName"))
                        etPhoneNumber.setText(document.getString("phone"))
                        etLocation.setText(document.getString("location"))
                        etDescription.setText(document.getString("description"))
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to load data", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun saveUserData() {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val userData = mapOf(
                "fullname" to etFullName.text.toString(),
                "businessName" to etBidangInvest.text.toString(),
                "phone" to etPhoneNumber.text.toString(),
                "location" to etLocation.text.toString(),
                "description" to etDescription.text.toString()
            )

            // Menggunakan update untuk memperbarui data
            firestore.collection("users").document(userId)
                .update(userData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    if (e.message?.contains("No document to update") == true) {
                        // Jika dokumen tidak ditemukan, gunakan set dengan merge
                        firestore.collection("users").document(userId)
                            .set(userData, SetOptions.merge())
                            .addOnSuccessListener {
                                Toast.makeText(this, "Profile created successfully", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                            .addOnFailureListener { innerError ->
                                Toast.makeText(this, "Failed to save data: ${innerError.message}", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(this, "Failed to update data: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}
