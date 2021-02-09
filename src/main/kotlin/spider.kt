import org.openqa.selenium.By
import org.openqa.selenium.remote.RemoteWebDriver
import java.io.BufferedWriter
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.util.concurrent.TimeUnit

fun craw(
    driver: RemoteWebDriver,
    targetFile: String,
    urlForFirstPage: String,
    retryTimes: Int,
    xpathTitle: String,
    xpathContent: String,
    xpathNext: String
) {
    driver.get(urlForFirstPage)
    while (true) {
        var previousContent = ""
        for (i in 1..retryTimes) {
            try {
                val eleTitle = driver.findElement(By.xpath(xpathTitle))
                val eleContent = driver.findElement(By.xpath(xpathContent))
                val content = "# ${eleTitle.text}\n\n${eleContent.text.replace("\n", "\n\n")}\n\n"
                if (previousContent == content) {
                    println("章节重复，抓取完成，准备退出...")
                    break
                }
                previousContent = content
                println("Download 《${eleTitle.text}》 success! ")
                writeToFileAppend(targetFile, content)
                break
            } catch (e: Exception) {
                println("第${i}次尝试遇到异常：${e}")
                if (i == retryTimes) return
                driver.navigate().refresh()
            }
        }
        val eleNext = driver.findElement(By.xpath(xpathNext))
        for (i in 1..retryTimes) {
            try {
                // 用这种方式点击是为了防止广告遮挡元素
                driver.executeScript("arguments[0].click()", eleNext)
                break
            } catch (e: Exception) {
                println("第${i}次尝试进入下一页失败：${e}")
                if (i == retryTimes) return
                driver.navigate().refresh()
            }
        }
    }
}

fun writeToFile(file: String, content: String) {
    BufferedWriter(OutputStreamWriter(FileOutputStream(file, false))).use {
        it.write(content)
    }
}

fun writeToFileAppend(file: String, content: String) {
    BufferedWriter(OutputStreamWriter(FileOutputStream(file, true))).use {
        it.write(content)
    }
}