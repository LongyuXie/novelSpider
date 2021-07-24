package com.novel.downloader

import okhttp3.*
import java.io.IOException

// 如果使用okhttp的多线程下载队列，
// 那么在下载一本书籍的过程中，需要许多不同的页面，
// 如何形成一个完整的流程？
class OkhttpDownloader : IDownloader {
  private val client = OkHttpClient()
  private val requestBuilder = Request.Builder()

  init {
    client.dispatcher.maxRequests = 64
  }

  fun shutdown() {
    client.dispatcher.executorService.shutdown()
  }

  override fun download(url: String): String? {
    val request = requestBuilder.url(url).build()
    val response = client.newCall(request).execute()
    return response.body?.string()
  }

  override fun download(req: BookHtmlPageRequest): BookHtmlPage {
    val request = requestBuilder.url(req.url).build()
    val response = client.newCall(request).execute()
    return BookHtmlPage(response.body!!.string(), req)
  }

  override fun download(req: BookHtmlPageRequest, callback: HtmlCallback) {
    val request = requestBuilder.url(req.url).build()
    client.newCall(request)
      .enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
          callback.onFailure(e)
        }
        override fun onResponse(call: Call, response: Response) {
          val html = response.body!!.string()
          val page = BookHtmlPage(html, req)
          callback.onSuccess(page)
        }
      })
  }
}