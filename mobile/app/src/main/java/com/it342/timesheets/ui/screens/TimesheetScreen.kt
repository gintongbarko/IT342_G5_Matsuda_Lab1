package com.it342.timesheets.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.it342.timesheets.data.UserResponse
import com.it342.timesheets.timesheet.TimesheetViewModel
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Composable
fun TimesheetScreen(
    currentUser: UserResponse,
    timesheetViewModel: TimesheetViewModel
) {
    val state = timesheetViewModel.state.collectAsState().value
    val scrollState = rememberScrollState()

    if (state.loading) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val dashboard = state.dashboard
    if (dashboard == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = state.error ?: "Unable to load dashboard")
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = { timesheetViewModel.refreshDashboard() }) {
                Text("Retry")
            }
        }
        return
    }

    val isEmployer = dashboard.role == "EMPLOYER"
    val isEmployee = dashboard.role == "EMPLOYEE"
    val filteredRecords = remember(dashboard.records, state.searchValue) {
        dashboard.records.filter { it.employeeName.contains(state.searchValue, ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        state.error?.let {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(12.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (isEmployee) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Employee Dashboard", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Employee: ${currentUser.username}")
                    Text("Employer: ${dashboard.employerName.orEmpty()}")
                    Text("Accumulated Hours: ${"%.2f".format(dashboard.accumulatedHours ?: 0.0)} hrs")
                    Text("Status: ${if (dashboard.clockedIn) "Currently Clocked In" else "Currently Clocked Out"}")
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = { timesheetViewModel.clockIn() },
                            enabled = !state.actionLoading && !dashboard.clockedIn,
                            modifier = Modifier.weight(1f)
                        ) { Text("Clock In") }
                        Spacer(modifier = Modifier.height(0.dp).weight(0.05f))
                        Button(
                            onClick = { timesheetViewModel.clockOut() },
                            enabled = !state.actionLoading && dashboard.clockedIn,
                            modifier = Modifier.weight(1f)
                        ) { Text("Clock Out") }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (isEmployer) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Employer Dashboard", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Employer: ${currentUser.username}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Employee List", style = MaterialTheme.typography.titleSmall)
                    if (dashboard.employees.isEmpty()) {
                        Text("No employees linked yet.")
                    } else {
                        dashboard.employees.forEach { employee ->
                            Text(employee)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    if (isEmployer) "Employee Clock Logs" else "My Clock Logs",
                    style = MaterialTheme.typography.titleMedium
                )
                if (isEmployer) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = state.searchValue,
                        onValueChange = { timesheetViewModel.onSearchChange(it) },
                        placeholder = { Text("Search Employee") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Employee", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelSmall)
                    Text("Hours", style = MaterialTheme.typography.labelSmall)
                }
                Spacer(modifier = Modifier.height(6.dp))

                if (filteredRecords.isEmpty()) {
                    Text("No records", style = MaterialTheme.typography.bodySmall)
                } else {
                    LazyColumn(modifier = Modifier.height(260.dp)) {
                        items(filteredRecords, key = { it.recordId }) { record ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(record.employeeName, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
                                Text(
                                    record.hoursWorked?.let { "%.2f hrs".format(it) } ?: "-",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Text(
                                "In: ${formatDateTime(record.clockInAt)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                "Out: ${formatDateTime(record.clockOutAt)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

private fun formatDateTime(value: String?): String {
    if (value.isNullOrBlank()) return "-"
    return try {
        val parsed = OffsetDateTime.parse(value)
        parsed.format(DateTimeFormatter.ofPattern("M/d/yyyy h:mm:ss a"))
    } catch (_: Exception) {
        value
    }
}
