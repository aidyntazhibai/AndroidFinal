import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.onlineshop.api.ApiService
import com.example.onlineshop.api.ProductService
import com.example.onlineshop.databinding.FragmentShopBinding
import com.example.onlineshop.models.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ShopFragment : Fragment() {

    private var _binding: FragmentShopBinding? = null
    private val binding get() = _binding!!

    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShopBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.recyclerViewProducts.layoutManager = LinearLayoutManager(requireContext())
        productAdapter = ProductAdapter(emptyList())
        binding.recyclerViewProducts.adapter = productAdapter

        loadProducts()

        return view
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun loadProducts() {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val productService = ApiService.retrofit.create(ProductService::class.java)
                val products = productService.getProducts()
                productAdapter.updateProducts(products)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
