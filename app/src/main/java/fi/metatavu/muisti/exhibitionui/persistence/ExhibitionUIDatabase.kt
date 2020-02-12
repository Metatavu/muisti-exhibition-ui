package fi.metatavu.muisti.exhibitionui.persistence

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import fi.metatavu.muisti.exhibitionui.persistence.dao.UpdateUserValueTaskDao
import fi.metatavu.muisti.exhibitionui.persistence.model.UpdateUserValueTask

/**
 * The Room database
 */
@Database(entities = [ UpdateUserValueTask::class], version = 1)
abstract class ExhibitionUIDatabase : RoomDatabase() {

    /**
     * Getter for UpdateUserValueTaskDao
     *
     * @return UpdateUserValueTaskDao
     */
    abstract fun updateUserValueTaskDao(): UpdateUserValueTaskDao

    companion object {

        /**
         * Private variable for singleton instance
         */
        @Volatile
        private var INSTANCE: ExhibitionUIDatabase? = null

        /**
         * Returns database instance
         *
         * @param context context
         * @return database instance
         */
        fun getDatabase(context: Context): ExhibitionUIDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }

            synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext, ExhibitionUIDatabase::class.java, "ExhibitionUI.db")
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                return instance
            }
        }
    }
}