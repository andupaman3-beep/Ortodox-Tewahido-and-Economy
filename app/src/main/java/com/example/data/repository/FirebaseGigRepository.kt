package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.model.FirestoreGig
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FirebaseGigRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    companion object {
        private const val TAG = "FirebaseGigRepo"
        private const val GIGS_COLLECTION = "gigs"
    }

    private fun requireUserId(): String {
        return auth.currentUser?.uid ?: error("User must be authenticated to perform gig operations")
    }

    fun observeActiveGigs(): Flow<List<FirestoreGig>> {
        return db.collection(GIGS_COLLECTION)
            .whereEqualTo("isActive", true)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(FirestoreGig::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
            }
    }

    fun observeEmployerGigs(employerId: String): Flow<List<FirestoreGig>> {
        return db.collection(GIGS_COLLECTION)
            .whereEqualTo("employerId", employerId)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(FirestoreGig::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
            }
    }

    suspend fun createGig(gig: FirestoreGig): Result<String> {
        val uid = try {
            requireUserId()
        } catch (e: Exception) {
            return Result.failure(e)
        }

        return try {
            val gigId = gig.id.ifBlank { "gig_${System.currentTimeMillis()}" }
            val data = mutableMapOf<String, Any>(
                "id" to gigId,
                "employerId" to uid,
                "employerName" to gig.employerName.ifBlank { auth.currentUser?.displayName ?: "Employer" },
                "employerVerified" to gig.employerVerified,
                "title" to gig.title,
                "amharicTitle" to gig.amharicTitle,
                "category" to gig.category,
                "location" to gig.location,
                "salaryRange" to gig.salaryRange,
                "employmentType" to gig.employmentType,
                "experienceLevel" to gig.experienceLevel,
                "description" to gig.description,
                "deadline" to gig.deadline,
                "projectType" to gig.projectType,
                "budgetAmount" to gig.budgetAmount,
                "deliveryDays" to gig.deliveryDays,
                "skillsRequired" to gig.skillsRequired,
                "proposalsCount" to gig.proposalsCount,
                "isActive" to gig.isActive,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )

            db.collection(GIGS_COLLECTION).document(gigId).set(data.filterValues { it != null }).await()
            Log.d(TAG, "Successfully created gig: $gigId")
            Result.success(gigId)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating gig", e)
            Result.failure(e)
        }
    }

    suspend fun updateGig(gigId: String, updates: Map<String, Any>): Result<Unit> {
        val uid = try {
            requireUserId()
        } catch (e: Exception) {
            return Result.failure(e)
        }

        return try {
            val mutableUpdates = updates.toMutableMap()
            mutableUpdates["employerId"] = uid
            mutableUpdates["updatedAt"] = FieldValue.serverTimestamp()

            db.collection(GIGS_COLLECTION).document(gigId)
                .set(mutableUpdates.filterValues { it != null }, SetOptions.merge())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating gig $gigId", e)
            Result.failure(e)
        }
    }

    suspend fun deleteGig(gigId: String): Result<Unit> {
        return try {
            requireUserId()
            db.collection(GIGS_COLLECTION).document(gigId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting gig $gigId", e)
            Result.failure(e)
        }
    }
}
