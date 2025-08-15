package com.jessejojojohnson.mobilelibraryapp.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.jessejojojohnson.mobilelibraryapp.models.Book
import com.jessejojojohnson.mobilelibraryapp.util.slog
import com.jessejojojohnson.mobilelibraryapp.util.sloge
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class BookEntryViewModel : ViewModel() {

    private val db = Firebase.firestore

    fun addBookFlow(book: Book, bookAlreadyExists: Boolean = false): Flow<Boolean> {
        return callbackFlow {
            val booksCollectionRef = db.collection(Book.KEY)
            if (!book.isValid()) {
                sloge("Invalid book rejected")
                trySend(false)
                cancel("Book details are invalid")
            } else {
                if (bookAlreadyExists) {
                    booksCollectionRef
                        .whereEqualTo("isbn", book.isbn).get()
                        .addOnSuccessListener { querySnapshot ->
                            if (querySnapshot.isEmpty) trySend(false)
                            querySnapshot.documents.first().let { document ->
                                slog("Updating book with id: ${document.id}...")
                                booksCollectionRef
                                    .document(document.id)
                                    .set(book)
                                    .addOnSuccessListener { trySend(true) }
                                    .addOnCanceledListener { cancel(message = "Update book operation cancelled") }
                                    .addOnFailureListener {
                                        sloge("Failed to update book: $it")
                                        trySend(false)
                                        cancel(message = "Failed to update book", cause = it)
                                    }
                            }
                        }
                } else {
                    booksCollectionRef
                        .add(book)
                        .addOnSuccessListener { trySend(true) }
                        .addOnCanceledListener { cancel(message = "Add book operation cancelled") }
                        .addOnFailureListener {
                            sloge(it.toString())
                            trySend(false)
                            cancel(message = "Failed to add book", cause = it)
                        }
                }
            }
            awaitClose { slog("awaitClose called") }
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
}