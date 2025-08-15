package com.jessejojojohnson.mobilelibraryapp.ui.screens

import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jessejojojohnson.mobilelibraryapp.R
import com.jessejojojohnson.mobilelibraryapp.models.Book
import com.jessejojojohnson.mobilelibraryapp.ui.components.MLBookItem
import com.jessejojojohnson.mobilelibraryapp.ui.components.MLBottomSheet
import com.jessejojojohnson.mobilelibraryapp.ui.components.MLDivider
import com.jessejojojohnson.mobilelibraryapp.ui.components.MLFab
import com.jessejojojohnson.mobilelibraryapp.ui.components.MLScreen
import com.jessejojojohnson.mobilelibraryapp.ui.components.MLScrollableList
import com.jessejojojohnson.mobilelibraryapp.ui.components.MLTopBar
import com.jessejojojohnson.mobilelibraryapp.ui.theme.MobileLibraryAppTheme
import com.jessejojojohnson.mobilelibraryapp.viewmodels.LandingViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun LandingScreen(
    viewModel: LandingViewModel = viewModel(),
    navigateToBookEntry: (String) -> Unit,
    navigateToLending: (String) -> Unit,
    navigateToReturn: (String) -> Unit,
    navigateToHistory: () -> Unit
) {

    val books = remember { mutableStateOf<List<Book>>(emptyList()) }
    val scanResult = remember { mutableStateOf("") }
    val bookSearchResult = remember { mutableStateOf<Book?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val showDialog = remember {
        mutableStateOf<ShowLandingScreenDialog>(ShowLandingScreenDialog.None)
    }

    LaunchedEffect(Unit) {
        viewModel.getBooksFlow().collect {
            books.value = it
        }
    }

    MLScreen {
        MLTopBar(
            actions = {
                IconButton(
                    onClick = { navigateToHistory.invoke() }
                ) {
                    Icon(
                        painterResource(id = R.drawable.ic_history),
                        "History button",
                        tint = Color.White
                    )
                }
            }
        )
        MLScrollableList {
            itemsIndexed(books.value) { index, book ->
                MLBookItem(book)
                if (index < books.value.lastIndex) { MLDivider() }
            }
        }
        when (showDialog.value) {
            ShowLandingScreenDialog.NewBook -> MLBottomSheet(
                editBookCallback = { navigateToBookEntry.invoke(scanResult.value) },
                dismissCallback = { showDialog.value = ShowLandingScreenDialog.None },
                lendBookCallback = {},
                returnBookCallback = {},
                bookSearchResult = bookSearchResult.value,
                scanResult = scanResult.value
            )
            ShowLandingScreenDialog.ExistingBook -> MLBottomSheet(
                editBookCallback = { navigateToBookEntry.invoke(bookSearchResult.value!!.isbn) },
                dismissCallback = { showDialog.value = ShowLandingScreenDialog.None },
                lendBookCallback = { navigateToLending.invoke(bookSearchResult.value!!.isbn) },
                returnBookCallback = { navigateToReturn.invoke(bookSearchResult.value!!.isbn) },
                bookSearchResult = bookSearchResult.value,
                scanResult = scanResult.value
            )
            ShowLandingScreenDialog.None -> {}
        }
        MLFab(
            modifier = Modifier.align(Alignment.BottomEnd),
            text = "Scan Book",
            onClick = {
                coroutineScope.launch {
                    viewModel.scan().filterNotNull()
                        .flatMapConcat {
                            scanResult.value = it
                            viewModel.findBook(it)
                        }.map { bookResult ->
                            bookResult?.let {
                                bookSearchResult.value = it
                                showDialog.value = ShowLandingScreenDialog.ExistingBook
                            } ?: run {
                                showDialog.value = ShowLandingScreenDialog.NewBook
                            }
                        }.collect()
                }
            }
        )
    }
}

private sealed class ShowLandingScreenDialog {
    object None : ShowLandingScreenDialog()
    object NewBook : ShowLandingScreenDialog()
    object ExistingBook : ShowLandingScreenDialog()
}

@Preview(showBackground = true)
@Composable
fun LandingPreview() {
    MobileLibraryAppTheme {
        //LandingScreen()
    }
}