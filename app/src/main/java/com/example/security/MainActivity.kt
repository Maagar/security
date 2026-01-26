package com.example.security

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.navigation.compose.rememberNavController
import com.example.security.navigation.AppNavHost
import com.example.security.presentation.screen.viewModel.AuthViewModel
import com.example.security.ui.theme.SecurityTheme
import com.google.firebase.Firebase
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import org.koin.androidx.compose.koinViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private val authViewModel: AuthViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Firebase.appCheck.installAppCheckProviderFactory(
            DebugAppCheckProviderFactory.getInstance()
        )
        enableEdgeToEdge()
        checkIntent(intent)
        setContent {
            SecurityTheme {
                SecurityApp(intent = intent)
            }
        }
    }
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        checkIntent(intent)
    }

    private fun checkIntent(intent: Intent?) {
        val targetScreen = intent?.getStringExtra("navigate_to")

        if (targetScreen == "security_alert_screen") {
            authViewModel.setPendingDestination("security_alert_screen")
            intent.removeExtra("navigate_to")
        }
    }
}

@PreviewScreenSizes
@Composable
fun SecurityApp(intent: Intent? = null) {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = koinViewModel()

    LaunchedEffect(Unit) {
        val targetScreen = intent?.getStringExtra("navigate_to")
        Log.d("DEBUG_NAV", "Odebrano intencję. Cel: $targetScreen")
        if (targetScreen == "secret_note_screen") {
            Log.d("DEBUG_NAV", "Cel pasuje! Ustawiam pendingDestination.")
            authViewModel.setPendingDestination("secret_note_screen")
            intent.removeExtra("navigate_to")
        } else {
            Log.d("DEBUG_NAV", "Cel pusty lub nie pasuje.")
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        AppNavHost(navController, modifier = Modifier.padding(innerPadding), authViewModel = authViewModel)
    }
}

