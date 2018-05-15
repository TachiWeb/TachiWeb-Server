package xyz.nulldev.ts.ext

import com.kizitonwose.time.Interval
import com.kizitonwose.time.TimeUnit
import com.kizitonwose.time.days
import spark.Response

fun Response.disableCache() = header("Cache-Control", "no-cache, no-store, must-revalidate")

fun Response.enableCache(time: Interval<TimeUnit> = 7.days) {
    header("Pragma", "public")
    header("Cache-Control", "max-age=${time.inSeconds.longValue}")
}