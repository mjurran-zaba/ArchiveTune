/*
 * ArchiveTune (2026)
 * © Rukamori — github.com/rukamori
 * GPL-3.0 License | Contributors: see git history
 * Do not remove or alter this notice. - Per GPL-3.0 Section 4 & Section 5
 */

package moe.rukamori.archivetune.ui.screens.library

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import moe.rukamori.archivetune.R
import moe.rukamori.archivetune.ads.presentation.SupportArchiveTuneScreenState
import moe.rukamori.archivetune.ads.presentation.SupportArchiveTuneUiEvent
import moe.rukamori.archivetune.ads.presentation.SupportArchiveTuneViewModel

internal const val supportArchiveTuneAvailable = true

@Composable
internal fun SupportArchiveTuneSection(
    modifier: Modifier = Modifier,
    onMessage: (String) -> Unit,
    viewModel: SupportArchiveTuneViewModel = hiltViewModel(),
) {
    val state by viewModel.screenState.collectAsStateWithLifecycle()
    val failureMessage = stringResource(R.string.support_archivetune_failed)
    val openSupportPage = remember(viewModel) { viewModel::onSupportArchiveTuneClick }
    var showQrModal by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel, onMessage, failureMessage) {
        viewModel.events.collect { event ->
            when (event) {
                SupportArchiveTuneUiEvent.OpenFailed -> onMessage(failureMessage)
            }
        }
    }

    SupportArchiveTuneCard(
        state = state,
        onClick = {
            showQrModal = true
            // [VoidTune Patch: Disable opening external ArchiveTune web page; preserve for future rebranding]
            // DO NOT DELETE BUT COMMENT:
            // openSupportPage()
        },
        modifier = modifier,
    )

    if (showQrModal) {
        SupportVoidTuneQrDialog(
            onDismiss = { showQrModal = false },
        )
    }
}

@Composable
private fun SupportVoidTuneQrDialog(
    onDismiss: () -> Unit,
) {
    val unifrakturCookFont = remember { FontFamily(Font(R.font.unifrakturcook)) }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth(0.92f)
                    .widthIn(max = 380.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF0D1117).copy(alpha = 0.95f))
                    .background(Color.White.copy(alpha = 0.14f)) // 35% faded white milk glassmorphism
                    .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.22f)), RoundedCornerShape(16.dp))
                    .padding(22.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                // Header: Title with UnifrakturCook font
                Text(
                    text = "Support VoidTune",
                    fontFamily = unifrakturCookFont,
                    style =
                        MaterialTheme.typography.titleLarge.copy(
                            fontSize = 22.sp,
                            letterSpacing = 1.1.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                    color = Color.White,
                )

                // Subtitle: Every single coin you offer, reforges Void's spell
                Text(
                    text = "“Every single coin you offer, reforges Void's spell”",
                    style =
                        MaterialTheme.typography.bodySmall.copy(
                            fontStyle = FontStyle.Italic,
                            fontSize = 12.5.sp,
                            lineHeight = 18.sp,
                        ),
                    textAlign = TextAlign.Center,
                    color = Color(0xFF38BDF8).copy(alpha = 0.90f),
                    modifier = Modifier.padding(horizontal = 8.dp),
                )

                // QR Code Image Box
                Box(
                    modifier =
                        Modifier
                            .size(220.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .padding(12.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(R.drawable.img_support_qr),
                        contentDescription = "VoidTune Support QR Code",
                        modifier = Modifier.fillMaxSize(),
                    )
                }

                // Footer note
                Text(
                    text = "Scan using any e-Wallet or Banking app (QRIS)\nDeveloper • Mjurran | INDONESIA",
                    style =
                        MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.5.sp,
                            lineHeight = 14.sp,
                        ),
                    textAlign = TextAlign.Center,
                    color = Color.White.copy(alpha = 0.50f),
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Close Button
                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp),
                    colors =
                        ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White.copy(alpha = 0.85f),
                        ),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.22f)),
                    modifier = Modifier.fillMaxWidth(0.5f),
                ) {
                    Text("Close")
                }
            }
        }
    }
}

@Composable
private fun SupportArchiveTuneCard(
    state: SupportArchiveTuneScreenState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // VoidTune: 35% faded white milk glassmorphism with curved rectangle (16dp)
    val containerColor = Color.White.copy(alpha = 0.12f)
    val contentColor = Color.White
    val iconContainerColor = Color(0xFF38BDF8).copy(alpha = 0.20f)
    val iconContainerModifier =
        remember(iconContainerColor) {
            Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(iconContainerColor)
                .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.45f), RoundedCornerShape(14.dp))
        }
    val description =
        when (state) {
            is SupportArchiveTuneScreenState.Loading -> {
                stringResource(R.string.support_archivetune_preparing)
            }

            is SupportArchiveTuneScreenState.Success -> {
                stringResource(R.string.support_archivetune_description)
            }

            is SupportArchiveTuneScreenState.Empty -> {
                stringResource(R.string.support_archivetune_unavailable)
            }

            is SupportArchiveTuneScreenState.Error -> {
                stringResource(R.string.support_archivetune_retry)
            }
        }

    Card(
        onClick = onClick,
        enabled =
            state !is SupportArchiveTuneScreenState.Loading &&
                state !is SupportArchiveTuneScreenState.Empty,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.22f)),
        colors =
            CardDefaults.cardColors(
                containerColor = containerColor,
                contentColor = contentColor,
                disabledContainerColor = containerColor,
                disabledContentColor = contentColor.copy(alpha = 0.7f),
            ),
        modifier =
            modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 22.dp),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = iconContainerModifier,
            ) {
                Icon(
                    painter = painterResource(R.drawable.star),
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(34.dp),
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = stringResource(R.string.support_archivetune_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(64.dp),
            ) {
                if (state is SupportArchiveTuneScreenState.Loading) {
                    CircularProgressIndicator(
                        color = contentColor,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(28.dp),
                    )
                } else {
                    Icon(
                        painter = painterResource(R.drawable.play),
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.size(32.dp),
                    )
                }
            }
        }
    }
}
