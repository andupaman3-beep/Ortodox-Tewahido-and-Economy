package com.example.data.model

import com.google.firebase.Timestamp

/**
 * Firestore data model for user profiles.
 * Reflects `/users/{userId}` documents.
 */
data class FirestoreUser(
    val userId: String = "",
    val email: String = "",
    val fullName: String = "",
    val username: String = "",
    val primaryRole: String = "JOB_SEEKER",
    val secondaryRoles: String = "",
    val profession: String = "",
    val businessName: String = "",
    val businessCategory: String = "",
    val location: String = "Addis Ababa",
    val phone: String = "",
    val bio: String = "",
    val profilePhotoUrl: String = "",
    val verificationStatus: String = "UNVERIFIED",
    val rating: Double = 5.0,
    val reviewCount: Long = 0L,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
)

/**
 * Firestore data model for Gigs, freelance jobs, and employment postings.
 * Reflects `/gigs/{gigId}` documents.
 */
data class FirestoreGig(
    val id: String = "",
    val employerId: String = "",
    val employerName: String = "",
    val employerVerified: Boolean = false,
    val title: String = "",
    val amharicTitle: String = "",
    val category: String = "",
    val location: String = "Addis Ababa",
    val salaryRange: String = "",
    val employmentType: String = "FULL_TIME",
    val experienceLevel: String = "Intermediate",
    val description: String = "",
    val deadline: String = "",
    val projectType: String = "FIXED_PRICE",
    val budgetAmount: Double = 0.0,
    val deliveryDays: Long = 7L,
    val skillsRequired: String = "",
    val proposalsCount: Long = 0L,
    val isActive: Boolean = true,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
)

/**
 * Firestore data model for Marketplace products and services.
 * Reflects `/products/{productId}` documents.
 */
data class FirestoreProduct(
    val id: String = "",
    val sellerId: String = "",
    val sellerName: String = "",
    val sellerVerified: Boolean = false,
    val title: String = "",
    val amharicTitle: String = "",
    val description: String = "",
    val category: String = "",
    val price: Double = 0.0,
    val currency: String = "ETB",
    val quantity: Long = 1L,
    val location: String = "Addis Ababa",
    val condition: String = "NEW",
    val imageUrl: String = "",
    val isAvailable: Boolean = true,
    val deliveryInfo: String = "Delivery available",
    val isFeatured: Boolean = false,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
)
