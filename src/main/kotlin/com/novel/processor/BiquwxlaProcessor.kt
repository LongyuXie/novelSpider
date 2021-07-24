package com.novel.processor

import com.novel.dao.BookInfo
import com.novel.dao.Chapter
import com.novel.processor.biquwxla.BookInfoProcessor
import com.novel.processor.biquwxla.CatalogProcessor
import com.novel.processor.biquwxla.ContentProcessor

class BiquwxlaProcessor {
  // site = "https://www.bqgwx.la/"
  val infoProcessor = BookInfoProcessor()
  val catalogProcessor = CatalogProcessor()
  val contentProcessor = ContentProcessor()
  private val data: MutableMap<String, Any> = HashMap()

  fun parseInfo(html: String): BookInfo {
    data.clear()
    infoProcessor.process(html, data)
    return data["info"] as BookInfo
  }

  fun parseCatalog(html: String): MutableList<Chapter> {
    data.clear()
    catalogProcessor.process(html, data)
    return data["catalog"] as ArrayList<Chapter>
  }

  fun parseContent(html: String): String {
    data.clear()
    contentProcessor.process(html, data)
    return data["content"] as String
  }

}