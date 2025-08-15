package com.jessejojojohnson.mobilelibraryapp.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.jessejojojohnson.mobilelibraryapp.App
import com.jessejojojohnson.mobilelibraryapp.models.Book
import com.jessejojojohnson.mobilelibraryapp.util.slog
import com.jessejojojohnson.mobilelibraryapp.util.sloge
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class LandingViewModel : ViewModel() {

    private val books: MutableState<List<Book>> = mutableStateOf(emptyList())
    private val db = Firebase.firestore

    private fun fetch() {
        val items = mutableListOf<Book>()
        db.collection(Book.KEY)
            .get()
            .addOnSuccessListener { result ->
                for (item in result) {
                    items.add(item.toObject<Book>())
                }
                books.value = items
            }
    }

    fun getBooksFlow(): Flow<List<Book>> {
        return callbackFlow {
            val booksCollectionRef = db.collection(Book.KEY)
            val listener = booksCollectionRef
                .addSnapshotListener { value, error ->
                    value?.let {
                        trySend(it.toObjects<Book>())
                    }
                    error?.let {
                        sloge(it.toString())
                        cancel(message = "Failed to start flow", cause = it)
                    }
                }
            awaitClose { listener.remove() }
        }
    }

    fun findBook(isbn: String): Flow<Book?> {
        return callbackFlow {
            val booksCollectionRef = db.collection(Book.KEY)
            booksCollectionRef
                .whereEqualTo("isbn", isbn).get()
                .addOnSuccessListener { querySnapShot ->
                    if (querySnapShot.isEmpty) trySend(null)
                    for (result in querySnapShot) {
                        trySend(result.toObject<Book>())
                    }
                }.addOnFailureListener {
                    sloge("Could not find book: $it")
                    trySend(null)
                }

            awaitClose {
                slog("awaitClose called")
            }
        }
    }

    fun scan(): Flow<String?> {
        return callbackFlow {
            //trySend("1234598098354089")
            App.getInstance().getScanner().startScan()
                .addOnSuccessListener {
                    slog("Read barcode!")
                    slog(it.toString())
                    slog(it.displayValue!!)
                    slog(it.rawValue!!)
                    it.rawValue?.let { rawValue -> trySend(rawValue) }
                }
                .addOnCanceledListener {
                    slog("Cancelled")
                    cancel(message = "User cancelled")
                }
                .addOnFailureListener {
                    sloge("Failed")
                    sloge(it.toString())
                    cancel("Scanning failed", cause = it)
                }
            awaitClose { }
        }
    }

    fun getBooks() = books.value
}