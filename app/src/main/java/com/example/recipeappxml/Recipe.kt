package com.example.recipeappxml

data class Recipe(
    val title: String,
    val ingredients: List<String>,
    val time: String,
    val difficulty: String,
    val imageUrl: String,
    val directions: String? = ""
)
