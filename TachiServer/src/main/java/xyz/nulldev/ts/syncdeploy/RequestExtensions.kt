package xyz.nulldev.ts.syncdeploy

import spark.Response

fun Response.disableCache() = header("Cache-Control", "no-cache, no-store, must-revalidate")