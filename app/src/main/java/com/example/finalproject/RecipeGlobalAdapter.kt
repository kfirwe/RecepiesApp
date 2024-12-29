//package com.example.finalproject
//
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageButton
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
//
//class RecipeGlobalAdapter(
//    private val recipes: List<GlobalRecipe>,
//    private val onRecipeClick: (GlobalRecipe) -> Unit // Pass a lambda for click handling
//) : RecyclerView.Adapter<RecipeGlobalAdapter.RecipeViewHolder>() {
//
//    class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val imageRecipe: ImageView = itemView.findViewById(R.id.imageRecipe)
//        val titleRecipe: TextView = itemView.findViewById(R.id.titleRecipe)
//        val descRecipe: TextView = itemView.findViewById(R.id.descRecipe)
//        val btnSaveRecipe: ImageButton = itemView.findViewById(R.id.btnSaveRecipe)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.item_recipe, parent, false)
//        return RecipeViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
//        val recipe = recipes[position]
//        holder.titleRecipe.text = recipe.title
//        holder.descRecipe.text = recipe.description
//
//        Glide.with(holder.itemView.context)
//            .load(recipe.imageUrl)
//            .placeholder(R.drawable.ic_placeholder_image)
//            .into(holder.imageRecipe)
//
//        holder.itemView.setOnClickListener {
//            onRecipeClick(recipe) // Invoke click callback
//        }
//    }
//
//    override fun getItemCount(): Int {
//        return recipes.size
//    }
//}