package com.example.aplikasikotlin

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.aplikasikotlin.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.aplikasikotlin.R



class SplashActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        mAuth = FirebaseAuth.getInstance()

        Handler().postDelayed({
            if (mAuth!!.currentUser != null) {
                val userId = mAuth!!.currentUser!!.uid
                val userRef = FirebaseFirestore.getInstance().collection("users").document(userId)

                userRef.get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            val role = document.getString("role")
                            val intent = when (role) {
                                "UMKM" -> Intent(this@SplashActivity, UMKMDashboardActivity::class.java)
                                "Investor" -> Intent(this@SplashActivity, InvestorDashboardActivity::class.java)
                                else -> Intent(this@SplashActivity, LoginActivity::class.java) // Default fallback
                            }
                            startActivity(intent)
                        } else {
                            startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                        }
                        finish()
                    }
                    .addOnFailureListener {
                        startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                        finish()
                    }
            } else {
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                finish()
            }

        }, SPLASH_TIME_OUT.toLong())
    }

    companion object {
        private const val SPLASH_TIME_OUT = 2000 // Durasi Splash Screen (2 detik)
    }
}