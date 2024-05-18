import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.appcompat.widget.SearchView
import com.example.onlineshop.R
import com.example.onlineshop.adapter.ProductAdapter
import com.example.onlineshop.fragment.ProductDetailsFragment
import com.example.onlineshop.models.Product
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ShopFragment : Fragment(), ProductAdapter.ProductClickListener {

    private lateinit var productAdapter: ProductAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var searchView: SearchView
    private var allProducts: MutableList<Product> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_shop, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewProducts)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        searchView = view.findViewById(R.id.searchView)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        productAdapter = ProductAdapter(emptyList(), this)
        recyclerView.adapter = productAdapter

        swipeRefreshLayout.setOnRefreshListener {
            loadProductsFromFirebase()
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterProducts(newText)
                return true
            }
        })

        loadProductsFromFirebase()

        return view
    }

    private fun loadProductsFromFirebase() {
        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("products")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                allProducts.clear()
                for (productSnapshot in snapshot.children) {
                    val product = productSnapshot.getValue(Product::class.java)
                    product?.let {
                        allProducts.add(it)
                    }
                }
                productAdapter.updateProducts(allProducts)
                swipeRefreshLayout.isRefreshing = false
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("ShopFragment", "Error loading products from Firebase", error.toException())

                swipeRefreshLayout.isRefreshing = false
            }
        })
    }

    private fun filterProducts(query: String?) {
        val filteredProducts = if (query.isNullOrEmpty()) {
            allProducts
        } else {
            allProducts.filter {
                it.title.contains(query, ignoreCase = true)
            }
        }
        productAdapter.updateProducts(filteredProducts)
    }

    override fun onProductClick(product: Product) {
        val fragment = ProductDetailsFragment.newInstance(product)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)
            .addToBackStack(null)
            .commit()
    }
}
