package fi.metatavu.muisti.exhibitionui.persistence

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import fi.metatavu.muisti.exhibitionui.ExhibitionUIApplication
import fi.metatavu.muisti.exhibitionui.persistence.dao.DeviceSettingDao
import fi.metatavu.muisti.exhibitionui.persistence.dao.UpdateUserValueTaskDao
import fi.metatavu.muisti.exhibitionui.persistence.model.DeviceSetting
import fi.metatavu.muisti.exhibitionui.persistence.model.UpdateUserValueTask

/**
 * The Room database
 */
@Database(entities = [ UpdateUserValueTask::class, DeviceSetting::class ], version = 2)
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

    companion object {

        /**
         * Private variable for singleton instance
         */
        @Volatile
        private var INSTANCE: ExhibitionUIDatabase? = null

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
                    database.execSQL("CREATE TABLE `DeviceSetting` (`id` INTEGER, `name` TEXT NOT NULL, `value` TEXT NOT NULL, PRIMARY KEY(`id`))")
                }
            }

            synchronized(this) {
                val instance = Room.databaseBuilder(ExhibitionUIApplication.instance.applicationContext, ExhibitionUIDatabase::class.java, "ExhibitionUI.db")
                    .fallbackToDestructiveMigration()
                    .addMigrations(MIGRATION_1_2)
                    .build()

                INSTANCE = instance
                return instance
            }
        }
    }
}