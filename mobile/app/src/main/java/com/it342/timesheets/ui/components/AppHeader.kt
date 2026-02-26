package com.it342.timesheets.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.it342.timesheets.data.UserResponse

@Composable
fun AppHeader(
    currentUser: UserResponse?,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onConfirmLogout: () -> Unit
) {
    var showLogoutModal by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Employee Timesheet Tracker",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f)
        )
        if (currentUser != null) {
            Text(
                text = "Hello, ${currentUser.username}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(end = 8.dp)
            )
            TextButton(onClick = { showLogoutModal = true }) {
                Text("Logout")
            }
        } else {
            TextButton(onClick = onLoginClick) {
                Text("Login")
            }
            TextButton(onClick = onRegisterClick) {
                Text("Register")
            }
        }
    }

    if (showLogoutModal && currentUser != null) {
        LogoutModal(
            user = currentUser,
            onKeepSignedIn = { showLogoutModal = false },
            onConfirmSignOut = {
                onConfirmLogout()
                showLogoutModal = false
            }
        )
    }
}
