package com.example.yokwe.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.yokwe.ui.auth.CreateFamilyScreen
import com.example.yokwe.ui.theme.YokweTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            YokweTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CreateFamilyScreen(
                        modifier = Modifier.padding(innerPadding),
                        onFamilyCreated = { }
                    )
                }
            }
        }
    }
}

