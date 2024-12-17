package com.example.finalproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class RecipeAdapter(private val recipes: List<Recipe>) :
    RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageRecipe: ImageView = itemView.findViewById(R.id.imageRecipe)
        val titleRecipe: TextView = itemView.findViewById(R.id.titleRecipe)
        val descRecipe: TextView = itemView.findViewById(R.id.descRecipe)
        val btnSaveRecipe: ImageButton = itemView.findViewById(R.id.btnSaveRecipe)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recipe, parent, false) // Ensure 'item_recipe' layout exists
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.titleRecipe.text = recipe.title
        holder.descRecipe.text = recipe.description

        // Load the recipe image using Glide
        Glide.with(holder.itemView.context)
            .load(recipe.imageUrl)
            .placeholder(R.drawable.ic_placeholder_image) // Placeholder image
            .into(holder.imageRecipe)

        // Add click listener for save button
        holder.btnSaveRecipe.setOnClickListener {
            // Implement save functionality here
        }
    }

    override fun getItemCount(): Int {
        return recipes.size
    }
}
