package com.example.yokwe.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.yokwe.ui.auth.states.AuthState
import com.example.yokwe.ui.auth.viewmodels.AuthViewModel
import com.example.yokwe.ui.theme.YokweTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            YokweTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val authViewModel: AuthViewModel = hiltViewModel()
                    val authState by authViewModel.authState.collectAsStateWithLifecycle()
                    val error by authViewModel.error.collectAsStateWithLifecycle()
                    val snackbarHostState = remember { SnackbarHostState() }

                    LaunchedEffect(error) {
                        if (error != null) {
                            snackbarHostState.showSnackbar(error!!)
                            authViewModel.clearError()
                        }

                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        when (val state = authState) {

                            is AuthState.Loading -> CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center)
                            )

                            is AuthState.Authenticated -> {
                                HomeNavGraph(familyId = state.familyId)
                            }

                            AuthState.NotAuthenticated -> {
                                AuthNavGraph(modifier = Modifier.fillMaxSize())
                            }
                        }
                    }
                }
            }

        }
    }
}

