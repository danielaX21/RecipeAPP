package com.example.recipeappxml

import android.widget.ImageView
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import kotlin.math.sqrt
import android.widget.TextView
import android.view.View

class RecipesActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecipeAdapter
    private val shownRecipes = mutableListOf<Recipe>()

    // Senzori
    private lateinit var sensorManager: SensorManager
    private var acceleration = 0f
    private var currentAcceleration = 0f
    private var lastAcceleration = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipes)

        // 1. Butonul Home (iconița din stânga sus)
        val backBtn = findViewById<ImageView>(R.id.backBtn)
        backBtn.setOnClickListener {
            // Închide activitatea curentă și te trimite automat la Home
            finish()
        }

        // 2. Butonul Favorites (iconița steluță din dreapta sus)
        val favIcon = findViewById<ImageView>(R.id.favIconHeader)
        favIcon.setOnClickListener {
            val intent = Intent(this, FavoritesActivity::class.java)
            startActivity(intent) // Deschide pagina de favorite
        }

        findViewById<TextView>(R.id.filterRating).setOnClickListener {
            shownRecipes.sortByDescending { it.rating }
            adapter.notifyDataSetChanged()
            Toast.makeText(this, "Sorted by Rating", Toast.LENGTH_SHORT).show()
        }

        findViewById<TextView>(R.id.filterTime).setOnClickListener {
            // Extragem doar numerele din string-ul de timp (ex: "25 min" -> 25)
            shownRecipes.sortBy { it.total_time.filter { char -> char.isDigit() }.toIntOrNull() ?: 999 }
            adapter.notifyDataSetChanged()
            Toast.makeText(this, "Sorted by Time", Toast.LENGTH_SHORT).show()
        }
        // Setup Senzor
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL)
        acceleration = 10f
        currentAcceleration = SensorManager.GRAVITY_EARTH
        lastAcceleration = SensorManager.GRAVITY_EARTH

        recyclerView = findViewById(R.id.recipesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = RecipeAdapter(shownRecipes)
        recyclerView.adapter = adapter

        val selectedIngredients = intent.getStringExtra("ingredients")
            ?.split(",")?.map { it.trim().lowercase() }?.filter { it.isNotEmpty() } ?: emptyList()

        loadFromRTDB(selectedIngredients)
    }

    private fun loadFromRTDB(selectedIngredients: List<String>) {
        val ref = FirebaseDatabase.getInstance().getReference("recipes")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val results = mutableListOf<Recipe>()
                for (recipeSnap in snapshot.children) {
                    // Citim exact cheile din baza ta de date
                    val title = recipeSnap.child("title").getValue(String::class.java) ?: ""
                    val ratingValue = recipeSnap.child("rating").getValue(Double::class.java) ?: 0.0
                    val timeValue = recipeSnap.child("total_time").getValue(String::class.java) ?: "N/A"
                    val imageUrl = recipeSnap.child("imageUrl").getValue(String::class.java) ?: ""
                    val directions = recipeSnap.child("directions").getValue(String::class.java) ?: ""

                    val ingredientsList = readIngredientsAsList(recipeSnap.child("ingredients"))

                    // Verificăm dacă ingredientele se potrivesc
                    val matchesAll = selectedIngredients.all { sel ->
                        ingredientsList.any { it.contains(sel, ignoreCase = true) }
                    }

                    if (matchesAll) {
                        results.add(Recipe(
                            title = title,
                            ingredients = ingredientsList,
                            total_time = timeValue, // Acum va lua valoarea reală
                            rating = ratingValue,    // Acum va lua valoarea reală
                            imageUrl = imageUrl,
                            directions = directions
                        ))
                    }
                }
                shownRecipes.clear()
                shownRecipes.addAll(results)
                val noResultsText = findViewById<TextView>(R.id.noResultsText)
                if (results.isEmpty()) {
                    noResultsText.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                } else {
                    noResultsText.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                }
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // În interiorul RecipesActivity.kt

    override fun onSensorChanged(event: SensorEvent) {
        val x = event.values[0]; val y = event.values[1]; val z = event.values[2]
        lastAcceleration = currentAcceleration
        currentAcceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
        val delta = currentAcceleration - lastAcceleration
        acceleration = acceleration * 0.9f + delta

        if (acceleration > 12) { // Prag scuturare
            if (shownRecipes.isNotEmpty()) {
                val randomRecipe = shownRecipes.random()

                // 1. Vibrație
                val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as android.os.Vibrator
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    vibrator.vibrate(android.os.VibrationEffect.createOneShot(200, android.os.VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    vibrator.vibrate(200)
                }

                // 2. Mesaj și Navigare către detalii
                Toast.makeText(this, "Surpriză: ${randomRecipe.title}!", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, RecipeDetailsActivity::class.java)
                intent.putExtra("title", randomRecipe.title)
                val ingredientsText = randomRecipe.ingredientsAsList().joinToString("\n• ", prefix = "• ")
                intent.putExtra("ingredients", ingredientsText)
                intent.putExtra("directions", randomRecipe.directions)
                intent.putExtra("imageUrl", randomRecipe.imageUrl)
                startActivity(intent)
            }
        }
    }
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun readIngredientsAsList(snap: DataSnapshot): List<String> {
        if (snap.childrenCount > 0) return snap.children.mapNotNull { it.getValue(String::class.java) }
        return snap.getValue(String::class.java)?.split(",") ?: emptyList()
    }
}