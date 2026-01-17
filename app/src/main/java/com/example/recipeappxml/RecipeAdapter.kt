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
        //holder.difficulty.text = recipe.difficulty

        // imaginea din rețetă
        if (recipe.imageUrl.isNotEmpty()) {
            Picasso.get()
                .load(recipe.imageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.image)
        }

        // când apeși View Details → deschide RecipeDetailsActivity
        holder.button.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, RecipeDetailsActivity::class.java)
            intent.putExtra("title", recipe.title)
            val ingredientsText = recipe.ingredientsAsList().joinToString("\n• ", prefix = "• ")
            intent.putExtra("ingredients", ingredientsText)
            intent.putExtra("directions", recipe.directions ?: "No directions available.")
            intent.putExtra("imageUrl", recipe.imageUrl)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = recipes.size
}
