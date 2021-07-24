package com.novel.processor.biquwxla

import com.novel.dao.Chapter
import com.novel.processor.IProcessor
import org.jsoup.Jsoup
import org.jsoup.select.Elements


class CatalogProcessor : IProcessor {
  override fun process(html: String, data: MutableMap<String, Any>) {
    val chapters = ArrayList<Chapter>()
    val doc = Jsoup.parse(html)
    val catalogList: Elements = doc.selectFirst("#list dl")!!.children()
    for (el in catalogList) {
      val title: String = el.child(0).attr("title")
      val href: String = el.child(0).attr("href")
      chapters.add(Chapter(title, href))
    }
    data["catalog"] = chapters
  }
}