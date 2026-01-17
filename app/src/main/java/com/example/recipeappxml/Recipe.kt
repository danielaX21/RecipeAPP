package com.example.recipeappxml

data class Recipe(
    var title: String = "",
    var ingredients: Any? = null,   // <--- IMPORTANT (nu List<String>)
    var total_time: String = "",
    var rating: Double = 0.0,
    var imageUrl: String = "",
    var directions: String = "",
    var url: String = "",
    var difficulty: String = "" // Adaugă această linie dacă lipsește
) {
    fun ingredientsAsList(): List<String> {
        // dacă e listă în DB
        if (ingredients is List<*>) {
            return (ingredients as List<*>)
                .mapNotNull { it?.toString() }
                .map { it.trim() }
                .filter { it.isNotEmpty() }
        }

        // dacă e map {0:"...",1:"..."} în DB
        if (ingredients is Map<*, *>) {
            return (ingredients as Map<*, *>)
                .toSortedMap(compareBy { it.toString().toIntOrNull() ?: Int.MAX_VALUE })
                .values
                .mapNotNull { it?.toString() }
                .map { it.trim() }
                .filter { it.isNotEmpty() }
        }

        // dacă e string mare "butter, apples, sugar..."
        val s = ingredients?.toString() ?: ""
        return s.split(",", ";")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }
}
