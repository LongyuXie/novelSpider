package com.novel.service

import com.novel.dao.BookInfo
import com.novel.dao.Chapter
import com.novel.downloader.BookHtmlPage
import com.novel.processor.BiquwxlaProcessor
import com.novel.processor.Bqg52DotNetProcessor

class ParseService(
  private val bookService: BookService
) : Runnable{

  private val biqugeProcessor = Bqg52DotNetProcessor()

  private fun parseContent(page: BookHtmlPage) {
    val content = biqugeProcessor.parseContent(page.html)
    bookService.updateChapter(page.request.belongTo, page.request.index!!, content)
  }
  private fun parseCatalog(page: BookHtmlPage) {
    val catalog = biqugeProcessor.parseCatalog(page.html)
    bookService.updateCatalog(page.request.belongTo, catalog)
  }

  private fun parseInfo(page: BookHtmlPage) {
    val info = biqugeProcessor.parseInfo(page.html)
    bookService.updateBookInfo(page.request.belongTo, info)

    if (info.catalogUrl == info.url) {
      parseCatalog(page)
    } else {
      bookService.fetchCatalog(page.request.belongTo)
    }
  }

  override fun run() {
    val parseQueue = bookService.parseQueue
    println("线程\$ParseThread启动")
    while (true) {
      val page = parseQueue.take()
      // 从BookService中获取书籍信息，将得到的页面解析后写入
//      println("正在解析页面: ${page.request.url}")
      when (page.request.type) {
        "content" -> parseContent(page)
        "catalog" -> parseCatalog(page)
        "info" -> parseInfo(page)
        "quit" -> {
          bookService.quitPersistenceService()
          break
        }
      }
    }
    println("线程\$ParseThread退出")
  }
}