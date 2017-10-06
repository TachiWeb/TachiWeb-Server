package xyz.nulldev.ts.api.java.impl.util

/**
 * Lazy list that delegates all operations on it to a dynamically obtained list
 */
class LazyList<T>(private val list: () -> List<T>): List<T> {
    override val size: Int
        get() = list().size

    override fun contains(element: T) = list().contains(element)

    override fun containsAll(elements: Collection<T>) = list().containsAll(elements)

    override fun get(index: Int) = list().get(index)

    override fun indexOf(element: T) = list().indexOf(element)

    override fun isEmpty() = list().isEmpty()

    override fun iterator() = list().iterator()

    override fun lastIndexOf(element: T) = list().lastIndexOf(element)

    override fun listIterator() = list().listIterator()

    override fun listIterator(index: Int) = list().listIterator(index)

    override fun subList(fromIndex: Int, toIndex: Int) = list().subList(fromIndex, toIndex)
}