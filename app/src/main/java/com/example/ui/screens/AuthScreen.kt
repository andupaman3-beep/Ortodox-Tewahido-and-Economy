package com.example.ui.screens

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.example.R
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.ui.state.*
import com.example.ui.theme.*
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler { viewModel.navigateBack() }

    val lang by viewModel.language.collectAsState()
    val isDark by viewModel.isDarkMode.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()

    var isSignUpMode by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("Addis Ababa") }
    var profession by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(UserRole.JOB_SEEKER) }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var forgotEmail by remember { mutableStateOf("") }
    var forgotSuccessMsg by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val isAuthLoading by viewModel.isAuthLoading.collectAsState()
    val firebaseUser by viewModel.firebaseUser.collectAsState()

    fun launchGoogleSignIn() {
        errorMessage = null
        coroutineScope.launch {
            try {
                val credentialManager = CredentialManager.create(context)
                val googleIdOption = GetSignInWithGoogleOption.Builder(
                    serverClientId = context.getString(R.string.default_web_client_id)
                ).build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val result = credentialManager.getCredential(
                    context = context,
                    request = request
                )

                val credential = result.credential
                if (credential is androidx.credentials.CustomCredential &&
                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                ) {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    viewModel.handleGoogleSignInSuccess(googleIdTokenCredential.idToken) { success, err ->
                        if (success) {
                            viewModel.navigateBack()
                        } else {
                            errorMessage = err ?: "Google authentication failed"
                        }
                    }
                } else {
                    errorMessage = "Unexpected credential type received"
                }
            } catch (e: GetCredentialCancellationException) {
                Log.w("AuthScreen", "Sign-in was cancelled by user: ${e.message}")
                errorMessage = null // User voluntarily closed dialog, reset error
            } catch (e: GetCredentialException) {
                Log.e("AuthScreen", "Google Sign-In failed", e)
                errorMessage = "Google Sign-In error: ${e.localizedMessage ?: "Unable to complete sign in"}"
            } catch (e: Exception) {
                Log.e("AuthScreen", "Unexpected Sign-In error", e)
                errorMessage = e.localizedMessage ?: "Sign-In failed"
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(if (isDark) DarkBackground else LightBackground)
            .testTag("auth_screen"),
        contentPadding = PaddingValues(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Branding Header
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(BrandNavy),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(AccentGoldBright),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Stars,
                        contentDescription = null,
                        tint = BrandNavyDark,
                        modifier = Modifier.size(34.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = Strings.get("app_name", lang),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = if (isDark) DarkTextPrimary else BrandNavyDark,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = Strings.get("tagline", lang),
                fontSize = 12.sp,
                color = AccentGold,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Mode Switcher (Sign In vs Sign Up Tabs)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = if (isDark) DarkSurface else LightSurfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    Button(
                        onClick = {
                            isSignUpMode = false
                            errorMessage = null
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!isSignUpMode) RoyalBlue else Color.Transparent,
                            contentColor = if (!isSignUpMode) Color.White else LightTextSecondary
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(Strings.get("sign_in", lang), fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            isSignUpMode = true
                            errorMessage = null
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSignUpMode) RoyalBlue else Color.Transparent,
                            contentColor = if (isSignUpMode) Color.White else LightTextSecondary
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(Strings.get("sign_up", lang), fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Form Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = if (isDark) DarkSurface else LightSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) DarkBorder else LightBorder)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Sign Up Specific Fields
                    if (isSignUpMode) {
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = { Text(Strings.get("full_name", lang)) },
                            leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null, tint = RoyalBlue) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text(Strings.get("phone_number", lang)) },
                            leadingIcon = { Icon(Icons.Filled.Phone, contentDescription = null, tint = RoyalBlue) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = profession,
                            onValueChange = { profession = it },
                            label = { Text("Profession / Trade (የሙያ ዘርፍ)") },
                            leadingIcon = { Icon(Icons.Filled.Work, contentDescription = null, tint = RoyalBlue) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Role Selector
                        Text(
                            text = Strings.get("user_role", lang),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(
                                UserRole.JOB_SEEKER,
                                UserRole.EMPLOYER,
                                UserRole.SERVICE_PROVIDER,
                                UserRole.SELLER,
                                UserRole.IMPORTER,
                                UserRole.DISTRIBUTOR
                            ).chunked(2).forEach { rowRoles ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    rowRoles.forEach { role ->
                                        val isSel = selectedRole == role
                                        Surface(
                                            onClick = { selectedRole = role },
                                            shape = RoundedCornerShape(10.dp),
                                            color = if (isSel) RoyalBlueSoft else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                            border = if (isSel) androidx.compose.foundation.BorderStroke(1.5.dp, RoyalBlue) else null,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                RadioButton(
                                                    selected = isSel,
                                                    onClick = { selectedRole = role },
                                                    colors = RadioButtonDefaults.colors(selectedColor = RoyalBlue)
                                                )
                                                Text(
                                                    text = if (lang == LanguageCode.AMHARIC) role.displayNameAm else role.displayNameEn,
                                                    fontSize = 11.sp,
                                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (isSel) RoyalBlue else MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Email Field
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text(Strings.get("email_address", lang)) },
                        leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null, tint = RoyalBlue) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Password Field
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text(Strings.get("password", lang)) },
                        leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null, tint = RoyalBlue) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                    contentDescription = null
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Confirm Password (Sign Up only)
                    if (isSignUpMode) {
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = { Text("Confirm Password") },
                            leadingIcon = { Icon(Icons.Filled.LockReset, contentDescription = null, tint = RoyalBlue) },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    // Error text if any
                    errorMessage?.let { err ->
                        Surface(
                            color = StatusErrorSoft,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = err,
                                color = StatusError,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    // Forgot Password link (Sign In only)
                    if (!isSignUpMode) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = {
                                forgotEmail = email
                                showForgotPasswordDialog = true
                            }) {
                                Text(
                                    text = Strings.get("forgot_password", lang),
                                    fontSize = 12.sp,
                                    color = RoyalBlue,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    // Submit Button
                    Button(
                        onClick = {
                            if (email.isBlank() || password.isBlank()) {
                                errorMessage = "Please enter both email and password."
                                return@Button
                            }
                            if (isSignUpMode) {
                                if (fullName.isBlank()) {
                                    errorMessage = "Please enter your full name."
                                    return@Button
                                }
                                if (password != confirmPassword) {
                                    errorMessage = "Passwords do not match."
                                    return@Button
                                }
                                viewModel.registerUser(
                                    fullName = fullName,
                                    email = email,
                                    username = email.substringBefore("@"),
                                    role = selectedRole,
                                    secondaryRoles = emptyList(),
                                    profession = profession,
                                    businessName = "",
                                    location = location,
                                    phone = phone,
                                    bio = "Member of Orthodox Christian Professional Community"
                                )
                            } else {
                                viewModel.loginWithEmail(email)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("auth_submit_btn")
                    ) {
                        Text(
                            text = if (isSignUpMode) Strings.get("sign_up", lang) else Strings.get("sign_in", lang),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    // Divider
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        HorizontalDivider(modifier = Modifier.weight(1f), color = if (isDark) DarkBorder else LightBorder)
                        Text(
                            text = "ወይም በ Google / OR WITH GOOGLE",
                            fontSize = 11.sp,
                            color = LightTextSecondary,
                            fontWeight = FontWeight.SemiBold
                        )
                        HorizontalDivider(modifier = Modifier.weight(1f), color = if (isDark) DarkBorder else LightBorder)
                    }

                    // Interactive Google Sign-In Button via Credential Manager
                    OutlinedButton(
                        onClick = { launchGoogleSignIn() },
                        enabled = !isAuthLoading,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isDark) DarkSurface else Color.White,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.2.dp, AccentGoldBright),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("google_signin_button")
                    ) {
                        if (isAuthLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = RoyalBlue
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Connecting with Google...", fontSize = 14.sp)
                        } else {
                            Icon(
                                imageVector = Icons.Filled.AccountCircle,
                                contentDescription = "Google Icon",
                                tint = AccentGoldBright,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Sign in with Google (በ Google ግባ)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (firebaseUser != null) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = AccentGoldLight,
                            border = androidx.compose.foundation.BorderStroke(1.dp, AccentGoldBright),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Authenticated with Google:",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BrandNavyDark
                                    )
                                    Text(
                                        text = firebaseUser?.email ?: "",
                                        fontSize = 12.sp,
                                        color = BrandNavy
                                    )
                                }
                                TextButton(onClick = { viewModel.signOutFirebase() }) {
                                    Text("Sign Out", fontSize = 12.sp, color = StatusError, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Quick Demo Accounts Switcher
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "⚡ Quick Switch Demo Personas",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = LightTextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                allUsers.take(4).forEach { user ->
                    Surface(
                        onClick = {
                            viewModel.switchUser(user)
                            viewModel.navigateBack()
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = if (user.id == currentUser?.id) RoyalBlueSoft else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        border = if (user.id == currentUser?.id) androidx.compose.foundation.BorderStroke(1.5.dp, RoyalBlue) else null,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(AccentGoldBright),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = user.fullName.take(1),
                                    fontWeight = FontWeight.Bold,
                                    color = BrandNavyDark
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = user.fullName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "${user.primaryRole} • ${user.email}",
                                    fontSize = 11.sp,
                                    color = LightTextSecondary
                                )
                            }
                            Icon(
                                imageVector = Icons.Filled.ArrowForward,
                                contentDescription = null,
                                tint = RoyalBlue,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // Forgot Password Dialog
    if (showForgotPasswordDialog) {
        AlertDialog(
            onDismissRequest = {
                showForgotPasswordDialog = false
                forgotSuccessMsg = null
            },
            title = {
                Text(
                    text = Strings.get("forgot_password", lang),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Enter your registered email address to receive password reset instructions.",
                        fontSize = 13.sp,
                        color = LightTextSecondary
                    )
                    OutlinedTextField(
                        value = forgotEmail,
                        onValueChange = { forgotEmail = it },
                        label = { Text(Strings.get("email_address", lang)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    forgotSuccessMsg?.let { success ->
                        Surface(
                            color = EmeraldSoft,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = success,
                                color = EmeraldSuccess,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (forgotEmail.isNotBlank()) {
                            forgotSuccessMsg = "Password reset instructions sent to $forgotEmail."
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue)
                ) {
                    Text("Send Reset Link", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showForgotPasswordDialog = false
                    forgotSuccessMsg = null
                }) {
                    Text(Strings.get("cancel", lang))
                }
            }
        )
    }
}
