package com.novel.processor

interface IProcessor {
  fun process(html: String, data: MutableMap<String, Any>)
}