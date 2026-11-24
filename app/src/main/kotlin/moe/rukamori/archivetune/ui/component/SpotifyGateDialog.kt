/*
 * VoidTune (2026)
 * © Mjurran — github.com/mjurran-zaba/ArchiveTune
 * GPL-3.0 License
 */

package moe.rukamori.archivetune.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import moe.rukamori.archivetune.R

@Composable
fun SpotifyGateDialog(
    onContinue: () -> Unit,
    onDismiss: () -> Unit,
) {
    val unifrakturCookFont = remember { FontFamily(Font(R.font.unifrakturcook)) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth(0.90f)
                    .widthIn(max = 400.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF16191E).copy(alpha = 0.55f)) // 45% glassmorphism faded grey charcoal
                    .background(Color.White.copy(alpha = 0.08f)) // Specular glass sheen
                    .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.22f)), RoundedCornerShape(20.dp))
                    .padding(24.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Header with VoidTune Seal Icon
                Box(
                    modifier =
                        Modifier
                            .size(68.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.18f))
                            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_voidtune_seal),
                        contentDescription = "VoidTune Magic Seal",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(56.dp).clip(RoundedCornerShape(14.dp)),
                    )
                }

                Text(
                    text = "Gates of VoidTune",
                    fontFamily = unifrakturCookFont,
                    style =
                        MaterialTheme.typography.titleLarge.copy(
                            fontSize = 24.sp,
                            letterSpacing = 1.1.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                    color = Color.White,
                    textAlign = TextAlign.Center,
                )

                // Enhanced RPG / Gate message
                Text(
                    text = "“We are still forging and fortifying the gates. Please follow our journey! If you don't mind, continue to connect your Spotify account for a seamless sync, or you can skip for later.”",
                    style =
                        MaterialTheme.typography.bodyMedium.copy(
                            fontStyle = FontStyle.Italic,
                            fontSize = 14.sp,
                            lineHeight = 21.sp,
                        ),
                    textAlign = TextAlign.Center,
                    color = Color.White.copy(alpha = 0.90f),
                    modifier = Modifier.padding(horizontal = 4.dp),
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp),
                        colors =
                            ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.White.copy(alpha = 0.80f),
                            ),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f)),
                        modifier = Modifier.weight(1f).height(46.dp),
                    ) {
                        Text("Skip")
                    }

                    Button(
                        onClick = onContinue,
                        shape = RoundedCornerShape(12.dp),
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                            ),
                        modifier = Modifier.weight(1.2f).height(46.dp),
                    ) {
                        Text(
                            text = "Continue",
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        }
    }
}
