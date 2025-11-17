package com.example.recipeappxml

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FavoritesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecipeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        recyclerView = findViewById(R.id.favoritesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        // 🔹 Obținem rețetele favorite din managerul global
        val favorites = RecipeFavoritesManager.getFavorites()

        if (favorites.isEmpty()) {
            Toast.makeText(this, "No favorites added yet!", Toast.LENGTH_SHORT).show()
        }

        // ✅ apelăm adapterul corect (cu un singur parametru)
        adapter = RecipeAdapter(favorites)
        recyclerView.adapter = adapter
    }
}
