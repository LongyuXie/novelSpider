package com.novel.downloader

data class BookHtmlPageRequest(
  val url: String,
  val type: String,
  val belongTo: String,
) {
  // 页面的类型"content", "catalog", "info"
  // 如果是章节内容页面，该章节在目录中的编号
  var index: Int? = null
}