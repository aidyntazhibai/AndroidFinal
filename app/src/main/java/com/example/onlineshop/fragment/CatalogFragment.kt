package com.example.onlineshop.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.onlineshop.R
import com.example.onlineshop.databinding.FragmentCatalogBinding

class CatalogFragment : Fragment() {

    private lateinit var binding: FragmentCatalogBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCatalogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSmartphones.setOnClickListener {
            navigateToCategory("smartphones")
            binding.buttonSmartphones.setBackgroundResource(R.drawable.phone_category)
        }

        binding.buttonLaptops.setOnClickListener {
            navigateToCategory("laptops")
        }

        binding.buttonFragrances.setOnClickListener {
            navigateToCategory("fragrances")
        }

        binding.buttonSkincare.setOnClickListener {
            navigateToCategory("skincare")
        }

        binding.buttonGroceries.setOnClickListener {
            navigateToCategory("groceries")
        }

        binding.buttonHomeDecoration.setOnClickListener {
            navigateToCategory("home-decoration")
        }
    }

    private fun navigateToCategory(category: String) {
        val fragment = CategoryProductsFragment.newInstance(category)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)
            .addToBackStack(null)
            .commit()
    }

    companion object {
        @JvmStatic
        fun newInstance() = CatalogFragment()
    }
}
