package com.example.data.local

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users ORDER BY fullName ASC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: String): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET verificationStatus = :status WHERE id = :userId")
    suspend fun updateVerificationStatus(userId: String, status: String)

    @Query("UPDATE users SET isSuspended = :isSuspended WHERE id = :userId")
    suspend fun setSuspendedStatus(userId: String, isSuspended: Boolean)
}

@Dao
interface JobDao {
    @Query("SELECT * FROM jobs WHERE isActive = 1 ORDER BY createdAt DESC")
    fun getAllActiveJobs(): Flow<List<JobEntity>>

    @Query("SELECT * FROM jobs WHERE employerId = :employerId ORDER BY createdAt DESC")
    fun getJobsByEmployer(employerId: String): Flow<List<JobEntity>>

    @Query("SELECT * FROM jobs WHERE id = :id LIMIT 1")
    suspend fun getJobById(id: String): JobEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJob(job: JobEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJobs(jobs: List<JobEntity>)

    @Query("DELETE FROM jobs WHERE id = :id")
    suspend fun deleteJob(id: String)

    @Query("SELECT * FROM job_applications WHERE applicantId = :applicantId ORDER BY appliedAt DESC")
    fun getApplicationsForApplicant(applicantId: String): Flow<List<ApplicationEntity>>

    @Query("SELECT * FROM job_applications WHERE jobId = :jobId ORDER BY appliedAt DESC")
    fun getApplicationsForJob(jobId: String): Flow<List<ApplicationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApplication(application: ApplicationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApplications(applications: List<ApplicationEntity>)

    @Query("UPDATE job_applications SET status = :status WHERE id = :id")
    suspend fun updateApplicationStatus(id: String, status: String)

    @Query("DELETE FROM job_applications WHERE id = :id")
    suspend fun deleteApplication(id: String)
}

@Dao
interface ProductDao {
    @Query("SELECT * FROM products WHERE isAvailable = 1 ORDER BY createdAt DESC")
    fun getAllAvailableProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE sellerId = :sellerId ORDER BY createdAt DESC")
    fun getProductsBySeller(sellerId: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    suspend fun getProductById(id: String): ProductEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    @Query("DELETE FROM products WHERE id = :id")
    suspend fun deleteProduct(id: String)
}

@Dao
interface B2BDao {
    @Query("SELECT * FROM b2b_listings ORDER BY createdAt DESC")
    fun getAllB2BListings(): Flow<List<B2BEntity>>

    @Query("SELECT * FROM b2b_listings WHERE b2bType = :type ORDER BY createdAt DESC")
    fun getB2BByType(type: String): Flow<List<B2BEntity>>

    @Query("SELECT * FROM b2b_listings WHERE id = :id LIMIT 1")
    suspend fun getB2BById(id: String): B2BEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertB2B(listing: B2BEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertB2BListings(listings: List<B2BEntity>)

    @Query("DELETE FROM b2b_listings WHERE id = :id")
    suspend fun deleteB2B(id: String)
}

@Dao
interface ServiceDao {
    @Query("SELECT * FROM services WHERE isAvailable = 1 ORDER BY createdAt DESC")
    fun getAllServices(): Flow<List<ServiceEntity>>

    @Query("SELECT * FROM services WHERE id = :id LIMIT 1")
    suspend fun getServiceById(id: String): ServiceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertService(service: ServiceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServices(services: List<ServiceEntity>)

    @Query("DELETE FROM services WHERE id = :id")
    suspend fun deleteService(id: String)
}

@Dao
interface MessageDao {
    @Query("SELECT * FROM conversations WHERE participant1Id = :userId OR participant2Id = :userId ORDER BY lastMessageTimestamp DESC")
    fun getConversationsForUser(userId: String): Flow<List<ConversationEntity>>

    @Query("SELECT * FROM messages WHERE conversationId = :convId ORDER BY timestamp ASC")
    fun getMessagesForConversation(convId: String): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conv: ConversationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversations(convs: List<ConversationEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(msg: MessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(msgs: List<MessageEntity>)

    @Query("UPDATE messages SET isRead = 1 WHERE conversationId = :convId AND recipientId = :userId")
    suspend fun markMessagesAsRead(convId: String, userId: String)
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY timestamp DESC")
    fun getNotificationsForUser(userId: String): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notif: NotificationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifs: List<NotificationEntity>)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: String)

    @Query("UPDATE notifications SET isRead = 1 WHERE userId = :userId")
    suspend fun markAllAsRead(userId: String)
}

@Dao
interface ReviewDao {
    @Query("SELECT * FROM reviews WHERE targetUserId = :userId ORDER BY createdAt DESC")
    fun getReviewsForUser(userId: String): Flow<List<ReviewEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ReviewEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReviews(reviews: List<ReviewEntity>)
}

@Dao
interface ReportDao {
    @Query("SELECT * FROM reports ORDER BY createdAt DESC")
    fun getAllReports(): Flow<List<ReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: ReportEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReports(reports: List<ReportEntity>)

    @Query("UPDATE reports SET status = :status WHERE id = :id")
    suspend fun updateReportStatus(id: String, status: String)
}

@Dao
interface CVDao {
    @Query("SELECT * FROM cv_resumes WHERE userId = :userId ORDER BY updatedAt DESC")
    fun getCVsForUser(userId: String): Flow<List<CVEntity>>

    @Query("SELECT * FROM cv_resumes WHERE userId = :userId AND isPrimary = 1 LIMIT 1")
    suspend fun getPrimaryCV(userId: String): CVEntity?

    @Query("SELECT * FROM cv_resumes WHERE id = :id LIMIT 1")
    suspend fun getCVById(id: String): CVEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCV(cv: CVEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCVs(cvs: List<CVEntity>)

    @Query("DELETE FROM cv_resumes WHERE id = :id")
    suspend fun deleteCV(id: String)
}

@Dao
interface DocumentDao {
    @Query("SELECT * FROM documents WHERE userId = :userId ORDER BY uploadedAt DESC")
    fun getDocumentsForUser(userId: String): Flow<List<DocumentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(doc: DocumentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocuments(docs: List<DocumentEntity>)

    @Query("DELETE FROM documents WHERE id = :id")
    suspend fun deleteDocument(id: String)
}

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites WHERE userId = :userId ORDER BY createdAt DESC")
    fun getFavoritesForUser(userId: String): Flow<List<FavoriteEntity>>

    @Query("SELECT COUNT(*) FROM favorites WHERE userId = :userId AND itemId = :itemId")
    suspend fun isFavorite(userId: String, itemId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(fav: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE userId = :userId AND itemId = :itemId")
    suspend fun removeFavorite(userId: String, itemId: String)
}

@Dao
interface VerificationRequestDao {
    @Query("SELECT * FROM verification_requests ORDER BY createdAt DESC")
    fun getAllVerificationRequests(): Flow<List<VerificationRequestEntity>>

    @Query("SELECT * FROM verification_requests WHERE userId = :userId ORDER BY createdAt DESC LIMIT 1")
    suspend fun getRequestForUser(userId: String): VerificationRequestEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVerificationRequest(req: VerificationRequestEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVerificationRequests(reqs: List<VerificationRequestEntity>)

    @Query("UPDATE verification_requests SET status = :status, reviewedBy = :reviewer, updatedAt = :time WHERE id = :id")
    suspend fun updateRequestStatus(id: String, status: String, reviewer: String, time: Long)
}

