/*
 * ArchiveTune / VoidTune (2026)
 * © Rukamori — github.com/rukamori
 * © Mjurran — github.com/mjurran-zaba/ArchiveTune
 * GPL-3.0 License | Contributors: see git history
 * Do not remove or alter this notice. - Per GPL-3.0 Section 4 & Section 5
 */

@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package moe.rukamori.archivetune.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SegmentedListItem
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import moe.rukamori.archivetune.R

// [VoidTune Patch: Custom Mjurran Greetings & Authentic Support QR Dialog]
// MAKSUD KODE ASLI: Menampilkan dialog standar ArchiveTune dari developer lama (rukamori)
// dengan tautan donasi ke miuka.moe dan repositori upstream rukamori/ArchiveTune.
// MENGAPA DIKOMEN:
// Mengganti teks salam personal dari Mjurran dengan gaya miring (italic) dan font gothic UnifrakturCook untuk kata VoidTune,
// serta menyisipkan link interaktif ke situs moezaq.vercel.app. Tombol GitHub dialihkan ke repo pengembang VoidTune
// dan tombol Support langsung memunculkan QRIS authentic Mjurran tanpa perantara pihak ketiga.
// ORIGINAL CODE:
/*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StarDialog(
    onDismissRequest: () -> Unit,
    onSupport: () -> Unit,
    onLater: () -> Unit,
) {
    val uriHandler = LocalUriHandler.current

    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = AlertDialogDefaults.shape,
            color = AlertDialogDefaults.containerColor,
            contentColor = AlertDialogDefaults.textContentColor,
            tonalElevation = AlertDialogDefaults.TonalElevation,
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Surface(
                        shape = MaterialTheme.shapes.large,
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.star),
                            contentDescription = null,
                            modifier =
                                Modifier
                                    .padding(12.dp)
                                    .size(AlertDialogDefaults.IconSize),
                        )
                    }

                    Text(
                        text = stringResource(R.string.support_development_title),
                        modifier = Modifier.weight(1f).semantics { heading() },
                        color = AlertDialogDefaults.titleContentColor,
                        style = MaterialTheme.typography.headlineSmall,
                    )
                }

                Spacer(Modifier.height(16.dp))

                Column(
                    modifier =
                        Modifier
                            .weight(1f, fill = false)
                            .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text(
                        text = stringResource(R.string.support_development_message),
                        style = MaterialTheme.typography.bodyMedium,
                    )

                    val supportItemColors =
                        ListItemDefaults.segmentedColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        )
                    Column(verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap)) {
                        SegmentedListItem(
                            shapes = ListItemDefaults.segmentedShapes(index = 0, count = 2),
                            colors = supportItemColors,
                            modifier = Modifier.semantics(mergeDescendants = true) {},
                            leadingContent = {
                                Icon(painterResource(R.drawable.coffee), contentDescription = null)
                            },
                            supportingContent = {
                                Text(stringResource(R.string.onboarding_community_donate_desc))
                            },
                        ) {
                            Text(stringResource(R.string.about_content_desc_donate))
                        }

                        SegmentedListItem(
                            shapes = ListItemDefaults.segmentedShapes(index = 1, count = 2),
                            colors = supportItemColors,
                            modifier = Modifier.semantics(mergeDescendants = true) {},
                            leadingContent = {
                                Icon(painterResource(R.drawable.github), contentDescription = null)
                            },
                            supportingContent = {
                                Text(stringResource(R.string.onboarding_community_github_desc))
                            },
                        ) {
                            Text(stringResource(R.string.about_content_desc_github))
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            uriHandler.openUri("https://miuka.moe/donate")
                            onSupport()
                        },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                        shapes = ButtonDefaults.shapes(),
                        contentPadding = ButtonDefaults.contentPaddingFor(ButtonDefaults.MinHeight, hasStartIcon = true),
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.coffee),
                            contentDescription = null,
                            modifier = Modifier.size(ButtonDefaults.IconSize),
                        )
                        Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                        Text(
                            text = stringResource(R.string.support_development_donate),
                            textAlign = TextAlign.Center,
                        )
                    }

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        TextButton(
                            onClick = onLater,
                            modifier = Modifier.heightIn(min = 48.dp),
                            shapes = ButtonDefaults.shapes(),
                        ) {
                            Text(stringResource(R.string.later), textAlign = TextAlign.Center)
                        }

                        FilledTonalButton(
                            onClick = {
                                uriHandler.openUri("https://github.com/mjurran-zaba/VoidTune")
                                onSupport()
                            },
                            modifier = Modifier.heightIn(min = 48.dp),
                            shapes = ButtonDefaults.shapes(),
                            contentPadding = ButtonDefaults.contentPaddingFor(ButtonDefaults.MinHeight, hasStartIcon = true),
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.github),
                                contentDescription = null,
                                modifier = Modifier.size(ButtonDefaults.IconSize),
                            )
                            Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                            Text(
                                text = stringResource(R.string.support_development_star),
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }
        }
    }
}
*/

// KODE PERUBAHAN DI BAWAH:
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StarDialog(
    onDismissRequest: () -> Unit,
    onSupport: () -> Unit,
    onLater: () -> Unit,
) {
    val uriHandler = LocalUriHandler.current
    val unifrakturCookFont = remember { FontFamily(Font(R.font.unifrakturcook)) }
    val primaryColor = MaterialTheme.colorScheme.primary
    var showQrDialog by remember { mutableStateOf(false) }

    val annotatedGreeting = remember(primaryColor) {
        buildAnnotatedString {
            withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                append("yoshaaa~ hope you always be blessed, here is Mjurran, arigatou gozaimuch for enjoying ")
            }
            withStyle(
                SpanStyle(
                    fontFamily = unifrakturCookFont,
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Bold,
                    color = primaryColor,
                    fontSize = 17.sp,
                )
            ) {
                append("VoidTune")
            }
            withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                append(", if you dont mind plesea visit ")
            }
            val linkStyles = TextLinkStyles(
                style = SpanStyle(
                    color = primaryColor,
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline,
                    fontStyle = FontStyle.Italic,
                )
            )
            val hereStart = length
            append("here")
            val hereEnd = length
            addLink(LinkAnnotation.Url("https://moezaq.vercel.app/", linkStyles), hereStart, hereEnd)

            withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                append(", or support ")
            }
            withStyle(
                SpanStyle(
                    fontFamily = unifrakturCookFont,
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Bold,
                    color = primaryColor,
                    fontSize = 17.sp,
                )
            ) {
                append("VoidTune")
            }
        }
    }

    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = AlertDialogDefaults.shape,
            color = AlertDialogDefaults.containerColor,
            contentColor = AlertDialogDefaults.textContentColor,
            tonalElevation = AlertDialogDefaults.TonalElevation,
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Surface(
                        shape = MaterialTheme.shapes.large,
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.star),
                            contentDescription = null,
                            modifier =
                                Modifier
                                    .padding(12.dp)
                                    .size(AlertDialogDefaults.IconSize),
                        )
                    }

                    Text(
                        text = "Support VoidTune",
                        fontFamily = unifrakturCookFont,
                        modifier = Modifier.weight(1f).semantics { heading() },
                        color = AlertDialogDefaults.titleContentColor,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                    )
                }

                Spacer(Modifier.height(16.dp))

                Column(
                    modifier =
                        Modifier
                            .weight(1f, fill = false)
                            .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text(
                        text = annotatedGreeting,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            lineHeight = 22.sp,
                            fontSize = 14.5.sp,
                        ),
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Tombol Support: Langsung menampilkan QR Code QRIS Mjurran
                    Button(
                        onClick = {
                            showQrDialog = true
                            onSupport()
                        },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                        shapes = ButtonDefaults.shapes(),
                        contentPadding = ButtonDefaults.contentPaddingFor(ButtonDefaults.MinHeight, hasStartIcon = true),
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.coffee),
                            contentDescription = null,
                            modifier = Modifier.size(ButtonDefaults.IconSize),
                        )
                        Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                        Text(
                            text = "Support VoidTune (QRIS)",
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        TextButton(
                            onClick = onLater,
                            modifier = Modifier.heightIn(min = 48.dp),
                            shapes = ButtonDefaults.shapes(),
                        ) {
                            Text(stringResource(R.string.later), textAlign = TextAlign.Center)
                        }

                        // Tombol GitHub: Mengarah langsung ke repository VoidTune pengembang
                        FilledTonalButton(
                            onClick = {
                                uriHandler.openUri("https://github.com/mjurran-zaba/ArchiveTune")
                                onSupport()
                            },
                            modifier = Modifier.heightIn(min = 48.dp),
                            shapes = ButtonDefaults.shapes(),
                            contentPadding = ButtonDefaults.contentPaddingFor(ButtonDefaults.MinHeight, hasStartIcon = true),
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.github),
                                contentDescription = null,
                                modifier = Modifier.size(ButtonDefaults.IconSize),
                            )
                            Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                            Text(
                                text = "GitHub",
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                }
            }
        }
    }

    if (showQrDialog) {
        SupportQrModal(
            unifrakturCookFont = unifrakturCookFont,
            onDismiss = { showQrDialog = false },
        )
    }
}

@Composable
private fun SupportQrModal(
    unifrakturCookFont: FontFamily,
    onDismiss: () -> Unit,
) {
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
