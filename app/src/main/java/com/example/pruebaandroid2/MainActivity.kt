package com.example.pruebaandroid2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.example.pruebaandroid2.ui.theme.PruebaAndroid2Theme

class MainActivity : ComponentActivity() {
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        db = FirebaseFirestore.getInstance()
        enableEdgeToEdge()
        setContent {
            PruebaAndroid2Theme {
                PantallaListaCompra(db)
            }
        }
    }
}