package com.example.data

import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class CyclePrediction(
    val cycleNumber: Int,
    val periodStartDate: LocalDate,
    val periodEndDate: LocalDate,
    val ovulationDate: LocalDate,
    val fertileStartDate: LocalDate,
    val fertileEndDate: LocalDate
)

object CycleCalculator {

    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

    fun calculatePredictions(
        lastPeriodStartStr: String,
        cycleLength: Int,
        periodDuration: Int,
        logs: List<PeriodLog> = emptyList(),
        count: Int = 12
    ): List<CyclePrediction> {
        val baseDate = if (logs.isNotEmpty()) {
            try {
                LocalDate.parse(logs.maxByOrNull { it.startDate }!!.startDate, formatter)
            } catch (e: Exception) {
                parseLocalDate(lastPeriodStartStr)
            }
        } else {
            parseLocalDate(lastPeriodStartStr)
        }

        val predictions = mutableListOf<CyclePrediction>()
        var currentStart = baseDate

        // Generate 12 months (or count) of predictions
        for (i in 1..count) {
            val periodStart = currentStart
            val periodEnd = periodStart.plusDays((periodDuration - 1).toLong())
            val nextPeriodStart = periodStart.plusDays(cycleLength.toLong())
            val ovulation = nextPeriodStart.minusDays(14)
            val fertileStart = ovulation.minusDays(5) // Fertile window starts 5 days before ovulation
            val fertileEnd = ovulation // and ends on ovulation day

            predictions.add(
                CyclePrediction(
                    cycleNumber = i,
                    periodStartDate = periodStart,
                    periodEndDate = periodEnd,
                    ovulationDate = ovulation,
                    fertileStartDate = fertileStart,
                    fertileEndDate = fertileEnd
                )
            )
            currentStart = nextPeriodStart
        }
        return predictions
    }

    private fun parseLocalDate(dateStr: String): LocalDate {
        return try {
            LocalDate.parse(dateStr, formatter)
        } catch (e: Exception) {
            LocalDate.now()
        }
    }
}
