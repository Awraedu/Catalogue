package com.awra.catalogue.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.awra.catalogue.data.dao.CatalogueDao
import com.awra.catalogue.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Subject::class,
        Chapter::class,
        Topic::class,
        Handout::class,
        Flashcard::class,
        Infographic::class,
        QuizQuestion::class,
        Term::class,
        QuizAttempt::class,
        FlashcardAttempt::class,
        StudySession::class,
        Utility::class,
        PinnedItem::class,
        ClassSchedule::class
    ],
    version = 52,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun catalogueDao(): CatalogueDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * List of manual migrations. Add new migration objects here when changing schema.
         */
        private val ALL_MIGRATIONS = arrayOf<Migration>(
            // Example Migration from 51 to 52:
            // object : Migration(51, 52) {
            //     override fun migrate(db: SupportSQLiteDatabase) {
            //         // Add migration logic here
            //     }
            // }
        )

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "catalogue_database"
                )
                    .addMigrations(*ALL_MIGRATIONS)
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
