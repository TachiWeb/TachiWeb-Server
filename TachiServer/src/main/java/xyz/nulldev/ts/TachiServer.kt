package xyz.nulldev.ts

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import eu.kanade.tachiyomi.App
import xyz.nulldev.androidcompat.AndroidCompat
import xyz.nulldev.androidcompat.AndroidCompatInitializer
import xyz.nulldev.ts.api.http.HttpAPI
import xyz.nulldev.ts.api.http.HttpModule
import xyz.nulldev.ts.config.ConfigKodeinModule
import xyz.nulldev.ts.config.ConfigManager
import xyz.nulldev.ts.config.ServerConfig
import java.io.File
import java.lang.management.ManagementFactory

/**
 * Server bootstrap class
 */

class TachiServer {

    val androidCompat by lazy { AndroidCompat() }

    var initialized = false
        private set

    var configModulesRegistered = false
        private set

    fun initInternals() {
        if(initialized) return
        initialized = true

        //Ensure config modules registered
        registerConfigModules()

        //Load config API
        Kodein.global.addImport(ConfigKodeinModule().create())
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

    fun registerConfigModules() {
        if(configModulesRegistered) return
        configModulesRegistered = true

        ConfigManager.registerModules(
                ServerConfig(ConfigManager.config.getConfig("ts.server"))
        )
    }

    companion object {
        private const val BOOTSTRAP_STATUS_PROPERTY = "ts.bootstrap.active"
        private const val JVM_PATCH = "java.base-patch.jar"

        @JvmStatic
        fun main(args: Array<String>) {
            val server = TachiServer()
            //Register config modules early
            server.registerConfigModules()

            //Check if JVM booted with bootstrap classpath
            if(!System.getProperty(BOOTSTRAP_STATUS_PROPERTY).equals("true", true)) {
                try {
                    println("JVM not booted with bootstrap classpath/patched modules! Attempting to boot new JVM...")
                    println("\tExtracting patches...")
                    extractPatch(JVM_PATCH)
                    println("\tAssembling command line...")
                    val jvmArgs = assembleBootCommand()
                    println("\t\tCommand line: ${jvmArgs.joinToString(separator = " ")}")
                    val exitCode = bootNewJvm(jvmArgs)
                    println("\tJVM finished with exit code $exitCode")
                    return
                } catch(e: Exception) {
                    println("\tAn error occurred while booting the new JVM, running TachiServer in the current JVM instead!")
                    e.printStackTrace()
                }
            }

            server.run(args)
        }

        private val patchesDir
                get() = ConfigManager.module<ServerConfig>().patchesDir

        private fun extractPatch(file: String) {
            println("\t\tExtracting patch: $file")
            TachiServer::class.java.getResourceAsStream("/patches/$file")?.use { input ->
                val outputDir = patchesDir
                outputDir.mkdirs()
                val outputFile = File(outputDir, file)
                outputFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        }

        private fun assembleBootCommand(): List<String> {
            val bean = ManagementFactory.getRuntimeMXBean()
            val jvmArgs = bean.inputArguments.toMutableList()

            //Add bootstrap classpath/module patch
            val patchPath = patchesDir.absolutePath + "/" + JVM_PATCH
            val vmVersion = ManagementFactory.getRuntimeMXBean().specVersion.toDouble()
            if(vmVersion >= 1.9) {
                jvmArgs += "--patch-module"
                jvmArgs += "java.base=$patchPath"
                jvmArgs += "--add-reads"
                jvmArgs += "java.base=java.logging"
            } else {
                jvmArgs += "-Xbootclasspath/p:$patchPath"
            }

            //Add classpath
            jvmArgs += "-cp"
            jvmArgs += System.getProperty("java.class.path")

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

            return jvmArgs + "-D$BOOTSTRAP_STATUS_PROPERTY=true" + programArgs.split(" ")
        }

        private fun bootNewJvm(args: List<String>)
                = ProcessBuilder()
                .command(args)
                .inheritIO()
                .start()
                .waitFor()
    }
}
