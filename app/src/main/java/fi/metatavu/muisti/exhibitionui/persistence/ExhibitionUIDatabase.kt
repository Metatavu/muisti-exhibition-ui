package fi.metatavu.muisti.exhibitionui.persistence

import android.content.pm.ActivityInfo
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import fi.metatavu.muisti.exhibitionui.BuildConfig
import fi.metatavu.muisti.exhibitionui.ExhibitionUIApplication
import fi.metatavu.muisti.exhibitionui.persistence.dao.DeviceSettingDao
import fi.metatavu.muisti.exhibitionui.persistence.dao.LayoutDao
import fi.metatavu.muisti.exhibitionui.persistence.dao.PageDao
import fi.metatavu.muisti.exhibitionui.persistence.dao.UpdateUserValueTaskDao
import fi.metatavu.muisti.exhibitionui.persistence.model.*
import fi.metatavu.muisti.exhibitionui.persistence.types.UUIDConverter

/**
 * The Room database
 */
@Database(entities = [ UpdateUserValueTask::class, DeviceSetting::class, Layout::class, Page::class], version = 9)
@TypeConverters(PageLayoutViewConverter::class, ExhibitionPageViewConverter::class, UUIDConverter::class)
abstract class ExhibitionUIDatabase : RoomDatabase() {

    /**
     * Getter for UpdateUserValueTaskDao
     *
     * @return UpdateUserValueTaskDao
     */
    abstract fun updateUserValueTaskDao(): UpdateUserValueTaskDao

    /**
     * Getter for DeviceSettingDao
     *
     * @return deviceSettingDao
     */
    abstract fun deviceSettingDao(): DeviceSettingDao

    /**
     * Getter for LayoutDao
     *
     * @return layoutDao
     */
    abstract fun layoutDao(): LayoutDao

    /**
     * Getter for PageDao
     *
     * @return pageDao
     */
    abstract fun pageDao(): PageDao

    companion object {

        /**
         * Private variable for singleton instance
         */
        @Volatile
        private var INSTANCE: ExhibitionUIDatabase? = null

        /**
         * Clears all tables from the database
         */
        fun clearData() {
            INSTANCE?.clearAllTables()
        }

        /**
         * Returns database instance
         *
         * @return database instance
         */
        fun getDatabase(): ExhibitionUIDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }

            val MIGRATION_1_2 = object : Migration(1, 2) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    database.execSQL("CREATE TABLE `DeviceSetting` (`id` INTEGER NOT NULL, `name` TEXT NOT NULL, `value` TEXT NOT NULL, PRIMARY KEY(`id`))")
                    database.execSQL("CREATE UNIQUE INDEX index_DeviceSetting_name ON DeviceSetting (name)")
                }
            }

            val MIGRATION_2_3 = object : Migration(2, 3) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    database.execSQL("CREATE TABLE `Layout` (`id` INTEGER NOT NULL,`name` TEXT NOT NULL, `data` TEXT NOT NULL, `layoutId` TEXT NOT NULL, `exhibitionId` TEXT NOT NULL, `modifiedAt` TEXT, PRIMARY KEY(`id`))")
                    database.execSQL("CREATE UNIQUE INDEX index_Layout_layoutId ON Layout (layoutId)")
                }
            }

            val MIGRATION_3_4 = object : Migration(3, 4) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    database.execSQL("CREATE TABLE `Page` (`id` INTEGER NOT NULL,`name` TEXT NOT NULL, `layoutId` TEXT NOT NULL, `pageId` TEXT NOT NULL, `exhibitionId` TEXT NOT NULL, `modifiedAt` TEXT NOT NULL, `resources` TEXT NOT NULL, `eventTriggers` TEXT NOT NULL, PRIMARY KEY(`id`))")
                    database.execSQL("CREATE INDEX index_Page_pageId ON Page (pageId)")
                }
            }

            val MIGRATION_4_5 = object : Migration(4, 5) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    database.execSQL("DROP TABLE `Page`")
                    database.execSQL("DROP TABLE `Layout`")
                    database.execSQL("CREATE TABLE `Layout` (`name` TEXT NOT NULL, `data` TEXT NOT NULL, `layoutId` TEXT NOT NULL, `modifiedAt` TEXT NOT NULL, PRIMARY KEY(`layoutId`))")
                    database.execSQL("CREATE TABLE `Page` (`name` TEXT NOT NULL, `layoutId` TEXT NOT NULL, `pageId` TEXT NOT NULL, `exhibitionId` TEXT NOT NULL, `modifiedAt` TEXT NOT NULL, `resources` TEXT NOT NULL, `eventTriggers` TEXT NOT NULL, PRIMARY KEY(`pageId`))")
                }
            }

            val MIGRATION_5_6 = object : Migration(5, 6) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    database.execSQL("ALTER TABLE `Layout` ADD `orientation` INT NOT NULL DEFAULT ${ActivityInfo.SCREEN_ORIENTATION_PORTRAIT}")
                }
            }

            val MIGRATION_6_7 = object : Migration(6, 7) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    database.execSQL("DROP TABLE `UpdateUserValueTask`")
                    database.execSQL("CREATE TABLE `UpdateUserValueTask` (`id` INTEGER NOT NULL,`sessionId` TEXT NOT NULL, `time` INTEGER NOT NULL, `priority` INTEGER NOT NULL,`name` TEXT NOT NULL,`value` TEXT NOT NULL, PRIMARY KEY(`id`))")
                }
            }

            val MIGRATION_7_8 = object : Migration(7, 8) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    database.execSQL("ALTER TABLE `Page` ADD COLUMN `enterTransition` TEXT NOT NULL ")
                    database.execSQL("ALTER TABLE `Page` ADD COLUMN `exitTransition` TEXT NOT NULL  ")
                }
            }

            val MIGRATION_8_9 = object : Migration(8, 9) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    database.execSQL("ALTER TABLE `Page` ADD COLUMN `language` TEXT NOT NULL default 'fi'")
                    database.execSQL("ALTER TABLE `Page` ADD COLUMN `orderNumber` INTEGER NOT NULL default 0")
                }
            }

            synchronized(this) {
                val builder =  Room.databaseBuilder(ExhibitionUIApplication.instance.applicationContext, ExhibitionUIDatabase::class.java, "ExhibitionUI.db")

                if (BuildConfig.DESTRUCTIVE_MIGRATIONS) {
                    builder.fallbackToDestructiveMigration()
                }

                val instance = builder
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9)
                    .build()

                INSTANCE = instance
                return instance
            }
        }
    }
}