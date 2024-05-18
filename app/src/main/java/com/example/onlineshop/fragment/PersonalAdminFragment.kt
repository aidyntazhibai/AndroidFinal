package com.example.onlineshop.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.example.onlineshop.R
import com.example.onlineshop.LoginActivity

class PersonalAdminFragment : Fragment() {

    private lateinit var listViewProducts: ListView
    private lateinit var listViewDeleteProducts: ListView
    private lateinit var editUpdateOldProductName: EditText
    private lateinit var editUpdateNewProductName: EditText
    private lateinit var editDeleteProductName: EditText
    private lateinit var tvEmail: TextView
    private lateinit var tvPassword: TextView
    private lateinit var databaseReference: DatabaseReference
    private var selectedProductName: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_personal_admin, container, false)

        listViewProducts = view.findViewById(R.id.listViewProducts)
        listViewDeleteProducts = view.findViewById(R.id.listViewProductsToDelete)
        editUpdateOldProductName = view.findViewById(R.id.editUpdateOldProductName)
        editUpdateNewProductName = view.findViewById(R.id.editUpdateNewProductName)
        editDeleteProductName = view.findViewById(R.id.editDeleteProductName)
        tvEmail = view.findViewById(R.id.tvEmail)
        tvPassword = view.findViewById(R.id.tvPassword)
        val buttonSelectProduct = view.findViewById<ImageButton>(R.id.imageButtonSelectProduct)
        val buttonSelectProductToDelete = view.findViewById<ImageButton>(R.id.imageButtonSelectProductToDelete)
        val buttonSendUpdateRequest = view.findViewById<Button>(R.id.buttonSendRequest)
        val buttonSendDeleteRequest = view.findViewById<Button>(R.id.buttonDeleteRequest)
        val buttonLogout = view.findViewById<ImageButton>(R.id.buttonLogout)

        databaseReference = FirebaseDatabase.getInstance().getReference("products")

        displayUserCredentials()

        loadProductNamesFromDatabase()

        buttonSelectProduct.setOnClickListener {
            listViewProducts.visibility = View.VISIBLE
        }

        buttonSelectProductToDelete.setOnClickListener {
            listViewDeleteProducts.visibility = View.VISIBLE
        }

        buttonSendUpdateRequest.setOnClickListener {
            val newName = editUpdateNewProductName.text.toString().trim()
            if (newName.isNotEmpty() && selectedProductName != null) {
                updateProductNameInDatabase(selectedProductName!!, newName)
            } else {
                Toast.makeText(requireContext(), "Please select a product and enter a new name", Toast.LENGTH_SHORT).show()
            }
        }

        buttonSendDeleteRequest.setOnClickListener {
            selectedProductName?.let { productName ->
                deleteProductFromDatabase(productName)
            } ?: run {
                Toast.makeText(requireContext(), "Please select a product to delete", Toast.LENGTH_SHORT).show()
            }
        }

        buttonLogout.setOnClickListener {
            logout()
        }

        return view
    }

    private fun displayUserCredentials() {
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            tvEmail.text = "email: ${it.email}"

            val sharedPref = activity?.getSharedPreferences("MySharedPref", Context.MODE_PRIVATE)
            val password = sharedPref?.getString("password", "No Password")
            tvPassword.text = "password: $password"
        }
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
                listViewDeleteProducts.adapter = adapter

                listViewProducts.onItemClickListener =
                    AdapterView.OnItemClickListener { _, _, position, _ ->
                        val selectedProductName = productNames[position]
                        editUpdateOldProductName.setText(selectedProductName)
                        listViewProducts.visibility = View.GONE
                        this@PersonalAdminFragment.selectedProductName = selectedProductName
                    }

                listViewDeleteProducts.onItemClickListener =
                    AdapterView.OnItemClickListener { _, _, position, _ ->
                        val selectedProductName = productNames[position]
                        editDeleteProductName.setText(selectedProductName)
                        listViewDeleteProducts.visibility = View.GONE
                        this@PersonalAdminFragment.selectedProductName = selectedProductName
                    }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle onCancelled event
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
                // Handle onCancelled event
            }
        })
    }

    private fun deleteProductFromDatabase(productName: String) {
        val productQuery: Query = databaseReference.orderByChild("title").equalTo(productName)
        productQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    snapshot.ref.removeValue()
                }
                Toast.makeText(requireContext(), "Product deleted successfully", Toast.LENGTH_SHORT).show()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle onCancelled event
            }
        })
    }

    private fun logout() {
        val sharedPref = activity?.getSharedPreferences("MySharedPref", Context.MODE_PRIVATE)
        val editor = sharedPref?.edit()
        editor?.clear()
        editor?.apply()

        FirebaseAuth.getInstance().signOut()

        val intent = Intent(activity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
