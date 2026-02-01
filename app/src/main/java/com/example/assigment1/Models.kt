package com.example.assigment1

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    val success: Boolean,
    val data: T?,
    val message: String? = null
)

data class User(
    val id: Int,
    val username: String,
    val email: String,
    @SerializedName("role_id") val roleId: Int,
    @SerializedName("role_name") val roleName: String? = null
)

data class Category(
    val id: Int,
    val name: String,
    val description: String? = null
)

data class Content(
    val id: Int,
    val title: String,
    val description: String,
    @SerializedName("category_id") val categoryId: Int,
    @SerializedName("creator_id") val creatorId: Int,
    @SerializedName("thumbnail_url") val thumbnailUrl: String? = null,
    @SerializedName("video_url") val videoUrl: String? = null,
    @SerializedName("category_name") val categoryName: String? = null,
    @SerializedName("creator_name") val creatorName: String? = null,
    @SerializedName("created_at") val createdAt: String? = null
)

data class LoginResponse(
    val user: User,
    val message: String
)
