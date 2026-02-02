package com.example.assigment1

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

sealed class UIState<out T> {
    object Loading : UIState<Nothing>()
    data class Success<T>(val data: T) : UIState<T>()
    data class Error(val message: String) : UIState<Nothing>()
}

class ContentViewModel : ViewModel() {
    private val _allContent = MutableStateFlow<List<Content>>(emptyList())
    private val _contentState = MutableStateFlow<UIState<List<Content>>>(UIState.Loading)
    val contentState: StateFlow<UIState<List<Content>>> = _contentState

    private val _categoriesState = MutableStateFlow<UIState<List<Category>>>(UIState.Loading)
    val categoriesState: StateFlow<UIState<List<Category>>> = _categoriesState

    private val _selectedCategoryId = MutableStateFlow<Int?>(null)
    val selectedCategoryId: StateFlow<Int?> = _selectedCategoryId

    init {
        refresh()
    }

    fun refresh() {
        fetchCategories()
        fetchContent()
    }

    fun selectCategory(categoryId: Int?) {
        _selectedCategoryId.value = categoryId
        filterContent()
    }

    private fun filterContent() {
        val selectedId = _selectedCategoryId.value
        if (selectedId == null) {
            _contentState.value = UIState.Success(_allContent.value)
        } else {
            val filtered = _allContent.value.filter { it.categoryId == selectedId }
            _contentState.value = UIState.Success(filtered)
        }
    }

    private fun fetchContent() {
        viewModelScope.launch {
            _contentState.value = UIState.Loading
            try {
                val response = RetrofitClient.apiService.getContent()
                if (response.isSuccessful && response.body()?.success == true) {
                    val content = response.body()?.data ?: emptyList()
                    _allContent.value = content
                    filterContent()
                } else {
                    _contentState.value = UIState.Error(response.body()?.message ?: "Failed to fetch content")
                }
            } catch (e: Exception) {
                _contentState.value = UIState.Error(e.message ?: "An error occurred")
            }
        }
    }

    private fun fetchCategories() {
        viewModelScope.launch {
            _categoriesState.value = UIState.Loading
            try {
                val response = RetrofitClient.apiService.getCategories()
                if (response.isSuccessful && response.body()?.success == true) {
                    _categoriesState.value = UIState.Success(response.body()?.data ?: emptyList())
                } else {
                    _categoriesState.value = UIState.Error(response.body()?.message ?: "Failed to fetch categories")
                }
            } catch (e: Exception) {
                _categoriesState.value = UIState.Error(e.message ?: "An error occurred")
            }
        }
    }
    
    fun createContent(title: String, description: String, categoryId: Int, creatorId: Int) {
        viewModelScope.launch {
            try {
                // For a school project, we just send a simple map. 
                // The backend API expects content creation fields.
                val data = mapOf(
                    "title" to title,
                    "description" to description,
                    "category_id" to categoryId.toString(),
                    "creator_id" to creatorId.toString()
                )
                // Assuming ApiService has a basic createContent method
                // RetrofitClient.apiService.createContent(data) 
                // Then refresh
                fetchContent() 
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
