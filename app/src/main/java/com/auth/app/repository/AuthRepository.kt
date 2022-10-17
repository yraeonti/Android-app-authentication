package com.auth.app.repository

import android.util.Log
import com.auth.app.data.LoginBody
import com.auth.app.data.RegisterBody
import com.auth.app.data.ValidateEmailBody
import com.auth.app.utils.APIConsumer
import com.auth.app.utils.RequestStatus
import com.auth.app.utils.SimplifiedMessage
import kotlinx.coroutines.flow.flow


class AuthRepository(private val consumer: APIConsumer) {
    fun validateEmailAddress(body: ValidateEmailBody) = flow {
        emit(RequestStatus.Waiting)
        val response = consumer.validateEmailAddress(body)
        Log.d("pint", response.toString())
        if (response.isSuccessful) {
            emit((RequestStatus.Success(response.body()!!)))
        } else {
            emit(
                RequestStatus.Error(
                    SimplifiedMessage.get(
                        response.errorBody()!!.byteStream().reader().readText()
                    )
                )
            )
        }

    }

    fun registerUser(body: RegisterBody) = flow {
        emit(RequestStatus.Waiting)
        val response = consumer.registerUser(body)
        Log.d("pint", response.toString())
        if (response.isSuccessful) {
            emit((RequestStatus.Success(response.body()!!)))
        } else {
            emit(
                RequestStatus.Error(
                    SimplifiedMessage.get(
                        response.errorBody()!!.byteStream().reader().readText()
                    )
                )
            )
        }
    }

    fun loginUser(body: LoginBody) = flow{
        emit(RequestStatus.Waiting)
        val response = consumer.loginUser(body)
        Log.d("pint", response.toString())
        if (response.isSuccessful) {
            emit((RequestStatus.Success(response.body()!!)))
        } else {
            emit(
                RequestStatus.Error(
                    SimplifiedMessage.get(
                        response.errorBody()!!.byteStream().reader().readText()
                    )
                )
            )
        }
    }
}