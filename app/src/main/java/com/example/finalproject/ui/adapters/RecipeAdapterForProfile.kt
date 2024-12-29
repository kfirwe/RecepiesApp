package com.example.finalproject.ui.adapters


import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.R
import com.example.finalproject.data.models.Recipe

class RecipeAdapterForProfile(
    private var recipes: MutableList<Recipe>, // Change to mutable list for updates
    private val onEditClick: (Recipe) -> Unit,
    private val onDeleteClick: (Recipe) -> Unit,
    private val onChatClick: (Recipe) -> Unit
) : RecyclerView.Adapter<RecipeAdapterForProfile.RecipeViewHolder>() {

    class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recipeImage: ImageView = itemView.findViewById(R.id.ivRecipeImage)
        val recipeTitle: TextView = itemView.findViewById(R.id.tvRecipeTitle)
        val recipeDescription: TextView = itemView.findViewById(R.id.tvRecipeDescription)
        val editButton: ImageButton = itemView.findViewById(R.id.btnEditRecipe)
        val deleteButton: ImageButton = itemView.findViewById(R.id.btnDeleteRecipe)
        val chatButton: ImageButton = itemView.findViewById(R.id.btnChatRecipe)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recepie_profile, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]

        holder.recipeTitle.text = recipe.title
        holder.recipeDescription.text = recipe.description

        recipe.imageBase64?.let {
            val imageBytes = Base64.decode(it, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            holder.recipeImage.setImageBitmap(bitmap)
        }

        holder.editButton.setOnClickListener { onEditClick(recipe) }
        holder.deleteButton.setOnClickListener { onDeleteClick(recipe) }
        holder.chatButton.setOnClickListener { onChatClick(recipe) }
    }

    override fun getItemCount(): Int = recipes.size

    // Add this method to update data dynamically
    fun updateData(newRecipes: List<Recipe>) {
        recipes.clear() // Clear the existing list
        recipes.addAll(newRecipes) // Add the new recipes
        notifyDataSetChanged() // Notify RecyclerView to refresh
    }
}

