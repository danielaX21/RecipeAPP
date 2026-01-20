package com.example.recipeappxml

import android.content.Context
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

        // 1. Inițializare View-uri din Header
        val profileIcon = findViewById<ImageView>(R.id.profileIcon)
        val favButton = findViewById<ImageView>(R.id.favButton)
        val welcomeText = findViewById<TextView>(R.id.welcomeText)

        // 2. Inițializare restul View-urilor
        ingredientInput = findViewById(R.id.ingredientInput)
        selectedContainer = findViewById(R.id.selectedContainer)
        val findButton = findViewById<Button>(R.id.findRecipeButton)

        // 3. Logică Nume Utilizator (Preluat de la Login)
        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userName = sharedPref.getString("userName", "Chef") ?: "Chef"
        welcomeText.text = "Hello, $userName! What's in your kitchen?"

        // 4. Încărcare favorite SPECIFICE utilizatorului logat
        // Mutăm apelul aici, după ce am obținut userName-ul corect
        RecipeFavoritesManager.loadFromDisk(this, userName)

        // 5. Navigare către PROFIL
        profileIcon.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        // 6. Navigare către FAVORITE
        favButton.setOnClickListener {
            val intent = Intent(this, FavoritesActivity::class.java)
            startActivity(intent)
        }
        val shoppingCartIcon = findViewById<ImageView>(R.id.shoppingCartIcon)
        shoppingCartIcon.setOnClickListener {
            startActivity(Intent(this, ShoppingListActivity::class.java))
        }
        // --- Logica pentru ingrediente ---

        val ingredients = listOf(
            "Almonds", "Anchovies", "Anise", "Apple", "Apricot", "Artichoke", "Arugula", "Asparagus", "Avocado", "Bacon",
            "Balsamic vinegar", "Banana", "Barley", "Basil", "Bay leaves", "Beans (dry)", "Beef", "Beef entrecote", "Beef mince", "Beef steak",
            "Beer", "Bell pepper", "Blackberries", "Blueberries", "Breadcrumbs", "Brie", "Broccoli", "Brown sugar", "Buckwheat", "Bulgur",
            "Butter", "Buttermilk", "Cabbage", "Cajun spice", "Camembert", "Capers", "Caraway", "Cardamom", "Carrot", "Cashews",
            "Cauliflower", "Cayenne pepper", "Celery", "Cheddar", "Cheese (cottage)", "Cherries", "Chia seeds", "Chicken", "Chicken breast", "Chicken thighs",
            "Chickpeas", "Chives", "Chocolate", "Cilantro", "Cinnamon", "Clams", "Cloves", "Cocoa powder", "Coconut milk", "Coconut oil",
            "Cod", "Coffee", "Condensed milk", "Cooking cream", "Coriander", "Corn", "Cornstarch", "Couscous", "Crab", "Cranberries",
            "Cream", "Cream cheese", "Cucumber", "Cumin", "Currants", "Curry powder", "Dill", "Duck", "Eggplant", "Eggs",
            "Fennel", "Feta", "Figs", "Fish sauce", "Flour (all-purpose)", "Flour (whole wheat)", "Garlic", "Ginger", "Goat cheese", "Gorgonzola",
            "Grapes", "Grapefruit", "Green beans", "Ground beef", "Ham", "Hazelnuts", "Honey", "Horseradish", "Hot pepper", "Kale",
            "Kefir", "Ketchup", "Kidney beans", "Kiwi", "Lamb", "Lard", "Leek", "Lemon", "Lemon juice", "Lentils",
            "Lettuce", "Lime", "Lobster", "Macaroni", "Mackerel", "Mango", "Maple syrup", "Margarine", "Marjoram", "Mascarpone",
            "Mayonnaise", "Milk", "Mint", "Mussels", "Mustard", "Mutton", "Nectarine", "Nutmeg", "Oats", "Octopus",
            "Olive oil", "Olives", "Onion", "Orange", "Oregano", "Oyster sauce", "Oysters", "Paprika (smoked)", "Paprika (sweet)", "Parmesan",
            "Parsley", "Parsnip", "Pasta (Farfalle)", "Pasta (Fusilli)", "Pasta (Penne)", "Pasta (Spaghetti)", "Peach", "Peanuts", "Pear", "Peas",
            "Pecans", "Pepper (black)", "Pepper (white)", "Pine nuts", "Pineapple", "Pistachios", "Plum", "Pomegranate", "Pork", "Pork chops",
            "Potato", "Prawns", "Prosciutto", "Pumpkin", "Pumpkin seeds", "Quail eggs", "Quinoa", "Radish", "Raisins", "Raspberries",
            "Red cabbage", "Red onion", "Ricotta", "Rice (Arborio)", "Rice (Basmati)", "Rice (Brown)", "Rice (White)", "Rosemary", "Saffron", "Sage",
            "Salami", "Salmon", "Salt", "Sardines", "Sausages", "Scallops", "Sea bass", "Sesame oil", "Sesame seeds", "Shrimp",
            "Smoked salmon", "Sour cream", "Soy sauce", "Soybean", "Spinach", "Spring onion", "Squash", "Squid", "Star anise", "Strawberries",
            "Sugar", "Sunflower oil", "Sunflower seeds", "Sweet potato", "Tarragon", "Tea", "Thyme", "Tofu", "Tomato", "Tomato paste",
            "Trout", "Tuna", "Turkey", "Turmeric", "Turnip", "Vanilla extract", "Veal", "Venison", "Vinegar", "Walnuts",
            "Watermelon", "Whipped cream", "White wine", "Yeast", "Yogurt", "Yogurt (Greek)", "Zucchini"
        )

        val adapter = ArrayAdapter(this, R.layout.dropdown_item, ingredients)
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

            val intent = Intent(this, RecipesActivity::class.java)
            intent.putExtra("ingredients", selectedIngredients.joinToString(","))
            startActivity(intent)
        }
    }

    private fun addTag(name: String) {
        val tag = TextView(this)
        tag.text = "$name  ✕"
        tag.setPadding(35, 15, 35, 15)
        tag.setTextColor(resources.getColor(R.color.deep_rose))
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