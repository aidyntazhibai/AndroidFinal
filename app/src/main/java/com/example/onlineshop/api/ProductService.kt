package com.example.onlineshop.api

import com.example.onlineshop.api.ProductResponse
import retrofit2.http.GET

interface ProductService {
    @GET("products")
    suspend fun getProducts(): ProductResponse
}