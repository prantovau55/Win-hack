package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.BetColour
import com.example.data.model.BetSize
import com.example.data.model.PeriodResult

@Entity(tableName = "periods")
data class PeriodEntity(
    @PrimaryKey
    val period: String,
    val number: Int,
    val size: String, // "BIG", "SMALL"
    val colour: String, // "GREEN", "RED", "VIOLET"
    val hasVioletHedge: Boolean = false,
    val predictedSize: String? = null,
    val predictedConfidence: Double? = null,
    val wasCorrect: Boolean? = null,
    val timestamp: Long = System.currentTimeMillis()
) {
    fun toDomain(): PeriodResult {
        val s = if (size == "BIG") BetSize.BIG else BetSize.SMALL
        val c = when (colour) {
            "RED" -> BetColour.RED
            "VIOLET" -> BetColour.VIOLET
            else -> BetColour.GREEN
        }
        val pSize = when (predictedSize) {
            "BIG" -> BetSize.BIG
            "SMALL" -> BetSize.SMALL
            else -> null
        }
        return PeriodResult(
            period = period,
            number = number,
            size = s,
            colour = c,
            secondaryColour = if (hasVioletHedge) BetColour.VIOLET else null,
            timestamp = timestamp,
            predictedSize = pSize,
            predictedConfidence = predictedConfidence,
            wasCorrect = wasCorrect
        )
    }

    companion object {
        fun fromDomain(domain: PeriodResult): PeriodEntity {
            return PeriodEntity(
                period = domain.period,
                number = domain.number,
                size = domain.size.name,
                colour = domain.colour.name,
                hasVioletHedge = domain.secondaryColour == BetColour.VIOLET,
                predictedSize = domain.predictedSize?.name,
                predictedConfidence = domain.predictedConfidence,
                wasCorrect = domain.wasCorrect,
                timestamp = domain.timestamp
            )
        }
    }
}
