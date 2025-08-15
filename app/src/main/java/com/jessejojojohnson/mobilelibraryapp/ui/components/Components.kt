package com.jessejojojohnson.mobilelibraryapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jessejojojohnson.mobilelibraryapp.R
import com.jessejojojohnson.mobilelibraryapp.models.Book
import com.jessejojojohnson.mobilelibraryapp.models.Transactable
import com.jessejojojohnson.mobilelibraryapp.ui.theme.BackgroundPink
import com.jessejojojohnson.mobilelibraryapp.ui.theme.ButtonBrown
import com.jessejojojohnson.mobilelibraryapp.ui.theme.DarkGreen
import com.jessejojojohnson.mobilelibraryapp.ui.theme.MobileLibraryAppTheme
import com.jessejojojohnson.mobilelibraryapp.util.Dimens
import com.jessejojojohnson.mobilelibraryapp.util.formatDateSimple

@Composable
fun MLScreen(
    content: @Composable BoxScope.() -> Unit
) = Box(
    modifier = Modifier
        .fillMaxSize()
        .background(color = BackgroundPink),
    content = {
        Image(
            painter = painterResource(id = R.drawable.bookbanner_dim),
            contentDescription = null,
            modifier = Modifier
                .align(alignment = Alignment.BottomCenter)
                .graphicsLayer { alpha = 0.3f }
                .fillMaxWidth(),
            contentScale = ContentScale.Crop,
        )
        content()
    }
)

@Composable
fun MLFab(
    modifier: Modifier,
    text: String,
    onClick: () -> Unit
) {
    ExtendedFloatingActionButton(
        modifier = modifier.padding(all = Dimens.Spacing.medium),
        shape = CircleShape,
        onClick = onClick,
        contentColor = Color.White,
        containerColor = ButtonBrown,
        content = {
            Icon(Icons.Filled.Search, contentDescription = text)
            Text(text = text)
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MLTopBar(
    title: String = "Mobile Library App",
    backButtonCallback: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) = CenterAlignedTopAppBar(
    title = {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(fontFamily = FontFamily.Serif)
        )
    },
    colors = TopAppBarDefaults.topAppBarColors(
        titleContentColor = Color.White,
        containerColor = ButtonBrown
    ),
    navigationIcon = {
        backButtonCallback?.let {
            IconButton(onClick = backButtonCallback) {
                Icon(painterResource(R.drawable.ic_back),
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
        }
    },
    actions = actions
)

@Composable
fun MLScrollableList(
    content: LazyListScope.() -> Unit
) = LazyColumn(
    modifier = Modifier
        .fillMaxSize()
        .padding(top = Dimens.AppBar.height)
        .padding(all = Dimens.Spacing.small),
    content = content
)

@Composable
fun MLBookItem(
    book: Book
) = Column(
    modifier = Modifier
        .fillMaxWidth()
        .padding(all = Dimens.Spacing.small)
) {
    val titleTextStyle = MaterialTheme.typography.titleLarge.copy(
        fontFamily = FontFamily.Serif,
        color = Color.DarkGray
    )
    val authorTextStyle = MaterialTheme.typography.titleMedium.copy(
        fontFamily = FontFamily.Serif,
        color = Color.DarkGray
    )
    val byTextStyle = MaterialTheme.typography.titleMedium.copy(
        fontFamily = FontFamily.Cursive,
        color = Color.DarkGray
    )
    val detailsTextStyle = MaterialTheme.typography.labelSmall.copy(
        fontFamily = FontFamily.Serif,
        color = Color.LightGray
    )
    Text(text = book.title, style = titleTextStyle)
    Row {
        Text(text = "by ", style = byTextStyle)
        Text(text = book.author, style = authorTextStyle)
    }
    Text(
        text = "ISBN: ${book.isbn} Publisher: ${book.publisher}",
        style = detailsTextStyle, color = Color.Gray
    )
}

@Composable
fun MLHistoryItem(
    transactable: Transactable
) = Column(
    modifier = Modifier
        .fillMaxWidth()
        .padding(all = Dimens.Spacing.small)
) {
    val titleTextStyle = MaterialTheme.typography.titleLarge.copy(
        fontFamily = FontFamily.Serif,
        color = Color.DarkGray
    )
    val authorTextStyle = MaterialTheme.typography.titleMedium.copy(
        fontFamily = FontFamily.Serif,
        color = Color.DarkGray
    )
    val detailsTextStyle = MaterialTheme.typography.labelSmall.copy(
        fontFamily = FontFamily.Serif,
        color = Color.LightGray
    )
    val transactionTypeTextStyle = MaterialTheme.typography.labelSmall.copy(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold,
        color = if (transactable.type() == "Lending") Color.Red else DarkGreen
    )

    Row(modifier = Modifier.fillMaxWidth()) {
        Text(text = "${formatDateSimple(transactable.date)} - ", style = detailsTextStyle)
        Text(text = transactable.javaClass.simpleName.uppercase(), style = transactionTypeTextStyle)
    }
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = transactable.name,
            style = titleTextStyle,
            modifier = Modifier.alignByBaseline()
        )
        if (transactable.contact.isNotBlank()) {
            Spacer(modifier = Modifier.width(Dimens.Spacing.medium))
            Text(
                text = transactable.contact,
                style = authorTextStyle,
                modifier = Modifier.alignByBaseline()
            )
        }
    }
    Text(text = transactable.bookIsbn, style = detailsTextStyle)
}

@Composable
fun MLDivider(
    modifier: Modifier = Modifier
) = Divider(
    color = Color.Gray.copy(alpha = 0.3f),
    thickness = 0.5.dp,
    modifier = modifier.padding(horizontal = Dimens.Spacing.extraLarge)
)

@Composable
fun MLButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit
) = Button(
    modifier = modifier
        .fillMaxWidth()
        .padding(Dimens.Spacing.large),
    colors = ButtonDefaults.buttonColors(
        contentColor = Color.White,
        containerColor = ButtonBrown,
    ),
    contentPadding = PaddingValues(all = Dimens.Spacing.medium),
    onClick = onClick){
    Text(text = text)
}

@Composable
fun MLDialog(
    title: String? = null,
    text: String,
    confirmCallback: () -> Unit,
    dismissCallback: () -> Unit
) = AlertDialog(
    title = {
        title?.let { Text(text = it) }
    },
    text = {
        Text(text = text)
    },
    onDismissRequest = dismissCallback,
    confirmButton = {
        TextButton(
            onClick = confirmCallback
        ) {
            Text("Ok")
        }
    },
    dismissButton = {
        TextButton(
            onClick = dismissCallback
        ) {
            Text("Cancel")
        }
    }
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MLBottomSheet(
    bookSearchResult: Book?,
    scanResult: String?,
    editBookCallback: () -> Unit,
    dismissCallback: () -> Unit,
    lendBookCallback: () -> Unit,
    returnBookCallback: () -> Unit
) = ModalBottomSheet(
    onDismissRequest = dismissCallback,
    dragHandle = {} // hide ugly handle
) {
    bookSearchResult?.let { book ->
        Column(modifier = Modifier.padding(all = Dimens.Spacing.medium)) {
            MLBookItem(book = book)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ElevatedButton(onClick = lendBookCallback) { Text(text = "Lend") }
                ElevatedButton(onClick = returnBookCallback) { Text(text = "Return") }
                ElevatedButton(onClick = editBookCallback) { Text(text = "Edit Book Details") }
            }
        }
    } ?: run {
        Column(modifier = Modifier.padding(all = Dimens.Spacing.medium)) {
            val titleString = "New book?"
            val titleStyle = MaterialTheme.typography.titleLarge.copy(fontFamily = FontFamily.Serif)
            val detailString = "We could not find book with code $scanResult on the app. Do you want to add it?"
            val detailStyle = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.Serif)
            Text(text = titleString, style = titleStyle)
            Text(text = detailString, style = detailStyle)
            ElevatedButton(onClick = editBookCallback) { Text(text = "Tap to add book") }
        }

    }
}

@Composable
fun mlTextFieldColorOverrides(): TextFieldColors = OutlinedTextFieldDefaults.colors(
    unfocusedLabelColor = Color.DarkGray,
    unfocusedTextColor = Color.DarkGray,
    focusedLabelColor = Color.DarkGray,
    focusedTextColor = Color.DarkGray
)

@Preview(showBackground = true)
@Composable
fun ComponentsPreview() {
    MobileLibraryAppTheme {
        MLTopBar()
    }
}