package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CyclePrediction
import com.example.data.UserSettings
import com.example.ui.theme.BloodDropColor
import com.example.ui.theme.FertileColor
import com.example.ui.theme.OvulationColor
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Composable
fun HomeScreen(
    settings: UserSettings,
    predictions: List<CyclePrediction>,
    currentMonth: LocalDate,
    onMonthChange: (LocalDate) -> Unit,
    selectedDate: LocalDate?,
    onDateSelect: (LocalDate?) -> Unit
) {
    val formatterMonthYear = DateTimeFormatter.ofPattern("MMMM yyyy")
    val formatterDayMonth = DateTimeFormatter.ofPattern("d MMM")

    val yearMonth = YearMonth.of(currentMonth.year, currentMonth.month)
    val firstOfMonth = yearMonth.atDay(1)
    val daysInMonth = yearMonth.lengthOfMonth()
    
    // Calculate empty grid cells for Sunday start of week
    // DayOfWeek: Monday is 1, Sunday is 7.
    // Sunday start: Sunday should have 0 cells, Mon 1, Tue 2... Sat 6.
    val firstDayOfWeekVal = firstOfMonth.dayOfWeek.value
    val emptyCells = if (firstDayOfWeekVal == 7) 0 else firstDayOfWeekVal

    val daysList = (1..daysInMonth).toList()

    // Find the next period prediction to display on Summary Cards
    val today = LocalDate.now()
    val nextPeriod = predictions.firstOrNull { !it.periodStartDate.isBefore(today) }
        ?: predictions.firstOrNull()

    // Find the next ovulation prediction
    val nextOvulation = predictions.firstOrNull { !it.ovulationDate.isBefore(today) }
        ?: predictions.firstOrNull()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.Start
    ) {
        // App title subtitle header (Natural Tones Left-Aligned Header)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 16.dp)
        ) {
            Text(
                text = "TPO",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    letterSpacing = (-0.5).sp
                )
            )
            Text(
                text = "TRACK PERIOD & OVULATION",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 2.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Month Selection Header Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onMonthChange(currentMonth.minusMonths(1)) },
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.surface, shape = CircleShape)
                    .testTag("prev_month_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Previous Month",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                text = currentMonth.format(formatterMonthYear).uppercase(),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                    letterSpacing = 1.sp
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = { onMonthChange(currentMonth.plusMonths(1)) },
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.surface, shape = CircleShape)
                    .testTag("next_month_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Next Month",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Calendar Card
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Day of Week Header Sun, Mon, Tue, Wed, Thu, Fri, Sat (Natural Tones layout style)
                val daysOfWeek = listOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT")
                Row(modifier = Modifier.fillMaxWidth()) {
                    daysOfWeek.forEach { day ->
                        Text(
                            text = day,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .weight(1f)
                                .padding(vertical = 8.dp)
                        )
                    }
                }

                // Days Grid
                val totalCells = emptyCells + daysInMonth
                val rows = (totalCells + 6) / 7

                for (r in 0 until rows) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        for (c in 0..6) {
                            val cellIndex = r * 7 + c
                            if (cellIndex < emptyCells || cellIndex >= totalCells) {
                                Box(modifier = Modifier.weight(1f))
                            } else {
                                val dayNum = daysList[cellIndex - emptyCells]
                                val cellDate = yearMonth.atDay(dayNum)

                                // Check if this day is a Period Day or Ovulation Day
                                val isPeriodDay = predictions.any {
                                    !cellDate.isBefore(it.periodStartDate) && !cellDate.isAfter(it.periodEndDate)
                                }
                                val isOvulationDay = predictions.any {
                                    !cellDate.isBefore(it.fertileStartDate) && !cellDate.isAfter(it.fertileEndDate)
                                }

                                val isSelected = selectedDate?.isEqual(cellDate) == true

                                val cellBgColor = when {
                                    isSelected -> MaterialTheme.colorScheme.surface
                                    isPeriodDay -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                    isOvulationDay -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                                    else -> Color.Transparent
                                }

                                val cellBorderColor = when {
                                    isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                                    else -> Color.Transparent
                                }

                                val cellTextColor = when {
                                    isPeriodDay -> BloodDropColor
                                    isOvulationDay -> OvulationColor
                                    isSelected -> MaterialTheme.colorScheme.primary
                                    else -> MaterialTheme.colorScheme.onSurface
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .padding(3.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(cellBgColor)
                                        .border(
                                            width = if (isSelected) 1.5.dp else 0.dp,
                                            color = cellBorderColor,
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .clickable { onDateSelect(cellDate) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Text(
                                            text = dayNum.toString(),
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = if (isSelected || isPeriodDay || isOvulationDay) FontWeight.ExtraBold else FontWeight.Medium,
                                                color = cellTextColor
                                            )
                                        )
                                        
                                        Spacer(modifier = Modifier.height(2.dp))
                                        
                                        Row(
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            if (isPeriodDay) {
                                                Text("🩸", fontSize = 8.sp)
                                            } else if (isOvulationDay) {
                                                Text("🧬", fontSize = 8.sp)
                                            } else if (isSelected) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(4.dp)
                                                        .background(MaterialTheme.colorScheme.primary, shape = CircleShape)
                                                )
                                            } else {
                                                Spacer(modifier = Modifier.height(4.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Period Legend
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), shape = RoundedCornerShape(6.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🩸", fontSize = 10.sp)
                        }
                        Text(
                            text = "Predicted Period",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Fertile/Ovulation Legend
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.weight(1.1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f), shape = RoundedCornerShape(6.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🧬", fontSize = 10.sp)
                        }
                        Text(
                            text = "Fertile Window",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Selected Date Status Detail Box (Custom Mini Detail layout from Natural Tones style)
        selectedDate?.let { date ->
            // Check if selected date is:
            // 1. Period Day
            val matchingPeriodPrediction = predictions.firstOrNull {
                !date.isBefore(it.periodStartDate) && !date.isAfter(it.periodEndDate)
            }
            // 2. Ovulation Day / Fertile Window
            val matchingOvulationPrediction = predictions.firstOrNull {
                !date.isBefore(it.fertileStartDate) && !date.isAfter(it.fertileEndDate)
            }

            val subtitleText = when {
                matchingPeriodPrediction != null -> {
                    val dayNum = ChronoUnit.DAYS.between(matchingPeriodPrediction.periodStartDate, date).toInt() + 1
                    "Day $dayNum within predicted period flow"
                }
                matchingOvulationPrediction != null -> {
                    if (date.isEqual(matchingOvulationPrediction.ovulationDate)) {
                        "Estimated peak ovulation day"
                    } else {
                        "High fertility window day"
                    }
                }
                else -> {
                    val futurePeriod = predictions.firstOrNull { !it.periodStartDate.isBefore(date) }
                    if (futurePeriod != null) {
                        val daysUntil = ChronoUnit.DAYS.between(date, futurePeriod.periodStartDate)
                        if (daysUntil == 0L) "Period starts today!" else "$daysUntil days until period"
                    } else {
                        "Low chance of conception"
                    }
                }
            }

            val miniBoxBg = when {
                matchingPeriodPrediction != null -> BloodDropColor.copy(alpha = 0.12f)
                matchingOvulationPrediction != null -> OvulationColor.copy(alpha = 0.12f)
                else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
            }

            val miniBoxTextColor = when {
                matchingPeriodPrediction != null -> BloodDropColor
                matchingOvulationPrediction != null -> OvulationColor
                else -> MaterialTheme.colorScheme.primary
            }

            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Mini circular date visualizer
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(miniBoxBg, shape = RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = date.dayOfMonth.toString(),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = miniBoxTextColor
                            )
                        )
                    }

                    Column {
                        Text(
                            text = date.format(DateTimeFormatter.ofPattern("d MMMM yyyy")).uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = subtitleText,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Bottom Summary Cards (Two Column Grid)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Card 1: Next Period
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)),
                modifier = Modifier
                    .weight(1f)
                    .height(116.dp)
                    .testTag("next_period_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "NEXT PERIOD",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        letterSpacing = 1.sp
                    )

                    Column {
                        if (nextPeriod != null) {
                            val daysRemaining = ChronoUnit.DAYS.between(today, nextPeriod.periodStartDate)
                            Text(
                                text = "${nextPeriod.periodStartDate.format(formatterDayMonth)} – ${nextPeriod.periodEndDate.format(formatterDayMonth)}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 15.sp,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (daysRemaining > 0) "$daysRemaining days left" else if (daysRemaining == 0L) "Starts today!" else "In progress",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            Text("No prediction", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            // Card 2: Next Ovulation
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)),
                modifier = Modifier
                    .weight(1f)
                    .height(116.dp)
                    .testTag("next_ovulation_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "FERTILE WINDOW",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        letterSpacing = 1.sp
                    )

                    Column {
                        if (nextOvulation != null) {
                            val daysRemaining = ChronoUnit.DAYS.between(today, nextOvulation.fertileStartDate)
                            Text(
                                text = "${nextOvulation.fertileStartDate.format(formatterDayMonth)} – ${nextOvulation.fertileEndDate.format(formatterDayMonth)}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = OvulationColor,
                                fontSize = 14.sp,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (daysRemaining > 0) "$daysRemaining days left" else if (ChronoUnit.DAYS.between(today, nextOvulation.fertileEndDate) >= 0) "Active window!" else "Completed",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            Text("No prediction", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}
