package xyz.nulldev.ts.api.java.model.categories

interface CategoriesEditOperation : MutableList<Category> {
    /**
     * Create a new category. Does NOT add the category into the edit operation.
     * Call `add()` to add it to the edit operation.
     *
     * @param name The name of the category
     * @return The newly created Category object
     */
    fun createCategory(name: String): Category

    /**
     * Save the edits
     */
    fun save()
}