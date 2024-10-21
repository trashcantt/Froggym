package com.sabdev.froggym.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.sabdev.froggym.data.entities.User
import com.sabdev.froggym.utils.BMICalculator
import com.sabdev.froggym.viewmodel.AuthViewModel
import java.util.Calendar
import kotlin.math.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(authViewModel: AuthViewModel) {
    val currentUser by authViewModel.currentUser.collectAsState()

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            SpotifyStyleWelcome(currentUser)
            Spacer(modifier = Modifier.height(16.dp))
            MinecraftStyleBMIIndicator(currentUser)
        }
    }
}

@Composable
fun SpotifyStyleWelcome(user: User?) {
    val greeting = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
        in 5..11 -> "Buenos días"
        in 12..18 -> "Buenas tardes"
        else -> "Buenas noches"
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = greeting,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Text(
                text = user?.name ?: "Usuario",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
fun MinecraftStyleBMIIndicator(user: User?) {
    val bmi = user?.let { BMICalculator.calculateBMI(it.height, it.weight) } ?: 0f
    val normalizedBMI = (bmi - 16f) / (40f - 16f)  // Normaliza el BMI entre 16 y 40
    val indicatorPosition = max(0f, min(1f, normalizedBMI))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Tu IMC: ${String.format("%.1f", bmi)}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
            ) {
                // Barra de fondo con degradado
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val gradient = Brush.horizontalGradient(
                        colors = listOf(Color(0xFF87CEEB), Color.Green, Color.Red),
                        startX = 0f,
                        endX = size.width
                    )
                    drawRoundRect(
                        brush = gradient,
                        cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                    )
                }

                // Marcas de la barra (más pequeñas y más numerosas)
                for (i in 0..20) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxHeight(0.5f)
                            .width(0.5.dp)
                            .align(Alignment.CenterStart)
                            .offset(x = (i / 20f) * LocalConfiguration.current.screenWidthDp.dp)
                    ) {
                        drawRect(Color.Black.copy(alpha = 0.3f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Flecha indicadora
            Box(modifier = Modifier.fillMaxWidth()) {
                Canvas(
                    modifier = Modifier
                        .size(16.dp)
                        .align(Alignment.TopStart)
                        .offset(x = (indicatorPosition * (LocalConfiguration.current.screenWidthDp - 32).dp) - 8.dp)
                ) {
                    val path = Path().apply {
                        moveTo(size.width / 2, 0f)
                        lineTo(0f, size.height)
                        lineTo(size.width, size.height)
                        close()
                    }
                    drawPath(path, Color.White)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Infrapeso", color = Color(0xFF87CEEB), fontSize = 12.sp)
                Text("Normal", color = Color.Green, fontSize = 12.sp)
                Text("Sobrepeso", color = Color.Red, fontSize = 12.sp)
            }
        }
    }
}