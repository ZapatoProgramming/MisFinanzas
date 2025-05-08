package com.example.misfinanzas.viewModels

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.*

class CreateCategoryViewModel : ViewModel() {
    var categoryName by mutableStateOf("")
        private set
    private var categoryColor by mutableStateOf("#FFFFFF")
    var categoryDescription by mutableStateOf("")
        private set
    
    fun updateCategoryName(name: String) {
        categoryName = name
    }
    
    fun updateCategoryColor(color: String) {
        categoryColor = color
    }
    
    fun updateCategoryDescription(description: String) {
        categoryDescription = description
    }


}