package com.example.aplikasikotlin

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.aplikasikotlin.databinding.ActivityRegisterBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {
    private var binding: ActivityRegisterBinding? = null
    private var progressDialog: ProgressDialog? = null
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        progressDialog = ProgressDialog(this).apply {
            setTitle("Registering")
            setMessage("Please wait...")
            setCancelable(false)
        }

        binding!!.tvSignIn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding!!.btnSignUp.setOnClickListener {
            val fullname = binding!!.etName.text.toString().trim()
            val email = binding!!.etEmail.text.toString().trim()
            val phone = binding!!.etPhone.text.toString().trim()
            val password = binding!!.etPassword.text.toString()
            val rePassword = binding!!.etRePassword.text.toString()

            if (fullname.isNotEmpty() && email.isNotEmpty() && phone.isNotEmpty() && password.isNotEmpty() && rePassword.isNotEmpty()) {
                if (password == rePassword) {
                    processRegister(fullname, email, phone, password)
                } else {
                    showToast("Passwords do not match")
                }
            } else {
                showToast("Please fill out all fields")
            }
        }
    }

    private fun processRegister(fullname: String, email: String, phone: String, password: String) {
        progressDialog!!.show()
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task: Task<AuthResult> ->
                if (task.isSuccessful) {
                    val user = task.result.user
                    val userProfile = userProfileChangeRequest {
                        displayName = fullname
                    }

                    user!!.updateProfile(userProfile).addOnCompleteListener { profileTask: Task<Void?> ->
                        if (profileTask.isSuccessful) {
                            saveUserToFirestore(user.uid, fullname, email, phone)
                        } else {
                            progressDialog!!.dismiss()
                            showToast("Failed to update profile")
                        }
                    }
                } else {
                    progressDialog!!.dismiss()
                    Log.e("RegisterError", "Registration Error: ${task.exception!!.localizedMessage}")
                    showToast(task.exception!!.localizedMessage ?: "Registration failed")
                }
            }
    }

    private fun saveUserToFirestore(userId: String, fullname: String, email: String, phone: String) {
        val userData = hashMapOf(
            "uid" to userId,
            "fullname" to fullname,
            "email" to email,
            "phone" to phone,
            "role" to null // Role diisi setelah user memilih di ChooseRoleActivity
        )

        FirebaseFirestore.getInstance().collection("users").document(userId)
            .set(userData)
            .addOnSuccessListener {
                progressDialog!!.dismiss()
                val intent = Intent(this, ChooseRoleActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { error ->
                Log.e("RegisterActivity", "Error saving user: ${error.localizedMessage}")
                progressDialog!!.dismiss()
                showToast("Failed to save user data")
            }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
