package com.example.onlineshop.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.onlineshop.R
import com.example.onlineshop.api.ApiService
import com.example.onlineshop.api.CategoryService
import com.example.onlineshop.databinding.FragmentCatalogBinding
import com.example.onlineshop.models.Category
import com.example.onlineshop.adapter.CategoryAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CatalogFragment : Fragment(), CategoryAdapter.CategoryClickListener {

    private var _binding: FragmentCatalogBinding? = null
    private val binding get() = _binding!!

    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCatalogBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.recyclerViewCategories.layoutManager = LinearLayoutManager(requireContext())
        categoryAdapter = CategoryAdapter(emptyList(), this)
        binding.recyclerViewCategories.adapter = categoryAdapter

        loadCategories()

        return view
    }

    private fun loadCategories() {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val categoryService = ApiService.retrofit.create(CategoryService::class.java)
                val categories = categoryService.getCategories()
                categoryAdapter.updateCategories(categories)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCategoryClick(category: Category) {
        val fragment = CategoryDetailsFragment.newInstance(category.id)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)
            .addToBackStack(null)
            .commit()
    }
}
