package xyz.nulldev.ts.api.v3.models.exceptions

enum class WErrorTypes {
    NAME_CONFLICT,
    ORDER_CONFLICT,
    AUTH_UNSUPPORTED,
    ALREADY_LOGGED_IN,
    INVALID_TYPE,
    NO_MANGA,
    NO_COVER,
    NO_SOURCE,
    MANGA_INFO_UPDATE_FAILED,
    COVER_DOWNLOAD_ERROR
}