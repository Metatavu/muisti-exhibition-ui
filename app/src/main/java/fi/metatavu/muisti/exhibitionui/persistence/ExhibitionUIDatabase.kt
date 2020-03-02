package fi.metatavu.muisti.exhibitionui.persistence

import android.content.Context
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
import fi.metatavu.muisti.exhibitionui.persistence.dao.UpdateUserValueTaskDao
import fi.metatavu.muisti.exhibitionui.persistence.model.DeviceSetting
import fi.metatavu.muisti.exhibitionui.persistence.model.ExhibitionPageLayoutViewConverter
import fi.metatavu.muisti.exhibitionui.persistence.model.Layout
import fi.metatavu.muisti.exhibitionui.persistence.model.UpdateUserValueTask
import fi.metatavu.muisti.exhibitionui.settings.DeviceSettings

/**
 * The Room database
 */
@Database(entities = [ UpdateUserValueTask::class, DeviceSetting::class, Layout::class], version = 3)
@TypeConverters(ExhibitionPageLayoutViewConverter::class)
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
     * Getter for DeviceSettingDao
     *
     * @return deviceSettingDao
     */
    abstract fun layoutDao(): LayoutDao

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
                    database.execSQL("CREATE TABLE `DeviceSetting` (`id` INTEGER NOT NULL, `name` TEXT NOT NULL, `value` TEXT NOT NULL, PRIMARY KEY(`id`))")
                    database.execSQL("CREATE UNIQUE INDEX index_DeviceSetting_name ON DeviceSetting (name)")
                }
            }

            val MIGRATION_2_3 = object : Migration(2, 3) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    database.execSQL("CREATE TABLE `Layout` (`id` INTEGER NOT NULL ,`name` TEXT NOT NULL, `data` TEXT NOT NULL, `layoutId` TEXT NOT NULL, `exhibitionId` TEXT NOT NULL, `creatorId` TEXT NOT NULL, `lastModifierId` TEXT NOT NULL, `createdAt` TEXT NOT NULL, `modifiedAt` TEXT NOT NULL, PRIMARY KEY(`id`))")
                    database.execSQL("CREATE UNIQUE INDEX index_Layout_layoutId ON Layout (layoutId)")
                }
            }

            synchronized(this) {
                val builder =  Room.databaseBuilder(ExhibitionUIApplication.instance.applicationContext, ExhibitionUIDatabase::class.java, "ExhibitionUI.db")

                if(BuildConfig.DESTRUCTIVE_MIGRATIONS) {
                    builder.fallbackToDestructiveMigration()
                }

                val instance = builder
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build()

                INSTANCE = instance
                return instance
            }
        }
    }
}