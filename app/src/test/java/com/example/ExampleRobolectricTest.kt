package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.model.*
import com.example.data.repository.AppRepository
import com.example.data.repository.SeedData
import com.example.ui.state.LanguageCode
import com.example.ui.state.Strings
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    private lateinit var db: AppDatabase
    private lateinit var repository: AppRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = AppRepository(db)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun testAppNameAndLocalization() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Ortodox Tewahido and Economy", appName)

        val amName = Strings.get("app_name", LanguageCode.AMHARIC)
        val enName = Strings.get("app_name", LanguageCode.ENGLISH)
        assertEquals("ኦርቶዶክስ ተዋሕዶ እና ኢኮኖሚ", amName)
        assertEquals("Ortodox Tewahido and Economy", enName)
    }

    @Test
    fun testSeedDataIntegrity() {
        assertTrue(SeedData.users.isNotEmpty())
        assertTrue(SeedData.jobs.isNotEmpty())
        assertTrue(SeedData.products.isNotEmpty())
        assertTrue(SeedData.b2bListings.isNotEmpty())
        assertTrue(SeedData.services.isNotEmpty())

        val admin = SeedData.users.firstOrNull { it.primaryRole == UserRole.ADMIN.name }
        assertNotNull(admin)
        assertEquals(VerificationStatus.VERIFIED.name, admin?.verificationStatus)
    }

    @Test
    fun testJobApplicationWorkflow() = runBlocking {
        // Insert a test employer and job
        val employer = UserEntity(
            id = "emp_1",
            email = "emp@test.et",
            passwordHash = "pass",
            fullName = "Test Employer",
            username = "emp1",
            primaryRole = UserRole.EMPLOYER.name
        )
        db.userDao().insertUser(employer)

        val job = JobEntity(
            id = "job_test",
            employerId = employer.id,
            employerName = employer.fullName,
            employerVerified = true,
            title = "Civil Engineer",
            amharicTitle = "የኮንስትራክሽን መሐንዲስ",
            category = "Construction",
            location = "Addis Ababa",
            salaryRange = "30,000 ETB",
            employmentType = EmploymentType.FULL_TIME.name,
            experienceLevel = "2 Yrs",
            description = "Test job desc",
            deadline = "2026-12-31"
        )
        repository.insertJob(job)

        // Applicant applies
        val app = ApplicationEntity(
            id = "app_test",
            jobId = job.id,
            jobTitle = job.title,
            applicantId = "applicant_1",
            applicantName = "Test Applicant",
            applicantEmail = "app@test.et",
            applicantPhone = "+251911111111",
            resumeSummary = "Civil Eng Degree",
            coverNote = "Looking forward to working"
        )
        repository.submitApplication(app, employer.id)

        // Verify employer received application
        val apps = repository.getApplicationsForJob(job.id).first()
        assertEquals(1, apps.size)
        assertEquals("app_test", apps.first().id)
        assertEquals(ApplicationStatus.SUBMITTED.name, apps.first().status)

        // Employer advances application to INTERVIEW
        repository.updateApplicationStatus(app.id, ApplicationStatus.INTERVIEW.name, app.applicantId, job.title)
        val updatedApps = repository.getApplicationsForJob(job.id).first()
        assertEquals(ApplicationStatus.INTERVIEW.name, updatedApps.first().status)
    }

    @Test
    fun testAdminVerificationAndModeration() = runBlocking {
        val user = UserEntity(
            id = "user_pending",
            email = "pending@test.et",
            passwordHash = "pass",
            fullName = "Pending User",
            username = "pending1",
            primaryRole = UserRole.SELLER.name,
            verificationStatus = VerificationStatus.PENDING.name,
            verificationDocNote = "Church ID attached"
        )
        repository.insertUser(user)

        // Admin approves verification
        repository.updateVerificationStatus(user.id, VerificationStatus.VERIFIED.name)
        val updated = repository.getUserById(user.id)
        assertNotNull(updated)
        assertEquals(VerificationStatus.VERIFIED.name, updated?.verificationStatus)

        // Admin suspends user
        repository.setSuspendedStatus(user.id, true)
        val suspended = repository.getUserById(user.id)
        assertTrue(suspended?.isSuspended == true)
    }

    @Test
    fun testMessagingBetweenUsers() = runBlocking {
        repository.sendMessage(
            conversationId = "conv_test",
            senderId = "u1",
            senderName = "User One",
            recipientId = "u2",
            recipientName = "User Two",
            content = "Peace be with you. I am inquiring about the wholesale order."
        )

        val messages = repository.getMessagesForConversation("conv_test").first()
        assertEquals(1, messages.size)
        assertEquals("User One", messages.first().senderName)
        assertEquals("Peace be with you. I am inquiring about the wholesale order.", messages.first().content)
    }
}
