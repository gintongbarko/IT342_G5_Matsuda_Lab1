// Select Elements
const employeeSelect = document.getElementById("employee");
const clockInBtn = document.getElementById("clockIn");
const clockOutBtn = document.getElementById("clockOut");
const timesheetTable = document.getElementById("timesheetTable");
const summaryTable = document.getElementById("summaryTable");
const employeeForm = document.getElementById("employeeForm");
const newEmployeeInput = document.getElementById("newEmployee");
const searchInput = document.getElementById("searchEmployee");

// Data Store
let employees = [];
let records = [];
let activeRecords = {};
let summary = {};

// Add Employee
employeeForm.addEventListener("submit", (e) => {
	e.preventDefault();
	const newEmployee = newEmployeeInput.value.trim();

	if (!newEmployee) {
		alert("Employee name cannot be empty!");
		return;
	}

	if (employees.includes(newEmployee)) {
		alert("Employee already exists!");
		return;
	}

	employees.push(newEmployee);
	updateEmployeeDropdown();
	employeeForm.reset();
});

// Update Employee Dropdown
function updateEmployeeDropdown() {
	employeeSelect.innerHTML =
		'<option value="" disabled selected>Select Employee</option>';
	employees.forEach((employee) => {
		const option = document.createElement("option");
		option.value = employee;
		option.textContent = employee;
		employeeSelect.appendChild(option);
	});
}

let selectedEmployee = null;

employeeSelect.addEventListener("change", () => {
	selectedEmployee = employeeSelect.value;

	// If the selected employee has clocked in, enable clock-out
	if (activeRecords[selectedEmployee]) {
		clockOutBtn.disabled = false;
	} else {
		clockOutBtn.disabled = true;
	}
});

searchInput.addEventListener("input", () => {
	const searchValue = searchInput.value.toLowerCase();
	const filteredRecords = records.filter((record) =>
		record.employee.toLowerCase().includes(searchValue)
	);

	timesheetTable.innerHTML = "";
	filteredRecords.forEach((record) => {
		const row = document.createElement("tr");
		row.innerHTML = `
            <td>${record.employee}</td>
            <td>${record.clockIn}</td>
            <td>${record.clockOut}</td>
            <td>${record.hoursWorked} hrs</td>
        `;
		timesheetTable.appendChild(row);
	});
});

// Clock In
clockInBtn.addEventListener("click", () => {
	const employee = employeeSelect.value;
	if (!employee) {
		alert("Please select an employee!");
		return;
	}

	if (activeRecords[employee]) {
		alert(`${employee} has already clocked in!`);
		return;
	}

	const clockInTime = new Date();
	activeRecords[employee] = { employee, clockIn: clockInTime };

	// Enable clock-out button
	clockOutBtn.disabled = false;
});

// Clock Out
clockOutBtn.addEventListener("click", () => {
	const employee = employeeSelect.value;
	if (!employee || !activeRecords[employee]) {
		alert("Please clock in first!");
		return;
	}

	const clockOutTime = new Date();
	const clockInTime = activeRecords[employee].clockIn;
	const hoursWorked = ((clockOutTime - clockInTime) / (1000 * 60 * 60)).toFixed(
		2
	);

	// Must be in format "MM/DD/YYYY HH:MM:SS AM/PM"
	const formattedClockInTime = `${
		clockInTime.getMonth() + 1
	}/${clockInTime.getDate()}/${clockInTime.getFullYear()} ${clockInTime.toLocaleTimeString()}`;

	const formattedClockOutTime = `${
		clockOutTime.getMonth() + 1
	}/${clockOutTime.getDate()}/${clockOutTime.getFullYear()} ${clockOutTime.toLocaleTimeString()}`;

	// Add to records
	records.push({
		employee,
		clockIn: formattedClockInTime,
		clockOut: formattedClockOutTime,
		hoursWorked,
	});

	// Update summary per employee
	if (!summary[employee]) {
		summary[employee] = 0;
	}
	summary[employee] += parseFloat(hoursWorked);

	delete activeRecords[employee];

	// Disable clock-out button
	clockOutBtn.disabled = true;

	updateTimesheet();
	updateSummary();
});

// Update Timesheet
function updateTimesheet() {
	timesheetTable.innerHTML = "";
	records.forEach((record) => {
		const row = document.createElement("tr");
		row.innerHTML = `
            <td>${record.employee}</td>
            <td>${record.clockIn}</td>
            <td>${record.clockOut}</td>
            <td>${record.hoursWorked} hrs</td>
        `;
		timesheetTable.appendChild(row);
	});
}

// Update Summary
function updateSummary() {
	summaryTable.innerHTML = "";
	Object.keys(summary).forEach((employee) => {
		const summaryItem = document.createElement("p");
		summaryItem.innerHTML = `<strong>${employee}:</strong> ${summary[
			employee
		].toFixed(2)} hrs`;
		summaryTable.appendChild(summaryItem);
	});
}

// Initialize Employee List
updateEmployeeDropdown();
