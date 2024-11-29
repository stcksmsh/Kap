package io.github.stcksmsh.kap.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface WaterIntakeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(waterIntake: WaterIntake)

    @Query("SELECT * FROM water_intake ORDER BY date DESC")
    fun getAllIntakes(): Flow<List<WaterIntake>>

    @Query("SELECT * FROM water_intake WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getIntakesBetweenDates(startDate: Date, endDate: Date): Flow<List<WaterIntake>>

    @Query(
        "SELECT SUM(intakeAmount) FROM water_intake WHERE DATE(date / 1000, 'unixepoch') = DATE(:date / 1000, 'unixepoch')"
    )
    fun getTodaysIntake(date: Date = Date()): Flow<Float>

    @Query(
        "SELECT SUM(intakeAmount) FROM water_intake WHERE DATE(date / 1000, 'unixepoch') = DATE(:date / 1000, 'unixepoch')"
    )
    fun getTodaysIntakeValue(date: Date = Date()): Float

    @Query("SELECT * FROM water_intake ORDER BY date DESC LIMIT 1")
    fun getLastIntake(): WaterIntake?

    @Delete
    suspend fun delete(waterIntake: WaterIntake)

    @Query("DELETE FROM water_intake")
    suspend fun clearAll()
}
