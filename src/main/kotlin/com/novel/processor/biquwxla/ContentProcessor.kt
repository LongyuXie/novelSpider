package com.novel.processor.biquwxla

import com.novel.processor.IProcessor
import org.jsoup.Jsoup


class ContentProcessor : IProcessor {
  override fun process(html: String, data: HashMap<String, Any>) {
    val doc = Jsoup.parse(html)
    val contentHtml: String = doc.getElementById("content")!!.html()
    val content = contentHtml.replace("(<br>|&nbsp;|<!--.*-->\\s*)".toRegex(), "")
      .replace("\n\n".toRegex(), "\n")
    data["content"] = content
  }
}