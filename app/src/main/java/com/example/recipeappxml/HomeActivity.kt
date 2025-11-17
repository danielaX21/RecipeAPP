package com.example.recipeappxml

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.flexbox.FlexboxLayout

class HomeActivity : AppCompatActivity() {

    private lateinit var ingredientInput: AutoCompleteTextView
    private lateinit var selectedContainer: FlexboxLayout
    private val selectedIngredients = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        ingredientInput = findViewById(R.id.ingredientInput)
        selectedContainer = findViewById(R.id.selectedContainer)
        val findButton = findViewById<Button>(R.id.findRecipeButton)
        val favButton = findViewById<ImageView>(R.id.favButton) // ⭐ butonul din bara de sus

        val ingredients = listOf(
            "Eggs", "Tomatoes", "Potatoes", "Onion", "Garlic",
            "Cheese", "Milk", "Butter", "Olive oil", "Flour",
            "Sugar", "Salt", "Pepper", "Chicken", "Beef", "Pasta"
        )

        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, ingredients)
        ingredientInput.setAdapter(adapter)

        ingredientInput.setOnClickListener {
            ingredientInput.showDropDown()
        }

        ingredientInput.setOnItemClickListener { _, _, position, _ ->
            val selected = adapter.getItem(position)
            if (selected != null && !selectedIngredients.contains(selected)) {
                selectedIngredients.add(selected)
                addTag(selected)
            }
            ingredientInput.setText("")
        }

        findButton.setOnClickListener {
            if (selectedIngredients.isEmpty()) {
                Toast.makeText(this, "Please select at least one ingredient!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedText = selectedIngredients.joinToString(",")
            val intent = Intent(this, RecipesActivity::class.java)
            intent.putExtra("ingredient", selectedText)
            startActivity(intent)
        }

        // ⭐ deschide pagina de favorite
        favButton.setOnClickListener {
            val intent = Intent(this, FavoritesActivity::class.java)
            startActivity(intent)
        }
    }

    private fun addTag(name: String) {
        val tag = TextView(this)
        tag.text = "$name  ✕"
        tag.setPadding(20, 10, 20, 10)
        tag.setTextColor(resources.getColor(android.R.color.white))
        tag.setBackgroundResource(R.drawable.tag_background)
        val params = FlexboxLayout.LayoutParams(
            FlexboxLayout.LayoutParams.WRAP_CONTENT,
            FlexboxLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(10, 10, 10, 10)
        tag.layoutParams = params

        tag.setOnClickListener {
            selectedContainer.removeView(tag)
            selectedIngredients.remove(name)
        }

        selectedContainer.addView(tag)
    }
}
