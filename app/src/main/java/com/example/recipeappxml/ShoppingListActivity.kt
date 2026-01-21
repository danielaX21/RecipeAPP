package com.example.recipeappxml

import android.content.Context
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class ShoppingListActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private val items = mutableListOf<String>()
    private lateinit var userName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping_list)

        listView = findViewById(R.id.shoppingListView)
        val backBtn = findViewById<ImageView>(R.id.backBtn)
        val btnClearAll = findViewById<ImageView>(R.id.btnClearAll)
        val btnAddManual = findViewById<Button>(R.id.btnAddManual)

        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        userName = sharedPref.getString("userName", "Chef") ?: "Chef"

        loadItems()

        backBtn.setOnClickListener { finish() }

        // Adăugare Manuală: Utilizatorul scrie exact ce are nevoie
        btnAddManual.setOnClickListener {
            val input = EditText(this)
            input.hint = "e.g. 2 liters of Milk, Eggs, Flour..."
            input.setPadding(50, 40, 50, 40)

            AlertDialog.Builder(this)
                .setTitle("Add to Shopping List")
                .setView(input)
                .setPositiveButton("Add") { _, _ ->
                    val newItem = input.text.toString().trim()
                    if (newItem.isNotEmpty()) {
                        ShoppingListManager.addItem(this, newItem, userName)
                        loadItems()
                        Toast.makeText(this, "Added: $newItem", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        // Ștergere element la click
        listView.setOnItemClickListener { _, _, position, _ ->
            val item = items[position]
            AlertDialog.Builder(this)
                .setTitle("Remove Item")
                .setMessage("Remove '$item'?")
                .setPositiveButton("Remove") { _, _ ->
                    ShoppingListManager.removeItem(this, item, userName)
                    loadItems()
                }
                .setNegativeButton("Keep", null)
                .show()
        }

        btnClearAll?.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Clear List")
                .setMessage("Do you want to delete everything?")
                .setPositiveButton("Clear All") { _, _ ->
                    saveAndClearAll()
                }
                .setNegativeButton("No", null)
                .show()
        }
    }

    private fun loadItems() {
        ShoppingListManager.loadFromDisk(this, userName)
        items.clear()
        items.addAll(ShoppingListManager.getItems())

        adapter = ArrayAdapter(this, R.layout.item_shopping, items)
        listView.adapter = adapter
    }

    private fun saveAndClearAll() {
        val sharedPref = getSharedPreferences("shopping_prefs", Context.MODE_PRIVATE)
        sharedPref.edit().remove("shop_$userName").apply()
        loadItems()
    }
}