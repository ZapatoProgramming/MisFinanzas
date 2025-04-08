package com.example.misfinanzas.utils

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object FirestoreUtils {

    suspend inline fun <reified T> fetchDocumentAs(
        collectionName: String,
        documentId: String
    ): T? {
        return try {
            val document = FirebaseFirestore.getInstance()
                .collection(collectionName)
                .document(documentId)
                .get()
                .await()

            if (document.exists()) {
                document.toObject(T::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}