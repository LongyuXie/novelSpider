package com.novel.downloader

interface HtmlCallback {
  fun onSuccess(html: String)
  fun onFailure()
}