package com.example.misfinanzas.viewModels

import androidx.lifecycle.ViewModel
import com.example.misfinanzas.repositories.RoomRepository
import com.example.misfinanzas.room.SubscriptionEntity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow

class SubscriptionsViewModel : ViewModel() {

    private val roomRepository = RoomRepository()

    fun getAllSubscriptions(): Flow<List<SubscriptionEntity>> {
        return roomRepository.getAllSubscriptions(getCurrentUserId().toString())
    }

    private fun getCurrentUserId(): String? = FirebaseAuth.getInstance().currentUser?.uid
}