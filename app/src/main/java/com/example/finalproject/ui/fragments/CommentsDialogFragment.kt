package com.example.finalproject.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.R
import com.example.finalproject.data.models.Comment
import com.example.finalproject.ui.adapters.CommentsAdapter
import com.example.finalproject.viewmodels.CommentsViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp

class CommentsDialogFragment : DialogFragment() {

    private val viewModel: CommentsViewModel by viewModels()
    private lateinit var recipeId: String
    private lateinit var recyclerView: RecyclerView
    private lateinit var addCommentButton: Button
    private lateinit var cancelEditButton: Button
    private lateinit var commentEditText: EditText
    private lateinit var ratingBar: RatingBar
    private var isProfileView: Boolean = false // Add this variable
    private var userName: String? = null // Store the fetched user name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recipeId = arguments?.getString("recipeId") ?: ""
        isProfileView = arguments?.getBoolean("isProfileView", false) ?: false // Retrieve from arguments
        fetchCurrentUserName()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_comments_dialog, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewComments)
        addCommentButton = view.findViewById(R.id.btnAddComment)
        cancelEditButton = view.findViewById(R.id.btnCancelEdit)
        commentEditText = view.findViewById(R.id.etCommentText)
        ratingBar = view.findViewById(R.id.ratingBarInput)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        setupObservers()

        addCommentButton.setOnClickListener {
            handleAddOrUpdateComment()
        }

        cancelEditButton.setOnClickListener {
            resetEditingState()
        }

        viewModel.fetchComments(recipeId)
        return view
    }

    private fun setupObservers() {
        viewModel.comments.observe(viewLifecycleOwner) { comments ->
            recyclerView.adapter = CommentsAdapter(
                comments = comments,
                onEditComment = { comment ->
                    if (isProfileView || comment.userId == FirebaseAuth.getInstance().currentUser?.uid) {
                        viewModel.setEditingComment(comment)
                        commentEditText.setText(comment.commentText)
                        ratingBar.rating = comment.rating?.toFloat() ?: 0f
                        showEditingState()
                    } else {
                        Toast.makeText(requireContext(), "You cannot edit this comment", Toast.LENGTH_SHORT).show()
                    }
                },
                onDeleteComment = { comment ->
                    if (isProfileView || comment.userId == FirebaseAuth.getInstance().currentUser?.uid) {
                        viewModel.deleteComment(recipeId, comment.id)
                        resetEditingState() // Reset editing state if the deleted comment was being edited
                    } else {
                        Toast.makeText(requireContext(), "You cannot delete this comment", Toast.LENGTH_SHORT).show()
                    }
                },
                currentUserName = userName, // Pass the fetched user name
                isProfileView = isProfileView
            )
        }

        viewModel.isEditing.observe(viewLifecycleOwner) { editingComment ->
            if (editingComment != null) {
                showEditingState()
            } else {
                resetEditingState()
            }
        }
    }

    private fun fetchCurrentUserName() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance().collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                userName = document.getString("name") ?: "Anonymous"
            }
            .addOnFailureListener {
                userName = "Anonymous"
            }
    }


    private fun handleAddOrUpdateComment() {
        val commentText = commentEditText.text.toString().trim()
        val rating = ratingBar.rating.toInt()
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (commentText.isEmpty()) {
            Toast.makeText(requireContext(), "Comment cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        // Ensure no heavy operations block the main thread
        val editingComment = viewModel.isEditing.value
        if (editingComment == null) {
            val newComment = Comment(
                userName = userName ?: "Anonymous", // Use fetched user name
                userId = userId,
                commentText = commentText,
                rating = rating,
                timestamp = Timestamp.now()
            )
            viewModel.addComment(recipeId, newComment)
        } else {
            val updatedData = mapOf(
                "commentText" to commentText,
                "rating" to rating
            )
            viewModel.updateComment(recipeId, editingComment.id, updatedData)
        }
//        resetEditingState() // Reset editing state after adding/updating
    }


    private fun resetEditingState() {
        println("resetEditingState called")

        // Check if already in the reset state to prevent looping
        if (viewModel.isEditing.value == null) {
            println("Already in reset state, skipping reset")
            return
        }

        viewModel.setEditingComment(null) // Notify ViewModel to reset editing state

        // Reset UI components directly on the main thread
        try {
            commentEditText.text.clear()
            println("commentEditText cleared")

            ratingBar.rating = 0f
            println("ratingBar reset")

            cancelEditButton.visibility = View.GONE
            println("cancelEditButton hidden")

            addCommentButton.text = "Add Comment"
            println("addCommentButton text set to 'Add Comment'")
        } catch (e: Exception) {
            println("Error resetting editing state: ${e.message}")
        }
    }



    private fun showEditingState() {
        cancelEditButton.visibility = View.VISIBLE
        addCommentButton.text = "Update Comment"
    }
}
