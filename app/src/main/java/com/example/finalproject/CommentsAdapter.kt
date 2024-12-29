//package com.example.finalproject
//
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageButton
//import android.widget.ImageView
//import android.widget.RatingBar
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore
//
//class CommentsAdapter(
//    private val comments: List<Comment>,
//    private val onEditComment: (Comment) -> Unit,
//    private val onDeleteComment: (Comment) -> Unit,
//    private val currentUserName: String?
//) : RecyclerView.Adapter<CommentsAdapter.CommentViewHolder>() {
//
//    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val userNameTextView: TextView = itemView.findViewById(R.id.tvUserName)
//        val commentTextView: TextView = itemView.findViewById(R.id.tvCommentText)
//        val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
//        val timestampTextView: TextView = itemView.findViewById(R.id.tvTimestamp) // New timestamp TextView
//        val editButton: ImageButton = itemView.findViewById(R.id.btnEditComment)
//        val deleteButton: ImageButton = itemView.findViewById(R.id.btnDeleteComment)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
//        return CommentViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
//        val comment = comments[position]
//
//        holder.userNameTextView.text = comment.userName ?: "Unknown"
//        holder.commentTextView.text = comment.commentText ?: ""
//        holder.ratingBar.rating = comment.rating?.toFloat() ?: 0f
//
//        // Format and set the timestamp
//        comment.timestamp?.let {
//            val timestampText = android.text.format.DateFormat.format("MMM dd, yyyy hh:mm a", it.toDate())
//            holder.timestampTextView.text = timestampText
//        } ?: run {
//            holder.timestampTextView.text = "Unknown time"
//        }
//
//        // Show/hide edit and delete buttons based on ownership
//        if (currentUserName != null && comment.userName == currentUserName) {
//            holder.editButton.visibility = View.VISIBLE
//            holder.deleteButton.visibility = View.VISIBLE
//
//            holder.editButton.setOnClickListener {
//                onEditComment(comment)
//            }
//
//            holder.deleteButton.setOnClickListener {
//                onDeleteComment(comment)
//            }
//        } else {
//            holder.editButton.visibility = View.GONE
//            holder.deleteButton.visibility = View.GONE
//        }
//    }
//
//    override fun getItemCount(): Int {
//        return comments.size
//    }
//}
//
//
//
//
//
