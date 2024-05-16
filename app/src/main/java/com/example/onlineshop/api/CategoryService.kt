package com.example.onlineshop.api

import com.example.onlineshop.models.Category
import retrofit2.http.GET

interface CategoryService {
    @GET("categories")
    suspend fun getCategories(): List<Category>
}
