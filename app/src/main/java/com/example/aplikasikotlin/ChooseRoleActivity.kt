package com.example.aplikasikotlin

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ChooseRoleActivity : AppCompatActivity() {
    private lateinit var progressDialog: ProgressDialog
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_role)

        val btnUMKM = findViewById<Button>(R.id.btnUMKM)
        val btnInvestor = findViewById<Button>(R.id.btnInvestor)

        progressDialog = ProgressDialog(this).apply {
            setTitle("Saving Role")
            setMessage("Please wait...")
            setCancelable(false)
        }

        val userId = firebaseAuth.currentUser?.uid

        btnUMKM.setOnClickListener {
            saveRoleToFirestore(userId, "UMKM")
        }

        btnInvestor.setOnClickListener {
            saveRoleToFirestore(userId, "Investor")
        }
    }

    private fun saveRoleToFirestore(userId: String?, role: String) {
        if (userId != null) {
            val userRef = FirebaseFirestore.getInstance().collection("users").document(userId)
            userRef.update("role", role)
                .addOnSuccessListener {
                    val intent = if (role == "UMKM") {
                        Intent(this, UMKMDashboardActivity::class.java)
                    } else {
                        Intent(this, InvestorDashboardActivity::class.java)
                    }
                    startActivity(intent)
                    finish() // Pastikan activity ini selesai
                }
                .addOnFailureListener { error ->
                    Log.e("ChooseRoleActivity", "Gagal menyimpan role: ${error.message}")
                    Toast.makeText(this, "Gagal menyimpan role: ${error.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "User ID tidak ditemukan", Toast.LENGTH_SHORT).show()
        }
    }

//    private fun saveRoleToFirestore(userId: String?, role: String) {
//        if (userId != null) {
//            progressDialog.show()
//            val userRef = db.collection("users").document(userId)
//            val roleData = mapOf("role" to role)
//
//            userRef.set(roleData, com.google.firebase.firestore.SetOptions.merge())
//                .addOnSuccessListener {
//                    progressDialog.dismiss()
//                    Toast.makeText(this, "Role berhasil disimpan!", Toast.LENGTH_SHORT).show()
//                    // Arahkan ke dashboard sesuai role
//                    val intent = if (role == "UMKM") {
//                        Intent(this, UMKMDashboardActivity::class.java)
//                    } else {
//                        Intent(this, InvestorDashboardActivity::class.java)
//                    }
//                    startActivity(intent)
//                    finish()
//                }
//                .addOnFailureListener { error ->
//                    progressDialog.dismiss()
//                    Toast.makeText(this, "Gagal menyimpan role: ${error.localizedMessage}", Toast.LENGTH_SHORT).show()
//                }
//        }
//    }
}
