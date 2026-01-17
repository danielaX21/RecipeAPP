package com.example.recipeappxml

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
                    val title = recipeSnap.child("title").getValue(String::class.java) ?: ""
                    val ingredientsList = readIngredientsAsList(recipeSnap.child("ingredients"))

                    val matchesAll = selectedIngredients.all { sel ->
                        ingredientsList.any { it.contains(sel, ignoreCase = true) }
                    }

                    if (matchesAll) {
                        results.add(Recipe(
                            title = title,
                            ingredients = ingredientsList,
                            total_time = recipeSnap.child("total_time").getValue(String::class.java) ?: "",
                            imageUrl = recipeSnap.child("imageUrl").getValue(String::class.java) ?: "",
                            directions = recipeSnap.child("directions").getValue(String::class.java) ?: ""
                        ))
                    }
                }
                shownRecipes.clear()
                shownRecipes.addAll(results)
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