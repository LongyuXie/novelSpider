package com.novel.service

import com.novel.Constant
import com.novel.dao.Book
import com.novel.dao.BookInfo
import com.novel.dao.Chapter
import com.novel.downloader.BookHtmlPage
import com.novel.downloader.BookHtmlPageRequest
import java.lang.Exception
import java.util.*
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicInteger

// 由于解析线程是多线程模型，因此book service必须被设计为线程安全的
// 当出现错误时如何进行恢复呢？
// 可能出现的错误
// - 下载出错
// - 解析出错
// - 存储出错
class BookService {

  // 由谁管理这三个队列
  // 队列中的数据应该有统一的格式
  val downloadQueue: BlockingQueue<QueueData<BookHtmlPageRequest>> = LinkedBlockingQueue()
  val parseQueue: BlockingQueue<QueueData<BookHtmlPage>> = LinkedBlockingQueue()
  val persistenceQueue: BlockingQueue<QueueData<Book>> = LinkedBlockingQueue()

  val lock = Object()

  private val total = AtomicInteger(0)
  val solved = AtomicInteger(0)

  private val recordMap: MutableMap<String, DownloadRecord> = HashMap()

  private fun addDownloadRequest(request: BookHtmlPageRequest) {
    downloadQueue.add(
      QueueData(Constant.NORMAL_SIGNAL, request)
    )
  }

  fun addParsePage(page: BookHtmlPage) {
    parseQueue.add(
      QueueData(Constant.NORMAL_SIGNAL, page)
    )
  }

  private fun addPersistenceBook(book: Book) {
    persistenceQueue.add(
      QueueData(Constant.NORMAL_SIGNAL, book)
    )
  }

  private fun generateUUID(): String {
    return UUID.randomUUID()!!.toString()
  }

  private fun newRecord(): String {
    val rec = DownloadRecord()
    val uuid = generateUUID()
    recordMap[uuid] = rec
    return uuid
  }

  fun fetchNewBook(url: String) {
    // 判断主页Url
//    if (!url.matches("(https?://)?www.biquwx\\.la/(\\w+)/?".toRegex())) {
//      throw IllegalArgumentException("不匹配的小说主页网址")
//    }
    if (!url.matches("(https?://)?www\\.52bqg\\.net/book_(\\d+)/?".toRegex())) {
      throw IllegalArgumentException("不匹配的小说主页网址")
    }
    synchronized(lock) {
      val uuid = newRecord()
      val req = BookHtmlPageRequest(url, "info", uuid)
      addDownloadRequest(req)
      total.incrementAndGet()
    }
  }

  fun fetchNewBooks(urlList: List<String>) {
    for (url in urlList) {
      try {
        fetchNewBook(url)
      } catch (e: Exception) {
        println("非法的小说主业地址：\"$url\"")
      }
    }
  }

  fun fetchCatalog(uuid: String) {
    val rec = recordMap[uuid]!!
    val request = BookHtmlPageRequest(
      rec.info!!.catalogUrl!!,
      "catalog",
      uuid
    )
    addDownloadRequest(request)
  }

  private fun fetchContents(uuid: String) {
    val rec = recordMap[uuid]!!
    val info = rec.info!!
    val catalog = rec.catalog!!
    for ((idx, ch) in catalog.withIndex()) {
      val request = BookHtmlPageRequest(info.url + ch.subUrl, "content", uuid)
      request.index = idx
      addDownloadRequest(request)
    }
  }

  class DownloadRecord {
    var info: BookInfo? = null
    var catalog: MutableList<Chapter>? = null
    var missingPage: MutableSet<Int>? = null

    fun isComplete(): Boolean {
      return info != null &&
          catalog != null &&
          missingPage != null &&
          catalog!!.size != 0 &&
          missingPage!!.size == 0
    }
  }

  fun updateChapter(uuid: String, idx: Int, content: String) {
    val rec = recordMap[uuid]!!
    rec.missingPage!!.remove(idx)
    rec.catalog!![idx].content = content

    if (rec.isComplete()) {
      this.addPersistenceBook(Book(rec.info!!, rec.catalog!!))
    }
  }

  fun updateBookInfo(uuid: String, info: BookInfo) {
    recordMap[uuid]!!.info = info
  }

  fun updateCatalog(uuid: String, catalog: MutableList<Chapter>) {
    val rec = recordMap[uuid]!!
    rec.catalog = catalog
    val set = HashSet<Int>().apply {
      for (i in 0 until catalog.size) add(i)
    }
    rec.missingPage = set
    fetchContents(uuid)
  }

  fun quitParseService() {
    this.parseQueue.add(QueueData(Constant.QUIT_SIGNAL))
//    this.addParsePage(
//      BookHtmlPage("html", BookHtmlPageRequest("url", "quit", ""))
//    )
  }

  fun quitPersistenceService() {
//    this.addPersistenceBook(Book.emptyBook)
    this.persistenceQueue.add(QueueData(Constant.QUIT_SIGNAL))
  }
  fun quitDownloadService() {
    this.persistenceQueue.add(QueueData(Constant.QUIT_SIGNAL))
//    this.addDownloadRequest(BookHtmlPageRequest("quit page", "quit", ""))
  }

  /**
   * 一种情况：首先由total.get()获取锁并取出值，在进行比较后发现没有要处理的书籍了，
   * 但是此时其他线程仍然使用bookService.fetchNewBook添加任务，而且这个线程添加的request
   * 在quit request之前。那么下载线程结束时，okhttp client依旧执行下载任务，但是下载线程的资源被销毁了
   * 如果quit request在正常的request之前加入到队列中就不会有这个问题了
   * 也就是说在进行判断和提交quit request的途中，我们禁止提交下载任务
   */
  fun hasUnSolvedBook(): Boolean {
    return solved.get() != total.get()
  }

}