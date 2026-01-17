package com.example.recipeappxml

import android.content.Intent
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

        // 1. Butonul din stânga sus (Logo/Back) -> acum duce la Home
        backButton.setOnClickListener {
            val intentHome = Intent(this, HomeActivity::class.java)
            // FLAG_ACTIVITY_CLEAR_TOP șterge istoricul de ecrane pentru a nu se întoarce în buclă
            intentHome.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intentHome)
            finish()
        }

        // 2. Butonul din dreapta sus (Steluța din bară) -> duce la pagina de Favorite
        favButton.setOnClickListener {
            val intentFav = Intent(this, FavoritesActivity::class.java)
            startActivity(intentFav)
        }

        // 3. Butonul mare de jos -> Salvează rețeta în lista de favorite
        addButton.setOnClickListener {
            val currentRecipe = Recipe(
                title = title,
                ingredients = ingredients,
                total_time = "N/A",
                rating = 0.0,
                imageUrl = imageUrl,
                directions = directions
            )

            // Apelăm managerul care salvează și pe disc (SharedPreferences)
            RecipeFavoritesManager.addFavorite(this, currentRecipe)
            Toast.makeText(this, "Adăugat la favorite! ❤️", Toast.LENGTH_SHORT).show()
        }
    }
}