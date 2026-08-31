package com.awra.catalogue.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
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
        StudySession::class,
        Utility::class
    ],
    version = 9,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun catalogueDao(): CatalogueDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "catalogue_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onOpen(db: SupportSQLiteDatabase) {
                        super.onOpen(db)
                        val coroutineScope = CoroutineScope(Dispatchers.IO)
                        coroutineScope.launch {
                            INSTANCE?.let { database ->
                                DatabaseSeeder.seed(database.catalogueDao())
                            }
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
