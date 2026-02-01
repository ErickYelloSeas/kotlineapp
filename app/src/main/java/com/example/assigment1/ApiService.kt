package com.example.assigment1

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @POST("auth/register")
    suspend fun register(@Body data: Map<String, String>): Response<ApiResponse<User>>

    @POST("auth/login")
    suspend fun login(@Body data: Map<String, String>): Response<ApiResponse<LoginResponse>>

    @POST("auth/logout")
    suspend fun logout(): Response<ApiResponse<Unit>>

    @GET("auth/me")
    suspend fun getCurrentUser(): Response<ApiResponse<User>>

    @GET("users")
    suspend fun getUsers(): Response<ApiResponse<List<User>>>

    @GET("content")
    suspend fun getContent(): Response<ApiResponse<List<Content>>>

    @GET("categories")
    suspend fun getCategories(): Response<ApiResponse<List<Category>>>
}
