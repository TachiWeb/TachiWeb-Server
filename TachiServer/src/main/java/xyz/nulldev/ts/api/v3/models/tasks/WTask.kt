package xyz.nulldev.ts.api.v3.models.tasks

data class WTask(
        val completed: Boolean,
        val data: String?,
        val progress: Float?,
        val progressText: String?,
        val startedAt: Long,
        val type: WTaskType,
        val uuid: String
)