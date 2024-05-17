package com.example.onlineshop.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.onlineshop.R
import com.example.onlineshop.adapter.ProductImagesAdapter
import com.example.onlineshop.databinding.FragmentProductDetailsBinding
import com.example.onlineshop.manager.BasketManager
import com.example.onlineshop.models.Product

class ProductDetailsFragment : Fragment() {

    private lateinit var binding: FragmentProductDetailsBinding
    private var product: Product? = null
    private var isFavorite: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        product = arguments?.getParcelable(ARG_PRODUCT)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        product?.let { product ->
            binding.textViewTitle.text = product.title
            binding.textViewPrice.text = getString(R.string.product_price, product.price)
            binding.textViewDescription.text = product.description
            binding.textViewCategory.text = product.category
            binding.textViewDiscount.text = getString(R.string.product_discount, product.discountPercentage)
            binding.textViewRating.text = getString(R.string.product_rating, product.rating)
            binding.textViewStock.text = getString(R.string.product_stock, product.stock)
            binding.textViewBrand.text = product.brand

            val adapter = ProductImagesAdapter(product.images)
            binding.viewPagerProductImages.adapter = adapter

            binding.buttonAddToCart.setOnClickListener {
                BasketManager.addToBasket(product)
                Toast.makeText(requireContext(), "Добавлено в корзину", Toast.LENGTH_SHORT).show()
            }

            val sharedPreferences = requireContext().getSharedPreferences("favorites", Context.MODE_PRIVATE)
            val favoriteIds = sharedPreferences.getStringSet("favorites", mutableSetOf()) ?: mutableSetOf()
            isFavorite = favoriteIds.contains(product.id.toString())

            updateFavoriteIcon()

            binding.buttonFavorite.setOnClickListener {
                isFavorite = !isFavorite
                updateFavoriteIcon()
                handleFavorite(product, isFavorite)
            }
        }
    }

    private fun updateFavoriteIcon() {
        if (isFavorite) {
            binding.buttonFavorite.setImageResource(R.drawable.ic_favorite)
        } else {
            binding.buttonFavorite.setImageResource(R.drawable.ic_favorite_border)
        }
    }

    private fun handleFavorite(product: Product, isFavorite: Boolean) {
        val sharedPreferences = requireContext().getSharedPreferences("favorites", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val favorites = sharedPreferences.getStringSet("favorites", mutableSetOf()) ?: mutableSetOf()

        if (isFavorite) {
            favorites.add(product.id.toString())
        } else {
            favorites.remove(product.id.toString())
        }

        editor.putStringSet("favorites", favorites)
        editor.apply()
    }

    companion object {
        private const val ARG_PRODUCT = "product"

        @JvmStatic
        fun newInstance(product: Product) =
            ProductDetailsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PRODUCT, product)
                }
            }
    }
}
