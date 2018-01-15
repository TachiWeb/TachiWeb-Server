package xyz.nulldev.ts.dui

import com.sun.javafx.webkit.WebConsoleListener
import tornadofx.View
import tornadofx.stackpane
import tornadofx.webview
import xyz.nulldev.ts.config.ConfigManager
import xyz.nulldev.ts.config.ServerConfig

class DUIView : View() {
    val serverConfig by lazy { ConfigManager.module<ServerConfig>() }

    override val root = stackpane {
        webview {
            engine.load("http://${serverConfig.ip}:${serverConfig.port}")

            WebConsoleListener.setDefaultListener { webView, message, lineNumber, sourceId ->
                println("Console: [$sourceId:$lineNumber] $message")
            }
        }
    }
}