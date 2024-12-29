//package com.example.finalproject
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.*
//import androidx.fragment.app.DialogFragment
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore
//import com.google.firebase.firestore.Query
//
//class CommentsDialogFragment : DialogFragment() {
//
//    private lateinit var recipeId: String
//    private lateinit var recyclerView: RecyclerView
//    private lateinit var addCommentButton: Button
//    private lateinit var cancelEditButton: Button
//    private lateinit var commentEditText: EditText
//    private lateinit var ratingBar: RatingBar
//    private val comments = mutableListOf<Comment>()
//
//    private var editingComment: Comment? = null // Track the comment being edited
//    private val firestore by lazy { FirebaseFirestore.getInstance() }
//    private val auth by lazy { FirebaseAuth.getInstance() }
//    private var currentUserName: String? = null // To store the logged-in user's name
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        recipeId = arguments?.getString("recipeId") ?: ""
//    }
//
//    override fun onStart() {
//        super.onStart()
//        dialog?.window?.setLayout(
//            (resources.displayMetrics.widthPixels * 0.9).toInt(),
//            ViewGroup.LayoutParams.WRAP_CONTENT
//        )
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        val view = inflater.inflate(R.layout.fragment_comments_dialog, container, false)
//        recyclerView = view.findViewById(R.id.recyclerViewComments)
//        addCommentButton = view.findViewById(R.id.btnAddComment)
//        cancelEditButton = view.findViewById(R.id.btnCancelEdit)
//        commentEditText = view.findViewById(R.id.etCommentText)
//        ratingBar = view.findViewById(R.id.ratingBarInput)
//
//        recyclerView.layoutManager = LinearLayoutManager(requireContext())
//
//        // Fetch current user's name
//        fetchCurrentUserName {
//            fetchComments() // Only fetch comments after the user name is fetched
//        }
//
//        addCommentButton.setOnClickListener {
//            handleAddOrUpdateComment()
//        }
//
//        cancelEditButton.setOnClickListener {
//            exitEditMode()
//        }
//
//        return view
//    }
//
//    private fun fetchCurrentUserName(onFetched: () -> Unit) {
//        val currentUserId = auth.currentUser?.uid
//        if (currentUserId != null) {
//            firestore.collection("users").document(currentUserId)
//                .get()
//                .addOnSuccessListener { document ->
//                    if (document.exists()) {
//                        currentUserName = document.getString("name")
//                    }
//                    onFetched() // Continue after fetching the name
//                }
//                .addOnFailureListener { onFetched() }
//        } else {
//            onFetched()
//        }
//    }
//
//    private fun fetchComments() {
//        firestore.collection("recipes").document(recipeId).collection("comments")
//            .orderBy("timestamp", Query.Direction.DESCENDING)
//            .addSnapshotListener { querySnapshot, error ->
//                if (error != null) return@addSnapshotListener
//
//                comments.clear()
//                comments.addAll(querySnapshot?.documents?.mapNotNull { document ->
//                    document.toObject(Comment::class.java)?.copy(id = document.id)
//                } ?: emptyList())
//
//                recyclerView.adapter = CommentsAdapter(comments, ::onEditComment, ::onDeleteComment, currentUserName)
//            }
//    }
//
//    private fun handleAddOrUpdateComment() {
//        val commentText = commentEditText.text.toString().trim()
//        val rating = ratingBar.rating.toInt()
//        val userName = currentUserName ?: "Anonymous"
//
//        if (commentText.isEmpty()) {
//            Toast.makeText(requireContext(), "Comment text cannot be empty", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        if (editingComment == null) {
//            // Add new comment
//            val commentData = hashMapOf(
//                "userName" to userName,
//                "commentText" to commentText,
//                "rating" to rating,
//                "timestamp" to com.google.firebase.Timestamp.now(),
//                "userId" to auth.currentUser?.uid
//            )
//
//            firestore.collection("recipes").document(recipeId).collection("comments")
//                .add(commentData)
//                .addOnSuccessListener {
//                    Toast.makeText(requireContext(), "Comment added successfully", Toast.LENGTH_SHORT).show()
//                    exitEditMode()
//                }
//                .addOnFailureListener {
//                    Toast.makeText(requireContext(), "Failed to add comment", Toast.LENGTH_SHORT).show()
//                }
//        } else {
//            // Update existing comment
//            val updatedData = mapOf(
//                "commentText" to commentText,
//                "rating" to rating
//            )
//
//            firestore.collection("recipes").document(recipeId).collection("comments")
//                .document(editingComment!!.id)
//                .update(updatedData)
//                .addOnSuccessListener {
//                    Toast.makeText(requireContext(), "Comment updated successfully", Toast.LENGTH_SHORT).show()
//                    exitEditMode()
//                }
//                .addOnFailureListener {
//                    Toast.makeText(requireContext(), "Failed to update comment", Toast.LENGTH_SHORT).show()
//                }
//        }
//    }
//
//    private fun onEditComment(comment: Comment) {
//        editingComment = comment
//        commentEditText.setText(comment.commentText)
//        ratingBar.rating = comment.rating.toFloat()
//        addCommentButton.text = "Edit Comment"
//        cancelEditButton.visibility = View.VISIBLE
//    }
//
//    private fun onDeleteComment(comment: Comment) {
//        firestore.collection("recipes").document(recipeId).collection("comments")
//            .document(comment.id)
//            .delete()
//            .addOnSuccessListener {
//                Toast.makeText(requireContext(), "Comment deleted successfully", Toast.LENGTH_SHORT).show()
//                // Exit edit mode if the deleted comment was being edited
//                if (editingComment?.id == comment.id) {
//                    exitEditMode()
//                }
//            }
//            .addOnFailureListener {
//                Toast.makeText(requireContext(), "Failed to delete comment", Toast.LENGTH_SHORT).show()
//            }
//    }
//
//    private fun exitEditMode() {
//        editingComment = null
//        commentEditText.text.clear()
//        ratingBar.rating = 0f
//        addCommentButton.text = "Add Comment"
//        cancelEditButton.visibility = View.GONE
//    }
//}
