package com.example.aplikasikotlin

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnSignIn: MaterialButton
    private lateinit var tvSignUp: TextView
    private lateinit var tvForgotPassword: TextView

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private lateinit var progressDialog: ProgressDialog

    override fun onStart() {
        super.onStart()
        firebaseAuth.currentUser?.let { checkUserRole(it.uid) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnSignIn = findViewById(R.id.btnSignIn)
        tvSignUp = findViewById(R.id.tvSignUp)
        tvForgotPassword = findViewById(R.id.tvForgotPassword)

        progressDialog = ProgressDialog(this).apply {
            setTitle("Logging")
            setMessage("Mohon tunggu...")
            setCancelable(false)
        }

        btnSignIn.setOnClickListener {
            if (validateInput()) processLogin()
        }

        tvSignUp.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun validateInput(): Boolean {
        return when {
            etEmail.text.isNullOrEmpty() -> {
                showToast("Email tidak boleh kosong")
                false
            }
            etPassword.text.isNullOrEmpty() -> {
                showToast("Password tidak boleh kosong")
                false
            }
            else -> true
        }
    }

    private fun processLogin() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        progressDialog.show()
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    firebaseAuth.currentUser?.uid?.let { checkUserRole(it) }
                } else {
                    progressDialog.dismiss()
                    showToast("Login gagal: ${task.exception?.localizedMessage}")
                }
            }
    }

    private fun checkUserRole(userId: String) {
        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                progressDialog.dismiss()
                if (document.exists()) {
                    val role = document.getString("role")
                    if (role.isNullOrEmpty()) {
                        navigateToChooseRole()
                    } else {
                        navigateToDashboard(role)
                    }
                } else {
                    showToast("Profil pengguna tidak ditemukan")
                }
            }
            .addOnFailureListener {
                progressDialog.dismiss()
                showToast("Gagal memuat data pengguna")
            }
    }

    private fun navigateToChooseRole() {
        startActivity(Intent(this, ChooseRoleActivity::class.java))
        finish()
    }

    private fun navigateToDashboard(role: String) {
        val intent = if (role == "UMKM") {
            Intent(this, UMKMDashboardActivity::class.java)
        } else {
            Intent(this, InvestorDashboardActivity::class.java)
        }
        startActivity(intent)
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
