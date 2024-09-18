package com.example.todoapp.settings.auth.domain

import com.example.todoapp.settings.auth.data.AuthRepository
import javax.inject.Inject

class SignInWithGoogleUseCase @Inject constructor(private val authRepository: AuthRepository) {
    operator fun invoke(idToken: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        authRepository.signInWithGoogle(idToken, onSuccess, onFailure)
    }
}