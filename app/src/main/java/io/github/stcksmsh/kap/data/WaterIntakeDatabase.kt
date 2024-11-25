package io.github.stcksmsh.kap.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [WaterIntake::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class WaterIntakeDatabase : RoomDatabase() {
    abstract fun waterIntakeDao(): WaterIntakeDao
}
