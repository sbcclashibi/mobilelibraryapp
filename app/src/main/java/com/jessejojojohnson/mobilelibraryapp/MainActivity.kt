package com.jessejojojohnson.mobilelibraryapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.jessejojojohnson.mobilelibraryapp.ui.nav.Navigation
import com.jessejojojohnson.mobilelibraryapp.ui.theme.MobileLibraryAppTheme
import com.jessejojojohnson.mobilelibraryapp.util.slog
import com.jessejojojohnson.mobilelibraryapp.util.sloge

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            )
        )
        if (FirebaseAuth.getInstance().currentUser != null) {
            displayLandingScreen()
        } else {
            authenticate()
        }
    }

    private fun displayLandingScreen() {
        setContent {
            MobileLibraryAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Navigation(navController = rememberNavController())
                }
            }
        }
    }

    private fun authenticate() {
        FirebaseAuth.getInstance().signInAnonymously()
            .addOnSuccessListener {
                slog("Authenticated anonymous user")
                displayLandingScreen()
            }
            .addOnFailureListener {
                sloge("Could not authenticate user :(")
            }
    }
}



/*
* Read Me
* This app implements the following functionality:
* 1. Scan a book's code
*   a. This might be a custom Mobile Library QR Code or
*   b. The books ISBN barcode thing
* 2. Attempt to retrieve information about the book from Firebase
*   using unique ID from #1
*   If successful:
*       a. Display title and borrowed status
*       b. Give user following actions:
*           i. Lend book
*           ii. Return book
*           iii. Update book information
*           iv. Remove book
*   else:
*       a. Give user following actions:
*           i. Add book
*
* Appendix:
* 1. Book information:
*   a. Title (and author)
*   b. Unique code
* 2. Borrower Information
*   a. Name
*   b. Book ID
*   c. Status (Borrowed, Returned)
* 3. Transaction
*   a. Name
*   b. Contact
*   c. Guarantor
*   d. Id
*
* */