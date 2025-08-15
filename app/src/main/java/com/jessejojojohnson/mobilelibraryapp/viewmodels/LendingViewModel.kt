package com.jessejojojohnson.mobilelibraryapp.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.jessejojojohnson.mobilelibraryapp.models.Lending
import com.jessejojojohnson.mobilelibraryapp.models.Return
import com.jessejojojohnson.mobilelibraryapp.models.Transactable
import com.jessejojojohnson.mobilelibraryapp.util.slog
import com.jessejojojohnson.mobilelibraryapp.util.sloge
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.Comparator

class LendingViewModel : ViewModel() {

    private val db = Firebase.firestore

    fun getHistoryEntries(isbn: String? = null): Flow<List<Transactable>> {
        return callbackFlow {
            val lendingCollectionRef = db.collection(Lending.KEY)
            val returnCollectionRef = db.collection(Return.KEY)

            var currentLendings = listOf<Lending>()
            var currentReturns = listOf<Return>()

            fun sendHistory() {
                val history = mutableListOf<Transactable>()
                history.addAll(currentLendings)
                history.addAll(currentReturns)
                history.sortByDescending { tr -> tr.date }
                trySend(history)
            }

            val lendingListener = lendingCollectionRef.addSnapshotListener { value, error ->
                    value?.let {
                        currentLendings = it.toObjects(Lending::class.java)
                        slog("Current lendings: $currentLendings")
                        sendHistory()
                    }
                    error?.let {
                        sloge(it.toString())
                        cancel(message = "Failed to start flow", it)
                        return@addSnapshotListener
                    }
                }
            val returnListener = returnCollectionRef.addSnapshotListener { value, error ->
                    value?.let {
                        currentReturns = it.toObjects(Return::class.java)
                        slog("Current returns: $currentReturns")
                        sendHistory()
                    }
                    error?.let {
                        sloge(it.toString())
                        cancel(message = "Failed to start flow", it)
                        return@addSnapshotListener
                    }
            }

            awaitClose {
                slog("Closing history listeners")
                lendingListener.remove()
                returnListener.remove()
            }
        }
    }

    fun addLendingEntry(entry: Transactable): Flow<Boolean> {
        return callbackFlow {
            val entryType = entry.javaClass.name
            val lendingCollectionRef = when (entry) {
                is Lending -> db.collection(Lending.KEY)
                is Return -> db.collection(Return.KEY)
                else -> {
                    trySend(false)
                    sloge("Could not add entry of type $entryType")
                    cancel(message = "Unknown Transactable $entryType was passed")
                    return@callbackFlow
                }
            }

            if (!entry.isValid()) {
                sloge("Invalid $entryType entry rejected")
                trySend(false)
                cancel("$entryType entry is invalid")
            } else {
                lendingCollectionRef
                    .add(entry)
                    .addOnSuccessListener { trySend(true) }
                    .addOnCanceledListener {
                        cancel(message = "Add $entryType operation cancelled")
                    }
                    .addOnFailureListener {
                        sloge(it.toString())
                        trySend(false)
                        cancel(message = "Failed to add $entryType entry", cause = it)
                    }
            }
            awaitClose { slog("awaitClose called") }
        }
    }
}
