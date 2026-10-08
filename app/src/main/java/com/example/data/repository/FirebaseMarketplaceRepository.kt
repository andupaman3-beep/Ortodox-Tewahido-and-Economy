package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.model.FirestoreProduct
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FirebaseMarketplaceRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    companion object {
        private const val TAG = "FirebaseMarketplaceRepo"
        private const val PRODUCTS_COLLECTION = "products"
    }

    private fun requireUserId(): String {
        return auth.currentUser?.uid ?: error("User must be authenticated to perform marketplace operations")
    }

    fun observeAvailableProducts(): Flow<List<FirestoreProduct>> {
        return db.collection(PRODUCTS_COLLECTION)
            .whereEqualTo("isAvailable", true)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(FirestoreProduct::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
            }
    }

    fun observeSellerProducts(sellerId: String): Flow<List<FirestoreProduct>> {
        return db.collection(PRODUCTS_COLLECTION)
            .whereEqualTo("sellerId", sellerId)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(FirestoreProduct::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
            }
    }

    suspend fun createProduct(product: FirestoreProduct): Result<String> {
        val uid = try {
            requireUserId()
        } catch (e: Exception) {
            return Result.failure(e)
        }

        return try {
            val prodId = product.id.ifBlank { "prod_${System.currentTimeMillis()}" }
            val data = mutableMapOf<String, Any>(
                "id" to prodId,
                "sellerId" to uid,
                "sellerName" to product.sellerName.ifBlank { auth.currentUser?.displayName ?: "Seller" },
                "sellerVerified" to product.sellerVerified,
                "title" to product.title,
                "amharicTitle" to product.amharicTitle,
                "description" to product.description,
                "category" to product.category,
                "price" to product.price,
                "currency" to product.currency,
                "quantity" to product.quantity,
                "location" to product.location,
                "condition" to product.condition,
                "imageUrl" to product.imageUrl,
                "isAvailable" to product.isAvailable,
                "deliveryInfo" to product.deliveryInfo,
                "isFeatured" to product.isFeatured,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )

            db.collection(PRODUCTS_COLLECTION).document(prodId).set(data.filterValues { it != null }).await()
            Log.d(TAG, "Successfully created marketplace product: $prodId")
            Result.success(prodId)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating marketplace product", e)
            Result.failure(e)
        }
    }

    suspend fun updateProduct(productId: String, updates: Map<String, Any>): Result<Unit> {
        val uid = try {
            requireUserId()
        } catch (e: Exception) {
            return Result.failure(e)
        }

        return try {
            val mutableUpdates = updates.toMutableMap()
            mutableUpdates["sellerId"] = uid
            mutableUpdates["updatedAt"] = FieldValue.serverTimestamp()

            db.collection(PRODUCTS_COLLECTION).document(productId)
                .set(mutableUpdates.filterValues { it != null }, SetOptions.merge())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating product $productId", e)
            Result.failure(e)
        }
    }

    suspend fun deleteProduct(productId: String): Result<Unit> {
        return try {
            requireUserId()
            db.collection(PRODUCTS_COLLECTION).document(productId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting product $productId", e)
            Result.failure(e)
        }
    }
}
