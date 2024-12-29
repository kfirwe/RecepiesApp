package com.example.finalproject.ui.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalproject.R
import com.example.finalproject.ui.adapters.RecipeAdapter
import com.example.finalproject.data.models.Recipe
import com.example.finalproject.data.repositories.RecipeRepository
import com.example.finalproject.databinding.FragmentSearchRecipeBinding
import com.example.finalproject.viewmodels.SearchRecipeViewModel
import kotlinx.coroutines.launch

class SearchRecipeFragment : Fragment() {

    private var _binding: FragmentSearchRecipeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchRecipeViewModel by viewModels()

    private lateinit var adapter: RecipeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchRecipeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupBottomNavigation()
        setupSearchButton()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = RecipeAdapter(mutableListOf()) { recipe ->
            showCommentsDialog(recipe)
        }

        binding.recyclerViewSearchResults.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@SearchRecipeFragment.adapter
            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.nav_search
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    findNavController().navigate(R.id.homeFragment)
                    true
                }
                R.id.nav_add -> {
                    findNavController().navigate(R.id.addRecipeFragment)
                    true
                }
                R.id.nav_profile -> {
                    findNavController().navigate(R.id.ProfileFragment)
                    true
                }
                R.id.nav_search -> true
                else -> false
            }
        }
    }

    private fun setupSearchButton() {
        binding.btnSearch.setOnClickListener {
            val query = binding.etSearchQuery.text.toString().trim()
            val searchBy = when (binding.spinnerSearchBy.selectedItem.toString()) {
                "Title" -> "title"
                "Description" -> "description"
                else -> "title"
            }
            val condition = binding.spinnerCondition.selectedItem.toString()
            val limit = binding.etLimit.text.toString().toIntOrNull() ?: 10

            if (query.isEmpty()) {
                Toast.makeText(requireContext(), "Enter a search query", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                viewModel.performSearch(query, searchBy, condition, limit)
            }
        }
    }

    private fun observeViewModel() {
        viewModel.recipes.observe(viewLifecycleOwner) { recipes ->
            adapter.updateData(recipes.toMutableList())
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.clearError()
            }
        }
    }

    private fun showCommentsDialog(recipe: Recipe) {
        val dialog = CommentsDialogFragment().apply {
            arguments = Bundle().apply {
                putString("recipeId", recipe.id)
                putBoolean("isProfileView", false) // Allow edit/delete only for own comments
            }
        }
        dialog.show(parentFragmentManager, "CommentsDialogFragment")
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


