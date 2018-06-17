package xyz.nulldev.ts.api.java.model

import xyz.nulldev.ts.api.java.model.backup.BackupController
import xyz.nulldev.ts.api.java.model.catalogue.Catalogue
import xyz.nulldev.ts.api.java.model.categories.Categories
import xyz.nulldev.ts.api.java.model.database.DatabaseController
import xyz.nulldev.ts.api.java.model.downloads.DownloadController
import xyz.nulldev.ts.api.java.model.image.ImageController
import xyz.nulldev.ts.api.java.model.library.LibraryController

interface ServerAPIInterface {
    val catalogue: Catalogue
    val downloads: DownloadController
    val images: ImageController
    val library: LibraryController
    val database: DatabaseController
    val backup: BackupController
    val categories: Categories
}