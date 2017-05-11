package xyz.nulldev.ts.api.http.serializer

import eu.kanade.tachiyomi.source.model.Filter
import eu.kanade.tachiyomi.source.model.FilterList
import eu.kanade.tachiyomi.source.online.english.Mangahere
import kotlin.test.assertEquals

class FilterSerializerTest {
    lateinit var filters1: FilterList
    lateinit var filters2: FilterList

    val serializer = FilterSerializer()

    @org.junit.Before
    fun setUp() {
        filters1 = Mangahere().getFilterList()
        filters2 = Mangahere().getFilterList()
    }

    @org.junit.Test
    fun testSerialization() {
        var serialized = serializer.serialize(filters1)
        println(serialized)
        serializer.deserialize(filters2, serialized)
        assertEquals(filters1, filters2)

        (filters1.find { it is Filter.Text }!! as Filter.Text).state = "Text changed!"

        serialized = serializer.serialize(filters1)
        println(serialized)
        serializer.deserialize(filters2, serialized)
        assertEquals(filters1, filters2)
    }

}