package com.novel.service

import com.novel.dao.BookInfo
import com.novel.dao.Chapter
import com.novel.downloader.BookHtmlPage
import com.novel.processor.BiquwxlaProcessor
import com.novel.processor.Bqg52DotNetProcessor

// 解析线程也可以使用多线程
// 持久化线程也可以多线程
class ParseService(
  private val bookService: BookService
) : Runnable{

  private val biqugeProcessor = Bqg52DotNetProcessor()

  private fun parseContent(page: BookHtmlPage) {
    println("正在解析内容：${page.request.url}")
    val content = biqugeProcessor.parseContent(page.html)
    bookService.updateChapter(page.request.belongTo, page.request.index!!, content)
  }
  private fun parseCatalog(page: BookHtmlPage) {
    println("正在解析目录：${page.request.url}")
    val catalog = biqugeProcessor.parseCatalog(page.html)
    bookService.updateCatalog(page.request.belongTo, catalog)
  }

  private fun parseInfo(page: BookHtmlPage) {
    println("正在解析信息页：${page.request.url}")
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