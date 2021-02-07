import androidx.compose.desktop.Window
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.ExperimentalKeyInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.openqa.selenium.edge.EdgeDriver
import org.openqa.selenium.edge.EdgeOptions
import org.openqa.selenium.remote.RemoteWebDriver
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

val driver: RemoteWebDriver
    get() {
        // 在https://developer.microsoft.com/zh-CN/microsoft-edge/tools/webdriver/下载
        System.setProperty("webdriver.edge.driver", "./driver/edge/msedgedriver")
        val options = EdgeOptions()
        options.setProxy(null)
        val driver= EdgeDriver(options)
        driver.manage().timeouts().implicitlyWait(2000, TimeUnit.MILLISECONDS);
        driver.manage().timeouts().pageLoadTimeout(40, TimeUnit.SECONDS);
        driver.manage().timeouts().setScriptTimeout(60, TimeUnit.SECONDS);
        return driver
    }

@ExperimentalKeyInput
fun main() = Window(title = "小说下载器", size = IntSize(400, 800)) {
    MaterialTheme {
        var title by remember { mutableStateOf("") }
        var author by remember { mutableStateOf("") }

        var urlForFirstPage by remember { mutableStateOf("") }
        var titleXpath by remember { mutableStateOf("") }
        var contentXpath by remember { mutableStateOf("") }
        var nextPageXpath by remember { mutableStateOf("") }

        var filename by remember { mutableStateOf("") }
        var retryTimes by remember { mutableStateOf(5) }

        Column(
            Modifier.fillMaxWidth().padding(5.dp), Arrangement.spacedBy(5.dp)
        ) {
            Text(
                "小说下载器",
                modifier = Modifier.fillMaxWidth(),
                fontSize = 24.sp,
                color = Color.Blue,
                textAlign = TextAlign.Center
            )
            Text("小说信息")
            TextField(
                value = title,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("标题") },
                onValueChange = { title = it }
            )
            TextField(
                value = author,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("作者") },
                onValueChange = { author = it }
            )
            Text("页面信息")
            TextField(
                value = urlForFirstPage,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("首页URL") },
                onValueChange = { urlForFirstPage = it }
            )
            TextField(
                value = titleXpath,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("标题XPath") },
                onValueChange = { titleXpath = it }
            )
            TextField(
                value = contentXpath,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("内容XPath") },
                onValueChange = { contentXpath = it }
            )
            TextField(
                value = nextPageXpath,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("下一页XPath") },
                onValueChange = { nextPageXpath = it }
            )
            Text("其他信息")
            TextField(
                value = filename,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("文件名") },
                onValueChange = { filename = it }
            )
            TextField(
                value = retryTimes.toString(),
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("重试次数") },
                onValueChange = {
                    retryTimes = it.toInt()
                }
            )
            Button(modifier = Modifier.align(Alignment.CenterHorizontally), onClick = {
                thread {
                    writeToFile(filename,"% ${title}\n\n% ${author}\n\n")
                    craw(driver, filename, urlForFirstPage, retryTimes, titleXpath, contentXpath, nextPageXpath)
                    driver.close()
                }.start()
            }) {
                Text("开始")
            }
        }
    }
}
