download service  
book persistence service


如何存放页面，并且知道页面是否全部下载完成？

如果只是存放缺失的页面数量，那么当下载失败时，就无法确定那些页面需要继续下载
```kotlin
class BookDownloadStatus {
  val info: BookInfo? = null
  val catalog: ArrayList<Chapter> = java.util.ArrayList<Chapter>()
  val missingPage: java.util.HashSet<Int>? = null
}
```

判断条件：info != null && catalog.size != 0 && missingPage.size == 0

一本书的页面组成
- 主页，包括书籍名称、作者等基本信息
- 目录页，目录页可能与主页相同，也可能单独作为一个页面
- 章节内容页，一本书有若干内容页，不考虑分页则一个html页面存放一个章节

线程中的数据传输、协作方式？

- 每当下载线程中完成一个页面的下载，唤醒工作线程
- 在工作线程中判断该页面的类型、所属的书籍，并且解析页面中的数据，如果某一本书籍所需的页面全部下载完成，则通知持久化线程
- 在持久化线程中根据书籍信息，将书籍以不同的形式存储：txt或epub

阻塞队列
- 等待下载队列
- 页面处理队列
- 书籍持久化队列

