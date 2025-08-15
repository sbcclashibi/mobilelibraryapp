package com.jessejojojohnson.mobilelibraryapp.models

import java.util.Date

data class Book(
    val isbn: String = "",
    val title: String = "",
    val author: String = "",
    val publisher: String = ""
) {
    companion object {
        const val KEY = "books"
    }

    fun isValid(): Boolean =
        isbn.isNotBlank() and
                title.isNotBlank() and
                author.isNotBlank() and
                publisher.isNotBlank()
}

sealed class Transactable {
    abstract val name: String
    abstract val contact: String
    abstract val bookIsbn: String
    abstract val date: Date
    abstract fun isValid(): Boolean
    abstract fun type(): String
}

data class Lending(
    override val name: String = "",
    override val contact: String = "",
    override val bookIsbn: String = "",
    override val date: Date = Date()
) : Transactable() {
    companion object {
        const val KEY = "lendings"
    }

    override fun isValid(): Boolean =
        name.isNotBlank() and
        bookIsbn.isNotBlank()

    override fun type(): String = this.javaClass.simpleName
}

data class Return(
    override val name: String = "",
    override val contact: String = "",
    override val bookIsbn: String = "",
    override val date: Date = Date()
) : Transactable() {
    companion object {
        const val KEY = "returns"
    }

    override fun isValid(): Boolean =
        name.isNotBlank() and
        bookIsbn.isNotBlank()

    override fun type(): String = this.javaClass.simpleName
}