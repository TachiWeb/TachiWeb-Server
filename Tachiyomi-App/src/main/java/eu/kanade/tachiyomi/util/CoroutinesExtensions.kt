package eu.kanade.tachiyomi.util

import kotlinx.coroutines.*

fun launchUI(block: suspend CoroutineScope.() -> Unit): Job =
        GlobalScope.launch(GlobalScope.coroutineContext, CoroutineStart.DEFAULT, block)

fun launchNow(block: suspend CoroutineScope.() -> Unit): Job =
        GlobalScope.launch(GlobalScope.coroutineContext, CoroutineStart.UNDISPATCHED, block)
