package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.UserBet
import com.example.data.model.UserBetType

@Entity(tableName = "bets")
data class BetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val period: String,
    val betType: String,
    val targetNumber: Int? = null,
    val amount: Int,
    val winAmount: Int = 0,
    val isSettled: Boolean = false,
    val isWin: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
) {
    fun toDomain(): UserBet {
        return UserBet(
            id = id,
            period = period,
            betType = try { UserBetType.valueOf(betType) } catch (e: Exception) { UserBetType.BIG },
            targetNumber = targetNumber,
            amount = amount,
            winAmount = winAmount,
            isSettled = isSettled,
            isWin = isWin,
            timestamp = timestamp
        )
    }

    companion object {
        fun fromDomain(domain: UserBet): BetEntity {
            return BetEntity(
                id = domain.id,
                period = domain.period,
                betType = domain.betType.name,
                targetNumber = domain.targetNumber,
                amount = domain.amount,
                winAmount = domain.winAmount,
                isSettled = domain.isSettled,
                isWin = domain.isWin,
                timestamp = domain.timestamp
            )
        }
    }
}
