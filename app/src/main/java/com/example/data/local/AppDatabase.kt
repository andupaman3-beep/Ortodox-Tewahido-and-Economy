package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.*

@Database(
    entities = [
        UserEntity::class,
        JobEntity::class,
        ApplicationEntity::class,
        ProductEntity::class,
        B2BEntity::class,
        ServiceEntity::class,
        ConversationEntity::class,
        MessageEntity::class,
        NotificationEntity::class,
        ReviewEntity::class,
        ReportEntity::class,
        CVEntity::class,
        DocumentEntity::class,
        FavoriteEntity::class,
        VerificationRequestEntity::class
    ],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun jobDao(): JobDao
    abstract fun productDao(): ProductDao
    abstract fun b2bDao(): B2BDao
    abstract fun serviceDao(): ServiceDao
    abstract fun messageDao(): MessageDao
    abstract fun notificationDao(): NotificationDao
    abstract fun reviewDao(): ReviewDao
    abstract fun reportDao(): ReportDao
    abstract fun cvDao(): CVDao
    abstract fun documentDao(): DocumentDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun verificationRequestDao(): VerificationRequestDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "orthodox_tewahido_economy.db"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
