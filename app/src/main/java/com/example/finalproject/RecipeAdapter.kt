//package com.example.finalproject
//
//import android.graphics.BitmapFactory
//import android.util.Base64
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageButton
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
//
//class RecipeAdapter(
//    private val recipes: List<Recipe>,
//    private val onRecipeClick: (Recipe) -> Unit
//) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {
//
//    inner class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val title: TextView = itemView.findViewById(R.id.titleRecipe)
//        val description: TextView = itemView.findViewById(R.id.descRecipe)
//        val image: ImageView = itemView.findViewById(R.id.imageRecipe)
//
//        fun bind(recipe: Recipe, onRecipeClick: (Recipe) -> Unit) {
//            title.text = recipe.title
//            description.text = recipe.description
//
//            // Load image using Base64 decoding or Glide/Picasso
//            recipe.imageBase64?.let {
//                val imageBytes = Base64.decode(it, Base64.DEFAULT)
//                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
//                image.setImageBitmap(bitmap)
//            }
//
//            // Set click listener on the entire item view
//            itemView.setOnClickListener {
//                onRecipeClick(recipe)
//            }
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recipe, parent, false)
//        return RecipeViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
//        val recipe = recipes[position]
//        holder.bind(recipe, onRecipeClick)
//    }
//
//    override fun getItemCount(): Int = recipes.size
//}
//
