package com.jessejojojohnson.mobilelibraryapp.ui.screens

import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jessejojojohnson.mobilelibraryapp.models.Lending
import com.jessejojojohnson.mobilelibraryapp.models.Return
import com.jessejojojohnson.mobilelibraryapp.models.Transactable
import com.jessejojojohnson.mobilelibraryapp.ui.components.MLDivider
import com.jessejojojohnson.mobilelibraryapp.ui.components.MLHistoryItem
import com.jessejojojohnson.mobilelibraryapp.ui.components.MLScreen
import com.jessejojojohnson.mobilelibraryapp.ui.components.MLScrollableList
import com.jessejojojohnson.mobilelibraryapp.ui.components.MLTopBar
import com.jessejojojohnson.mobilelibraryapp.util.slog
import com.jessejojojohnson.mobilelibraryapp.viewmodels.LandingViewModel
import com.jessejojojohnson.mobilelibraryapp.viewmodels.LendingViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flattenConcat
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun HistoryScreen(
    viewModel: LendingViewModel = viewModel(),
    bookViewModel: LandingViewModel = viewModel(),
    navigateBack: () -> Unit
){
    val history = remember { mutableStateOf<List<Transactable>>(emptyList()) }

    LaunchedEffect(Unit) {
        viewModel.getHistoryEntries()
            .flatMapLatest { transactables ->
                bookViewModel.getBooksFlow().map { books ->
                    transactables.mapNotNull { transactable ->
                        books.find { it.isbn == transactable.bookIsbn }?.title?.let { bookTitle ->
                            when (transactable) {
                                is Lending -> transactable.copy(bookIsbn = bookTitle)
                                is Return -> transactable.copy(bookIsbn = bookTitle)
                            }
                        }
                    }
                }
            }.collect { updatedList ->
                history.value = updatedList
            }
    }

    MLScreen {
        MLTopBar(title = "History", backButtonCallback = navigateBack)
    }
    MLScrollableList {
        itemsIndexed(history.value) { index, item ->
            MLHistoryItem(item)
            if (index < history.value.lastIndex) { MLDivider() }
        }
    }
}