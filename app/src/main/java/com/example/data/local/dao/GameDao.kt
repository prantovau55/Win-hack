package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.BetEntity
import com.example.data.local.entity.PeriodEntity
import com.example.data.local.entity.UserAccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("SELECT * FROM periods ORDER BY timestamp DESC LIMIT 100")
    fun getRecentPeriodsFlow(): Flow<List<PeriodEntity>>

    @Query("SELECT * FROM periods ORDER BY timestamp DESC LIMIT 100")
    suspend fun getRecentPeriods(): List<PeriodEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPeriod(period: PeriodEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPeriods(periods: List<PeriodEntity>)

    @Query("DELETE FROM periods")
    suspend fun clearPeriods()

    // Bets
    @Query("SELECT * FROM bets ORDER BY timestamp DESC LIMIT 50")
    fun getRecentBetsFlow(): Flow<List<BetEntity>>

    @Query("SELECT * FROM bets WHERE period = :period AND isSettled = 0")
    suspend fun getPendingBetsForPeriod(period: String): List<BetEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBet(bet: BetEntity): Long

    @Update
    suspend fun updateBet(bet: BetEntity)

    // User Account
    @Query("SELECT * FROM user_account WHERE id = 1")
    fun getUserAccountFlow(): Flow<UserAccountEntity?>

    @Query("SELECT * FROM user_account WHERE id = 1")
    suspend fun getUserAccount(): UserAccountEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserAccount(account: UserAccountEntity)

    @Update
    suspend fun updateUserAccount(account: UserAccountEntity)
}
