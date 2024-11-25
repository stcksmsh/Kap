package io.github.stcksmsh.kap.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "water_intake")
data class WaterIntake(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val intakeAmount: Float, // Amount of water in milliliters
    val date: Date // Date and time of intake
)
