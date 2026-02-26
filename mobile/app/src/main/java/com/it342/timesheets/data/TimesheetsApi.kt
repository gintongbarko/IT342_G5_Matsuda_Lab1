package com.it342.timesheets.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface TimesheetsApi {

    @POST("api/auth/register")
    suspend fun register(@Body body: RegisterRequest): Response<AuthResponse>

    @POST("api/auth/login")
    suspend fun login(@Body body: LoginRequest): Response<AuthResponse>

    @POST("api/auth/logout")
    suspend fun logout(@Header("Authorization") authorization: String): Response<Unit>

    @GET("api/user/me")
    suspend fun getMe(@Header("Authorization") authorization: String): Response<UserResponse>

    @GET("api/auth/employers/search")
    suspend fun searchEmployers(@Query("q") query: String): Response<List<UserResponse>>

    @GET("api/timesheets/dashboard")
    suspend fun getTimesheetDashboard(@Header("Authorization") authorization: String): Response<TimesheetDashboardResponse>

    @POST("api/timesheets/clock-in")
    suspend fun clockIn(@Header("Authorization") authorization: String): Response<Unit>

    @POST("api/timesheets/clock-out")
    suspend fun clockOut(@Header("Authorization") authorization: String): Response<Unit>
}
