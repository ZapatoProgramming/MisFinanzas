package com.example.misfinanzas.viewModels

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.*
import androidx.lifecycle.viewModelScope
import com.example.misfinanzas.repositories.RoomRepository
import com.example.misfinanzas.room.CategoryEntity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.util.UUID

class CreateCategoryViewModel : ViewModel() {

    val roomRepository = RoomRepository()

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

    fun createCategory() = viewModelScope.launch{
        val categoryEntity = CategoryEntity(
            userId = getCurrentUserId().toString(),
            id = UUID.randomUUID().toString(),
            name = categoryName,
            description = categoryDescription,
            color = categoryColor
        )

        roomRepository.insertCategory(categoryEntity)
    }

    private fun getCurrentUserId(): String? = FirebaseAuth.getInstance().currentUser?.uid
}