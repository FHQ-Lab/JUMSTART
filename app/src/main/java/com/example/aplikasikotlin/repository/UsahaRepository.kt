package com.example.aplikasikotlin.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.example.aplikasikotlin.model.Usaha

class UsahaRepository {
    private val db = FirebaseFirestore.getInstance()
    private val usahaCollection = db.collection("usaha")

    // Fungsi untuk menambahkan data usaha
    fun tambahUsaha(usaha: Usaha, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        usahaCollection.document(usaha.id)
            .set(usaha)
            .addOnSuccessListener {
                Log.d("Firestore", "Data usaha berhasil disimpan!")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Gagal menyimpan data", e)
                onFailure(e)
            }
    }

    // Fungsi untuk mengambil daftar usaha
    fun getUsaha(onSuccess: (List<Usaha>) -> Unit, onFailure: (Exception) -> Unit) {
        usahaCollection.get()
            .addOnSuccessListener { result ->
                val usahaList = result.map { it.toObject(Usaha::class.java) }
                onSuccess(usahaList)
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error mendapatkan data", e)
                onFailure(e)
            }
    }
}