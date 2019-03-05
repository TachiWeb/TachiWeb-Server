package eu.kanade.tachiyomi.util

import kotlinx.coroutines.*
import kotlinx.coroutines.experimental.android.UI

fun launchUI(block: suspend CoroutineScope.() -> Unit): Job =
        GlobalScope.launch(UI, CoroutineStart.DEFAULT, block)

fun launchNow(block: suspend CoroutineScope.() -> Unit): Job =
        GlobalScope.launch(UI, CoroutineStart.UNDISPATCHED, block)
