package com.example.recipeappxml

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.opencsv.CSVReaderBuilder
import java.io.InputStreamReader

class RecipesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecipeAdapter
    private val allRecipes = mutableListOf<Recipe>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipes)

        recyclerView = findViewById(R.id.recipesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val homeButton = findViewById<ImageView>(R.id.homeButton)
        val favButton = findViewById<ImageView>(R.id.favButton)

        homeButton.setOnClickListener {
            finish() // te întorci la Home
        }

        favButton.setOnClickListener {
            val intent = Intent(this, FavoritesActivity::class.java)
            startActivity(intent)
        }

        val selectedIngredients =
            intent.getStringExtra("ingredient")
                ?.split(",")
                ?.map { it.trim().lowercase() }
                ?.filter { it.isNotEmpty() }
                ?: emptyList()

        if (selectedIngredients.isEmpty()) {
            Toast.makeText(this, "No ingredients selected.", Toast.LENGTH_SHORT).show()
            return
        }

        loadRecipesFromCSV(selectedIngredients)
    }

    private fun loadRecipesFromCSV(selectedIngredients: List<String>) {
        try {
            val inputStream = assets.open("recipes.csv")
            val reader = CSVReaderBuilder(InputStreamReader(inputStream))
                .withSkipLines(1)
                .build()

            val filteredList = mutableListOf<Recipe>()
            var line: Array<String>?

            while (reader.readNext().also { line = it } != null) {
                if (line == null || line!!.size < 9) continue

                val title = line!![1].trim()
                val ingredientsRaw = line!![7].lowercase()
                val directions = line!!.getOrNull(8)?.trim() ?: "No directions available."
                val time = line!![2].ifEmpty { "N/A" }
                val imageUrl = line!!.getOrNull(14) ?: ""

                val recipeIngredients = ingredientsRaw
                    .replace("[\\[\\]\"]".toRegex(), "")
                    .split(",")
                    .map { it.trim().lowercase() }
                    .filter { it.isNotEmpty() }

                val matchesAll = selectedIngredients.all { sel ->
                    recipeIngredients.any { it.contains(sel, ignoreCase = true) }
                }

                if (matchesAll) {
                    filteredList.add(
                        Recipe(
                            title = title,
                            ingredients = recipeIngredients,
                            time = time,
                            difficulty = "Medium",
                            imageUrl = imageUrl,
                            directions = directions
                        )
                    )
                }
            }

            reader.close()
            inputStream.close()

            allRecipes.clear()
            allRecipes.addAll(filteredList)
            adapter = RecipeAdapter(allRecipes)
            recyclerView.adapter = adapter

            if (filteredList.isEmpty()) {
                Toast.makeText(this, "No recipes found for your ingredients!", Toast.LENGTH_LONG).show()
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error reading CSV: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
