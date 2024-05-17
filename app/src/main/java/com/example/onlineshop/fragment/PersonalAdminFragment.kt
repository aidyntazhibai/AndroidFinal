import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.database.*
import com.example.onlineshop.R


class PersonalAdminFragment : Fragment() {

    private lateinit var listViewProducts: ListView
    private lateinit var editTextNewName: EditText
    private lateinit var editTextNewProductName: EditText
    private lateinit var databaseReference: DatabaseReference
    private var selectedProductName: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_personal_admin, container, false)

        listViewProducts = view.findViewById(R.id.listViewProducts)
        editTextNewName = view.findViewById(R.id.editTextNewName)
        editTextNewProductName = view.findViewById(R.id.editTextNewProductName)
        val buttonSelectProduct = view.findViewById<ImageButton>(R.id.imageButtonSelectProduct)
        val buttonSendRequest = view.findViewById<Button>(R.id.buttonSendRequest)

        databaseReference = FirebaseDatabase.getInstance().getReference("products")

        loadProductNamesFromDatabase()

        buttonSelectProduct.setOnClickListener {
            listViewProducts.visibility = View.VISIBLE
        }

        buttonSendRequest.setOnClickListener {
            val newName = editTextNewProductName.text.toString().trim()
            if (newName.isNotEmpty() && selectedProductName != null) {
                updateProductNameInDatabase(selectedProductName!!, newName)
            } else {
                Toast.makeText(requireContext(), "Please select a product and enter a new name", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun loadProductNamesFromDatabase() {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val productNames = mutableListOf<String>()

                for (snapshot in dataSnapshot.children) {
                    val productName = snapshot.child("title").getValue(String::class.java)
                    productName?.let {
                        productNames.add(it)
                    }
                }

                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, productNames)
                listViewProducts.adapter = adapter

                listViewProducts.onItemClickListener =
                    AdapterView.OnItemClickListener { _, _, position, _ ->
                        val selectedProductName = productNames[position]
                        editTextNewName.setText(selectedProductName)
                        listViewProducts.visibility = View.GONE
                        this@PersonalAdminFragment.selectedProductName = selectedProductName
                    }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    private fun updateProductNameInDatabase(oldName: String, newName: String) {
        val productQuery: Query = databaseReference.orderByChild("title").equalTo(oldName)
        productQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    snapshot.ref.child("title").setValue(newName)
                }
                Toast.makeText(requireContext(), "Product name updated successfully", Toast.LENGTH_SHORT).show()
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }
}
