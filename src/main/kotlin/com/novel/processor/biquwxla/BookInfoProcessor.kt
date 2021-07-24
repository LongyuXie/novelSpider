package com.novel.processor.biquwxla

import com.novel.dao.BookInfo
import com.novel.processor.IProcessor
import com.novel.processor.biquwxla.BookInfoProcessor.SetBookInfo
import org.jsoup.Jsoup


class BookInfoProcessor : IProcessor {

  private fun interface SetBookInfo {
    fun setProperty(value: String, info: BookInfo)
  }

  override fun process(html: String, data: MutableMap<String, Any>) {
    val nodes = Jsoup.parse(html).head().select("meta[property]")
    val book = BookInfo()
    val properties = arrayOf(
      "og:description", "og:image", "og:novel:category",
      "og:novel:author", "og:novel:book_name", "og:novel:read_url",
      "og:novel:update_time", "og:novel:latest_chapter_name", "og:novel:latest_chapter_url"
    )
    val map = HashMap<String, SetBookInfo>()
    val functions = arrayOf(
      SetBookInfo { desp: String, b: BookInfo -> b.desp = desp },
      SetBookInfo { img: String?, b: BookInfo -> b.coverUrl = img },
      SetBookInfo { category: String?, b: BookInfo -> b.category = category },
      SetBookInfo { author: String?, b: BookInfo -> b.author = author },
      SetBookInfo { name: String?, b: BookInfo -> b.name = name },
      SetBookInfo { url: String?, b: BookInfo -> b.url = url },
      SetBookInfo { upd: String?, b: BookInfo -> b.lastUpdate = upd },
      SetBookInfo { latest: String?, b: BookInfo -> b.latestChapter = latest },
      SetBookInfo { latestUrl: String?, b: BookInfo -> b.latestChapterUrl = latestUrl }
    )
    for (i in properties.indices) {
      map[properties[i]] = functions[i]
    }

    for (el in nodes) {
      val property = el.attr("property")
      val content = el.attr("content")
      map[property]?.setProperty(content, book)
    }
    book.catalogUrl = book.url
    data["info"] = book
  }
}