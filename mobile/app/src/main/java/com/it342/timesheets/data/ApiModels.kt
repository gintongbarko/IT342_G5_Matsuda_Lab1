package com.it342.timesheets.data

import com.google.gson.annotations.SerializedName

/** Matches backend UserResponse */
data class UserResponse(
    @SerializedName("userId") val userId: Int,
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("role") val role: String? = null,
    @SerializedName("employerName") val employerName: String? = null
)

/** Matches backend AuthResponse */
data class AuthResponse(
    @SerializedName("token") val token: String,
    @SerializedName("user") val user: UserResponse
)

/** Matches backend LoginRequest */
data class LoginRequest(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String
)

/** Matches backend RegisterRequest */
data class RegisterRequest(
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("role") val role: String,
    @SerializedName("employerUsername") val employerUsername: String? = null
)

data class TimesheetRecordResponse(
    @SerializedName("recordId") val recordId: Int,
    @SerializedName("employeeName") val employeeName: String,
    @SerializedName("employerName") val employerName: String,
    @SerializedName("clockInAt") val clockInAt: String,
    @SerializedName("clockOutAt") val clockOutAt: String?,
    @SerializedName("hoursWorked") val hoursWorked: Double?
)

data class TimesheetDashboardResponse(
    @SerializedName("role") val role: String,
    @SerializedName("employerName") val employerName: String?,
    @SerializedName("accumulatedHours") val accumulatedHours: Double?,
    @SerializedName("clockedIn") val clockedIn: Boolean,
    @SerializedName("employees") val employees: List<String>,
    @SerializedName("records") val records: List<TimesheetRecordResponse>
)

/** Backend error body: { "error": "message" } */
data class ErrorResponse(
    @SerializedName("error") val error: String? = null,
    @SerializedName("message") val message: String? = null
)
