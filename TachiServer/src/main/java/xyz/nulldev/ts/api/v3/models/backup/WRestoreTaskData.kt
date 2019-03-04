package xyz.nulldev.ts.api.v3.models.backup

data class WRestoreTaskData(
        val errors: List<String>,
        val log: String,
        val result: WRestoreTaskDataResult
)