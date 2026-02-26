package com.it342.timesheets.timesheet

import com.it342.timesheets.data.ApiClient
import com.it342.timesheets.data.TimesheetDashboardResponse
import com.it342.timesheets.data.TokenStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TimesheetRepository(private val tokenStore: TokenStore) {

    private fun bearerToken(): String? = tokenStore.getToken()?.let { "Bearer $it" }

    suspend fun getDashboard(): Result<TimesheetDashboardResponse> = withContext(Dispatchers.IO) {
        val auth = bearerToken() ?: return@withContext Result.failure(IllegalStateException("Missing session token"))
        try {
            val response = ApiClient.api.getTimesheetDashboard(auth)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(IllegalStateException("Failed to load dashboard"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun clockIn(): Result<Unit> = withContext(Dispatchers.IO) {
        val auth = bearerToken() ?: return@withContext Result.failure(IllegalStateException("Missing session token"))
        try {
            val response = ApiClient.api.clockIn(auth)
            if (response.isSuccessful) Result.success(Unit) else Result.failure(IllegalStateException("Clock in failed"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun clockOut(): Result<Unit> = withContext(Dispatchers.IO) {
        val auth = bearerToken() ?: return@withContext Result.failure(IllegalStateException("Missing session token"))
        try {
            val response = ApiClient.api.clockOut(auth)
            if (response.isSuccessful) Result.success(Unit) else Result.failure(IllegalStateException("Clock out failed"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
