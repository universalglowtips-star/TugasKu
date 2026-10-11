package com.bagus.tugasku.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.bagus.tugasku.data.local.Priority
import com.bagus.tugasku.data.local.Task
import com.bagus.tugasku.data.local.TaskStatus
import com.bagus.tugasku.util.DateUtils

/** Warna penanda prioritas (garis di sisi kiri kartu). */
fun priorityColor(priority: Priority): Color = when (priority) {
    Priority.TINGGI -> Color(0xFFD32F2F)
    Priority.SEDANG -> Color(0xFFF9A825)
    Priority.RENDAH -> Color(0xFF388E3C)
}

@Composable
fun StatusBadge(status: TaskStatus, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val (container, content) = when (status) {
        TaskStatus.BELUM -> colors.surfaceVariant to colors.onSurfaceVariant
        TaskStatus.PROSES -> colors.tertiaryContainer to colors.onTertiaryContainer
        TaskStatus.SELESAI -> colors.primaryContainer to colors.onPrimaryContainer
    }
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = container,
        contentColor = content,
    ) {
        Text(
            text = status.label,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
        )
    }
}

/**
 * Kartu satu tugas di layar Daftar. Tugas yang belum selesai dan deadline-nya dekat/lewat
 * ditandai dengan teks sisa hari berwarna merah.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskCard(
    task: Task,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val daysLeft = remember(task.deadline) { DateUtils.daysUntil(task.deadline) }
    val urgent = task.status != TaskStatus.SELESAI && daysLeft <= DateUtils.DUE_SOON_DAYS

    Card(onClick = onClick, modifier = modifier.fillMaxWidth()) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(priorityColor(task.prioritas)),
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = task.mataKuliah,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = task.judul,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column {
                        Text(
                            text = DateUtils.formatDate(task.deadline),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        if (task.status != TaskStatus.SELESAI) {
                            Text(
                                text = DateUtils.deadlineLabel(daysLeft),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = if (urgent) FontWeight.Bold else FontWeight.Normal,
                                color = if (urgent) {
                                    MaterialTheme.colorScheme.error
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                },
                            )
                        }
                    }
                    StatusBadge(status = task.status)
                }
            }
        }
    }
}
