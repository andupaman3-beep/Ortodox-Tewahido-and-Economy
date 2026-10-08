package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class UserRole(val displayNameEn: String, val displayNameAm: String) {
    JOB_SEEKER("Job Seeker", "ሥራ ፈላጊ"),
    EMPLOYER("Employer", "አሠሪ ድርጅት"),
    BUYER("Buyer", "ገዢ"),
    SELLER("Seller", "ሻጭ"),
    IMPORTER("Importer", "አስመጪ"),
    DISTRIBUTOR("Distributor", "አከፋፋይ"),
    SERVICE_PROVIDER("Service Provider", "የሙያ አገልግሎት ሰጪ"),
    BUSINESS("Business", "የንግድ ድርጅት"),
    ADMIN("Administrator", "ዋና አስተዳዳሪ")
}

enum class VerificationStatus {
    UNVERIFIED,
    PENDING,
    VERIFIED,
    REJECTED
}

enum class ApplicationStatus(val labelEn: String, val labelAm: String) {
    SUBMITTED("Submitted", "ተልኳል"),
    UNDER_REVIEW("Under Review", "በመመርመር ላይ"),
    SHORTLISTED("Shortlisted", "ተመርጧል"),
    INTERVIEW("Interview", "የቃል መጠይቅ"),
    ACCEPTED("Accepted", "ተቀባይነት አግኝቷል"),
    REJECTED("Rejected", "ውድቅ ተደርጓል")
}

enum class EmploymentType(val labelEn: String, val labelAm: String) {
    FULL_TIME("Full Time", "ሙሉ ሰዓት (ቋሚ)"),
    PART_TIME("Part Time", "የትርፍ ሰዓት"),
    CONTRACT("Contract / Project", "የውል / ፕሮጀክት"),
    DAILY_RATE("Daily Rate", "የቀን / አጭር ጊዜ"),
    REMOTE("Remote Work", "የርቀት ሥራ"),
    INTERNSHIP("Internship", "ልምምድ")
}

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val email: String,
    val passwordHash: String,
    val fullName: String,
    val username: String,
    val primaryRole: String, // UserRole name
    val secondaryRoles: String = "", // Comma separated UserRoles
    val profession: String = "",
    val businessName: String = "",
    val businessCategory: String = "",
    val location: String = "Addis Ababa",
    val phone: String = "",
    val bio: String = "",
    val profilePhotoUrl: String = "",
    val verificationStatus: String = VerificationStatus.UNVERIFIED.name,
    val rating: Float = 5.0f,
    val reviewCount: Int = 0,
    val isSuspended: Boolean = false,
    val joinedDate: String = "2025-01-01",
    val verificationDocNote: String = "",
    val skills: String = "Project Management, Woodwork, Digital Accounting",
    val telegramHandle: String = ""
)

@Entity(tableName = "jobs")
data class JobEntity(
    @PrimaryKey val id: String,
    val employerId: String,
    val employerName: String,
    val employerVerified: Boolean,
    val title: String,
    val amharicTitle: String,
    val category: String,
    val location: String,
    val salaryRange: String,
    val employmentType: String, // EmploymentType name
    val experienceLevel: String,
    val description: String,
    val deadline: String,
    val projectType: String = "FIXED_PRICE", // "FIXED_PRICE", "HOURLY", "GIG"
    val budgetAmount: Double = 35000.0,
    val deliveryDays: Int = 7,
    val skillsRequired: String = "Kotlin, Architecture, Design",
    val proposalsCount: Int = 4,
    val createdAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true,
    val employerPhone: String = "+251911223344"
)

@Entity(tableName = "job_applications")
data class ApplicationEntity(
    @PrimaryKey val id: String,
    val jobId: String,
    val jobTitle: String,
    val applicantId: String,
    val applicantName: String,
    val applicantEmail: String,
    val applicantPhone: String,
    val resumeSummary: String,
    val coverNote: String,
    val cvAttachedName: String = "Marta_Wolde_CV.pdf",
    val bidAmount: Double = 30000.0,
    val deliveryDays: Int = 5,
    val milestonesSummary: String = "Milestone 1: Design (50%), Milestone 2: Delivery (50%)",
    val status: String = ApplicationStatus.SUBMITTED.name,
    val appliedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val sellerId: String,
    val sellerName: String,
    val sellerVerified: Boolean,
    val title: String,
    val amharicTitle: String,
    val description: String,
    val category: String,
    val price: Double,
    val currency: String = "ETB",
    val quantity: Int,
    val location: String,
    val condition: String = "NEW", // NEW, LIKE_NEW, USED
    val imageUrl: String = "",
    val isAvailable: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val deliveryInfo: String = "Local delivery available",
    val isFeatured: Boolean = false
)

@Entity(tableName = "b2b_listings")
data class B2BEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val companyName: String,
    val isVerified: Boolean,
    val b2bType: String, // "IMPORTER" or "DISTRIBUTOR"
    val title: String,
    val productOrigin: String,
    val wholesalePrice: Double,
    val currency: String = "ETB",
    val minimumOrderQty: String,
    val totalAvailableQty: String,
    val location: String,
    val deliveryTerms: String,
    val description: String,
    val contactPhone: String,
    val contactEmail: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "services")
data class ServiceEntity(
    @PrimaryKey val id: String,
    val providerId: String,
    val providerName: String,
    val isVerified: Boolean,
    val title: String,
    val amharicTitle: String,
    val category: String,
    val rateDescription: String,
    val location: String,
    val description: String,
    val experienceYears: Int,
    val phone: String,
    val rating: Float = 5.0f,
    val reviewsCount: Int = 0,
    val isAvailable: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey val id: String,
    val participant1Id: String,
    val participant2Id: String,
    val participant1Name: String,
    val participant2Name: String,
    val lastMessage: String,
    val lastMessageTimestamp: Long,
    val unreadCountUser1: Int = 0,
    val unreadCountUser2: Int = 0
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String,
    val conversationId: String,
    val senderId: String,
    val senderName: String,
    val recipientId: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val amharicTitle: String,
    val message: String,
    val type: String, // JOB_APPLICATION, STATUS_CHANGE, INQUIRY, VERIFICATION, REVIEW, etc.
    val isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey val id: String,
    val targetUserId: String,
    val authorId: String,
    val authorName: String,
    val rating: Int,
    val comment: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "reports")
data class ReportEntity(
    @PrimaryKey val id: String,
    val reporterId: String,
    val reporterName: String,
    val targetType: String, // "USER", "JOB", "PRODUCT", "SERVICE"
    val targetId: String,
    val targetTitle: String,
    val reason: String,
    val details: String,
    val status: String = "PENDING", // PENDING, RESOLVED, DISMISSED
    val createdAt: Long = System.currentTimeMillis()
)

// Phase 7: Dedicated CV/Resume System
@Entity(tableName = "cv_resumes")
data class CVEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val fullName: String,
    val professionTitle: String,
    val summary: String,
    val experience: String,
    val education: String,
    val skills: String,
    val languages: String = "Amharic, English, Ge'ez",
    val certifications: String = "",
    val fileName: String = "Resume.pdf",
    val fileType: String = "PDF", // "PDF", "DOCX", "BUILT_IN"
    val fileSizeKb: Int = 240,
    val isPrimary: Boolean = true,
    val isPublic: Boolean = true,
    val updatedAt: Long = System.currentTimeMillis()
)

// Phase 8: Professional Document Management
@Entity(tableName = "documents")
data class DocumentEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val docType: String, // "CERTIFICATE", "PORTFOLIO", "BUSINESS_LICENSE", "PARISH_RECOMMENDATION"
    val fileName: String,
    val fileSizeKb: Int,
    val isPublic: Boolean = false,
    val uploadedAt: Long = System.currentTimeMillis()
)

// Phase 12: Favorites / Saved Items
@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val itemType: String, // "JOB", "PRODUCT", "SERVICE", "B2B"
    val itemId: String,
    val itemTitle: String,
    val itemSubtitle: String,
    val createdAt: Long = System.currentTimeMillis()
)

// Phase 20: Official Community Verification Requests
@Entity(tableName = "verification_requests")
data class VerificationRequestEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val userName: String,
    val userEmail: String,
    val parishChurchName: String,
    val idDocType: String, // "NATIONAL_ID", "PARISH_CERTIFICATE", "BUSINESS_LICENSE"
    val docFileName: String,
    val notes: String,
    val status: String = "PENDING", // "PENDING", "APPROVED", "REJECTED"
    val reviewedBy: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
