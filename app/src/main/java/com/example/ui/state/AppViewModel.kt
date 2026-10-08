package com.example.ui.state

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.*
import com.example.data.repository.AppRepository
import com.example.data.repository.FirebaseGigRepository
import com.example.data.repository.FirebaseMarketplaceRepository
import com.example.data.repository.FirebaseProfileRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("orthodox_app_prefs", Context.MODE_PRIVATE)
    private val repository = AppRepository(AppDatabase.getDatabase(application))
    private val firebaseProfileRepo = FirebaseProfileRepository(application)
    val firebaseGigRepo = FirebaseGigRepository(application)
    val firebaseMarketplaceRepo = FirebaseMarketplaceRepository(application)
    private val firebaseAuth = FirebaseAuth.getInstance()

    // Firebase Auth State
    private val _firebaseUser = MutableStateFlow<FirebaseUser?>(firebaseAuth.currentUser)
    val firebaseUser: StateFlow<FirebaseUser?> = _firebaseUser.asStateFlow()

    private val _isAuthLoading = MutableStateFlow(false)
    val isAuthLoading: StateFlow<Boolean> = _isAuthLoading.asStateFlow()

    // Navigation Stack
    private val _screenStack = MutableStateFlow(listOf(AppScreen.LANDING_HOME))
    val currentScreen: StateFlow<AppScreen> = _screenStack.map { it.lastOrNull() ?: AppScreen.LANDING_HOME }
        .stateIn(viewModelScope, SharingStarted.Eagerly, AppScreen.LANDING_HOME)

    // Language state (Persisted in SharedPreferences)
    private val initialLang = try {
        val saved = prefs.getString("selected_lang", LanguageCode.AMHARIC.name)
        LanguageCode.valueOf(saved ?: LanguageCode.AMHARIC.name)
    } catch (_: Exception) {
        LanguageCode.AMHARIC
    }
    private val _language = MutableStateFlow(initialLang)
    val language: StateFlow<LanguageCode> = _language.asStateFlow()

    // Dark Mode state (Persisted in SharedPreferences)
    private val _isDarkMode = MutableStateFlow(prefs.getBoolean("is_dark_mode", false))
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    // Current City / Region Location
    private val _currentLocation = MutableStateFlow(prefs.getString("selected_location", "Addis Ababa, Ethiopia") ?: "Addis Ababa, Ethiopia")
    val currentLocation: StateFlow<String> = _currentLocation.asStateFlow()

    // Current Logged-in User (default to Dn. Amanuel - Admin or Marta - Seeker)
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    // All Users
    val allUsers: StateFlow<List<UserEntity>> = repository.getAllUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Observe Firebase Auth changes and synchronize with local user state
        viewModelScope.launch {
            val listener = FirebaseAuth.AuthStateListener { auth ->
                val fbUser = auth.currentUser
                _firebaseUser.value = fbUser
                if (fbUser != null) {
                    viewModelScope.launch {
                        val existing = allUsers.value.firstOrNull { it.id == fbUser.uid || it.email.equals(fbUser.email, ignoreCase = true) }
                        if (existing != null) {
                            _currentUser.value = existing
                        } else {
                            val cloudProfile = firebaseProfileRepo.fetchUserProfile(fbUser.uid)
                            if (cloudProfile != null) {
                                repository.insertUser(cloudProfile)
                                _currentUser.value = cloudProfile
                            } else {
                                val newProfile = UserEntity(
                                    id = fbUser.uid,
                                    email = fbUser.email ?: "",
                                    passwordHash = "google_auth",
                                    fullName = fbUser.displayName?.ifBlank { "Orthodox Professional" } ?: "Orthodox Professional",
                                    username = (fbUser.email ?: "user").substringBefore("@"),
                                    primaryRole = UserRole.JOB_SEEKER.name,
                                    profilePhotoUrl = fbUser.photoUrl?.toString() ?: "",
                                    location = "Addis Ababa",
                                    verificationStatus = VerificationStatus.VERIFIED.name,
                                    joinedDate = "2026-10-08"
                                )
                                repository.insertUser(newProfile)
                                firebaseProfileRepo.syncUserProfile(newProfile)
                                _currentUser.value = newProfile
                            }
                        }
                    }
                }
            }
            firebaseAuth.addAuthStateListener(listener)
        }

        // Automatically set first user once loaded if unauthenticated
        viewModelScope.launch {
            allUsers.collect { users ->
                if (_currentUser.value == null && users.isNotEmpty()) {
                    val fbUser = firebaseAuth.currentUser
                    if (fbUser != null) {
                        _currentUser.value = users.firstOrNull { it.id == fbUser.uid || it.email.equals(fbUser.email, ignoreCase = true) }
                    } else {
                        _currentUser.value = users.firstOrNull { it.id == "usr_admin" } ?: users.first()
                    }
                }
            }
        }
    }

    // Platform Data Streams
    val allJobs: StateFlow<List<JobEntity>> = repository.getAllActiveJobs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allProducts: StateFlow<List<ProductEntity>> = repository.getAllProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allB2BListings: StateFlow<List<B2BEntity>> = repository.getAllB2BListings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allServices: StateFlow<List<ServiceEntity>> = repository.getAllServices()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allReports: StateFlow<List<ReportEntity>> = repository.getAllReports()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active User Specific Streams
    val userConversations: StateFlow<List<ConversationEntity>> = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getConversationsForUser(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userNotifications: StateFlow<List<NotificationEntity>> = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getNotificationsForUser(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userApplications: StateFlow<List<ApplicationEntity>> = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getApplicationsForApplicant(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userCVs: StateFlow<List<CVEntity>> = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getCVsForUser(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userDocuments: StateFlow<List<DocumentEntity>> = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getDocumentsForUser(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userFavorites: StateFlow<List<FavoriteEntity>> = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getFavoritesForUser(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allVerificationRequests: StateFlow<List<VerificationRequestEntity>> = repository.getAllVerificationRequests()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected Items for Details
    val selectedJob = MutableStateFlow<JobEntity?>(null)
    val selectedProduct = MutableStateFlow<ProductEntity?>(null)
    val selectedB2B = MutableStateFlow<B2BEntity?>(null)
    val selectedService = MutableStateFlow<ServiceEntity?>(null)
    val selectedUser = MutableStateFlow<UserEntity?>(null)

    // Chat
    val activeConversationId = MutableStateFlow<String?>(null)
    val activeChatRecipient = MutableStateFlow<Pair<String, String>?>(null) // Pair(id, name)
    val activeChatMessages: StateFlow<List<MessageEntity>> = activeConversationId.flatMapLatest { convId ->
        if (convId != null) repository.getMessagesForConversation(convId) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Job Applicants for Selected Job
    val currentJobApplications: StateFlow<List<ApplicationEntity>> = selectedJob.flatMapLatest { job ->
        if (job != null) repository.getApplicationsForJob(job.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Reviews for Selected User
    val currentSelectedUserReviews: StateFlow<List<ReviewEntity>> = selectedUser.flatMapLatest { user ->
        if (user != null) repository.getReviewsForUser(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Global Search Query & Filter
    val globalSearchQuery = MutableStateFlow("")
    val activeFilter = MutableStateFlow(FilterState())

    // Toast / Feedback message
    val snackbarMessage = MutableStateFlow<String?>(null)

    fun clearSnackbar() {
        snackbarMessage.value = null
    }

    // AI Assistant State
    val aiPromptInput = MutableStateFlow("")
    val aiResultOutput = MutableStateFlow<String?>(null)
    val isAiLoading = MutableStateFlow(false)

    // Navigation Methods
    fun navigateTo(screen: AppScreen) {
        val current = _screenStack.value
        if (current.lastOrNull() != screen) {
            _screenStack.value = current + screen
        }
    }

    fun navigateBack(): Boolean {
        val current = _screenStack.value
        if (current.size > 1) {
            _screenStack.value = current.dropLast(1)
            return true
        }
        return false
    }

    fun setLanguage(lang: LanguageCode) {
        _language.value = lang
        prefs.edit().putString("selected_lang", lang.name).apply()
    }

    fun toggleLanguage() {
        val next = when (_language.value) {
            LanguageCode.AMHARIC -> LanguageCode.ENGLISH
            LanguageCode.ENGLISH -> LanguageCode.OROMO
            LanguageCode.OROMO -> LanguageCode.TIGRINYA
            LanguageCode.TIGRINYA -> LanguageCode.AMHARIC
        }
        setLanguage(next)
    }

    fun toggleDarkMode() {
        val newVal = !_isDarkMode.value
        _isDarkMode.value = newVal
        prefs.edit().putBoolean("is_dark_mode", newVal).apply()
    }

    fun setLocation(loc: String) {
        _currentLocation.value = loc
        prefs.edit().putString("selected_location", loc).apply()
    }

    fun switchUser(user: UserEntity) {
        _currentUser.value = user
        snackbarMessage.value = "Active profile: ${user.fullName} (${user.primaryRole})"
    }

    fun registerUser(
        fullName: String,
        email: String,
        username: String,
        role: UserRole,
        secondaryRoles: List<UserRole>,
        profession: String,
        businessName: String,
        location: String,
        phone: String,
        bio: String
    ) {
        viewModelScope.launch {
            val newUser = UserEntity(
                id = "usr_${System.currentTimeMillis()}",
                email = email.trim(),
                passwordHash = "pass123",
                fullName = fullName.trim(),
                username = username.trim().ifBlank { email.substringBefore("@") },
                primaryRole = role.name,
                secondaryRoles = secondaryRoles.joinToString(",") { it.name },
                profession = profession.trim(),
                businessName = businessName.trim(),
                location = location.trim().ifBlank { "Addis Ababa" },
                phone = phone.trim(),
                bio = bio.trim(),
                verificationStatus = VerificationStatus.UNVERIFIED.name,
                joinedDate = "2026-10-06"
            )
            repository.insertUser(newUser)
            _currentUser.value = newUser
            viewModelScope.launch {
                firebaseProfileRepo.syncUserProfile(newUser)
            }
            snackbarMessage.value = "Welcome ${newUser.fullName}! Account created successfully."
            navigateTo(AppScreen.PROFILE)
        }
    }

    fun handleGoogleSignInSuccess(idToken: String, onComplete: (Boolean, String?) -> Unit) {
        _isAuthLoading.value = true
        val credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener { result ->
                val fbUser = result.user
                _isAuthLoading.value = false
                if (fbUser != null) {
                    viewModelScope.launch {
                        val existing = allUsers.value.firstOrNull { it.id == fbUser.uid || it.email.equals(fbUser.email, ignoreCase = true) }
                        val userToSet = if (existing != null) {
                            existing
                        } else {
                            val fetched = firebaseProfileRepo.fetchUserProfile(fbUser.uid)
                            if (fetched != null) {
                                repository.insertUser(fetched)
                                fetched
                            } else {
                                val created = UserEntity(
                                    id = fbUser.uid,
                                    email = fbUser.email ?: "",
                                    passwordHash = "google_auth",
                                    fullName = fbUser.displayName?.ifBlank { "Orthodox Professional" } ?: "Orthodox Professional",
                                    username = (fbUser.email ?: "user").substringBefore("@"),
                                    primaryRole = UserRole.JOB_SEEKER.name,
                                    profilePhotoUrl = fbUser.photoUrl?.toString() ?: "",
                                    location = "Addis Ababa",
                                    verificationStatus = VerificationStatus.VERIFIED.name,
                                    joinedDate = "2026-10-08"
                                )
                                repository.insertUser(created)
                                firebaseProfileRepo.syncUserProfile(created)
                                created
                            }
                        }
                        _currentUser.value = userToSet
                        snackbarMessage.value = "Signed in with Google as ${userToSet.fullName}!"
                        onComplete(true, null)
                    }
                } else {
                    onComplete(false, "No user returned from Google credential")
                }
            }
            .addOnFailureListener { exc ->
                _isAuthLoading.value = false
                val err = exc.localizedMessage ?: "Google Sign-In failed"
                snackbarMessage.value = err
                onComplete(false, err)
            }
    }

    fun signOutFirebase() {
        firebaseAuth.signOut()
        _firebaseUser.value = null
        snackbarMessage.value = "Signed out from Google account"
    }

    fun updateUserProfile(
        bio: String,
        skills: String,
        phone: String,
        location: String,
        telegramHandle: String,
        profession: String,
        fullName: String,
        onComplete: ((Boolean) -> Unit)? = null
    ) {
        val user = _currentUser.value ?: return
        val updated = user.copy(
            bio = bio.trim(),
            skills = skills.trim(),
            phone = phone.trim(),
            location = location.trim().ifBlank { "Addis Ababa" },
            telegramHandle = telegramHandle.trim().removePrefix("@"),
            profession = profession.trim().ifBlank { user.profession },
            fullName = fullName.trim().ifBlank { user.fullName }
        )
        _currentUser.value = updated
        viewModelScope.launch {
            _isAuthLoading.value = true
            repository.updateUser(updated)
            val firestoreSynced = firebaseProfileRepo.syncUserProfile(updated)
            _isAuthLoading.value = false
            if (firestoreSynced) {
                snackbarMessage.value = "Profile synced to Firestore cloud database!"
            } else {
                snackbarMessage.value = "Profile updated locally."
            }
            onComplete?.invoke(firestoreSynced)
        }
    }

    fun refreshProfileFromFirestore() {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            _isAuthLoading.value = true
            val remote = firebaseProfileRepo.fetchUserProfile(user.id)
            if (remote != null) {
                repository.updateUser(remote)
                _currentUser.value = remote
                snackbarMessage.value = "Profile refreshed from Firestore cloud database"
            } else {
                snackbarMessage.value = "Using local profile data"
            }
            _isAuthLoading.value = false
        }
    }

    fun loginWithEmail(email: String) {
        viewModelScope.launch {
            val found = allUsers.value.firstOrNull { it.email.equals(email.trim(), ignoreCase = true) }
            if (found != null) {
                _currentUser.value = found
                snackbarMessage.value = "Welcome back, ${found.fullName}!"
            } else {
                snackbarMessage.value = "User with email $email not found. You can register or pick a persona."
            }
        }
    }

    fun openJobDetail(job: JobEntity) {
        selectedJob.value = job
        navigateTo(AppScreen.JOB_DETAIL)
    }

    fun openProductDetail(product: ProductEntity) {
        selectedProduct.value = product
        navigateTo(AppScreen.PRODUCT_DETAIL)
    }

    fun openB2BDetail(listing: B2BEntity) {
        selectedB2B.value = listing
        navigateTo(AppScreen.B2B_DETAIL)
    }

    fun openServiceDetail(service: ServiceEntity) {
        selectedService.value = service
        navigateTo(AppScreen.SERVICE_DETAIL)
    }

    fun openUserProfile(user: UserEntity) {
        selectedUser.value = user
        navigateTo(AppScreen.PROFILE)
    }

    fun openChatWith(recipientId: String, recipientName: String) {
        val user = _currentUser.value ?: return
        val convId = if (user.id < recipientId) "conv_${user.id}_$recipientId" else "conv_${recipientId}_${user.id}"
        activeConversationId.value = convId
        activeChatRecipient.value = Pair(recipientId, recipientName)
        viewModelScope.launch {
            repository.markMessagesAsRead(convId, user.id)
        }
        navigateTo(AppScreen.CHAT)
    }

    fun sendChatMessage(content: String) {
        val user = _currentUser.value ?: return
        val convId = activeConversationId.value ?: return
        val recipient = activeChatRecipient.value ?: return
        if (content.isBlank()) return

        viewModelScope.launch {
            repository.sendMessage(
                conversationId = convId,
                senderId = user.id,
                senderName = user.fullName,
                recipientId = recipient.first,
                recipientName = recipient.second,
                content = content.trim()
            )
        }
    }

    fun submitProposal(
        job: JobEntity,
        bidAmount: Double,
        deliveryDays: Int,
        milestones: String,
        coverNote: String
    ) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val app = ApplicationEntity(
                id = "prop_${System.currentTimeMillis()}",
                jobId = job.id,
                jobTitle = job.title,
                applicantId = user.id,
                applicantName = user.fullName,
                applicantEmail = user.email,
                applicantPhone = user.phone,
                resumeSummary = "${user.profession} • Bid: $bidAmount ETB in $deliveryDays days",
                coverNote = coverNote.trim(),
                bidAmount = bidAmount,
                deliveryDays = deliveryDays,
                milestonesSummary = milestones.trim(),
                status = ApplicationStatus.SUBMITTED.name
            )
            repository.submitApplication(app, job.employerId)
            snackbarMessage.value = "Proposal of $bidAmount ETB submitted to ${job.employerName}!"
            navigateBack()
        }
    }

    fun applyForJob(job: JobEntity, resumeSummary: String, coverNote: String) {
        submitProposal(job, job.budgetAmount, job.deliveryDays, "Complete Delivery", coverNote)
    }

    fun updateApplicationStatus(appId: String, status: ApplicationStatus, applicantId: String, jobTitle: String) {
        viewModelScope.launch {
            repository.updateApplicationStatus(appId, status.name, applicantId, jobTitle)
            snackbarMessage.value = "Application marked as ${status.labelEn}."
        }
    }

    fun withdrawApplication(appId: String) {
        viewModelScope.launch {
            repository.withdrawApplication(appId)
            snackbarMessage.value = "Application withdrawn."
        }
    }

    fun createProjectOrGig(
        title: String,
        amharicTitle: String,
        category: String,
        location: String,
        budget: Double,
        projectType: String,
        deliveryDays: Int,
        skills: String,
        experience: String,
        desc: String,
        deadline: String
    ) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val job = JobEntity(
                id = if (projectType == "GIG") "gig_${System.currentTimeMillis()}" else "proj_${System.currentTimeMillis()}",
                employerId = user.id,
                employerName = user.businessName.ifBlank { user.fullName },
                employerVerified = user.verificationStatus == VerificationStatus.VERIFIED.name,
                title = title.trim(),
                amharicTitle = amharicTitle.trim().ifBlank { title.trim() },
                category = category,
                location = location.trim(),
                salaryRange = if (projectType == "HOURLY") "$budget ETB / Hour" else "$budget ETB Fixed",
                employmentType = if (projectType == "HOURLY") EmploymentType.PART_TIME.name else EmploymentType.CONTRACT.name,
                experienceLevel = experience.trim(),
                description = desc.trim(),
                deadline = deadline.trim(),
                projectType = projectType,
                budgetAmount = budget,
                deliveryDays = deliveryDays,
                skillsRequired = skills.trim(),
                proposalsCount = 0
            )
            repository.insertJob(job)
            if (firebaseAuth.currentUser != null) {
                firebaseGigRepo.createGig(
                    FirestoreGig(
                        id = job.id,
                        employerId = job.employerId,
                        employerName = job.employerName,
                        employerVerified = job.employerVerified,
                        title = job.title,
                        amharicTitle = job.amharicTitle,
                        category = job.category,
                        location = job.location,
                        salaryRange = job.salaryRange,
                        employmentType = job.employmentType,
                        experienceLevel = job.experienceLevel,
                        description = job.description,
                        deadline = job.deadline,
                        projectType = job.projectType,
                        budgetAmount = job.budgetAmount,
                        deliveryDays = job.deliveryDays.toLong(),
                        skillsRequired = job.skillsRequired,
                        proposalsCount = job.proposalsCount.toLong(),
                        isActive = job.isActive
                    )
                )
            }
            snackbarMessage.value = "የሥራ ማስታወቂያው በተሳካ ሁኔታ ተለጥፏል! (Job vacancy posted successfully!)"
            navigateTo(AppScreen.JOBS)
        }
    }

    fun createJob(
        title: String,
        amharicTitle: String,
        category: String,
        location: String,
        salary: String,
        type: EmploymentType,
        experience: String,
        desc: String,
        deadline: String
    ) {
        createProjectOrGig(
            title = title,
            amharicTitle = amharicTitle,
            category = category,
            location = location,
            budget = 35000.0,
            projectType = "FIXED_PRICE",
            deliveryDays = 7,
            skills = "Professional Skill",
            experience = experience,
            desc = desc,
            deadline = deadline
        )
    }

    fun createProduct(
        title: String,
        amharicTitle: String,
        desc: String,
        category: String,
        price: Double,
        qty: Int,
        location: String,
        delivery: String,
        condition: String
    ) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val prod = ProductEntity(
                id = "prod_${System.currentTimeMillis()}",
                sellerId = user.id,
                sellerName = user.businessName.ifBlank { user.fullName },
                sellerVerified = user.verificationStatus == VerificationStatus.VERIFIED.name,
                title = title.trim(),
                amharicTitle = amharicTitle.trim().ifBlank { title.trim() },
                description = desc.trim(),
                category = category,
                price = price,
                quantity = qty,
                location = location.trim(),
                condition = condition.trim()
            )
            repository.insertProduct(prod)
            if (firebaseAuth.currentUser != null) {
                firebaseMarketplaceRepo.createProduct(
                    FirestoreProduct(
                        id = prod.id,
                        sellerId = prod.sellerId,
                        sellerName = prod.sellerName,
                        sellerVerified = prod.sellerVerified,
                        title = prod.title,
                        amharicTitle = prod.amharicTitle,
                        description = prod.description,
                        category = prod.category,
                        price = prod.price,
                        currency = prod.currency,
                        quantity = prod.quantity.toLong(),
                        location = prod.location,
                        condition = prod.condition,
                        imageUrl = prod.imageUrl,
                        isAvailable = prod.isAvailable,
                        deliveryInfo = prod.deliveryInfo,
                        isFeatured = prod.isFeatured
                    )
                )
            }
            snackbarMessage.value = "Product published on Marketplace!"
            navigateTo(AppScreen.MARKETPLACE)
        }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            repository.deleteProduct(productId)
            if (firebaseAuth.currentUser != null) {
                firebaseMarketplaceRepo.deleteProduct(productId)
            }
            snackbarMessage.value = "Product removed from marketplace."
        }
    }

    // Phase 7: CV / Resume System
    fun createOrUpdateCV(
        fullName: String,
        title: String,
        summary: String,
        experience: String,
        education: String,
        skills: String,
        languages: String,
        certifications: String
    ) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val cv = CVEntity(
                id = "cv_${System.currentTimeMillis()}",
                userId = user.id,
                fullName = fullName.trim(),
                professionTitle = title.trim(),
                summary = summary.trim(),
                experience = experience.trim(),
                education = education.trim(),
                skills = skills.trim(),
                languages = languages.trim(),
                certifications = certifications.trim(),
                fileName = "${fullName.trim().replace(" ", "_")}_CV.pdf",
                fileType = "BUILT_IN",
                fileSizeKb = 180,
                isPrimary = true,
                isPublic = true
            )
            repository.insertCV(cv)
            snackbarMessage.value = "CV created successfully!"
            navigateBack()
        }
    }

    fun uploadCVFile(title: String, fileName: String, fileType: String, sizeKb: Int) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val cv = CVEntity(
                id = "cv_${System.currentTimeMillis()}",
                userId = user.id,
                fullName = user.fullName,
                professionTitle = title.trim().ifBlank { user.profession },
                summary = user.bio,
                experience = "Verified on file",
                education = "Verified on file",
                skills = "Professional Profile",
                fileName = fileName.trim(),
                fileType = fileType.uppercase(),
                fileSizeKb = sizeKb,
                isPrimary = true,
                isPublic = true
            )
            repository.insertCV(cv)
            snackbarMessage.value = "CV uploaded and validated successfully!"
        }
    }

    fun deleteCV(cvId: String) {
        viewModelScope.launch {
            repository.deleteCV(cvId)
            snackbarMessage.value = "CV removed."
        }
    }

    // Phase 8: Document Management
    fun uploadDocument(title: String, docType: String, fileName: String, sizeKb: Int, isPublic: Boolean) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val doc = DocumentEntity(
                id = "doc_${System.currentTimeMillis()}",
                userId = user.id,
                title = title.trim(),
                docType = docType,
                fileName = fileName.trim(),
                fileSizeKb = sizeKb,
                isPublic = isPublic
            )
            repository.insertDocument(doc)
            snackbarMessage.value = "Document uploaded securely."
        }
    }

    fun deleteDocument(docId: String) {
        viewModelScope.launch {
            repository.deleteDocument(docId)
            snackbarMessage.value = "Document deleted."
        }
    }

    // Phase 12: Favorites
    fun toggleFavorite(itemId: String, itemType: String, title: String, subtitle: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.toggleFavorite(user.id, itemId, itemType, title, subtitle)
            snackbarMessage.value = "Favorites updated."
        }
    }

    // Phase 20: Official Community Verification Requests
    fun submitVerificationRequest(note: String) {
        submitVerificationRequest("Holy Trinity / Local Parish", "PARISH_CERTIFICATE", "recommendation.pdf", note)
    }

    fun submitVerificationRequest(churchName: String, docType: String, docFileName: String, notes: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val req = VerificationRequestEntity(
                id = "vreq_${System.currentTimeMillis()}",
                userId = user.id,
                userName = user.fullName,
                userEmail = user.email,
                parishChurchName = churchName.trim(),
                idDocType = docType,
                docFileName = docFileName.trim(),
                notes = notes.trim()
            )
            repository.submitVerificationRequest(req)
            val updatedUser = user.copy(verificationStatus = VerificationStatus.PENDING.name)
            _currentUser.value = updatedUser
            snackbarMessage.value = "Verification request submitted for admin review."
        }
    }

    fun reviewVerificationRequest(reqId: String, userId: String, status: String) {
        val admin = _currentUser.value ?: return
        viewModelScope.launch {
            repository.updateVerificationRequestStatus(reqId, userId, status, admin.fullName)
            snackbarMessage.value = "Verification $status."
        }
    }

    // Phase 9: Job Application Status Management
    fun updateJobApplicationStatus(appId: String, status: String, applicantId: String, jobTitle: String) {
        viewModelScope.launch {
            repository.updateApplicationStatus(appId, status, applicantId, jobTitle)
            snackbarMessage.value = "Application status updated to $status."
        }
    }

    fun createB2BListing(
        type: String,
        title: String,
        origin: String,
        wholesalePrice: Double,
        minQty: String,
        totalQty: String,
        location: String,
        delivery: String,
        desc: String,
        phone: String,
        email: String
    ) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val b2b = B2BEntity(
                id = "b2b_${System.currentTimeMillis()}",
                userId = user.id,
                companyName = user.businessName.ifBlank { user.fullName },
                isVerified = user.verificationStatus == VerificationStatus.VERIFIED.name,
                b2bType = type,
                title = title.trim(),
                productOrigin = origin.trim(),
                wholesalePrice = wholesalePrice,
                minimumOrderQty = minQty.trim(),
                totalAvailableQty = totalQty.trim(),
                location = location.trim(),
                deliveryTerms = delivery.trim(),
                description = desc.trim(),
                contactPhone = phone.trim(),
                contactEmail = email.trim()
            )
            repository.insertB2B(b2b)
            snackbarMessage.value = "B2B wholesale listing published!"
            navigateTo(AppScreen.B2B)
        }
    }

    fun createService(
        title: String,
        amharicTitle: String,
        category: String,
        rate: String,
        location: String,
        desc: String,
        experienceYears: Int,
        phone: String
    ) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val svc = ServiceEntity(
                id = "svc_${System.currentTimeMillis()}",
                providerId = user.id,
                providerName = user.fullName,
                isVerified = user.verificationStatus == VerificationStatus.VERIFIED.name,
                title = title.trim(),
                amharicTitle = amharicTitle.trim().ifBlank { title.trim() },
                category = category,
                rateDescription = rate.trim(),
                location = location.trim(),
                description = desc.trim(),
                experienceYears = experienceYears,
                phone = phone.trim()
            )
            repository.insertService(svc)
            snackbarMessage.value = "Professional service listing added!"
            navigateTo(AppScreen.SERVICES)
        }
    }

    fun adminApproveVerification(userId: String) {
        viewModelScope.launch {
            repository.updateVerificationStatus(userId, VerificationStatus.VERIFIED.name)
            if (_currentUser.value?.id == userId) {
                _currentUser.value = _currentUser.value?.copy(verificationStatus = VerificationStatus.VERIFIED.name)
            }
            snackbarMessage.value = "User verified successfully."
        }
    }

    fun adminRejectVerification(userId: String) {
        viewModelScope.launch {
            repository.updateVerificationStatus(userId, VerificationStatus.REJECTED.name)
            if (_currentUser.value?.id == userId) {
                _currentUser.value = _currentUser.value?.copy(verificationStatus = VerificationStatus.REJECTED.name)
            }
            snackbarMessage.value = "Verification request rejected."
        }
    }

    fun adminToggleUserSuspension(userId: String, isSuspended: Boolean) {
        viewModelScope.launch {
            repository.setSuspendedStatus(userId, isSuspended)
            snackbarMessage.value = if (isSuspended) "User suspended from platform." else "User restored."
        }
    }

    fun submitReport(targetType: String, targetId: String, title: String, reason: String, details: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val rep = ReportEntity(
                id = "rep_${System.currentTimeMillis()}",
                reporterId = user.id,
                reporterName = user.fullName,
                targetType = targetType,
                targetId = targetId,
                targetTitle = title,
                reason = reason,
                details = details.trim()
            )
            repository.submitReport(rep)
            snackbarMessage.value = "Report submitted to Platform Moderation. Thank you for keeping our community safe."
        }
    }

    fun adminResolveReport(reportId: String) {
        viewModelScope.launch {
            repository.updateReportStatus(reportId, "RESOLVED")
            snackbarMessage.value = "Report marked as resolved."
        }
    }

    fun adminDismissReport(reportId: String) {
        viewModelScope.launch {
            repository.updateReportStatus(reportId, "DISMISSED")
            snackbarMessage.value = "Report dismissed."
        }
    }

    fun submitReview(targetUserId: String, rating: Int, comment: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val rev = ReviewEntity(
                id = "rev_${System.currentTimeMillis()}",
                targetUserId = targetUserId,
                authorId = user.id,
                authorName = user.fullName,
                rating = rating,
                comment = comment.trim()
            )
            repository.insertReview(rev)
            snackbarMessage.value = "Review and rating submitted!"
        }
    }

    fun markNotificationAsRead(id: String) {
        viewModelScope.launch {
            repository.markNotificationAsRead(id)
        }
    }

    fun markAllNotificationsAsRead() {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.markAllNotificationsAsRead(user.id)
            snackbarMessage.value = "All notifications marked as read."
        }
    }

    // AI Features: Rule-based & Smart matching with deterministic advice for Ethiopian Orthodox professionals
    fun runAiFeature(featureType: String, input: String) {
        isAiLoading.value = true
        viewModelScope.launch {
            kotlinx.coroutines.delay(600) // Realistic processing feedback
            val result = when (featureType) {
                "JOB_MATCH" -> {
                    val matchingJobs = allJobs.value.filter {
                        it.title.contains(input, ignoreCase = true) ||
                        it.description.contains(input, ignoreCase = true) ||
                        it.category.contains(input, ignoreCase = true)
                    }
                    if (matchingJobs.isNotEmpty()) {
                        "✨ AI Matching Recommendation:\nBased on skills \"$input\", we found ${matchingJobs.size} highly compatible positions:\n" +
                                matchingJobs.joinToString("\n\n") { "• ${it.title} at ${it.employerName} (${it.salaryRange})\n  Location: ${it.location}" }
                    } else {
                        "✨ AI Recommendation for \"$input\":\nCurrently exploring matching opportunities. We recommend creating an open Service listing or checking our Technology and Accounting listings."
                    }
                }
                "ENHANCE_DESCRIPTION" -> {
                    "✨ Professional Enhanced Copy:\n\"$input\n\nበእግዚአብሔር ፈቃድ ጥራት፣ ታማኝነትና ቅንነትን መርህ አድርገን የምንሰራ ሲሆን፣ ለደንበኞቻችን አስተማማኝና ዘመኑን የዋጀ አገልግሎት እናቀርባለን። Committed to excellence, integrity, and timely delivery.\""
                }
                "FRAUD_DETECTOR" -> {
                    val spamKeywords = listOf("lottery", "urgent wire", "free gift", "million dollars", "password", "crypto hack", "quick cash")
                    val isFlagged = spamKeywords.any { input.contains(it, ignoreCase = true) }
                    if (isFlagged) {
                        "⚠️ Security Alert: The listing contains suspicious high-risk terminology often linked to fraudulent advance-fee schemes. Recommendation: Require administrator manual verification before publishing."
                    } else {
                        "✅ Clean & Trustworthy: Listing complies with Tewahido Net community ethical guidelines and verification standards."
                    }
                }
                else -> "AI analysis completed."
            }
            aiResultOutput.value = result
            isAiLoading.value = false
        }
    }
}
