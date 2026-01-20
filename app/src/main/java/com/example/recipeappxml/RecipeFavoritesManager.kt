package com.example.recipeappxml

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object RecipeFavoritesManager {
    private val favorites = mutableListOf<Recipe>()
    private const val PREFS_NAME = "recipe_prefs"

    // Adăugăm userName pentru a salva în "cheia" corectă
    fun addFavorite(context: Context, recipe: Recipe, userName: String) {
        if (!favorites.any { it.title == recipe.title }) {
            favorites.add(recipe)
            saveToDisk(context, userName)
        }
    }

    fun removeFavorite(context: Context, recipe: Recipe, userName: String) {
        favorites.removeAll { it.title == recipe.title }
        saveToDisk(context, userName)
    }

    fun loadFromDisk(context: Context, userName: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        // Cheia devine unică per utilizator: favorites_list_Iulia
        val json = prefs.getString("favorites_list_$userName", null)
        favorites.clear()
        if (json != null) {
            val type = object : com.google.gson.reflect.TypeToken<MutableList<Recipe>>() {}.type
            val loaded: MutableList<Recipe> = com.google.gson.Gson().fromJson(json, type)
            favorites.addAll(loaded)
        }
    }

    private fun saveToDisk(context: Context, userName: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = com.google.gson.Gson().toJson(favorites)
        prefs.edit().putString("favorites_list_$userName", json).apply()
    }

    fun getFavorites(): List<Recipe> = favorites
}