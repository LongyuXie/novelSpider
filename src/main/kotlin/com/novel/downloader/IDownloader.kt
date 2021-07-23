package com.novel.downloader

/**
 * html页面下载接口
 */
interface IDownloader {
  /**
   * @return 返回一个html页面，失败返回null
   */
  fun download(url: String): String?
  fun download(url: String, callback: HtmlCallback)
}