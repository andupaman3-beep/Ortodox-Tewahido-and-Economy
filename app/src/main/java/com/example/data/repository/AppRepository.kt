package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppRepository(private val db: AppDatabase) {

    private val userDao = db.userDao()
    private val jobDao = db.jobDao()
    private val productDao = db.productDao()
    private val b2bDao = db.b2bDao()
    private val serviceDao = db.serviceDao()
    private val messageDao = db.messageDao()
    private val notificationDao = db.notificationDao()
    private val reviewDao = db.reviewDao()
    private val reportDao = db.reportDao()
    private val cvDao = db.cvDao()
    private val documentDao = db.documentDao()
    private val favoriteDao = db.favoriteDao()
    private val verificationRequestDao = db.verificationRequestDao()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            seedInitialDataIfNeeded()
        }
    }

    private suspend fun seedInitialDataIfNeeded() {
        val existingUsers = userDao.getAllUsers().first()
        if (existingUsers.isEmpty()) {
            userDao.insertUsers(SeedData.users)
            jobDao.insertJobs(SeedData.jobs)
            jobDao.insertApplications(SeedData.applications)
            productDao.insertProducts(SeedData.products)
            b2bDao.insertB2BListings(SeedData.b2bListings)
            serviceDao.insertServices(SeedData.services)
            messageDao.insertConversations(SeedData.conversations)
            messageDao.insertMessages(SeedData.messages)
            notificationDao.insertNotifications(SeedData.notifications)
            reviewDao.insertReviews(SeedData.reviews)
            reportDao.insertReports(SeedData.reports)
            cvDao.insertCVs(SeedData.cvResumes)
            documentDao.insertDocuments(SeedData.documents)
            SeedData.favorites.forEach { favoriteDao.insertFavorite(it) }
            verificationRequestDao.insertVerificationRequests(SeedData.verificationRequests)
        }
    }

    // Users
    fun getAllUsers(): Flow<List<UserEntity>> = userDao.getAllUsers()
    suspend fun getUserById(id: String): UserEntity? = withContext(Dispatchers.IO) { userDao.getUserById(id) }
    suspend fun getUserByEmail(email: String): UserEntity? = withContext(Dispatchers.IO) { userDao.getUserByEmail(email) }
    suspend fun insertUser(user: UserEntity) = withContext(Dispatchers.IO) { userDao.insertUser(user) }
    suspend fun updateUser(user: UserEntity) = withContext(Dispatchers.IO) { userDao.updateUser(user) }
    suspend fun updateVerificationStatus(userId: String, status: String) = withContext(Dispatchers.IO) {
        userDao.updateVerificationStatus(userId, status)
        val notifTitle = if (status == VerificationStatus.VERIFIED.name) "Verification Approved" else "Verification Update"
        val notifAm = if (status == VerificationStatus.VERIFIED.name) "የአባልነት ማረጋገጫዎ ጸድቋል!" else "የአባልነት ማረጋገጫ ሁኔታ ተቀይሯል"
        val notifMsg = if (status == VerificationStatus.VERIFIED.name)
            "Your profile is now verified as an Orthodox Community member."
        else "Verification status updated to $status."

        notificationDao.insertNotification(
            NotificationEntity(
                id = "notif_${System.currentTimeMillis()}",
                userId = userId,
                title = notifTitle,
                amharicTitle = notifAm,
                message = notifMsg,
                type = "VERIFICATION",
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun setSuspendedStatus(userId: String, isSuspended: Boolean) = withContext(Dispatchers.IO) {
        userDao.setSuspendedStatus(userId, isSuspended)
    }

    // Jobs
    fun getAllActiveJobs(): Flow<List<JobEntity>> = jobDao.getAllActiveJobs()
    fun getJobsByEmployer(employerId: String): Flow<List<JobEntity>> = jobDao.getJobsByEmployer(employerId)
    suspend fun getJobById(id: String): JobEntity? = withContext(Dispatchers.IO) { jobDao.getJobById(id) }
    suspend fun insertJob(job: JobEntity) = withContext(Dispatchers.IO) { jobDao.insertJob(job) }
    suspend fun deleteJob(id: String) = withContext(Dispatchers.IO) { jobDao.deleteJob(id) }

    // Applications
    fun getApplicationsForApplicant(applicantId: String): Flow<List<ApplicationEntity>> =
        jobDao.getApplicationsForApplicant(applicantId)
    fun getApplicationsForJob(jobId: String): Flow<List<ApplicationEntity>> =
        jobDao.getApplicationsForJob(jobId)

    suspend fun submitApplication(application: ApplicationEntity, employerId: String) = withContext(Dispatchers.IO) {
        jobDao.insertApplication(application)
        notificationDao.insertNotification(
            NotificationEntity(
                id = "notif_${System.currentTimeMillis()}",
                userId = employerId,
                title = "New Application for ${application.jobTitle}",
                amharicTitle = "አዲስ ማመልከቻ ለ ${application.jobTitle} ደርሶዎታል",
                message = "${application.applicantName} submitted a proposal (${application.bidAmount} ETB). Attached CV: ${application.cvAttachedName}",
                type = "JOB_APPLICATION",
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun updateApplicationStatus(id: String, status: String, applicantId: String, jobTitle: String) = withContext(Dispatchers.IO) {
        jobDao.updateApplicationStatus(id, status)
        notificationDao.insertNotification(
            NotificationEntity(
                id = "notif_${System.currentTimeMillis()}",
                userId = applicantId,
                title = "Application Update: $jobTitle",
                amharicTitle = "የማመልከቻዎ ሁኔታ ተቀይሯል: $jobTitle",
                message = "Your application status for $jobTitle is now: $status",
                type = "STATUS_CHANGE",
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun withdrawApplication(id: String) = withContext(Dispatchers.IO) {
        jobDao.deleteApplication(id)
    }

    // Products
    fun getAllProducts(): Flow<List<ProductEntity>> = productDao.getAllAvailableProducts()
    fun getProductsBySeller(sellerId: String): Flow<List<ProductEntity>> = productDao.getProductsBySeller(sellerId)
    suspend fun getProductById(id: String): ProductEntity? = withContext(Dispatchers.IO) { productDao.getProductById(id) }
    suspend fun insertProduct(product: ProductEntity) = withContext(Dispatchers.IO) { productDao.insertProduct(product) }
    suspend fun deleteProduct(id: String) = withContext(Dispatchers.IO) { productDao.deleteProduct(id) }

    // B2B
    fun getAllB2BListings(): Flow<List<B2BEntity>> = b2bDao.getAllB2BListings()
    fun getB2BByType(type: String): Flow<List<B2BEntity>> = b2bDao.getB2BByType(type)
    suspend fun getB2BById(id: String): B2BEntity? = withContext(Dispatchers.IO) { b2bDao.getB2BById(id) }
    suspend fun insertB2B(listing: B2BEntity) = withContext(Dispatchers.IO) { b2bDao.insertB2B(listing) }
    suspend fun deleteB2B(id: String) = withContext(Dispatchers.IO) { b2bDao.deleteB2B(id) }

    // Services
    fun getAllServices(): Flow<List<ServiceEntity>> = serviceDao.getAllServices()
    suspend fun getServiceById(id: String): ServiceEntity? = withContext(Dispatchers.IO) { serviceDao.getServiceById(id) }
    suspend fun insertService(service: ServiceEntity) = withContext(Dispatchers.IO) { serviceDao.insertService(service) }
    suspend fun deleteService(id: String) = withContext(Dispatchers.IO) { serviceDao.deleteService(id) }

    // Messaging
    fun getConversationsForUser(userId: String): Flow<List<ConversationEntity>> =
        messageDao.getConversationsForUser(userId)
    fun getMessagesForConversation(convId: String): Flow<List<MessageEntity>> =
        messageDao.getMessagesForConversation(convId)

    suspend fun sendMessage(
        conversationId: String,
        senderId: String,
        senderName: String,
        recipientId: String,
        recipientName: String,
        content: String
    ) = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val msg = MessageEntity(
            id = "msg_$now",
            conversationId = conversationId,
            senderId = senderId,
            senderName = senderName,
            recipientId = recipientId,
            content = content,
            timestamp = now,
            isRead = false
        )
        messageDao.insertMessage(msg)

        val conv = ConversationEntity(
            id = conversationId,
            participant1Id = senderId,
            participant2Id = recipientId,
            participant1Name = senderName,
            participant2Name = recipientName,
            lastMessage = content,
            lastMessageTimestamp = now,
            unreadCountUser1 = 0,
            unreadCountUser2 = 1
        )
        messageDao.insertConversation(conv)

        notificationDao.insertNotification(
            NotificationEntity(
                id = "notif_$now",
                userId = recipientId,
                title = "New Message from $senderName",
                amharicTitle = "አዲስ መልእክት ከ $senderName ደርሶዎታል",
                message = content.take(60),
                type = "MESSAGE",
                timestamp = now
            )
        )
    }

    suspend fun markMessagesAsRead(convId: String, userId: String) = withContext(Dispatchers.IO) {
        messageDao.markMessagesAsRead(convId, userId)
    }

    // Notifications
    fun getNotificationsForUser(userId: String): Flow<List<NotificationEntity>> =
        notificationDao.getNotificationsForUser(userId)

    suspend fun markNotificationAsRead(id: String) = withContext(Dispatchers.IO) {
        notificationDao.markAsRead(id)
    }

    suspend fun markAllNotificationsAsRead(userId: String) = withContext(Dispatchers.IO) {
        notificationDao.markAllAsRead(userId)
    }

    // Reviews
    fun getReviewsForUser(userId: String): Flow<List<ReviewEntity>> = reviewDao.getReviewsForUser(userId)
    suspend fun insertReview(review: ReviewEntity) = withContext(Dispatchers.IO) {
        reviewDao.insertReview(review)
        notificationDao.insertNotification(
            NotificationEntity(
                id = "notif_${System.currentTimeMillis()}",
                userId = review.targetUserId,
                title = "New Review Received",
                amharicTitle = "አዲስ አስተያየትና ደረጃ ተሰጥቶዎታል",
                message = "${review.authorName} rated you ${review.rating} stars: \"${review.comment.take(40)}\"",
                type = "REVIEW",
                timestamp = System.currentTimeMillis()
            )
        )
    }

    // Reports
    fun getAllReports(): Flow<List<ReportEntity>> = reportDao.getAllReports()
    suspend fun submitReport(report: ReportEntity) = withContext(Dispatchers.IO) {
        reportDao.insertReport(report)
    }
    suspend fun updateReportStatus(id: String, status: String) = withContext(Dispatchers.IO) {
        reportDao.updateReportStatus(id, status)
    }

    // Phase 7: CV / Resumes
    fun getCVsForUser(userId: String): Flow<List<CVEntity>> = cvDao.getCVsForUser(userId)
    suspend fun getPrimaryCV(userId: String): CVEntity? = withContext(Dispatchers.IO) { cvDao.getPrimaryCV(userId) }
    suspend fun insertCV(cv: CVEntity) = withContext(Dispatchers.IO) { cvDao.insertCV(cv) }
    suspend fun deleteCV(id: String) = withContext(Dispatchers.IO) { cvDao.deleteCV(id) }

    // Phase 8: Documents
    fun getDocumentsForUser(userId: String): Flow<List<DocumentEntity>> = documentDao.getDocumentsForUser(userId)
    suspend fun insertDocument(doc: DocumentEntity) = withContext(Dispatchers.IO) { documentDao.insertDocument(doc) }
    suspend fun deleteDocument(id: String) = withContext(Dispatchers.IO) { documentDao.deleteDocument(id) }

    // Phase 12: Favorites
    fun getFavoritesForUser(userId: String): Flow<List<FavoriteEntity>> = favoriteDao.getFavoritesForUser(userId)
    suspend fun isFavorite(userId: String, itemId: String): Boolean = withContext(Dispatchers.IO) {
        favoriteDao.isFavorite(userId, itemId) > 0
    }
    suspend fun toggleFavorite(userId: String, itemId: String, itemType: String, title: String, subtitle: String) = withContext(Dispatchers.IO) {
        if (favoriteDao.isFavorite(userId, itemId) > 0) {
            favoriteDao.removeFavorite(userId, itemId)
        } else {
            favoriteDao.insertFavorite(
                FavoriteEntity(
                    id = "fav_${System.currentTimeMillis()}",
                    userId = userId,
                    itemType = itemType,
                    itemId = itemId,
                    itemTitle = title,
                    itemSubtitle = subtitle
                )
            )
        }
    }

    // Phase 20: Verification Requests
    fun getAllVerificationRequests(): Flow<List<VerificationRequestEntity>> = verificationRequestDao.getAllVerificationRequests()
    suspend fun getVerificationRequestForUser(userId: String): VerificationRequestEntity? = withContext(Dispatchers.IO) {
        verificationRequestDao.getRequestForUser(userId)
    }
    suspend fun submitVerificationRequest(req: VerificationRequestEntity) = withContext(Dispatchers.IO) {
        verificationRequestDao.insertVerificationRequest(req)
        userDao.updateVerificationStatus(req.userId, VerificationStatus.PENDING.name)
    }
    suspend fun updateVerificationRequestStatus(id: String, userId: String, status: String, reviewer: String) = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        verificationRequestDao.updateRequestStatus(id, status, reviewer, now)
        userDao.updateVerificationStatus(userId, status)
        notificationDao.insertNotification(
            NotificationEntity(
                id = "notif_$now",
                userId = userId,
                title = if (status == "APPROVED") "Verification Approved!" else "Verification Declined",
                amharicTitle = if (status == "APPROVED") "የአባልነት ማረጋገጫዎ ጸድቋል!" else "የአባልነት ማረጋገጫዎ አልተሳካም",
                message = if (status == "APPROVED")
                    "Your verified member badge is now active across the platform."
                else "Your verification request could not be approved at this time.",
                type = "VERIFICATION",
                timestamp = now
            )
        )
    }
}
