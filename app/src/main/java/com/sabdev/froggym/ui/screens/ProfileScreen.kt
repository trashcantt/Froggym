package com.sabdev.froggym.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.sabdev.froggym.data.entities.User
import kotlin.math.pow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    user: User?,
    onEditProfile: () -> Unit
) {
    val screenPadding = 16.dp
    val spaceBetweenElements = 16.dp
    val profilePictureSize = 120.dp
    val statsSpacing = 8.dp
    val borderWidth = 3.dp

    // Obtener colores del tema actual
    val colorScheme = MaterialTheme.colorScheme
    val gradientColors = listOf(
        colorScheme.primary,
        colorScheme.onSecondary,
        colorScheme.primary,
        colorScheme.onTertiary,
        colorScheme.primary
    )

    if (user == null) {
        Text("No se ha encontrado información del usuario")
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(screenPadding)
        ) {
            Text(
                text = user.name,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(spaceBetweenElements))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(profilePictureSize),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .border(
                                width = borderWidth,
                                brush = Brush.linearGradient(gradientColors),
                                shape = CircleShape
                            )
                    )
                    Image(
                        painter = rememberAsyncImagePainter(
                            ImageRequest.Builder(LocalContext.current)
                                .data(data = user.profilePicturePath)
                                .build()
                        ),
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .size(profilePictureSize - borderWidth * 2)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }

                Row(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    UserStat("Altura", "${user.height.toInt()} cm")
                    Spacer(modifier = Modifier.width(statsSpacing))
                    UserStat("Peso", String.format("%.1f kg", user.weight))
                    Spacer(modifier = Modifier.width(statsSpacing))
                    UserStat("IMC", String.format("%.1f", calculateBMI(user.height, user.weight)))
                }
            }

            Spacer(modifier = Modifier.height(spaceBetweenElements))

            Button(
                onClick = onEditProfile,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Editar")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Editar Perfil")
            }
        }
    }
}

@Composable
fun UserStat(label: String, value: String) {
    val valueFontSize = 18.sp
    val labelFontSize = 14.sp
    val spaceBetweenValueAndLabel = 4.dp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = value,
            fontWeight = FontWeight.Bold,
            fontSize = valueFontSize
        )
        Spacer(modifier = Modifier.height(spaceBetweenValueAndLabel))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontSize = labelFontSize
        )
    }
}

fun calculateBMI(height: Float, weight: Float): Float {
    return weight / (height / 100).pow(2)
}