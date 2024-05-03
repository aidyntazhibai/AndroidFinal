package com.example.onlineshop.api

import com.example.onlineshop.models.Product
import retrofit2.http.GET

interface ProductService {
    @GET("products")
    suspend fun getProducts(): List<Product>
}