package com.novel.processor

import com.novel.dao.BookInfo
import com.novel.dao.Chapter
import com.novel.processor.biquwxla.BookInfoProcessor
import org.apache.commons.io.FileUtils
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import java.io.File

class Bqg52DotNetProcessor {
  // site = "https://www.52bqg.net/"
  private val infoProcessor = BookInfoProcessor()
  private val data = HashMap<String, Any>()

  fun parseInfo(html: String): BookInfo {
    infoProcessor.process(html, data)
    return data["info"] as BookInfo
  }

  fun parseCatalog(html: String): MutableList<Chapter> {
    val chapters = ArrayList<Chapter>()
    val doc = Jsoup.parse(html)
    val catalogList: Elements = doc.selectFirst("#list dl")!!.children()
    for (i in 15 until catalogList.size) {
      val el = catalogList[i].selectFirst("a") ?: continue
      chapters.add(Chapter(el.text(), el.attr("href")))
    }
    return chapters
  }

  fun parseContent(html: String): String {
    val doc = Jsoup.parse(html)
    val contentHtml: String = doc.getElementById("content")!!.html()
    return contentHtml.replace("(<br>|&nbsp;)".toRegex(), "")
      .replace("\n\n".toRegex(), "\n")
  }
}