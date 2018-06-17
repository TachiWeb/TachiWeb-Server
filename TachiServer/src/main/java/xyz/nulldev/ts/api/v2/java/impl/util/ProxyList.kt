package xyz.nulldev.ts.api.v2.java.impl.util

class ProxyList<T, V>(private val orig: List<T>,
                      private val proxy: (T) -> V): AbstractList<V>() {
    override val size = orig.size

    override fun get(index: Int) = proxy(orig[index])
}