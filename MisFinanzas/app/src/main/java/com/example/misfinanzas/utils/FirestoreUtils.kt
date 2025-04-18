package com.example.misfinanzas.utils

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
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

    suspend fun uploadDocument(
        collectionName: String,
        documentId: String,
        data: Any
    ): Boolean {
        return try {
            FirebaseFirestore.getInstance()
                .collection(collectionName)
                .document(documentId)
                .set(data)
                .await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun updateField(
        collectionName: String,
        documentId: String,
        fieldName: String,
        fieldValue: Any
    ): Boolean {
        return try {
            FirebaseFirestore.getInstance()
                .collection(collectionName)
                .document(documentId)
                .update(fieldName, fieldValue)
                .await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend inline fun <reified T> fetchCollectionAs(
        collectionName: String
    ): List<T>? {
        return try {
            val querySnapshot: QuerySnapshot = FirebaseFirestore.getInstance()
                .collection(collectionName)
                .get()
                .await()

            querySnapshot.documents.mapNotNull { document ->
                document.toObject(T::class.java)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}