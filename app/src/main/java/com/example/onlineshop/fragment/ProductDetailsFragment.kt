package com.example.onlineshop.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.onlineshop.R
import com.example.onlineshop.adapter.ProductAdapter
import com.example.onlineshop.adapter.ProductImagesAdapter
import com.example.onlineshop.databinding.FragmentProductDetailsBinding
import com.example.onlineshop.manager.BasketManager
import com.example.onlineshop.models.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ProductDetailsFragment : Fragment(), ProductAdapter.ProductClickListener {

    private var _binding: FragmentProductDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: FirebaseDatabase
    private lateinit var productsRef: DatabaseReference
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
        _binding = FragmentProductDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = FirebaseDatabase.getInstance()
        productsRef = database.getReference("products")

        binding.topAppBar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        product?.let { product ->
            bindProductDetails(product)
            setupFavoriteButton(product)
            binding.buttonAddToCart.setOnClickListener {
                addToBasket(product)
            }
        }

        loadProductFromFirebase()
    }

    private fun loadProductFromFirebase() {
        val productId = product?.id.toString()
        productsRef.child(productId).get().addOnSuccessListener { dataSnapshot ->
            val product = dataSnapshot.getValue(Product::class.java)
            product?.let {
                bindProductDetails(it)
                setupFavoriteButton(it)
            }
        }.addOnFailureListener { e ->
            Log.e("ProductDetailsFragment", "Error loading product details from Firebase", e)
            Toast.makeText(context, "Failed to load product details", Toast.LENGTH_SHORT).show()
        }
    }

    private fun bindProductDetails(product: Product) {
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
    }

    private fun setupFavoriteButton(product: Product) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            binding.buttonFavorite.setImageResource(R.drawable.ic_favorite_border)
            return
        }

        val favoritesRef = FirebaseDatabase.getInstance().getReference("favorites").child(user.uid)
        favoritesRef.child(product.id.toString()).get().addOnSuccessListener { dataSnapshot ->
            isFavorite = dataSnapshot.exists()
            updateFavoriteIcon()

            binding.buttonFavorite.setOnClickListener {
                isFavorite = !isFavorite
                updateFavoriteIcon()
                handleFavorite(product, isFavorite)
            }
        }
    }

    private fun updateFavoriteIcon() {
        if (_binding != null) {
            if (isFavorite) {
                binding.buttonFavorite.setImageResource(R.drawable.ic_favorite)
            } else {
                binding.buttonFavorite.setImageResource(R.drawable.ic_favorite_border)
            }
        }
    }

    private fun handleFavorite(product: Product, isFavorite: Boolean) {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val favoritesRef = FirebaseDatabase.getInstance().getReference("favorites").child(user.uid)

        if (isFavorite) {
            favoritesRef.child(product.id.toString()).setValue(true)
        } else {
            favoritesRef.child(product.id.toString()).removeValue()
        }
    }

    private fun addToBasket(product: Product) {
        BasketManager.addToBasket(product)
        Toast.makeText(requireContext(), "Added to cart", Toast.LENGTH_SHORT).show()
    }

    override fun onProductClick(product: Product) {
        // Handle product click if needed
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
