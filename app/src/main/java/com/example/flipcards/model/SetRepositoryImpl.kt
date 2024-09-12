package com.example.flipcards.model

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

private const val TAG = "SetRepositoryImpl"
class SetRepositoryImpl(private val db: FirebaseFirestore) : SetRepository {
    override fun getReviews(docId: String, onSuccess: (List<Review>) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("cardSets").document(docId).collection("reviews").get()
            .addOnSuccessListener { snapshot ->
                if (snapshot != null && !snapshot.isEmpty) {
                    val reviewsList = snapshot.toObjects(Review::class.java)
                    onSuccess(reviewsList)
                } else {
                    onSuccess(emptyList())
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    override fun deleteDocumentWithSubcollections(docId: String, onDeleteComplete: () -> Unit) {
        val documentRef = db.collection("cardSets").document(docId)

        val batch = db.batch()

        documentRef.collection("reviews").get().addOnSuccessListener { querySnapshot ->
            for (document in querySnapshot) {
                batch.delete(document.reference)
            }
            documentRef.collection("cards").get().addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    batch.delete(document.reference)
                }
                batch.delete(documentRef)
                batch.commit().addOnSuccessListener {
                    onDeleteComplete()
                }.addOnFailureListener { e ->
                    Log.e(TAG, "Fehler beim Löschen des Dokuments oder der Unterkollektionen: $e")
                }
            }.addOnFailureListener { e ->
                Log.e(TAG, "Fehler beim Abrufen der Unterkollektion 'cards': $e")
            }
        }.addOnFailureListener { e ->
            Log.e(TAG, "Fehler beim Abrufen der Unterkollektion 'reviews': $e")
        }
    }

    override fun getUserCollections(
        uid: String,
        onSuccess: (List<Pair<Array<String>, Array<String>>>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("userCollections").document(uid)
            .collection("collections")
            .get().addOnSuccessListener { snapshot ->
                if (snapshot != null && !snapshot.isEmpty) {
                    val titles = snapshot.documents.mapNotNull { it.getString("title") }.toTypedArray()
                    val collectionIds = snapshot.documents.mapNotNull { it.id }.toTypedArray()
                    onSuccess(listOf(Pair(titles, collectionIds)))
                } else {
                    onSuccess(emptyList())
                }
            }.addOnFailureListener { exception ->
                onFailure(exception)
            }

    }

    override fun alreadyRated(docId: String, uid: String, callback: (Boolean) -> Unit) {
        db.collection("cardSets").document(docId)
            .collection("reviews")
            .whereEqualTo("userId", uid).get()
            .addOnSuccessListener { result ->
                callback(result.isEmpty.not())
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    override fun saveDocumentToCollection(userId: String, collectionId: String, docId: String,
                                          onSuccess: () -> Unit,
                                          onAlreadyExists: () -> Unit,
                                          onFailure: (Exception) -> Unit) {
        val documentRef = db.collection("userCollections")
            .document(userId)
            .collection("collections")
            .document(collectionId)
            .collection("documentIds")
            .document(docId)

        documentRef.get().addOnSuccessListener { documentSnapshot ->
            if (!documentSnapshot.exists()) {
                documentRef.set(hashMapOf<String, Any>())
                    .addOnSuccessListener {
                        onSuccess()
                    }
                    .addOnFailureListener { e ->
                        onFailure(e)
                    }
            } else {
                onAlreadyExists()
            }
        }.addOnFailureListener { e ->
            onFailure(e)
        }
    }

    override fun createNewCollection(userId: String, docId: String, title: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val newCollectionRef = db.collection("userCollections")
            .document(userId)
            .collection("collections")
            .document()

        val newCollectionData = hashMapOf("title" to title)

        newCollectionRef.set(newCollectionData)
            .addOnSuccessListener {
                val documentRef = newCollectionRef.collection("documentIds").document(docId)
                documentRef.set(hashMapOf<String, Any>())
                    .addOnSuccessListener {
                        onSuccess()
                    }
                    .addOnFailureListener { e ->
                        onFailure(e)
                    }
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    override fun deleteDocIdFromCollection(
        uid: String,
        collectionId: String,
        docId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val docIdRef = db.collection("userCollections")
            .document(uid)
            .collection("collections")
            .document(collectionId)
            .collection("documentIds")
            .document(docId)

        docIdRef.delete().addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener{ e ->
            onFailure(e)
        }
    }

    override fun saveReview(
        docId: String,
        review: Review,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("cardSets").document(docId).collection("reviews")
            .add(review)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }
}