package com.it342.timesheets.timesheet

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.it342.timesheets.data.TimesheetDashboardResponse
import com.it342.timesheets.data.TokenStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TimesheetUiState(
    val loading: Boolean = true,
    val dashboard: TimesheetDashboardResponse? = null,
    val error: String? = null,
    val actionLoading: Boolean = false,
    val searchValue: String = ""
)

class TimesheetViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TimesheetRepository(TokenStore(application))

    private val _state = MutableStateFlow(TimesheetUiState())
    val state: StateFlow<TimesheetUiState> = _state.asStateFlow()

    init {
        refreshDashboard()
    }

    fun refreshDashboard() {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)
            val result = repository.getDashboard()
            _state.value = if (result.isSuccess) {
                _state.value.copy(loading = false, dashboard = result.getOrNull(), error = null)
            } else {
                _state.value.copy(loading = false, error = result.exceptionOrNull()?.message ?: "Failed to load timesheet dashboard.")
            }
        }
    }

    fun onSearchChange(value: String) {
        _state.value = _state.value.copy(searchValue = value)
    }

    fun clockIn() {
        viewModelScope.launch {
            _state.value = _state.value.copy(actionLoading = true, error = null)
            val result = repository.clockIn()
            if (result.isFailure) {
                _state.value = _state.value.copy(actionLoading = false, error = result.exceptionOrNull()?.message ?: "Clock in failed")
                return@launch
            }
            _state.value = _state.value.copy(actionLoading = false)
            refreshDashboard()
        }
    }

    fun clockOut() {
        viewModelScope.launch {
            _state.value = _state.value.copy(actionLoading = true, error = null)
            val result = repository.clockOut()
            if (result.isFailure) {
                _state.value = _state.value.copy(actionLoading = false, error = result.exceptionOrNull()?.message ?: "Clock out failed")
                return@launch
            }
            _state.value = _state.value.copy(actionLoading = false)
            refreshDashboard()
        }
    }
}
