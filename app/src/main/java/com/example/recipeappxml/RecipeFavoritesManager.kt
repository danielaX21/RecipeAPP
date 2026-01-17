package com.example.recipeappxml

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object RecipeFavoritesManager {
    private val favorites = mutableListOf<Recipe>()
    private const val PREFS_NAME = "recipe_prefs"
    private const val FAV_KEY = "favorites_list"

    fun addFavorite(context: Context, recipe: Recipe) {
        if (!favorites.any { it.title == recipe.title }) {
            favorites.add(recipe)
            saveToDisk(context)
        }
    }

    fun removeFavorite(context: Context, recipe: Recipe) {
        favorites.removeAll { it.title == recipe.title }
        saveToDisk(context)
    }

    fun loadFromDisk(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(FAV_KEY, null)
        if (json != null) {
            val type = object : TypeToken<MutableList<Recipe>>() {}.type
            val loaded: MutableList<Recipe> = Gson().fromJson(json, type)
            favorites.clear()
            favorites.addAll(loaded)
        }
    }

    private fun saveToDisk(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = Gson().toJson(favorites)
        prefs.edit().putString(FAV_KEY, json).apply()
    }

    fun getFavorites(): List<Recipe> = favorites
}