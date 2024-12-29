package com.example.finalproject.ui.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.R
import com.example.finalproject.data.models.Comment
import com.google.firebase.auth.FirebaseAuth

class CommentsAdapter(
    private val comments: List<Comment>,
    private val onEditComment: (Comment) -> Unit,
    private val onDeleteComment: (Comment) -> Unit,
    private val currentUserName: String?,
    private val isProfileView: Boolean // New parameter
) : RecyclerView.Adapter<CommentsAdapter.CommentViewHolder>() {

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userNameTextView: TextView = itemView.findViewById(R.id.tvUserName)
        val commentTextView: TextView = itemView.findViewById(R.id.tvCommentText)
        val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
        val timestampTextView: TextView = itemView.findViewById(R.id.tvTimestamp)
        val editButton: ImageButton = itemView.findViewById(R.id.btnEditComment)
        val deleteButton: ImageButton = itemView.findViewById(R.id.btnDeleteComment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        holder.userNameTextView.text = comment.userName ?: "Unknown"
        holder.commentTextView.text = comment.commentText ?: ""
        holder.ratingBar.rating = comment.rating?.toFloat() ?: 0f
        holder.timestampTextView.text = comment.timestamp?.toDate().toString() ?: "Unknown"

        val isCurrentUserComment = comment.userId == FirebaseAuth.getInstance().currentUser?.uid

        // Show/hide buttons based on ownership and view context
        if (isCurrentUserComment) {
            holder.editButton.visibility = View.VISIBLE
            holder.deleteButton.visibility = View.VISIBLE
        } else {
            holder.editButton.visibility = View.GONE
            holder.deleteButton.visibility = if (isProfileView) View.VISIBLE else View.GONE
        }

        holder.editButton.setOnClickListener { onEditComment(comment) }
        holder.deleteButton.setOnClickListener { onDeleteComment(comment) }
    }


    override fun getItemCount(): Int = comments.size
}

