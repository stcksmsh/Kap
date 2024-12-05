package io.github.stcksmsh.kap.data

import androidx.paging.PagingSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import java.util.Date

class WaterIntakeRepository(private val waterIntakeDao: WaterIntakeDao) {

    val coroutineScope = CoroutineScope(Dispatchers.IO)

    fun getPagedIntakes(): PagingSource<Int, WaterIntake> {
        return waterIntakeDao.getPagedIntakes()
    }

    suspend fun insertWaterIntake(waterIntake: WaterIntake) {
        withContext(Dispatchers.IO) {
            waterIntakeDao.insert(waterIntake)
        }
    }

    fun getCurrentIntake(): StateFlow<Float> {
        return waterIntakeDao.getTodaysIntake().stateIn(
            scope = coroutineScope,
            started = SharingStarted.Eagerly,
            initialValue = 0f
        )
    }

    fun getCurrentIntakeValue(): Float {
        return waterIntakeDao.getTodaysIntakeValue()
    }

    fun getLastWaterIntake(): WaterIntake? {
        return waterIntakeDao.getLastIntake()
    }

    fun getWaterIntakesBetween(start: Date, end: Date): Flow<List<WaterIntake>> {
        return waterIntakeDao.getIntakesBetweenDates(start, end)
    }

    suspend fun deleteWaterIntake(waterIntake: WaterIntake) {
        withContext(Dispatchers.IO) {
            waterIntakeDao.delete(waterIntake)
        }
    }

    suspend fun clearAllIntakes() {
        withContext(Dispatchers.IO) {
            waterIntakeDao.clearAll()
        }
    }
}
