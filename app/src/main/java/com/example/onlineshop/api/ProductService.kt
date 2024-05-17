package com.example.onlineshop.api

import com.example.onlineshop.models.Product
import com.example.onlineshop.api.ProductResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ProductService {
    @GET("products")
    suspend fun getProducts(): Response<ProductResponse>

    @POST("products")
    suspend fun addProduct(@Body product: Product): Response<Product>

    @DELETE("products/{id}")
    suspend fun deleteProduct(@Path("id") productId: Int): Response<Void>

    @PUT("products/{id}")
    suspend fun updateProduct(@Path("id") productId: Int, @Body product: Product): Response<Product>
}
