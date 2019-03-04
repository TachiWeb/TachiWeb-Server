package xyz.nulldev.ts.api.v3.models.downloads

data class WDownloader(
        val downloads: List<WDownload>,
        val paused: Boolean
)