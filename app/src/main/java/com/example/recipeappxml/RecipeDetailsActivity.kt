package com.example.recipeappxml

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog // Import adăugat pentru selecție
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import android.content.res.ColorStateList

class RecipeDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_details)

        // Initializare View-uri
        val imageView: ImageView = findViewById(R.id.recipeDetailImage)
        val titleView: TextView = findViewById(R.id.recipeDetailTitle)
        val ingredientsView: TextView = findViewById(R.id.recipeDetailIngredients)
        val directionsView: TextView = findViewById(R.id.recipeDetailDirections)
        val backButton: ImageView = findViewById(R.id.backButton)
        val favButton: ImageView = findViewById(R.id.favButton)
        val addButton: Button = findViewById(R.id.addToFavoritesButton)
        val btnShopping: Button = findViewById(R.id.btnAddToShoppingList)

        // Preluăm datele utilizatorului și ale rețetei
        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userName = sharedPref.getString("userName", "Chef") ?: "Chef"

        val title = intent.getStringExtra("title") ?: "No title"
        val ingredients = intent.getStringExtra("ingredients") ?: "No ingredients"
        val directions = intent.getStringExtra("directions") ?: "No directions"
        val imageUrl = intent.getStringExtra("imageUrl") ?: ""
        val rating = intent.getDoubleExtra("rating", 0.0)
        val totalTime = intent.getStringExtra("total_time") ?: "N/A"

        // Setăm valorile în interfață
        titleView.text = title
        ingredientsView.text = ingredients
        directionsView.text = directions

        if (imageUrl.isNotEmpty()) {
            Picasso.get().load(imageUrl).placeholder(android.R.drawable.ic_menu_gallery).into(imageView)
        }


        backButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

        favButton.setOnClickListener { startActivity(Intent(this, FavoritesActivity::class.java)) }


        val currentRecipe = Recipe(
            title = title,
            ingredients = ingredients,
            total_time = totalTime,
            rating = rating,
            imageUrl = imageUrl,
            directions = directions
        )

        // --- Logica Favorite (per utilizator) ---
        var isFavorite = RecipeFavoritesManager.getFavorites().any { it.title == title }

        fun updateButtonUI(favorite: Boolean) {
            if (favorite) {
                addButton.text = "Remove from Favorites"
                addButton.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.soft_pink))
            } else {
                addButton.text = "Add to Favorites"
                addButton.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.deep_rose))
            }
        }
        updateButtonUI(isFavorite)

        addButton.setOnClickListener {
            if (RecipeFavoritesManager.getFavorites().any { it.title == title }) {
                RecipeFavoritesManager.removeFavorite(this, currentRecipe, userName)
                updateButtonUI(false)
            } else {
                RecipeFavoritesManager.addFavorite(this, currentRecipe, userName)
                updateButtonUI(true)
            }
        }

        // --- Logica Shopping List (cu selecție multiplă) ---
        btnShopping.setOnClickListener {
            val intent = Intent(this, ShoppingListActivity::class.java)
            startActivity(intent)
        }
    }
}