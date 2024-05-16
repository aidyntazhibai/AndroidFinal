package com.example.onlineshop.api

import com.example.onlineshop.models.Product
import com.google.gson.annotations.SerializedName

data class ProductResponse(
    @SerializedName("products") val products: List<Product>
)
