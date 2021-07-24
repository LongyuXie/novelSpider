import com.novel.spider.NovelSpider
import com.novel.spider.NovelSpiderMutiThread
import kotlin.system.exitProcess

fun main(args: Array<String>) {
  val spider = NovelSpiderMutiThread()
  spider.run()
  println("程序结束")
//  exitProcess(0)
}