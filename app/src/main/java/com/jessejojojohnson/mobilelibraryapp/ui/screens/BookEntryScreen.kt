package com.jessejojojohnson.mobilelibraryapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import com.jessejojojohnson.mobilelibraryapp.R
import com.jessejojojohnson.mobilelibraryapp.models.Book
import com.jessejojojohnson.mobilelibraryapp.ui.components.MLButton
import com.jessejojojohnson.mobilelibraryapp.ui.components.MLDialog
import com.jessejojojohnson.mobilelibraryapp.ui.components.MLScreen
import com.jessejojojohnson.mobilelibraryapp.ui.components.MLTopBar
import com.jessejojojohnson.mobilelibraryapp.ui.components.mlTextFieldColorOverrides
import com.jessejojojohnson.mobilelibraryapp.ui.theme.MobileLibraryAppTheme
import com.jessejojojohnson.mobilelibraryapp.util.Dimens
import com.jessejojojohnson.mobilelibraryapp.viewmodels.BookEntryViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Composable
fun BookEntryScreen(
    isbn: String,
    bookEntrySuccessCallback: () -> Unit,
    viewModel: BookEntryViewModel = viewModel()
) {
    val book = remember { mutableStateOf(Book(isbn = isbn)) }
    val bookAlreadyExists = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val showDialog = remember {
        mutableStateOf<NewBookEntryDialog>(NewBookEntryDialog.None)
    }

    LaunchedEffect(Unit) {
        viewModel.findBook(isbn).filterNotNull().collect {
            book.value = it
            bookAlreadyExists.value = true
        }
    }

    MLScreen {
        MLTopBar(
            title = if (bookAlreadyExists.value) { stringResource(R.string.title_update_book)
            } else { stringResource(R.string.title_add_new_book)
            },
            backButtonCallback = bookEntrySuccessCallback
        )
        Column(modifier = Modifier
            .align(Alignment.TopCenter)
            .fillMaxWidth()
            .padding(Dimens.Spacing.medium)
            .padding(top = Dimens.AppBar.height)
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                colors = mlTextFieldColorOverrides(),
                value = book.value.title,
                onValueChange = { book.value = book.value.copy(title = it) },
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                label = { Text(text = stringResource(R.string.label_title)) }
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                colors = mlTextFieldColorOverrides(),
                value = book.value.author,
                onValueChange = { book.value = book.value.copy(author = it) },
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                label = { Text(text = stringResource(R.string.label_author)) }
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                colors = mlTextFieldColorOverrides(),
                value = book.value.publisher,
                onValueChange = { book.value = book.value.copy(publisher = it) },
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                label = { Text(text = stringResource(R.string.label_publisher)) }
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                colors = mlTextFieldColorOverrides(),
                value = book.value.isbn,
                onValueChange = { },
                enabled = false,
                label = { Text(text = stringResource(R.string.label_isbn)) }
            )
        }
        when (showDialog.value) {
            NewBookEntryDialog.BookAdded -> MLDialog(
                text = if (bookAlreadyExists.value) "Book updated" else "Book added",
                confirmCallback = { bookEntrySuccessCallback.invoke() },
                dismissCallback = { bookEntrySuccessCallback.invoke() }
            )
            NewBookEntryDialog.BookNotAdded -> MLDialog(
                text = if (bookAlreadyExists.value) "Failed to update book" else "Failed to add book",
                confirmCallback = { bookEntrySuccessCallback.invoke() },
                dismissCallback = { showDialog.value = NewBookEntryDialog.None }
            )
            NewBookEntryDialog.None -> {}
        }
        MLButton(
            modifier = Modifier.align(Alignment.BottomCenter),
            text = if (bookAlreadyExists.value) "Update Book" else "Add Book",
        ) {
            coroutineScope.launch {
                viewModel.addBookFlow(book.value, bookAlreadyExists.value)
                    .map { result ->
                        if (result) {
                            showDialog.value = NewBookEntryDialog.BookAdded
                        } else {
                            showDialog.value = NewBookEntryDialog.BookNotAdded
                        }
                    }.collect()
            }
        }
    }
}

private sealed class NewBookEntryDialog {
    object None : NewBookEntryDialog()
    object BookAdded : NewBookEntryDialog()
    object BookNotAdded : NewBookEntryDialog()
}

@Preview(showBackground = true)
@Composable
fun ComponentsPreview() {
    MobileLibraryAppTheme {
        BookEntryScreen(
            isbn = "1234567",
            bookEntrySuccessCallback = {}
        )
    }
}