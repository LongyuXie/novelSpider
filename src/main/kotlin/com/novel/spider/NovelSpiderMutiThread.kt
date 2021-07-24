package com.novel.spider

import com.novel.downloader.OkhttpDownloader
import com.novel.service.BookPersistenceService
import com.novel.service.BookService
import com.novel.service.HtmlDownloadService
import com.novel.service.ParseService
import java.beans.PersistenceDelegate

class NovelSpiderMutiThread {
  private val bookService = BookService()
  val downloader = OkhttpDownloader()
  private val downloadService : Runnable by lazy {
    HtmlDownloadService(bookService, downloader)
  }
  private val parseService: Runnable by lazy {
    ParseService(bookService)
  }
  private val persistenceService : Runnable by lazy {
    BookPersistenceService(bookService)
  }
  fun run() {
    val downloadThread = Thread(downloadService)
    val parseThread = Thread(parseService)
    val persistenceThread = Thread(persistenceService)

    val list = listOf(
//      "https://www.52bqg.net/book_99054/",
//      "https://www.52bqg.net/book_58815/",
//      "https://www.52bqg.net/book_100580/",
      "https://www.52bqg.net/book_127071/",
    )
    bookService.fetchNewBooks(list)
//    bookService.fetchNewBook("https://www.52bqg.net/book_99524/")

    persistenceThread.start()
    parseThread.start()
    downloadThread.start()

    persistenceThread.join()
    parseThread.join()
    downloadThread.join()

    downloader.shutdown()
//    downloader.client.dispatcher.executorService.shutdown()
    println("主线程退出！")
  }
}