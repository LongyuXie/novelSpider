package com.novel.downloader

import java.io.IOException

interface HtmlCallback {
  fun onSuccess(html: String)
  fun onFailure(e: IOException)
}