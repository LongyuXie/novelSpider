package com.novel.processor

import com.novel.processor.biquwxla.BookInfoProcessor
import com.novel.processor.biquwxla.CatalogProcessor
import com.novel.processor.biquwxla.ContentProcessor

class BiquwxlaProcessor {
  val infoProcessor = BookInfoProcessor()
  val catalogProcessor = CatalogProcessor()
  val contentProcessor = ContentProcessor()
}