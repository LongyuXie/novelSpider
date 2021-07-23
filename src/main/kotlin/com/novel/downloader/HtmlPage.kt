package com.novel.downloader

class HtmlPage {
  var html: String? = null
  var url: String? = null
  var type: String? = null

  constructor(url: String, type : String = "normal") {
    this.url = url
    this.type = type
  }
}