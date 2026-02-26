package com.it342.timesheets.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.it342.timesheets.data.UserResponse
import com.it342.timesheets.ui.theme.ErrorText

data class PasswordRule(val id: String, val label: String, val test: (String) -> Boolean)

private val PASSWORD_RULES = listOf(
    PasswordRule("length", "At least 8 characters long") { it.length >= 8 },
    PasswordRule("lowercase", "Mix of uppercase and lowercase characters") {
        it.any { c -> c.isLowerCase() } && it.any { c -> c.isUpperCase() }
    },
    PasswordRule("number", "Contains numbers") { it.any { c -> c.isDigit() } },
    PasswordRule("special", "Contains special characters (e.g. !@#\$%)") {
        it.any { c -> !c.isLetterOrDigit() }
    }
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    error: String?,
    employerOptions: List<UserResponse>,
    loadingEmployers: Boolean,
    onSearchEmployers: (query: String) -> Unit,
    onRegister: (username: String, email: String, password: String, role: String, employerUsername: String?) -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var username by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var role by rememberSaveable { mutableStateOf("EMPLOYEE") }
    var roleMenuExpanded by rememberSaveable { mutableStateOf(false) }
    var employerQuery by rememberSaveable { mutableStateOf("") }
    var selectedEmployer by rememberSaveable { mutableStateOf("") }
    var employerMenuExpanded by rememberSaveable { mutableStateOf(false) }
    var localError by rememberSaveable { mutableStateOf<String?>(null) }
    val displayError = error ?: localError
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Register",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(24.dp))

        if (displayError != null) {
            Text(
                text = displayError,
                color = ErrorText,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
        }

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            placeholder = { Text("Enter username") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            placeholder = { Text("Enter your email") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            placeholder = { Text("Enter your password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Column(modifier = Modifier.fillMaxWidth()) {
            PASSWORD_RULES.forEach { rule ->
                val passed = rule.test(password)
                Text(
                    text = "${if (passed) "✓" else "○"} ${rule.label}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (passed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            placeholder = { Text("Confirm your password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        ExposedDropdownMenuBox(
            expanded = roleMenuExpanded,
            onExpandedChange = { roleMenuExpanded = it }
        ) {
            OutlinedTextField(
                value = if (role == "EMPLOYER") "Employer (Admin)" else "Employee",
                onValueChange = {},
                label = { Text("Sign up as") },
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roleMenuExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            DropdownMenu(
                expanded = roleMenuExpanded,
                onDismissRequest = { roleMenuExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Employer (Admin)") },
                    onClick = {
                        role = "EMPLOYER"
                        selectedEmployer = ""
                        employerQuery = ""
                        roleMenuExpanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Employee") },
                    onClick = {
                        role = "EMPLOYEE"
                        roleMenuExpanded = false
                    }
                )
            }
        }

        if (role == "EMPLOYEE") {
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = employerQuery,
                onValueChange = {
                    employerQuery = it
                    selectedEmployer = ""
                    onSearchEmployers(it.trim())
                },
                label = { Text("Find Employer") },
                placeholder = { Text("Search employer username") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            ExposedDropdownMenuBox(
                expanded = employerMenuExpanded,
                onExpandedChange = { employerMenuExpanded = it }
            ) {
                OutlinedTextField(
                    value = selectedEmployer,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Select Employer") },
                    placeholder = { Text(if (loadingEmployers) "Searching employers..." else "Choose employer") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = employerMenuExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                DropdownMenu(
                    expanded = employerMenuExpanded,
                    onDismissRequest = { employerMenuExpanded = false }
                ) {
                    employerOptions.forEach { employer ->
                        DropdownMenuItem(
                            text = { Text(employer.username) },
                            onClick = {
                                selectedEmployer = employer.username
                                employerMenuExpanded = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                focusManager.clearFocus()
                localError = null
                if (password != confirmPassword) {
                    localError = "Passwords do not match."
                    return@Button
                }
                val passwordValid = PASSWORD_RULES.all { it.test(password) }
                if (!passwordValid) {
                    localError = "Password does not meet all requirements."
                    return@Button
                }
                if (role == "EMPLOYEE" && selectedEmployer.isBlank()) {
                    localError = "Please choose your employer."
                    return@Button
                }
                onRegister(
                    username.trim(),
                    email.trim(),
                    password,
                    role,
                    if (role == "EMPLOYEE") selectedEmployer else null
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Already have an account? ",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Button(
            onClick = onNavigateToLogin,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }
    }
}
