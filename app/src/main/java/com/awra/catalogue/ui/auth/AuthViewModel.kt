package com.awra.catalogue.ui.auth

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.awra.catalogue.data.util.PreferenceManager
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed interface AuthState {
    object Idle : AuthState
    object Loading : AuthState
    object Success : AuthState
    data class Error(val message: String) : AuthState
}

class AuthViewModel(private val preferenceManager: PreferenceManager) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _isSignUpMode = MutableStateFlow(false)
    val isSignUpMode: StateFlow<Boolean> = _isSignUpMode.asStateFlow()

    fun toggleMode() {
        _isSignUpMode.value = !_isSignUpMode.value
        _authState.value = AuthState.Idle
    }

    fun clearError() {
        if (_authState.value is AuthState.Error) {
            _authState.value = AuthState.Idle
        }
    }

    fun signInWithEmail(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Email and Password cannot be empty.")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                val user = result.user
                if (user != null) {
                    val currentName = preferenceManager.userName.value
                    if (currentName == "User" || currentName.isBlank()) {
                        val nameFromEmail = user.displayName ?: email.substringBefore("@")
                        preferenceManager.setUserName(nameFromEmail)
                    }
                    _authState.value = AuthState.Success
                } else {
                    _authState.value = AuthState.Error("Sign in failed. Unknown error.")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.localizedMessage ?: "Authentication failed.")
            }
        }
    }

    fun signUpWithEmail(email: String, password: String, name: String) {
        if (email.isBlank() || password.isBlank() || name.isBlank()) {
            _authState.value = AuthState.Error("All fields are required.")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val user = result.user
                if (user != null) {
                    preferenceManager.setUserName(name)
                    _authState.value = AuthState.Success
                } else {
                    _authState.value = AuthState.Error("Sign up failed. Unknown error.")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.localizedMessage ?: "Registration failed.")
            }
        }
    }

    fun signInWithGoogle(context: Context) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val credentialManager = CredentialManager.create(context)
                
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId("428129794918-rk5fona5009c1f8agl7jeuutlr1kkp3m.apps.googleusercontent.com")
                    .setAutoSelectEnabled(false)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val result = credentialManager.getCredential(context = context, request = request)
                val credential = result.credential

                if (credential is GoogleIdTokenCredential) {
                    val googleIdToken = credential.idToken
                    val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                    val authResult = auth.signInWithCredential(firebaseCredential).await()
                    val user = authResult.user
                    if (user != null) {
                        val name = user.displayName ?: credential.displayName ?: user.email?.substringBefore("@") ?: "User"
                        preferenceManager.setUserName(name)
                        _authState.value = AuthState.Success
                    } else {
                        _authState.value = AuthState.Error("Google Sign-In failed.")
                    }
                } else {
                    _authState.value = AuthState.Error("Received unexpected credential type.")
                }
            } catch (e: GetCredentialException) {
                _authState.value = AuthState.Error("Google Sign-In canceled or failed: ${e.message}")
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.localizedMessage ?: "Google Authentication failed.")
            }
        }
    }

    fun signOut(context: Context) {
        auth.signOut()
        viewModelScope.launch {
            try {
                val credentialManager = CredentialManager.create(context)
                credentialManager.clearCredentialState(ClearCredentialStateRequest())
            } catch (e: Exception) {
                // Ignore clear errors
            }
        }
        _authState.value = AuthState.Idle
    }
}
