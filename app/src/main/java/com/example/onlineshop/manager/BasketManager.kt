package com.example.onlineshop.manager

import com.example.onlineshop.models.Product

object BasketManager {
    private val basket = mutableListOf<Product>()

    fun addToBasket(product: Product) {
        basket.add(product)
    }

    fun getBasket(): List<Product> {
        return basket
    }

    fun clearBasket() {
        basket.clear()
    }
}
