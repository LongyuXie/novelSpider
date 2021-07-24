package com.novel.dao

class Book(
  val info: BookInfo,
  val chapters: MutableList<Chapter>
) {
  companion object {
    val emptyBook: Book = Book(BookInfo(), emptyList<Chapter>().toMutableList())
  }
}