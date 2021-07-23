package com.novel.pipeline

import com.google.gson.Gson
import com.novel.dao.Book
import org.apache.commons.io.FileUtils
import java.io.File

class JsonFilePipeline {
  // 将book中的内容写入到json文件中
  fun process(book: Book, fileName: String) {
    val gson = Gson()
    val jsonStr = gson.toJson(book)
    FileUtils.writeStringToFile(File(fileName), jsonStr, "utf-8")
  }
}