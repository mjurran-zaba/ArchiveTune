// Always meticulous in everyhthing to minimize the case
// Be honest in everything
// Be meticulous in everything
// Dont replace or delete code, coment and explain what and why, the write your change below
/*
 * VoidTune (2026)
 * © Mjurran — github.com/mjurran-zaba/VoidTune
 * GPL-3.0 License
 */

@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package moe.rukamori.archivetune.ui.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moe.rukamori.archivetune.LocalPlayerAwareWindowInsets
import moe.rukamori.archivetune.R
import moe.rukamori.archivetune.constants.AccountEmailKey
import moe.rukamori.archivetune.constants.AccountNameKey
import moe.rukamori.archivetune.constants.CustomProfileAvatarPathKey
import moe.rukamori.archivetune.constants.CustomProfileNameKey
import moe.rukamori.archivetune.constants.SpotifyAccountNameKey
import moe.rukamori.archivetune.constants.SpotifySpDcKey
import moe.rukamori.archivetune.ui.component.IconButton
import moe.rukamori.archivetune.ui.utils.backToMain
import moe.rukamori.archivetune.utils.rememberPreference
import java.io.File
import java.io.FileOutputStream

@Composable
fun ProfileScreen(
    navController: NavController,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val (customName, onCustomNameChange) = rememberPreference(CustomProfileNameKey, "")
    val (customAvatarPath, onCustomAvatarPathChange) = rememberPreference(CustomProfileAvatarPathKey, "")
    val (spotifyName) = rememberPreference(SpotifyAccountNameKey, "")
    val (spotifySpDc) = rememberPreference(SpotifySpDcKey, "")
    val (ytName) = rememberPreference(AccountNameKey, "")
    val (ytEmail) = rememberPreference(AccountEmailKey, "")

    val isSpotifyConnected = spotifySpDc.isNotBlank()
    val isYouTubeConnected = ytName.isNotBlank() || ytEmail.isNotBlank()

    val photoPickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
        ) { uri: Uri? ->
            if (uri != null) {
                coroutineScope.launch(Dispatchers.IO) {
                    val savedPath = saveImageToInternalStorage(context, uri)
                    if (savedPath != null) {
                        withContext(Dispatchers.Main) {
                            onCustomAvatarPathChange(savedPath)
                        }
                    }
                }
            }
        }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.account),
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = navController::navigateUp,
                        onLongClick = navController::backToMain,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_back),
                            contentDescription = null,
                        )
                    }
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                        navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    ),
            )
        },
        contentWindowInsets = LocalPlayerAwareWindowInsets.current,
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(12.dp))

            // 1. Large Circular Avatar (~100dp) with Tap to Pick from Gallery
            Box(
                modifier =
                    Modifier
                        .size(104.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                        .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.6f), CircleShape)
                        .clickable {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                            )
                        },
                contentAlignment = Alignment.Center,
            ) {
                if (customAvatarPath.isNotBlank() && File(customAvatarPath).exists()) {
                    AsyncImage(
                        model = File(customAvatarPath),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                    )
                } else {
                    Icon(
                        painter = painterResource(R.drawable.account),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(54.dp),
                    )
                }

                // Overlay Camera Badge
                Box(
                    modifier =
                        Modifier
                            .align(Alignment.BottomEnd)
                            .padding(2.dp)
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .border(1.5.dp, MaterialTheme.colorScheme.surface, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PhotoCamera,
                        contentDescription = "Change photo",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // 2. Centered Username Input Column (Height ~1.5 lines, Minimalist)
            Text(
                text = "Username",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(6.dp))
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth(0.85f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.7f))
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                        .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center,
            ) {
                if (customName.isEmpty()) {
                    Text(
                        text = "Enter display name...",
                        style =
                            TextStyle(
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                            ),
                    )
                }
                BasicTextField(
                    value = customName,
                    onValueChange = onCustomNameChange,
                    singleLine = true,
                    textStyle =
                        TextStyle(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                        ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Spacer(Modifier.height(36.dp))

            // 3. Connected Services / Integrations Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Connected Services",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }

            Spacer(Modifier.height(14.dp))

            // 4. Spotify Service Card
            ConnectedServiceCard(
                serviceName = "Spotify",
                iconRes = R.drawable.spotify,
                iconTint = Color(0xFF1DB954),
                isConnected = isSpotifyConnected,
                accountDetail = if (isSpotifyConnected) spotifyName.ifBlank { "Connected" } else "Not connected",
                onActionClick = {
                    navController.navigate("settings/integration") { launchSingleTop = true }
                },
                actionLabel = if (isSpotifyConnected) "Manage" else "Connect",
                actionContainerColor = if (isSpotifyConnected) MaterialTheme.colorScheme.surfaceContainerHighest else Color(0xFF1DB954),
                actionContentColor = if (isSpotifyConnected) MaterialTheme.colorScheme.onSurface else Color.White,
            )

            Spacer(Modifier.height(12.dp))

            // 5. YouTube Music Service Card
            ConnectedServiceCard(
                serviceName = "YouTube Music",
                iconRes = R.drawable.youtube,
                iconTint = Color(0xFFEF4444),
                isConnected = isYouTubeConnected,
                accountDetail = if (isYouTubeConnected) ytName.ifBlank { ytEmail.ifBlank { "Logged in" } } else "Guest Mode (Not logged in)",
                onActionClick = {
                    if (isYouTubeConnected) {
                        navController.navigate("settings/account") { launchSingleTop = true }
                    } else {
                        navController.navigate("login") { launchSingleTop = true }
                    }
                },
                actionLabel = if (isYouTubeConnected) "Manage" else "Login",
                actionContainerColor = if (isYouTubeConnected) MaterialTheme.colorScheme.surfaceContainerHighest else MaterialTheme.colorScheme.primary,
                actionContentColor = if (isYouTubeConnected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onPrimary,
            )
        }
    }
}

@Composable
private fun ConnectedServiceCard(
    serviceName: String,
    iconRes: Int,
    iconTint: Color,
    isConnected: Boolean,
    accountDetail: String,
    onActionClick: () -> Unit,
    actionLabel: String,
    actionContainerColor: Color,
    actionContentColor: Color,
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.85f),
            ),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f),
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(iconTint.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(iconRes),
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(24.dp),
                    )
                }

                Spacer(Modifier.width(14.dp))

                Column {
                    Text(
                        text = serviceName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = accountDetail,
                        style = MaterialTheme.typography.bodySmall,
                        color =
                            if (isConnected) {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f)
                            },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            Spacer(Modifier.width(10.dp))

            Button(
                onClick = onActionClick,
                shape = RoundedCornerShape(12.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = actionContainerColor,
                        contentColor = actionContentColor,
                    ),
                modifier = Modifier.height(38.dp),
            ) {
                Text(
                    text = actionLabel,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

private fun saveImageToInternalStorage(context: Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val targetFile = File(context.filesDir, "custom_profile_avatar.png")
        FileOutputStream(targetFile).use { outputStream ->
            inputStream.copyTo(outputStream)
        }
        targetFile.absolutePath
    } catch (e: Exception) {
        null
    }
}
