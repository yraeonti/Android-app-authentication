package com.auth.app.utils

import com.auth.app.data.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface APIConsumer {
    @POST("users/validate-unique-email")
    suspend fun validateEmailAddress(@Body body: ValidateEmailBody): Response<UniqueEmailValidationResponse>

    @POST("users/register")
    suspend fun registerUser(@Body body: RegisterBody): Response<AuthUserResponse>

    @POST("users/login")
    suspend fun loginUser(@Body body: LoginBody): Response<AuthUserResponse>
}