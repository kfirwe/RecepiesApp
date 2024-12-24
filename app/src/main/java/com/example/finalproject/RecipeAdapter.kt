package com.example.finalproject

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class RecipeAdapter(
    private val recipes: List<Recipe>,
    private val onRecipeClick: (Recipe) -> Unit // Pass a lambda for click handling
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageRecipe: ImageView = itemView.findViewById(R.id.imageRecipe)
        val titleRecipe: TextView = itemView.findViewById(R.id.titleRecipe)
        val descRecipe: TextView = itemView.findViewById(R.id.descRecipe)
        val btnSaveRecipe: ImageButton = itemView.findViewById(R.id.btnSaveRecipe)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recipe, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.titleRecipe.text = recipe.title
        holder.descRecipe.text = recipe.description

        // Check if the image is in Base64 format and decode it
        if (!recipe.imageBase64.isNullOrEmpty()) {
            try {
                val imageBytes = Base64.decode(recipe.imageBase64, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                holder.imageRecipe.setImageBitmap(bitmap)
            } catch (e: IllegalArgumentException) {
                // Fallback to Glide in case of an error
                Glide.with(holder.itemView.context)
                    .load(recipe.imageBase64) // Treat as a URL if decoding fails
                    .placeholder(R.drawable.ic_placeholder_image)
                    .into(holder.imageRecipe)
            }
        } else {
            holder.imageRecipe.setImageResource(R.drawable.ic_placeholder_image) // Default placeholder
        }

        holder.itemView.setOnClickListener {
            onRecipeClick(recipe) // Invoke click callback
        }
    }

    override fun getItemCount(): Int {
        return recipes.size
    }
}
