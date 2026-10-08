package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.model.UserEntity
import com.example.data.model.VerificationStatus
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class FirebaseProfileRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    companion object {
        private const val TAG = "FirebaseProfileRepo"
        private const val USERS_COLLECTION = "users"
    }

    suspend fun syncUserProfile(user: UserEntity): Boolean {
        val currentUid = auth.currentUser?.uid ?: return false
        try {
            val userMap = mutableMapOf<String, Any>(
                "userId" to currentUid,
                "email" to (auth.currentUser?.email ?: user.email),
                "fullName" to user.fullName,
                "username" to user.username,
                "primaryRole" to user.primaryRole,
                "secondaryRoles" to user.secondaryRoles,
                "profession" to user.profession,
                "businessName" to user.businessName,
                "businessCategory" to user.businessCategory,
                "location" to user.location,
                "phone" to user.phone,
                "bio" to user.bio,
                "skills" to user.skills,
                "telegramHandle" to user.telegramHandle,
                "profilePhotoUrl" to (auth.currentUser?.photoUrl?.toString() ?: user.profilePhotoUrl),
                "verificationStatus" to user.verificationStatus,
                "rating" to user.rating.toDouble(),
                "reviewCount" to user.reviewCount.toLong(),
                "updatedAt" to FieldValue.serverTimestamp()
            )

            val docRef = db.collection(USERS_COLLECTION).document(currentUid)
            val existing = docRef.get().await()
            if (!existing.exists()) {
                userMap["createdAt"] = FieldValue.serverTimestamp()
            }
            docRef.set(userMap.filterValues { it != null }, SetOptions.merge()).await()
            Log.d(TAG, "Synced profile successfully to Firestore for $currentUid")
            return true
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing profile to Firestore", e)
            return false
        }
    }

    suspend fun fetchUserProfile(userId: String): UserEntity? {
        try {
            val doc = db.collection(USERS_COLLECTION).document(userId).get().await()
            if (!doc.exists()) return null
            val data = doc.data ?: return null
            return UserEntity(
                id = userId,
                email = data["email"] as? String ?: "",
                passwordHash = "google_auth",
                fullName = data["fullName"] as? String ?: "Orthodox Member",
                username = data["username"] as? String ?: (data["email"] as? String)?.substringBefore("@") ?: "",
                primaryRole = data["primaryRole"] as? String ?: "JOB_SEEKER",
                secondaryRoles = data["secondaryRoles"] as? String ?: "",
                profession = data["profession"] as? String ?: "",
                businessName = data["businessName"] as? String ?: "",
                businessCategory = data["businessCategory"] as? String ?: "",
                location = data["location"] as? String ?: "Addis Ababa",
                phone = data["phone"] as? String ?: "",
                bio = data["bio"] as? String ?: "",
                skills = data["skills"] as? String ?: "",
                telegramHandle = data["telegramHandle"] as? String ?: "",
                profilePhotoUrl = data["profilePhotoUrl"] as? String ?: "",
                verificationStatus = data["verificationStatus"] as? String ?: VerificationStatus.UNVERIFIED.name,
                rating = (data["rating"] as? Number)?.toFloat() ?: 5.0f,
                reviewCount = (data["reviewCount"] as? Number)?.toInt() ?: 0,
                joinedDate = "2026-10-08"
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching profile from Firestore", e)
            return null
        }
    }
}
