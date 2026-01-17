package com.example.recipeappxml

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
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

        // Preluăm datele din Intent
        val title = intent.getStringExtra("title") ?: "No title"
        val ingredients = intent.getStringExtra("ingredients") ?: "No ingredients"
        val directions = intent.getStringExtra("directions") ?: "No directions"
        val imageUrl = intent.getStringExtra("imageUrl") ?: ""

        // IMPORTANT: Preluăm rating și timp din intent pentru a nu fi 0.0/NA
        val rating = intent.getDoubleExtra("rating", 0.0)
        val totalTime = intent.getStringExtra("total_time") ?: "N/A"

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

        // Navigare înapoi la Home
        backButton.setOnClickListener {
            val intentHome = Intent(this, HomeActivity::class.java)
            intentHome.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intentHome)
            finish()
        }

        // Navigare la pagina de Favorite
        favButton.setOnClickListener {
            val intentFav = Intent(this, FavoritesActivity::class.java)
            startActivity(intentFav)
        }

        // Creăm obiectul rețetă COMPLET (cu rating și timp)
        val currentRecipe = Recipe(
            title = title,
            ingredients = ingredients,
            total_time = totalTime,
            rating = rating,
            imageUrl = imageUrl,
            directions = directions
        )

        // Verificăm dacă este deja la favorite
        var isFavorite = RecipeFavoritesManager.getFavorites().any { it.title == title }

        // Setăm aspectul inițial al butonului
        if (isFavorite) {
            addButton.text = "Remove from Favorites"
            addButton.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.soft_pink))
        } else {
            addButton.text = "Add to Favorites"
            addButton.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.deep_rose))
        }

        // Logica de Add/Remove (o singură dată!)
        addButton.setOnClickListener {
            if (RecipeFavoritesManager.getFavorites().any { it.title == title }) {
                // Dacă există deja, îl ștergem
                RecipeFavoritesManager.removeFavorite(this, currentRecipe)
                Toast.makeText(this, "Removed from Favorites", Toast.LENGTH_SHORT).show()
                addButton.text = "Add to Favorites"
                addButton.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.deep_rose))
            } else {
                // Dacă nu există, îl adăugăm
                RecipeFavoritesManager.addFavorite(this, currentRecipe)
                Toast.makeText(this, "Added to Favorites", Toast.LENGTH_SHORT).show()
                addButton.text = "Remove from Favorites"
                addButton.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.soft_pink))
            }
        }
    }
}