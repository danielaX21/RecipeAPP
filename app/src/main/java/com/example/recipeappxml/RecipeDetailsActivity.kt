package com.example.recipeappxml

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso

class RecipeDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_details)

        val imageView: ImageView = findViewById(R.id.recipeDetailImage)
        val titleView: TextView = findViewById(R.id.recipeDetailTitle)
        val ingredientsView: TextView = findViewById(R.id.recipeDetailIngredients)
        val directionsView: TextView = findViewById(R.id.recipeDetailDirections)
        val backButton: ImageView = findViewById(R.id.backButton)
        val favButton: ImageView = findViewById(R.id.favButton)
        val addButton = findViewById<Button>(R.id.addToFavoritesButton)
        addButton.setOnClickListener {
            val title = intent.getStringExtra("title") ?: return@setOnClickListener
            val ingredients = intent.getStringExtra("ingredients")?.split("\n• ") ?: emptyList()
            val directions = intent.getStringExtra("directions") ?: ""
            val imageUrl = intent.getStringExtra("imageUrl") ?: ""

            val recipe = Recipe(title, ingredients, "N/A", "Medium", imageUrl, directions)
            RecipeFavoritesManager.addFavorite(recipe)
            Toast.makeText(this, "$title added to favorites!", Toast.LENGTH_SHORT).show()
        }


        // Preluăm datele din Intent
        val title = intent.getStringExtra("title") ?: "No title"
        val ingredients = intent.getStringExtra("ingredients") ?: "No ingredients"
        val directions = intent.getStringExtra("directions") ?: "No directions"
        val imageUrl = intent.getStringExtra("imageUrl") ?: ""

        // Setăm valorile în UI
        titleView.text = title
        ingredientsView.text = ingredients
        directionsView.text = directions

        if (imageUrl.isNotEmpty()) {
            Picasso.get()
                .load(imageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(imageView)
        }

        // Butonul de întoarcere
        backButton.setOnClickListener {
            finish()
        }

        // Favorite
        favButton.setOnClickListener {
            Toast.makeText(this, "Added to favorites ❤️", Toast.LENGTH_SHORT).show()
        }


    }
}
