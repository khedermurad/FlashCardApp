package com.example.flipcards.model

import com.google.firebase.firestore.FirebaseFirestore

private const val TAG = "CollAdapterRepositoryImpl"

class CollAdapterRepositoryImpl(private val db: FirebaseFirestore) : CollAdapterRepository {
    override fun getCollectionTitle(
        uid: String,
        collectionId: String,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("userCollections")
            .document(uid)
            .collection("collections")
            .document(collectionId)
            .get()
            .addOnSuccessListener { result ->
                val collectionTitle = result.getString("title").toString()
                onSuccess(collectionTitle)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    override fun updateCollectionTitle(
        uid: String,
        collectionId: String,
        newTitle: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("userCollections")
            .document(uid)
            .collection("collections")
            .document(collectionId).update("title", newTitle).addOnSuccessListener {
                onSuccess()
            }.addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    override fun deleteCollection(
        uid: String,
        collectionId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val collectionRef = db.collection("userCollections")
            .document(uid)
            .collection("collections")
            .document(collectionId)

        val batch = db.batch()

        collectionRef.collection("documentIds").get().addOnSuccessListener { querySnapshot ->
            for (document in querySnapshot) {
                batch.delete(document.reference)
            }

            batch.delete(collectionRef)
            batch.commit().addOnSuccessListener {
                onSuccess()
            }.addOnFailureListener { e ->
                onFailure(e)
            }
        }.addOnFailureListener { e ->
            onFailure(e)
        }
    }

}