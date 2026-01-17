package com.example.recipeappxml

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class RecipeAdapter(
    private val recipes: List<Recipe>
) : RecyclerView.Adapter<RecipeAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.recipeTitle)
        val time: TextView = itemView.findViewById(R.id.recipeTime)
        val difficulty: TextView = itemView.findViewById(R.id.recipeDifficulty)
        val button: Button = itemView.findViewById(R.id.viewDetailsButton)
        val image: ImageView = itemView.findViewById(R.id.recipeImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recipe, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recipe = recipes[position]

        holder.title.text = recipe.title
        holder.time.text = recipe.total_time
        holder.difficulty.text = "${recipe.rating} ★"
        holder.difficulty.setTextColor(holder.itemView.context.getColor(R.color.deep_rose))
        // imaginea din rețetă
        if (recipe.imageUrl.isNotEmpty()) {
            Picasso.get()
                .load(recipe.imageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.image)
        }

        // când apeși View Details → deschide RecipeDetailsActivity
        // În onBindViewHolder, modificăm modul în care formatăm textul pentru a fi mai "fancy"
        holder.button.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, RecipeDetailsActivity::class.java)
            intent.putExtra("title", recipe.title)

            // Folosim un simbol de inimioară sau bulină elegantă pentru listă
// Caută linia unde pui ingredientele în Intent și schimbă prefixul:
            val ingredientsText = recipe.ingredientsAsList().joinToString("\n• ", prefix = "• ")
            intent.putExtra("ingredients", ingredientsText)
            intent.putExtra("directions", recipe.directions.ifEmpty { "No directions available." })
            intent.putExtra("imageUrl", recipe.imageUrl)
            // ADAUGĂ ACESTE DOUĂ LINII:
            intent.putExtra("rating", recipe.rating)
            intent.putExtra("total_time", recipe.total_time)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = recipes.size
}
