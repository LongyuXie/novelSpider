package com.novel.downloader

import java.io.IOException

interface HtmlCallback {
  fun onSuccess(page: BookHtmlPage)
  fun onFailure(e: IOException)
}