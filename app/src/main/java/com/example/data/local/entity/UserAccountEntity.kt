package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_account")
data class UserAccountEntity(
    @PrimaryKey
    val id: Int = 1,
    val balance: Int = 10000,
    val totalTrades: Int = 0,
    val winTrades: Int = 0,
    val totalProfit: Int = 0
)
