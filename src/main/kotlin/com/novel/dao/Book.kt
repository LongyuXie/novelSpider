package com.novel.dao

class Book {
  var info: BookInfo? = null
  var chapters: List<Chapter>? = null

  constructor() {
    info = BookInfo()
    chapters = ArrayList()
  }

  constructor(info: BookInfo?, chapters: List<Chapter>?) {
    this.chapters = chapters
    this.info = info
  }
}