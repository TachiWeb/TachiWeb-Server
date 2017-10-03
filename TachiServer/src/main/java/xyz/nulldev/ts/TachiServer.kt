package xyz.nulldev.ts

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import eu.kanade.tachiyomi.App
import xyz.nulldev.androidcompat.AndroidCompat
import xyz.nulldev.androidcompat.AndroidCompatInitializer
import xyz.nulldev.ts.api.http.HttpAPI
import xyz.nulldev.ts.api.http.HttpModule
import java.lang.management.ManagementFactory
import java.io.File

/**
 * Server bootstrap class
 */

class TachiServer {

    val androidCompat by lazy { AndroidCompat() }

    fun initInternals() {
        //Load Android compatibility dependencies
        AndroidCompatInitializer().init()
        //Load TachiServer and Tachiyomi dependencies
        Kodein.global.addImport(TachiyomiKodeinModule().create())
        //Load HTTP server dependencies
        Kodein.global.addImport(HttpModule().create())

        //Start app
        androidCompat.startApp(App())
    }

    fun run(args: Array<String>) {
        //Initialize internals
        initInternals()

        //Start UI server
        TachiWebUIServer().start()

        //Start HTTP API
        HttpAPI().start()
    }

    companion object {
        private const val JVM_PROPERTY = "ts.jvm"

        @JvmStatic
        fun main(args: Array<String>) {
            //Check if JVM booted with bootstrap classpath
            if(!System.getProperty(JVM_PROPERTY).equals("true", true)) {
                try {
                    println("JVM not booted with bootstrap classpath! Attempting to boot new JVM...")
                    println("\tAssembling command line...")
                    val jvmArgs = assembleBootCommand()
                    println("\tCommand line: ${jvmArgs.joinToString(separator = " ")}")
                    val exitCode = bootNewJvm(jvmArgs)
                    println("\tJVM finished with exit code $exitCode")
                    return
                } catch(e: Exception) {
                    println("\tAn error occurred while booting the new JVM, running TachiServer in the current JVM instead!")
                    e.printStackTrace()
                }
            }

            TachiServer().run(args)
        }

        fun assembleBootCommand(): List<String> {
            val classpath = System.getProperty("java.class.path")
            val classpathEntries = classpath.split(File.pathSeparator)

            val bean = ManagementFactory.getRuntimeMXBean()
            // Add boot classpath to JVM arguments
            val jvmArgs = (bean.inputArguments + classpathEntries.map {
                "-Xbootclasspath/p:$it"
            }).toMutableList()

            //Check if JVM booted in debug mode
            if(bean.inputArguments.any {
                it.startsWith("-agentlib:", true)
            }) throw IllegalStateException("JVM booted in debug mode!")

            var programArgs = System.getProperty("sun.java.command")

            //Check if started from JAR
            if(TachiServer::class.java.getResource("${TachiServer::class.simpleName}.class")
                    .protocol == "jar") {
                programArgs = "-jar $programArgs"
            }

            //Find Java binary
            val javaBinFolder = File(File(System.getProperty("java.home")), "bin")
            val windowsExec = File(javaBinFolder, "java.exe")
            val unixExec = File(javaBinFolder, "java")

            jvmArgs.add(0, when {
                windowsExec.exists() -> windowsExec.absolutePath
                unixExec.exists() -> unixExec.absolutePath
                else -> throw RuntimeException("Cannot find JVM binary!")
            })

            return jvmArgs + "-D$JVM_PROPERTY=true" + programArgs.split(" ")
        }

        fun bootNewJvm(args: List<String>)
                = ProcessBuilder()
                .command(args)
                .inheritIO()
                .start()
                .waitFor()
    }
}
