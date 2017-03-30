package xyz.nulldev.ts.ext

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy

inline fun <reified T : Any> kInstanceLazy() = Kodein.global.lazy.instance<T>()
inline fun <reified T : Any> kInstance() = Kodein.global.instance<T>()
