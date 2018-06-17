package xyz.nulldev.ts.api.java.impl

import xyz.nulldev.ts.api.java.impl.backup.BackupControllerImpl
import xyz.nulldev.ts.api.java.impl.catalogue.CatalogueImpl
import xyz.nulldev.ts.api.java.impl.categories.CategoriesImpl
import xyz.nulldev.ts.api.java.impl.database.DatabaseControllerImpl
import xyz.nulldev.ts.api.java.impl.downloads.DownloadControllerImpl
import xyz.nulldev.ts.api.java.impl.image.ImageControllerImpl
import xyz.nulldev.ts.api.java.impl.library.LibraryControllerImpl
import xyz.nulldev.ts.api.java.model.ServerAPIInterface

class ServerAPIImpl : ServerAPIInterface {
    override val catalogue = CatalogueImpl()
    override val downloads = DownloadControllerImpl()
    override val images = ImageControllerImpl()
    override val library = LibraryControllerImpl()
    override val database = DatabaseControllerImpl()
    override val backup = BackupControllerImpl()
    override val categories = CategoriesImpl()
}