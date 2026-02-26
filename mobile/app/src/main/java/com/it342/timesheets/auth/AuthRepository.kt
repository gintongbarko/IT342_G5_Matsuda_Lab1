package com.it342.timesheets.auth

import com.it342.timesheets.data.ApiClient
import com.it342.timesheets.data.AuthResponse
import com.it342.timesheets.data.LoginRequest
import com.it342.timesheets.data.RegisterRequest
import com.it342.timesheets.data.TokenStore
import com.it342.timesheets.data.UserResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

sealed class AuthResult {
    data class Success(val user: UserResponse) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

class AuthRepository(private val tokenStore: TokenStore) {

    fun getStoredToken(): String? = tokenStore.getToken()
    fun getStoredUser(): UserResponse? = tokenStore.getStoredUser()

    suspend fun fetchCurrentUser(): UserResponse? = withContext(Dispatchers.IO) {
        val token = tokenStore.getToken() ?: return@withContext null
        ApiClient.setToken(token)
        try {
            val response = ApiClient.api.getMe("Bearer $token")
            if (response.isSuccessful) {
                response.body()?.also { tokenStore.saveAuth(token, it) }
            } else {
                tokenStore.clear()
                ApiClient.setToken(null)
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    suspend fun register(
        username: String,
        email: String,
        password: String,
        role: String,
        employerUsername: String?
    ): AuthResult =
        withContext(Dispatchers.IO) {
            ApiClient.setToken(null)
            try {
                val response = ApiClient.api.register(
                    RegisterRequest(
                        username = username,
                        email = email,
                        password = password,
                        role = role,
                        employerUsername = employerUsername
                    )
                )
                if (response.isSuccessful) {
                    val body = response.body()!!
                    tokenStore.saveAuth(body.token, body.user)
                    ApiClient.setToken(body.token)
                    AuthResult.Success(body.user)
                } else {
                    AuthResult.Error(parseError(response.code(), response.errorBody()?.string()))
                }
            } catch (e: HttpException) {
                AuthResult.Error(parseError(e.code(), e.response()?.errorBody()?.string()))
            } catch (e: IOException) {
                AuthResult.Error("Registration failed.")
            } catch (e: Exception) {
                AuthResult.Error(e.message ?: "Registration failed.")
            }
        }

    suspend fun searchEmployers(query: String): List<UserResponse> =
        withContext(Dispatchers.IO) {
            try {
                val response = ApiClient.api.searchEmployers(query)
                if (response.isSuccessful) {
                    response.body().orEmpty()
                } else {
                    emptyList()
                }
            } catch (_: Exception) {
                emptyList()
            }
        }

    suspend fun login(username: String, password: String): AuthResult =
        withContext(Dispatchers.IO) {
            ApiClient.setToken(null)
            try {
                val response = ApiClient.api.login(LoginRequest(username, password))
                if (response.isSuccessful) {
                    val body = response.body()!!
                    tokenStore.saveAuth(body.token, body.user)
                    ApiClient.setToken(body.token)
                    AuthResult.Success(body.user)
                } else {
                    AuthResult.Error(parseError(response.code(), response.errorBody()?.string()))
                }
            } catch (e: HttpException) {
                AuthResult.Error(parseError(e.code(), e.response()?.errorBody()?.string()))
            } catch (e: IOException) {
                AuthResult.Error("Login failed.")
            } catch (e: Exception) {
                AuthResult.Error(e.message ?: "Login failed.")
            }
        }

    suspend fun logout() = withContext(Dispatchers.IO) {
        val token = tokenStore.getToken()
        if (token != null) {
            try {
                ApiClient.api.logout("Bearer $token")
            } catch (_: Exception) { }
        }
        tokenStore.clear()
        ApiClient.setToken(null)
    }

    private fun parseError(code: Int, body: String?): String {
        if (!body.isNullOrBlank()) {
            val error = try {
                com.google.gson.Gson().fromJson(body, com.it342.timesheets.data.ErrorResponse::class.java)
            } catch (_: Exception) { null }
            (error?.error ?: error?.message)?.let { return it }
        }
        return "Request failed with $code"
    }
}
