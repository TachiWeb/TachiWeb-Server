package xyz.nulldev.ts.api.java.impl.categories

import xyz.nulldev.ts.api.java.model.categories.Category

class CategoryImpl(name: String,
                   private val mutable: Boolean,
                   val id: Int? = null): Category {
    override var name: String = name
        set(value) {
            if(!mutable) {
                throw IllegalStateException("Cannot modify immutable category!")
            }
            field = value
        }
}