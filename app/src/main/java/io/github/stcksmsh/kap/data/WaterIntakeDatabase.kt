package io.github.stcksmsh.kap.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [WaterIntake::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class WaterIntakeDatabase : RoomDatabase() {
    abstract fun waterIntakeDao(): WaterIntakeDao

    companion object {
        const val DATABASE_NAME = "water_intake_database"
        @Volatile
        private var INSTANCE: WaterIntakeDatabase? = null

        fun getDatabase(context: Context): WaterIntakeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WaterIntakeDatabase::class.java,
                    DATABASE_NAME
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
