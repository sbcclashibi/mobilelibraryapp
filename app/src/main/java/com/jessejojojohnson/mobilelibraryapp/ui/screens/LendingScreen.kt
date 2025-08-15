package com.jessejojojohnson.mobilelibraryapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jessejojojohnson.mobilelibraryapp.models.Lending
import com.jessejojojohnson.mobilelibraryapp.models.Return
import com.jessejojojohnson.mobilelibraryapp.ui.components.MLButton
import com.jessejojojohnson.mobilelibraryapp.ui.components.MLDialog
import com.jessejojojohnson.mobilelibraryapp.ui.components.MLScreen
import com.jessejojojohnson.mobilelibraryapp.ui.components.MLTopBar
import com.jessejojojohnson.mobilelibraryapp.ui.components.mlTextFieldColorOverrides
import com.jessejojojohnson.mobilelibraryapp.util.Dimens
import com.jessejojojohnson.mobilelibraryapp.viewmodels.LendingViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Composable
fun LendingScreen(
    isbn: String,
    isReturning: Boolean,
    lendingSuccessCallback: () -> Unit,
    viewModel: LendingViewModel = viewModel()
) {
    val entry = remember { mutableStateOf(getEntry(isReturning, isbn)) }
    val coroutineScope = rememberCoroutineScope()
    val showDialog = remember {
        mutableStateOf<LendingDialog>(LendingDialog.None)
    }

    MLScreen {
        MLTopBar(
            title = if (isReturning) "Returning" else "Lending",
            backButtonCallback = lendingSuccessCallback
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
                value = entry.value.name,
                onValueChange = {
                    when (entry.value) {
                        is Lending -> entry.value = (entry.value as Lending).copy(name = it)
                        is Return -> entry.value = (entry.value as Return).copy(name = it)
                    }
                },
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                label = { Text(text = "Name") }
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                colors = mlTextFieldColorOverrides(),
                value = entry.value.contact,
                onValueChange = {
                    when (entry.value) {
                        is Lending -> entry.value = (entry.value as Lending).copy(contact = it)
                        is Return -> entry.value = (entry.value as Return).copy(contact = it)
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                label = { Text(text = "Contact") }
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                colors = mlTextFieldColorOverrides(),
                value = entry.value.bookIsbn,
                onValueChange = {
                    when (entry.value) {
                        is Lending -> entry.value = (entry.value as Lending).copy(bookIsbn = it)
                        is Return -> entry.value = (entry.value as Return).copy(bookIsbn = it)
                    }
                },
                enabled = false,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                label = { Text(text = "ISBN") }
            )
        }
        when (showDialog.value) {
            LendingDialog.EntryAdded -> MLDialog(
                text = if (isReturning) "The book has been returned."
                else "The book was lent.",
                confirmCallback = { lendingSuccessCallback.invoke() },
                dismissCallback = { lendingSuccessCallback.invoke() }
            )
            LendingDialog.EntryNotAdded -> MLDialog(
                text = "An error occurred while performing this action.",
                confirmCallback = { lendingSuccessCallback.invoke() },
                dismissCallback = { showDialog.value = LendingDialog.None }
            )
            LendingDialog.None -> {}
        }
        MLButton(
            modifier = Modifier.align(Alignment.BottomCenter),
            text = if (isReturning) "Return Book" else "Lend Book"
        ) {
            coroutineScope.launch {
                viewModel.addLendingEntry(entry.value)
                    .map { result ->
                        if (result) {
                            showDialog.value = LendingDialog.EntryAdded
                        } else {
                            showDialog.value = LendingDialog.EntryNotAdded
                        }
                    }.collect()
            }
        }
    }
}

private fun getEntry(
    isReturning: Boolean,
    isbn: String
) = if (isReturning) {
    Return(bookIsbn = isbn)
} else {
    Lending(bookIsbn = isbn)
}

 sealed class LendingDialog {
    object None : LendingDialog()
    object EntryAdded : LendingDialog()
    object EntryNotAdded : LendingDialog()
}