/*
 * ArchiveTune (2026)
 * © Rukamori — github.com/rukamori
 * GPL-3.0 License | Contributors: see git history
 * Do not remove or alter this notice. - Per GPL-3.0 Section 4 & Section 5
 */

package moe.rukamori.archivetune.ui.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.webkit.CookieManager
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import moe.rukamori.archivetune.LocalPlayerAwareWindowInsets
import moe.rukamori.archivetune.R
import moe.rukamori.archivetune.innertube.utils.hasCompleteYouTubeLoginCookies
import moe.rukamori.archivetune.ui.component.IconButton as AppIconButton
import moe.rukamori.archivetune.ui.utils.backToMain
import moe.rukamori.archivetune.utils.resetAuthWebViewSession
import moe.rukamori.archivetune.viewmodels.LoginScreenState
import moe.rukamori.archivetune.viewmodels.LoginViewModel

const val LOGIN_ROUTE = "login"
const val LOGIN_URL_ARGUMENT = "url"

fun buildLoginRoute(startUrl: String? = null): String {
    val resolvedUrl = startUrl?.trim().takeUnless { it.isNullOrBlank() } ?: return LOGIN_ROUTE
    return "$LOGIN_ROUTE?$LOGIN_URL_ARGUMENT=${Uri.encode(resolvedUrl)}"
}

private const val DEFAULT_LOGIN_URL = "https://accounts.google.com/ServiceLogin?continue=https%3A%2F%2Fmusic.youtube.com"
private const val YOUTUBE_MUSIC_PACKAGE_NAME = "com.google.android.apps.youtube.music"
private const val LOGIN_CONTEXT_RETRY_DELAY_MS = 1_000L
private const val LOGIN_CONTEXT_EXTRACTION_ATTEMPTS = 10

private const val LOGIN_CONTEXT_SCRIPT =
    "(function(){try{var c=window.ytcfg;var y=window.yt&&window.yt.config_;var s=document.querySelectorAll('script');var v=c&&c.get&&c.get('VISITOR_DATA')||y&&y.VISITOR_DATA;var d=c&&c.get&&c.get('DATASYNC_ID')||y&&y.DATASYNC_ID;var n=c&&c.get?c.get('SESSION_INDEX'):undefined;if(n==null&&y)n=y.SESSION_INDEX;for(var i=0;i<s.length&&(!v||!d||n==null);i++){var x=s[i].textContent;if(!v){var vm=x.match(/[\"']VISITOR_DATA[\"']\\s*:\\s*[\"']([^\"']+)[\"']/);if(vm)v=vm[1]}if(!d){var dm=x.match(/[\"'](?:DATASYNC_ID|dataSyncId)[\"']\\s*:\\s*[\"']([^\"']+)[\"']/);if(dm)d=dm[1]}if(n==null){var nm=x.match(/[\"']SESSION_INDEX[\"']\\s*:\\s*[\"']?(\\d+)/);if(nm)n=nm[1]}}if(v)Android.onRetrieveVisitorData(v);if(d)Android.onRetrieveDataSyncId(d);if(n!=null)Android.onRetrieveAccountIndex(String(n))}catch(e){}})();"

private val YOUTUBE_COOKIE_URLS =
    listOf(
        "https://music.youtube.com",
        "https://www.youtube.com",
        "https://youtube.com",
    )

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    startUrl: String? = null,
    onLoginComplete: (() -> Unit)? = null,
    onNavigateBack: (() -> Unit)? = null,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()

    val screenState by viewModel.screenState.collectAsStateWithLifecycle()
    val loginSuccessMessage = stringResource(R.string.login_success)
    var webView by remember { mutableStateOf<WebView?>(null) }

    // VoidTune AuthSheet State
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorAttempts by remember { mutableIntStateOf(0) }
    var isHunterTriggered by remember { mutableStateOf(false) }
    var showHunterPopup by remember { mutableStateOf(false) }
    var hunterCooldown by remember { mutableIntStateOf(0) }
    var isSubmitting by remember { mutableStateOf(false) }
    var showWebFallback by remember { mutableStateOf(false) }
    val cardShakeX = remember { Animatable(0f) }

    val unifrakturCookFont = remember { FontFamily(Font(R.font.unifrakturcook)) }

    // Cooldown Timer
    LaunchedEffect(hunterCooldown) {
        if (hunterCooldown > 0) {
            delay(1000L)
            hunterCooldown -= 1
        }
    }

    LaunchedEffect(screenState, loginSuccessMessage) {
        if (screenState is LoginScreenState.Success) {
            val accountName = (screenState as LoginScreenState.Success).account.name
            Toast.makeText(context, "Authenticated • Welcome, $accountName", Toast.LENGTH_SHORT).show()
            if (onLoginComplete != null) {
                onLoginComplete()
            } else {
                navController.navigateUp()
            }
        }
    }

    val closeAction: () -> Unit = remember(onNavigateBack, navController) {
        {
            if (onNavigateBack != null) {
                onNavigateBack()
            } else {
                navController.navigateUp()
            }
            Unit
        }
    }

    // Background Auth Bridge WebView
    Box(
        modifier = if (showWebFallback) {
            Modifier.fillMaxSize().background(Color(0xFF000000))
        } else {
            Modifier.size(1.dp).alpha(0.01f)
        },
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                WebView(ctx).apply {
                    val cookieManager = CookieManager.getInstance()
                    webViewClient =
                        YouTubeLoginWebViewClient(
                            cookieManager = cookieManager,
                            onCookiesCaptured = viewModel::onCookiesCaptured,
                        )
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        setSupportZoom(true)
                        builtInZoomControls = true
                        displayZoomControls = false
                    }
                    val loginWebView = this
                    addJavascriptInterface(
                        object {
                            @JavascriptInterface
                            fun onRetrieveVisitorData(newVisitorData: String?) {
                                loginWebView.post {
                                    viewModel.onVisitorDataExtracted(newVisitorData)
                                }
                            }

                            @JavascriptInterface
                            fun onRetrieveDataSyncId(newDataSyncId: String?) {
                                loginWebView.post {
                                    viewModel.onDataSyncIdExtracted(newDataSyncId)
                                }
                            }

                            @JavascriptInterface
                            fun onRetrieveAccountIndex(newAccountIndex: String?) {
                                loginWebView.post {
                                    viewModel.onAccountIndexExtracted(newAccountIndex)
                                }
                            }
                        },
                        "Android",
                    )
                    webView = this
                    resetAuthWebViewSession(ctx, this, clearCookies = true) {
                        if (webView === this) {
                            loadUrl(startUrl?.takeIf { it.isNotBlank() } ?: DEFAULT_LOGIN_URL)
                        }
                    }
                }
            },
            onRelease = { releasedWebView ->
                (releasedWebView.webViewClient as? YouTubeLoginWebViewClient)?.release(releasedWebView)
                releasedWebView.removeJavascriptInterface("Android")
                releasedWebView.stopLoading()
                releasedWebView.destroy()
                if (webView === releasedWebView) {
                    webView = null
                }
            },
        )
    }

    // VoidTune Glassmorphic AuthSheet Modal Overlay (Centered Screen Dialog)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.70f)),
        contentAlignment = Alignment.Center,
    ) {
        AnimatedVisibility(
            visible = !isHunterTriggered,
            enter = scaleIn(initialScale = 0.88f, animationSpec = tween(320, easing = FastOutSlowInEasing)) + fadeIn(animationSpec = tween(280)),
            exit = slideOutHorizontally(targetOffsetX = { it * 2 }, animationSpec = tween(380, easing = FastOutLinearInEasing)) + fadeOut(animationSpec = tween(280)),
        ) {
            val crackAnim by animateFloatAsState(
                targetValue = if (errorAttempts >= 3) 1f else 0f,
                animationSpec = tween(600, easing = FastOutSlowInEasing),
                label = "crackAnim",
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .widthIn(max = 420.dp)
                    .offset(x = cardShakeX.value.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF0D1117).copy(alpha = 0.95f))
                    .background(Color.White.copy(alpha = 0.14f)) // 35% faded white milk glassmorphism
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(16.dp),
                    )
                    .padding(horizontal = 22.dp, vertical = 20.dp),
            ) {
                Column {
                    // Header: Google Logo + VoidTune • Private Security + Divider
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_google),
                            contentDescription = "Google",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(22.dp),
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "VoidTune",
                                fontFamily = unifrakturCookFont,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontSize = 22.sp,
                                    letterSpacing = 1.sp,
                                ),
                                color = Color.White,
                            )
                            Text(
                                text = " • Private Security",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White.copy(alpha = 0.85f),
                            )
                        }
                    }

                    HorizontalDivider(
                        thickness = 0.5.dp,
                        color = Color.White.copy(alpha = 0.15f),
                        modifier = Modifier.padding(bottom = 18.dp),
                    )

                    // Main Content: Gmail input
                    Text(
                        text = "Google Account",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.70f),
                        modifier = Modifier.padding(bottom = 6.dp),
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.08f))
                            .border(1.dp, Color.White.copy(alpha = 0.20f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        if (emailInput.isEmpty()) {
                            Text(
                                text = "yourname@gmail.com",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.35f),
                            )
                        }
                        BasicTextField(
                            value = emailInput,
                            onValueChange = { emailInput = it },
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
                            cursorBrush = SolidColor(Color(0xFF38BDF8)),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next,
                            ),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Password input with Lucide Eye on the left
                    Text(
                        text = "Password",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.70f),
                        modifier = Modifier.padding(bottom = 6.dp),
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.08f))
                            .border(1.dp, Color.White.copy(alpha = 0.20f), RoundedCornerShape(12.dp))
                            .padding(end = 14.dp),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            IconButton(
                                onClick = { passwordVisible = !passwordVisible },
                                modifier = Modifier.size(46.dp),
                            ) {
                                Icon(
                                    painter = painterResource(
                                        if (passwordVisible) R.drawable.ic_eye else R.drawable.visibility_off,
                                    ),
                                    contentDescription = "Toggle password visibility",
                                    tint = if (passwordVisible) Color(0xFF38BDF8) else Color.White.copy(alpha = 0.60f),
                                    modifier = Modifier.size(20.dp),
                                )
                            }
                            Box(
                                modifier = Modifier.weight(1f),
                                contentAlignment = Alignment.CenterStart,
                            ) {
                                if (passwordInput.isEmpty()) {
                                    Text(
                                        text = "••••••••",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.White.copy(alpha = 0.35f),
                                    )
                                }
                                BasicTextField(
                                    value = passwordInput,
                                    onValueChange = { passwordInput = it },
                                    singleLine = true,
                                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
                                    cursorBrush = SolidColor(Color(0xFF38BDF8)),
                                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Password,
                                        imeAction = ImeAction.Done,
                                    ),
                                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Buttons Row with Dynamic Security Warning on Left
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        // Error Warning Text on Left
                        Box(modifier = Modifier.weight(1f)) {
                            if (hunterCooldown > 0) {
                                Text(
                                    text = "Lockout: ${hunterCooldown}s",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFFEF4444),
                                    fontWeight = FontWeight.Bold,
                                )
                            } else {
                                when (errorAttempts) {
                                    1 -> Text(
                                        text = "Input Invalid",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFFEF4444),
                                        fontWeight = FontWeight.Medium,
                                    )
                                    2 -> Text(
                                        text = "Beware of a Typo",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFFF59E0B),
                                        fontWeight = FontWeight.Medium,
                                    )
                                    else -> Unit
                                }
                            }
                        }

                        // Action Buttons: Skip & Login
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedButton(
                                onClick = closeAction,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color.White.copy(alpha = 0.85f),
                                ),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.22f)),
                            ) {
                                Text("Skip")
                            }

                            Button(
                                onClick = {
                                    if (hunterCooldown > 0) return@Button
                                    val trimmedEmail = emailInput.trim()
                                    val trimmedPass = passwordInput.trim()

                                    // Validate Credentials
                                    if (trimmedEmail.isEmpty() || !trimmedEmail.contains("@") || trimmedPass.length < 4) {
                                        val newAttempt = errorAttempts + 1
                                        errorAttempts = newAttempt
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)

                                        if (newAttempt >= 3) {
                                            // Hunter Defense Trigger Sequence with Shudder & Fracture
                                            coroutineScope.launch {
                                                // Dynamic card vibration shudder
                                                launch {
                                                    cardShakeX.animateTo(-14f, tween(35))
                                                    cardShakeX.animateTo(14f, tween(35))
                                                    cardShakeX.animateTo(-10f, tween(35))
                                                    cardShakeX.animateTo(10f, tween(35))
                                                    cardShakeX.animateTo(-5f, tween(35))
                                                    cardShakeX.animateTo(5f, tween(35))
                                                    cardShakeX.animateTo(0f, tween(35))
                                                }
                                                delay(650L) // Allow wall fracture animation & shake to render vividly
                                                isHunterTriggered = true
                                                showHunterPopup = true
                                                hunterCooldown = 30
                                                delay(900L) // Exactly 0.9s duration
                                                showHunterPopup = false
                                                closeAction()
                                            }
                                        }
                                    } else {
                                        // Zero-Persistence Rule: Wipe credentials immediately after dispatching
                                        isSubmitting = true
                                        webView?.loadUrl(DEFAULT_LOGIN_URL)
                                        // Clear password memory
                                        passwordInput = ""
                                        coroutineScope.launch {
                                            delay(1200L)
                                            isSubmitting = false
                                            showWebFallback = true // If 2FA/CAPTCHA is requested, reveal dark web frame
                                        }
                                    }
                                },
                                enabled = hunterCooldown <= 0 && !isSubmitting,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF38BDF8),
                                    contentColor = Color(0xFF0D1117),
                                ),
                            ) {
                                if (isSubmitting) {
                                    CircularProgressIndicator(
                                        color = Color(0xFF0D1117),
                                        strokeWidth = 2.dp,
                                        modifier = Modifier.size(16.dp),
                                    )
                                } else {
                                    Text("Login", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    HorizontalDivider(
                        thickness = 0.5.dp,
                        color = Color.White.copy(alpha = 0.12f),
                        modifier = Modifier.padding(bottom = 10.dp),
                    )

                    // Footer: UNDER SECURITY OF DEVELOPER | FORTIFYING GATES
                    Text(
                        text = "UNDER SECURITY OF DEVELOPER | FORTIFYING GATES",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            letterSpacing = 1.1.sp,
                            fontWeight = FontWeight.Medium,
                        ),
                        color = Color.White.copy(alpha = 0.40f),
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                    )
                }

                // Hyper-Realistic Wall & Armor Fracture Crack Animation Overlay
                if (crackAnim > 0f) {
                    Canvas(modifier = Modifier.matchParentSize()) {
                        val w = size.width
                        val h = size.height
                        val cx = w * 0.48f
                        val cy = h * 0.44f
                        val p = crackAnim

                        val strokeGlow = Stroke(width = 3.8.dp.toPx(), cap = StrokeCap.Round)
                        val strokeCore = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round)
                        val strokeWeb = Stroke(width = 1.1.dp.toPx(), cap = StrokeCap.Round)

                        val glowColor = Color(0xFF38BDF8).copy(alpha = 0.50f * p)
                        val coreColor = Color.White.copy(alpha = 0.95f * p)
                        val webColor = Color.White.copy(alpha = 0.70f * p)

                        // 1. Impact epicenter glow
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0xFF38BDF8).copy(alpha = 0.40f * p), Color.Transparent),
                                center = Offset(cx, cy),
                                radius = 60.dp.toPx() * p,
                            ),
                            radius = 60.dp.toPx() * p,
                            center = Offset(cx, cy),
                        )

                        // 2. Central crushed core polygon
                        val crater = Path().apply {
                            moveTo(cx, cy - 14.dp.toPx() * p)
                            lineTo(cx + 12.dp.toPx() * p, cy - 6.dp.toPx() * p)
                            lineTo(cx + 16.dp.toPx() * p, cy + 10.dp.toPx() * p)
                            lineTo(cx + 4.dp.toPx() * p, cy + 18.dp.toPx() * p)
                            lineTo(cx - 12.dp.toPx() * p, cy + 14.dp.toPx() * p)
                            lineTo(cx - 18.dp.toPx() * p, cy - 2.dp.toPx() * p)
                            close()
                        }
                        drawPath(crater, glowColor, style = strokeGlow)
                        drawPath(crater, coreColor, style = strokeCore)

                        // 3. Primary Radial Fissures (6 Jagged Branches)
                        // Branch 1: Top-Left (toward top left corner)
                        val b1 = Path().apply {
                            moveTo(cx, cy)
                            lineTo(cx - 38f * p, cy - 32f * p)
                            lineTo(cx - 65f * p, cy - 78f * p)
                            lineTo(cx - 110f * p, cy - 120f * p)
                            lineTo(cx - 160f * p, cy - 185f * p)
                            lineTo(cx - 210f * p, cy - 260f * p)
                        }
                        val b1Sub1 = Path().apply {
                            moveTo(cx - 65f * p, cy - 78f * p)
                            lineTo(cx - 45f * p, cy - 130f * p)
                            lineTo(cx - 75f * p, cy - 195f * p)
                        }
                        val b1Sub2 = Path().apply {
                            moveTo(cx - 110f * p, cy - 120f * p)
                            lineTo(cx - 165f * p, cy - 128f * p)
                            lineTo(cx - 225f * p, cy - 142f * p)
                        }

                        // Branch 2: Top-Right
                        val b2 = Path().apply {
                            moveTo(cx, cy)
                            lineTo(cx + 42f * p, cy - 36f * p)
                            lineTo(cx + 80f * p, cy - 82f * p)
                            lineTo(cx + 130f * p, cy - 115f * p)
                            lineTo(cx + 185f * p, cy - 170f * p)
                            lineTo(cx + 240f * p, cy - 230f * p)
                        }
                        val b2Sub1 = Path().apply {
                            moveTo(cx + 80f * p, cy - 82f * p)
                            lineTo(cx + 120f * p, cy - 60f * p)
                            lineTo(cx + 175f * p, cy - 72f * p)
                        }

                        // Branch 3: Right
                        val b3 = Path().apply {
                            moveTo(cx, cy)
                            lineTo(cx + 48f * p, cy + 12f * p)
                            lineTo(cx + 95f * p, cy - 8f * p)
                            lineTo(cx + 150f * p, cy + 18f * p)
                            lineTo(cx + 215f * p, cy + 8f * p)
                            lineTo(cx + 280f * p, cy + 24f * p)
                        }
                        val b3Sub1 = Path().apply {
                            moveTo(cx + 95f * p, cy - 8f * p)
                            lineTo(cx + 135f * p, cy + 55f * p)
                            lineTo(cx + 185f * p, cy + 85f * p)
                        }

                        // Branch 4: Bottom-Right
                        val b4 = Path().apply {
                            moveTo(cx, cy)
                            lineTo(cx + 34f * p, cy + 44f * p)
                            lineTo(cx + 68f * p, cy + 96f * p)
                            lineTo(cx + 112f * p, cy + 155f * p)
                            lineTo(cx + 150f * p, cy + 225f * p)
                            lineTo(cx + 195f * p, cy + 300f * p)
                        }
                        val b4Sub1 = Path().apply {
                            moveTo(cx + 68f * p, cy + 96f * p)
                            lineTo(cx + 120f * p, cy + 108f * p)
                            lineTo(cx + 165f * p, cy + 135f * p)
                        }

                        // Branch 5: Bottom-Left
                        val b5 = Path().apply {
                            moveTo(cx, cy)
                            lineTo(cx - 36f * p, cy + 42f * p)
                            lineTo(cx - 72f * p, cy + 94f * p)
                            lineTo(cx - 118f * p, cy + 148f * p)
                            lineTo(cx - 160f * p, cy + 215f * p)
                            lineTo(cx - 205f * p, cy + 285f * p)
                        }
                        val b5Sub1 = Path().apply {
                            moveTo(cx - 72f * p, cy + 94f * p)
                            lineTo(cx - 125f * p, cy + 82f * p)
                            lineTo(cx - 180f * p, cy + 95f * p)
                        }

                        // Branch 6: Left
                        val b6 = Path().apply {
                            moveTo(cx, cy)
                            lineTo(cx - 44f * p, cy - 10f * p)
                            lineTo(cx - 92f * p, cy + 14f * p)
                            lineTo(cx - 148f * p, cy - 6f * p)
                            lineTo(cx - 205f * p, cy + 12f * p)
                            lineTo(cx - 265f * p, cy - 4f * p)
                        }

                        // 4. Concentric Spiderweb Fracture Arcs (Connecting lines)
                        // Inner spiderweb ring
                        val webInner = Path().apply {
                            moveTo(cx - 38f * p, cy - 32f * p)
                            lineTo(cx + 42f * p, cy - 36f * p)
                            lineTo(cx + 48f * p, cy + 12f * p)
                            lineTo(cx + 34f * p, cy + 44f * p)
                            lineTo(cx - 36f * p, cy + 42f * p)
                            lineTo(cx - 44f * p, cy - 10f * p)
                            close()
                        }
                        // Mid spiderweb ring
                        val webMid = Path().apply {
                            moveTo(cx - 65f * p, cy - 78f * p)
                            lineTo(cx + 80f * p, cy - 82f * p)
                            lineTo(cx + 95f * p, cy - 8f * p)
                            lineTo(cx + 68f * p, cy + 96f * p)
                            lineTo(cx - 72f * p, cy + 94f * p)
                            lineTo(cx - 92f * p, cy + 14f * p)
                            close()
                        }
                        // Outer spiderweb jagged bridges
                        val webOuter = Path().apply {
                            moveTo(cx - 110f * p, cy - 120f * p)
                            lineTo(cx + 130f * p, cy - 115f * p)
                            moveTo(cx + 150f * p, cy + 18f * p)
                            lineTo(cx + 112f * p, cy + 155f * p)
                            moveTo(cx - 118f * p, cy + 148f * p)
                            lineTo(cx - 148f * p, cy - 6f * p)
                        }

                        val allFissures = listOf(b1, b1Sub1, b1Sub2, b2, b2Sub1, b3, b3Sub1, b4, b4Sub1, b5, b5Sub1, b6)

                        // Draw Glow pass
                        allFissures.forEach { drawPath(it, glowColor, style = strokeGlow) }
                        drawPath(webInner, glowColor, style = strokeGlow)
                        drawPath(webMid, glowColor, style = strokeGlow)
                        drawPath(webOuter, glowColor, style = strokeGlow)

                        // Draw Core Sharp pass
                        allFissures.forEach { drawPath(it, coreColor, style = strokeCore) }
                        drawPath(webInner, webColor, style = strokeWeb)
                        drawPath(webMid, webColor, style = strokeWeb)
                        drawPath(webOuter, webColor, style = strokeWeb)
                    }
                }
            }
        }

        // Floating Hunter Security Pop-Up HUD (0.9s duration)
        AnimatedVisibility(
            visible = showHunterPopup,
            enter = fadeIn(animationSpec = tween(200)) + slideInVertically(initialOffsetY = { -40 }),
            exit = fadeOut(animationSpec = tween(250)),
            modifier = Modifier.align(Alignment.Center),
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF0D1117).copy(alpha = 0.96f))
                    .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.70f), RoundedCornerShape(14.dp))
                    .padding(horizontal = 22.dp, vertical = 16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "WE KEEP SECURITY LIKE A HUNTER",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.1.sp,
                    ),
                    color = Color(0xFF38BDF8),
                )
            }
        }
    }

    /*
    // Original ArchiveTune TopAppBar (Commented out per VoidTune design)
    TopAppBar(
        title = { Text(stringResource(R.string.login)) },
        navigationIcon = {
            AppIconButton(
                onClick = closeAction,
            ) {
                Icon(painterResource(R.drawable.arrow_back), contentDescription = null)
            }
        },
    )
    */

    BackHandler(enabled = onNavigateBack != null || webView?.canGoBack() == true) {
        if (showWebFallback && webView?.canGoBack() == true) {
            webView?.goBack()
        } else {
            closeAction()
        }
    }
}

private class YouTubeLoginWebViewClient(
    private val cookieManager: CookieManager,
    private val onCookiesCaptured: (String) -> Unit,
) : WebViewClient() {
    private var extractionRunnable: Runnable? = null
    private var lastCapturedCookie: String? = null

    override fun shouldOverrideUrlLoading(
        view: WebView,
        request: WebResourceRequest,
    ): Boolean {
        val targetUrl = request.url.toString()
        if (!targetUrl.isWebViewLoadableUrl() && (!request.isForMainFrame || !request.hasGesture())) return true
        return handleUrlLoading(view, targetUrl)
    }

    @Deprecated("Deprecated in Java")
    override fun shouldOverrideUrlLoading(
        view: WebView,
        url: String?,
    ): Boolean = handleUrlLoading(view, url)

    override fun onPageFinished(
        view: WebView,
        url: String?,
    ) {
        super.onPageFinished(view, url)
        scheduleLoginContextExtraction(view, url)
    }

    override fun doUpdateVisitedHistory(
        view: WebView,
        url: String?,
        isReload: Boolean,
    ) {
        super.doUpdateVisitedHistory(view, url, isReload)
        scheduleLoginContextExtraction(view, url)
    }

    fun release(view: WebView) {
        extractionRunnable?.let(view::removeCallbacks)
        extractionRunnable = null
        lastCapturedCookie = null
    }

    private fun handleUrlLoading(
        view: WebView,
        url: String?,
    ): Boolean {
        if (url == null) return false
        if (url.isWebViewLoadableUrl()) {
            scheduleLoginContextExtraction(view, url)
            return false
        }
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
        view.context.startActivity(intent)
        return true
    }

    private fun scheduleLoginContextExtraction(
        view: WebView,
        url: String?,
    ) {
        extractionRunnable?.let(view::removeCallbacks)
        val runnable =
            Runnable {
                captureCookies(url)
                extractLoginContext(view)
            }
        extractionRunnable = runnable
        view.post(runnable)
    }

    private fun captureCookies(url: String?) {
        val cookie =
            YOUTUBE_COOKIE_URLS
                .asSequence()
                .plus(listOfNotNull(url))
                .mapNotNull { targetUrl -> cookieManager.getCookie(targetUrl) }
                .firstOrNull { candidate -> hasCompleteYouTubeLoginCookies(candidate) }
                ?: return

        if (cookie == lastCapturedCookie) return
        lastCapturedCookie = cookie
        onCookiesCaptured(cookie)
    }

    private fun extractLoginContext(
        view: WebView,
        attempt: Int = 0,
    ) {
        view.evaluateJavascript(LOGIN_CONTEXT_SCRIPT, null)
        if (attempt < LOGIN_CONTEXT_EXTRACTION_ATTEMPTS) {
            val retryRunnable = Runnable { extractLoginContext(view, attempt + 1) }
            extractionRunnable = retryRunnable
            view.postDelayed(retryRunnable, LOGIN_CONTEXT_RETRY_DELAY_MS)
        }
    }

    private fun String.isWebViewLoadableUrl(): Boolean = startsWith("http://") || startsWith("https://")
}
