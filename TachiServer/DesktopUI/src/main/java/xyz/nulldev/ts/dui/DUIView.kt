package xyz.nulldev.ts.dui

import com.github.salomonbrys.kodein.conf.KodeinGlobalAware
import com.github.salomonbrys.kodein.instance
import tornadofx.View
import tornadofx.stackpane
import tornadofx.webview
import xyz.nulldev.ts.config.ConfigManager
import xyz.nulldev.ts.config.ServerConfig

class DUIView : View(), KodeinGlobalAware {
    val serverConfig by lazy { instance<ConfigManager>().module<ServerConfig>() }

    override val root = stackpane {
        webview {
            engine.load("http://${serverConfig.ip}:${serverConfig.port}")

//            WebConsoleListener.setDefaultListener { webView, message, lineNumber, sourceId ->
//                println("Console: [$sourceId:$lineNumber] $message")
//            }
        }
    }
}