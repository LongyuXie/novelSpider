package com.novel.downloader

data class BookHtmlPage(
  var html: String,
  val request: BookHtmlPageRequest)
{

}