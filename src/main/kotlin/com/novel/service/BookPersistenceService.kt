package com.novel.service

import com.novel.Constant
import com.novel.dao.Book
import com.novel.pipeline.JsonFilePipeline
import java.util.concurrent.BlockingQueue

class BookPersistenceService(
  private val bookService: BookService,
) : Runnable {

  private val pipelines = JsonFilePipeline()
  private val dir = "D:/test/"

  override fun run() {
    println("线程\$PersistenceThread启动")
    while (true) {
      val queueData = bookService.persistenceQueue.take()
      if (queueData.signal == Constant.QUIT_SIGNAL) {
        break
      }
      val book = queueData.data!!
      println("正在处理: ${book.info.name}")
      pipelines.process(book, dir + book.info.name + ".json")
      bookService.solved.incrementAndGet()
      println("下载完成：${dir+book.info.name+".json"}")
      synchronized(bookService.lock) {
        // 判断是否存在未处理完的书籍，包括下载和解析两个过程
        if (!bookService.hasUnSolvedBook()) {
          bookService.quitDownloadService()
        }
      }
    }
    println("线程\$PersistenceThread退出")

  }
}