package com.novel.service

import com.novel.downloader.HtmlCallback
import com.novel.downloader.BookHtmlPage
import com.novel.downloader.BookHtmlPageRequest
import com.novel.downloader.IDownloader
import java.io.IOException
import java.util.concurrent.BlockingDeque
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

/**
 * 使用Okhttp的多线程下载机制下载Html页面
 */
class HtmlDownloadService(
  private val bookService: BookService,
  private val downloader: IDownloader
) : Runnable {

  override fun run() {
    println("线程\$DownloadThread启动")
    val callback = object: HtmlCallback {
      override fun onSuccess(page: BookHtmlPage) {
        bookService.addParsePage(page)
      }
      override fun onFailure(e: IOException) {
//        e.printStackTrace()
        println("下载页面失败")
      }
    }
    Thread.sleep(1000)
    while (true) {
      val request = bookService.downloadQueue.take()
      if (request.type == "quit") {
        // 当接受到退出请求时，okhttp client依旧在下载，需要等待下载完成
        // 或者说，只有全部下载完成时才会发出退出命令
        bookService.quitParseService()
        break
      }
      println("下载html页面: ${request.url}")
      downloader.download(request, callback)
    }
    println("线程\$DownloadThread退出")
  }
}