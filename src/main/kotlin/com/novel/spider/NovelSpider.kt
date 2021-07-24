package com.novel.spider

import com.novel.dao.Book
import com.novel.dao.BookInfo
import com.novel.dao.Chapter
import com.novel.downloader.IDownloader
import com.novel.downloader.OkhttpDownloader
import com.novel.pipeline.JsonFilePipeline
import com.novel.processor.BiquwxlaProcessor
import com.novel.processor.IProcessor

class NovelSpider {
  private val site = "https://www.biquwx.la/"
  fun getSite(): String {
    return site
  }

  private var downloader: IDownloader = OkhttpDownloader()
  private var processors = HashMap<String, IProcessor>().apply {
    val biqugeProcessor = BiquwxlaProcessor()
    this["content"] = biqugeProcessor.contentProcessor
    this["catalog"] = biqugeProcessor.catalogProcessor
    this["info"] = biqugeProcessor.infoProcessor
  }
  private var pipeline = JsonFilePipeline()

  // 使用同步下载的方法
  fun run() {
    val mainPageUrl = getNovelMainPageUrl()
    // TODO: check url match the site
//    downloader.download(mainPageUrl, object : HtmlCallback {
//      override fun onSuccess(html: String) {
//
//      }
//
//      override fun onFailure(e: IOException) {
//        e.printStackTrace()
//      }
//    })


    val mainPageHtml = downloader.download(mainPageUrl)
    if (mainPageHtml != null) {
      val data = HashMap<String, Any>()
      println("获取书籍主页")
      processors["info"]!!.process(mainPageHtml, data)
      val info = data["info"]!! as BookInfo
      println("获取书籍目录")
      processors["catalog"]!!.process(mainPageHtml, data)
      val catalog = data["catalog"]!! as ArrayList<Chapter>
      for ((idx, chapter) in catalog.withIndex()) {
        println("抓取章节：${idx}-${chapter.title}")
        val chapterUrl = info.url + chapter.subUrl
        val chapterHtml = downloader.download(chapterUrl)
        processors["content"]!!.process(chapterHtml!!, data)
        chapter.content = data["content"] as String?
      }
      val book = Book(info, catalog)
      pipeline.process(book, "/home/xielongyu/test/${info.name}.json")
    }
  }

  private fun getNovelMainPageUrl(): String {
    return "https://www.biquwx.la/108_108762/"
  }
}