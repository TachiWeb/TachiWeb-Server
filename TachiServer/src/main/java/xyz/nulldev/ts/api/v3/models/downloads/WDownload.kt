package xyz.nulldev.ts.api.v3.models.downloads

data class WDownload(
        val chapterId: Long,
        val chapterName: String,
        val downloadedPages: Long,
        val mangaId: Long,
        val mangaTitle: String,
        val progress: Float,
        val status: WDownloadStatus,
        val totalPages: Long?
)