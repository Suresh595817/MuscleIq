package com.example.muscleiq.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.muscleiq.data.StrengthRecord
import com.example.muscleiq.data.VolumeRecord
import com.example.muscleiq.ui.viewmodel.AnalyticsViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    onNavigateBack: () -> Unit,
    viewModel: AnalyticsViewModel = viewModel()
) {
    val strengthData by viewModel.strengthData.collectAsState()
    val volumeData by viewModel.volumeData.collectAsState()
    val totalWorkouts by viewModel.totalWorkouts.collectAsState()
    val currentStreak by viewModel.currentStreak.collectAsState()
    val totalWeightThisMonth by viewModel.totalWeightThisMonth.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Insights & Progress", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Summary Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SummaryCard(
                    modifier = Modifier.weight(1f),
                    title = "Workouts",
                    value = totalWorkouts.toString(),
                    icon = { Icon(Icons.Filled.Timer, contentDescription = null, tint = Color(0xFF4A90E2)) }
                )
                SummaryCard(
                    modifier = Modifier.weight(1f),
                    title = "Streak",
                    value = "$currentStreak Days",
                    icon = { Icon(Icons.Filled.LocalFireDepartment, contentDescription = null, tint = Color(0xFFFF9800)) }
                )
            }

            // Total Volume Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.MonitorWeight,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = Color(0xFFE91E63)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Total Volume (This Month)", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f), fontSize = 14.sp)
                        val formattedWeight = NumberFormat.getNumberInstance(Locale.US).format(totalWeightThisMonth)
                        Text("$formattedWeight kg", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // Strength Progression Chart
            Text("Deadlift 1RM Progression", fontWeight = FontWeight.Bold, fontSize = 20.sp, modifier = Modifier.padding(top = 8.dp))
            Card(
                modifier = Modifier.fillMaxWidth().height(250.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(modifier = Modifier.padding(16.dp).fillMaxSize()) {
                    StrengthLineChart(data = strengthData)
                }
            }

            // Weekly Volume Chart
            Text("Weekly Volume", fontWeight = FontWeight.Bold, fontSize = 20.sp, modifier = Modifier.padding(top = 8.dp))
            Card(
                modifier = Modifier.fillMaxWidth().height(250.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(modifier = Modifier.padding(16.dp).fillMaxSize()) {
                    VolumeBarChart(data = volumeData)
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun SummaryCard(modifier: Modifier = Modifier, title: String, value: String, icon: @Composable () -> Unit) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                icon()
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(value, fontWeight = FontWeight.Bold, fontSize = 22.sp)
        }
    }
}

@Composable
fun StrengthLineChart(data: List<StrengthRecord>) {
    if (data.isEmpty()) return
    
    val textMeasurer = rememberTextMeasurer()
    val lineColor = Color(0xFF4A90E2)
    val textColor = MaterialTheme.colorScheme.onSurfaceVariant
    val backgroundColor = MaterialTheme.colorScheme.background
    
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        
        val maxWeight = data.maxOf { it.maxWeight }
        val minWeight = (data.minOf { it.maxWeight } - 10f).coerceAtLeast(0f)
        val range = maxWeight - minWeight
        
        val paddingX = 40.dp.toPx()
        val paddingY = 30.dp.toPx()
        
        val graphWidth = width - paddingX
        val graphHeight = height - paddingY
        
        val xStep = graphWidth / (data.size - 1).coerceAtLeast(1)
        
        val path = Path()
        val coordinates = mutableListOf<Offset>()
        
        data.forEachIndexed { index, record ->
            val x = index * xStep
            val y = graphHeight - ((record.maxWeight - minWeight) / range) * graphHeight
            val offset = Offset(x, y)
            coordinates.add(offset)
            
            if (index == 0) {
                path.moveTo(x, y)
            } else {
                val prevX = (index - 1) * xStep
                val prevY = graphHeight - ((data[index - 1].maxWeight - minWeight) / range) * graphHeight
                // Cubic bezier for smooth curves
                path.cubicTo(
                    prevX + xStep / 2, prevY,
                    x - xStep / 2, y,
                    x, y
                )
            }
        }
        
        // Fill area under curve
        val fillPath = Path().apply {
            addPath(path)
            lineTo(graphWidth, graphHeight)
            lineTo(0f, graphHeight)
            close()
        }
        
        clipRect(right = graphWidth, bottom = graphHeight) {
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(lineColor.copy(alpha = 0.4f), Color.Transparent),
                    startY = 0f,
                    endY = graphHeight
                )
            )
        }
        
        // Draw the line
        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
        )
        
        // Draw points and labels
        coordinates.forEachIndexed { index, offset ->
            drawCircle(color = backgroundColor, radius = 6.dp.toPx(), center = offset)
            drawCircle(color = lineColor, radius = 4.dp.toPx(), center = offset)
            
            // X-axis label
            drawText(
                textMeasurer = textMeasurer,
                text = data[index].date,
                style = TextStyle(color = textColor, fontSize = 12.sp),
                topLeft = Offset(offset.x - 10.dp.toPx(), graphHeight + 8.dp.toPx())
            )
        }
        
        // Y-axis labels
        val ySteps = 4
        for (i in 0..ySteps) {
            val weight = minWeight + (range * i / ySteps)
            val y = graphHeight - (graphHeight * i / ySteps)
            drawText(
                textMeasurer = textMeasurer,
                text = "${weight.toInt()}k",
                style = TextStyle(color = textColor, fontSize = 12.sp),
                topLeft = Offset(graphWidth + 8.dp.toPx(), y - 8.dp.toPx())
            )
        }
    }
}

@Composable
fun VolumeBarChart(data: List<VolumeRecord>) {
    if (data.isEmpty()) return
    
    val textMeasurer = rememberTextMeasurer()
    val barColor = Color(0xFF673AB7)
    val textColor = MaterialTheme.colorScheme.onSurfaceVariant
    
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        
        val maxVolume = data.maxOf { it.totalVolume }.coerceAtLeast(1f)
        
        val paddingY = 30.dp.toPx()
        val graphHeight = height - paddingY
        val barSpacing = 16.dp.toPx()
        val totalSpacing = barSpacing * (data.size - 1)
        val barWidth = (width - totalSpacing) / data.size
        
        data.forEachIndexed { index, record ->
            val barHeight = (record.totalVolume / maxVolume) * graphHeight
            val x = index * (barWidth + barSpacing)
            val y = graphHeight - barHeight
            
            drawRoundRect(
                color = barColor,
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
            )
            
            // Draw value text above bar if not 0
            if (record.totalVolume > 0f) {
                val formattedVal = if (record.totalVolume >= 1000f) "${(record.totalVolume / 1000f).toInt()}k" else record.totalVolume.toInt().toString()
                drawText(
                    textMeasurer = textMeasurer,
                    text = formattedVal,
                    style = TextStyle(color = textColor, fontSize = 10.sp, fontWeight = FontWeight.Bold),
                    topLeft = Offset(x, y - 16.dp.toPx())
                )
            }
            
            // X-axis label
            drawText(
                textMeasurer = textMeasurer,
                text = record.dayOfWeek,
                style = TextStyle(color = textColor, fontSize = 12.sp),
                topLeft = Offset(x + (barWidth / 4), graphHeight + 8.dp.toPx())
            )
        }
    }
}
