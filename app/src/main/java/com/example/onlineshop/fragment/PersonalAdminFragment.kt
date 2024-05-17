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
    private lateinit var databaseReference: DatabaseReference
    private var selectedProductName: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_personal_admin, container, false)

        listViewProducts = view.findViewById(R.id.listViewProducts)
        editTextNewName = view.findViewById(R.id.editTextNewName)
        val buttonSelectProduct = view.findViewById<ImageButton>(R.id.imageButtonSelectProduct)
        val buttonSendRequest = view.findViewById<Button>(R.id.buttonSendRequest)

        databaseReference = FirebaseDatabase.getInstance().getReference("products")

        loadProductNamesFromDatabase()

        buttonSelectProduct.setOnClickListener {
            listViewProducts.visibility = View.VISIBLE
        }

        buttonSendRequest.setOnClickListener {
            selectedProductName?.let { newName ->
                updateProductNameInDatabase(newName)
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

    private fun updateProductNameInDatabase(newName: String) {
        Toast.makeText(requireContext(), "Request sent to update product name to '$newName'", Toast.LENGTH_SHORT).show()
    }
}
