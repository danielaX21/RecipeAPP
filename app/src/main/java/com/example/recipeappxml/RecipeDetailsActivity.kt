package com.example.recipeappxml

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class RecipeDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_details)

        val title = intent.getStringExtra("title") ?: "Recipe"
        val ingredients = intent.getStringExtra("ingredients") ?: ""
        val directions = intent.getStringExtra("directions") ?: ""
        val imageUrl = intent.getStringExtra("imageUrl") ?: ""

        val titleText = findViewById<TextView>(R.id.recipeTitle)
        val ingredientsText = findViewById<TextView>(R.id.recipeIngredients)
        val directionsText = findViewById<TextView>(R.id.recipeDirections)
        val imageView = findViewById<ImageView>(R.id.recipeImage)
        val favButton = findViewById<Button>(R.id.addToFavoritesButton)

        titleText.text = title
        ingredientsText.text = ingredients
        directionsText.text = directions

        // 📸 încarcă imaginea rețetei (dacă există)
        if (imageUrl.isNotEmpty()) {
            Glide.with(this)
                .load(imageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(imageView)
        }

        favButton.setOnClickListener {
            // poți adăuga mai târziu salvarea la favorite
        }
    }
}
