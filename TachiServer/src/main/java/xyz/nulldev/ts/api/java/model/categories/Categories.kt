package xyz.nulldev.ts.api.java.model.categories

interface Categories : List<Category> {
    /**
     * Start an edit operation on the categories
     */
    fun edit(): CategoriesEditOperation
}
