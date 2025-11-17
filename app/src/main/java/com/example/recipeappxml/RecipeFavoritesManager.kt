package com.example.recipeappxml

object RecipeFavoritesManager {
    private val favorites = mutableListOf<Recipe>()

    fun addFavorite(recipe: Recipe) {
        if (!favorites.any { it.title == recipe.title }) {
            favorites.add(recipe)
        }
    }

    fun removeFavorite(recipe: Recipe) {
        favorites.removeAll { it.title == recipe.title }
    }

    fun getFavorites(): List<Recipe> = favorites
}
