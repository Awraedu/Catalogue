# Prevent Destructive Database Migrations

This plan addresses the issue where updating the `AppDatabase` version causes a full data reset. By enabling Room's **AutoMigrations** and removing the **Destructive Fallback**, we ensure that user data is preserved across app updates.

## Proposed Changes

### Database Configuration

#### [AppDatabase.kt](file:///C:/Users/Dagi/AndroidStudioProjects/Catalogue/app/src/main/java/com/awra/catalogue/data/database/AppDatabase.kt)

- Enable `autoMigrations` in the `@Database` annotation to handle version transitions automatically using existing schema files.
- Remove `.fallbackToDestructiveMigration(dropAllTables = true)` to prevent accidental data wipes.
- Provide a clear template for future manual migrations if structural changes become too complex for AutoMigration.

```kotlin
@Database(
    entities = [...],
    version = 55,
    autoMigrations = [
        AutoMigration(from = 51, to = 52),
        AutoMigration(from = 52, to = 53),
        AutoMigration(from = 53, to = 54),
        AutoMigration(from = 54, to = 55)
    ],
    exportSchema = true
)
```

```kotlin
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "catalogue_database"
                )
                    .addMigrations(*ALL_MIGRATIONS)
                    // .fallbackToDestructiveMigration() // REMOVED to prevent data loss
                    .build()
                INSTANCE = instance
                instance
            }
        }
```

## Verification Plan

### Automated Tests
- I will run a build to ensure the Room compiler validates the `autoMigrations` against the schema files in the `schemas/` directory.
- Command: `./gradlew app:assembleDebug`

### Manual Verification
1.  **Check Schema Consistency**: I have already verified that versions 51-55 have identical `identityHash`, confirming that AutoMigration will succeed without complex logic.
2.  **Code Analysis**: Verify that `fallbackToDestructiveMigration` is completely removed from the builder to ensure Room throws an error instead of wiping data if a migration path is missing in the future (forcing the developer to provide one).
