package com.example.onlineshop.manager

import com.example.onlineshop.models.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

object BasketManager {
    fun addToBasket(product: Product) {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val database = FirebaseDatabase.getInstance()
        val basketRef = database.getReference("baskets").child(user.uid).child(product.id.toString())
        basketRef.setValue(product)
    }

    fun removeFromBasket(product: Product) {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val database = FirebaseDatabase.getInstance()
        val basketRef = database.getReference("baskets").child(user.uid).child(product.id.toString())
        basketRef.removeValue()
    }

    fun getBasket(onSuccess: (List<Product>) -> Unit, onFailure: (Exception) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val database = FirebaseDatabase.getInstance()
        val basketRef = database.getReference("baskets").child(user.uid)

        basketRef.get().addOnSuccessListener { dataSnapshot ->
            val products = dataSnapshot.children.mapNotNull { it.getValue(Product::class.java) }
            onSuccess(products)
        }.addOnFailureListener { exception ->
            onFailure(exception)
        }
    }

    fun clearBasket() {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val database = FirebaseDatabase.getInstance()
        val basketRef = database.getReference("baskets").child(user.uid)
        basketRef.removeValue()
    }
}
