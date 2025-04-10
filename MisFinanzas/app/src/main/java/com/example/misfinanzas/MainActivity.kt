package com.example.misfinanzas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.misfinanzas.auth.FirebaseAuthService
import com.example.misfinanzas.navigation.NavGraph
import com.example.misfinanzas.room.GlobalDatabase
import com.example.misfinanzas.ui.theme.MisFinanzasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val firebaseAuthService = FirebaseAuthService()
        GlobalDatabase.initialize(applicationContext)
        setContent {
            MisFinanzasTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavGraph(firebaseAuthService)
                }
            }
        }
    }
}