package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.StudioService
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.GoldBrassPrimary
import com.example.ui.theme.GoldBrassSecondary
import com.example.ui.theme.MixeCrimson
import com.example.ui.theme.MixeTurquoise
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MusicViewModel

import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Logout
import com.example.ui.components.AppDownloadDialog
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun ProduccionesScreen(
    viewModel: MusicViewModel,
    bookingNotice: String?,
    onNavigatePublisher: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    var contactName by remember { mutableStateOf("") }
    var communityName by remember { mutableStateOf("") }
    var eventType by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var showDownloadDialog by remember { mutableStateOf(false) }

    val services = viewModel.allServices
    val currentUser = Firebase.auth.currentUser

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("producciones_screen_content"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Producer Bio Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(DarkBorder, DarkBorder)))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.gd_music_mixe_icon_1791515980161),
                            contentDescription = "GD Music Logo",
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color.Black),
                            contentScale = ContentScale.Crop
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "GD MUSIC MIXE PRODUCCIONES",
                                style = MaterialTheme.typography.titleMedium,
                                color = GoldBrassPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Gilito Daniel • Productor & Director",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextPrimary
                            )
                            Text(
                                text = "Sierra Mixe • Oaxaca, México",
                                style = MaterialTheme.typography.labelSmall,
                                color = MixeTurquoise
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Sello discográfico y productora audiovisual especializada en la preservación, grabación y difusión de la música tradicional de las bandas filarmónicas de la Sierra Mixe (Ayuuk). Llevamos la tecnología de audio profesional hasta las cumbres de Oaxaca.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        lineHeight = 20.sp
                    )
                }
            }
        }

        // Dedicated APK Download & Name Info Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("download_apk_info_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(listOf(GoldBrassPrimary, MixeTurquoise))
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(GoldBrassPrimary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = null,
                                tint = GoldBrassPrimary
                            )
                        }

                        Column {
                            Text(
                                text = "Descargar Aplicación (APK)",
                                style = MaterialTheme.typography.titleSmall,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Nombre oficial: GD Music Mixe",
                                style = MaterialTheme.typography.labelSmall,
                                color = GoldBrassPrimary
                            )
                        }
                    }

                    Text(
                        text = "Obtén el archivo instalador oficial 'GD_Music_MIXE_Producciones.apk' para instalarlo en cualquier celular Android. Consulta el nombre exacto, paquete y la guía de descarga paso a paso.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )

                    Button(
                        onClick = { showDownloadDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("open_download_guide_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldBrassPrimary,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Download, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Ver Nombre y Pasos para Descargar APK",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // Dedicated Cloud Publishing Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(listOf(GoldBrassPrimary, MixeCrimson))
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(GoldBrassPrimary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudUpload,
                                contentDescription = null,
                                tint = GoldBrassPrimary
                            )
                        }

                        Column {
                            Text(
                                text = "Panel de Publicación en la Nube",
                                style = MaterialTheme.typography.titleSmall,
                                color = TextPrimary
                            )
                            Text(
                                text = "Sube sones y compártelos con la audiencia",
                                style = MaterialTheme.typography.labelSmall,
                                color = GoldBrassPrimary
                            )
                        }
                    }

                    Text(
                        text = "Cualquier son, jarabe o cumbia que publiques se sincronizará automáticamente a través de Firebase Firestore con todos los teléfonos que tengan instalada la app GD Music.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )

                    Button(
                        onClick = onNavigatePublisher,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("go_to_publish_screen_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldBrassPrimary,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Publicar Nueva Canción / Estreno",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    // Account Info Row
                    if (currentUser != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Conectado como: ${currentUser.email ?: currentUser.displayName}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                            TextButton(onClick = onSignOut) {
                                Icon(
                                    imageVector = Icons.Default.Logout,
                                    contentDescription = "Cerrar sesión",
                                    tint = MixeCrimson,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Salir", color = MixeCrimson, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        // Studio Services Section
        item {
            Text(
                text = "Servicios de Producción para Bandas y Comités",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary
            )
        }

        items(services) { service ->
            StudioServiceCard(service = service)
        }

        // Booking & Quotation Form
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(DarkBorder, DarkBorder)))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = null,
                            tint = GoldBrassPrimary
                        )
                        Text(
                            text = "Contrataciones & Cotizaciones 2026/2027",
                            style = MaterialTheme.typography.titleSmall,
                            color = TextPrimary
                        )
                    }

                    Text(
                        text = "¿Deseas grabar un disco con tu banda o transmitir la fiesta patronal de tu comunidad? Déjanos tus datos y nos pondremos en contacto.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    OutlinedTextField(
                        value = contactName,
                        onValueChange = { contactName = it },
                        label = { Text("Nombre del Mayordomo o Comité") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldBrassPrimary,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("booking_name_input")
                    )

                    OutlinedTextField(
                        value = communityName,
                        onValueChange = { communityName = it },
                        label = { Text("Comunidad / Municipio (ej. Ayutla Mixe)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldBrassPrimary,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("booking_community_input")
                    )

                    OutlinedTextField(
                        value = eventType,
                        onValueChange = { eventType = it },
                        label = { Text("Tipo de Servicio (Calenda, Disco, Video)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldBrassPrimary,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("booking_service_input")
                    )

                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        label = { Text("Teléfono de Contacto / WhatsApp") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldBrassPrimary,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("booking_phone_input")
                    )

                    Button(
                        onClick = {
                            viewModel.submitBookingInquiry(
                                contactName = contactName,
                                community = communityName,
                                eventType = eventType,
                                phone = phoneNumber
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldBrassPrimary,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("submit_booking_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Enviar Solicitud a Gilito Daniel",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    // Notice if submitted
                    if (bookingNotice != null) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MixeCrimson.copy(alpha = 0.2f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = GoldBrassPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = bookingNotice,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextPrimary
                                    )
                                }
                                TextButton(
                                    onClick = { viewModel.clearBookingNotice() },
                                    modifier = Modifier.align(Alignment.End)
                                ) {
                                    Text("Aceptar", color = GoldBrassPrimary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDownloadDialog) {
        AppDownloadDialog(onDismiss = { showDownloadDialog = false })
    }
}

@Composable
fun StudioServiceCard(service: StudioService) {
    val icon = when (service.iconName) {
        "Mic" -> Icons.Default.Mic
        "Videocam" -> Icons.Default.Videocam
        "Equalizer" -> Icons.Default.Equalizer
        else -> Icons.Default.Album
    }

    val iconColor = when (service.iconName) {
        "Mic" -> GoldBrassPrimary
        "Videocam" -> MixeCrimson
        "Equalizer" -> MixeTurquoise
        else -> GoldBrassSecondary
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(DarkBorder, DarkBorder)))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = service.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary
                )
                Text(
                    text = service.subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = GoldBrassPrimary,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = service.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }
        }
    }
}
