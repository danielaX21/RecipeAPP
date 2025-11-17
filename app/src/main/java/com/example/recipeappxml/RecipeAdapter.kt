package com.example.recipeappxml

import android.content.Context
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
    private val context: Context,
    private val recipes: List<Recipe>,
    private val onClick: (Recipe) -> Unit
) : RecyclerView.Adapter<RecipeAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.recipeTitle)
        val time: TextView = itemView.findViewById(R.id.recipeTime)
        val difficulty: TextView = itemView.findViewById(R.id.recipeDifficulty)
        val button: Button = itemView.findViewById(R.id.viewDetailsButton)
        val image: ImageView = itemView.findViewById(R.id.recipeImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_recipe, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recipe = recipes[position]

        holder.title.text = recipe.title
        holder.time.text = recipe.time
        holder.difficulty.text = recipe.difficulty

        // 📸 Încarcă imaginea dacă există
        if (recipe.imageUrl.isNotEmpty()) {
            Picasso.get()
                .load(recipe.imageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.image)
        }

        // 🔸 Când apeși pe „View Details”, deschide pagina detaliilor
        holder.button.setOnClickListener {
            val intent = Intent(context, RecipeDetailsActivity::class.java)
            intent.putExtra("title", recipe.title)
            intent.putExtra("ingredients", recipe.ingredients.joinToString("\n• "))
            intent.putExtra("directions", recipe.directions ?: "No directions available.")
            intent.putExtra("imageUrl", recipe.imageUrl)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = recipes.size
}
