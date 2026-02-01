package com.example.assigment1

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class UIState<out T> {
    object Loading : UIState<Nothing>()
    data class Success<T>(val data: T) : UIState<T>()
    data class Error(val message: String) : UIState<Nothing>()
}

class ContentViewModel : ViewModel() {
    private val _contentState = MutableStateFlow<UIState<List<Content>>>(UIState.Loading)
    val contentState: StateFlow<UIState<List<Content>>> = _contentState

    private val _categoriesState = MutableStateFlow<UIState<List<Category>>>(UIState.Loading)
    val categoriesState: StateFlow<UIState<List<Category>>> = _categoriesState

    init {
        refresh()
    }

    fun refresh() {
        fetchCategories()
        fetchContent()
    }

    private fun fetchContent() {
        viewModelScope.launch {
            _contentState.value = UIState.Loading
            try {
                val response = RetrofitClient.apiService.getContent()
                if (response.isSuccessful && response.body()?.success == true) {
                    _contentState.value = UIState.Success(response.body()?.data ?: emptyList())
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
}