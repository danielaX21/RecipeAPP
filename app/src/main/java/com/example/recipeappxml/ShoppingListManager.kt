package com.example.recipeappxml

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object ShoppingListManager {
    private val items = mutableListOf<String>()
    private const val PREFS_NAME = "shopping_prefs"

    fun addItem(context: Context, item: String, userName: String) {
        if (!items.contains(item)) {
            items.add(item)
            saveToDisk(context, userName)
        }
    }

    fun removeItem(context: Context, item: String, userName: String) {
        items.remove(item)
        saveToDisk(context, userName)
    }

    fun loadFromDisk(context: Context, userName: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString("shop_$userName", null)
        items.clear()
        if (json != null) {
            val type = object : TypeToken<MutableList<String>>() {}.type
            items.addAll(Gson().fromJson(json, type))
        }
    }

    private fun saveToDisk(context: Context, userName: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString("shop_$userName", Gson().toJson(items)).apply()
    }

    fun getItems(): List<String> = items
}