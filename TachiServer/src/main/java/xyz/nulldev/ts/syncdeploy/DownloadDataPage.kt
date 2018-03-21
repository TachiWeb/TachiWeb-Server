package xyz.nulldev.ts.syncdeploy

import mu.KotlinLogging
import spark.Request
import spark.Response
import spark.Route
import xyz.nulldev.ts.syncdeploy.api.JsonError
import java.nio.file.Files
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class DownloadDataPage(private val am: AccountManager) : Route {
    private val logger = KotlinLogging.logger {}

    override fun handle(request: Request, response: Response): Any {
        val username = request.cookie("username")
        val token = request.cookie("token")

        // Validate token
        if(username == null || token == null || !am.authToken(username, token)) {
            return JsonError("Invalid auth token!")
        }

        am.lockAcc(username) {
            try {
                response.status(200)
                response.type("application/zip")

                val zout = ZipOutputStream(response.raw().outputStream)

                val root = it.syncDataFolder

                if (root.exists()) {
                    val rootPath = root.toPath()

                    root.walkBottomUp().filter { it.isFile }.forEach {
                        val path = it.toPath()
                        val zipEntry = ZipEntry(rootPath.relativize(path).toString()).apply {
                            size = it.length()
                        }
                        zout.putNextEntry(zipEntry)
                        Files.copy(path, zout)
                        zout.closeEntry()
                    }
                }

                zout.finish()
                zout.flush()
            } catch(e: Exception) {
                logger.error(e) {
                    "An error occurred while zipping sync data!"
                }
            }
        }

        return ""
    }
}
