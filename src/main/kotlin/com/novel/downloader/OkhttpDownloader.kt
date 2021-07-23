package com.novel.downloader

import okhttp3.*
import java.io.IOException

class OkhttpDownloader : IDownloader {
  private val client = OkHttpClient()
  private val requestBuilder = Request.Builder()
  override fun download(url: String): String? {
    val request = requestBuilder.url(url).build()
    val response = client.newCall(request).execute()
    return response.body?.string()
  }

  override fun download(url: String, callback: HtmlCallback) {
    val request = requestBuilder.url(url).build()
    client.newCall(request)
      .enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
          callback.onFailure()
        }

        override fun onResponse(call: Call, response: Response) {
          callback.onSuccess(response.body!!.string())
        }
      })
  }
}