import { useEffect, useMemo, useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { clockIn, clockOut, getDashboard } from '../../api/timesheetApi';
import './TimesheetPage.css';

export default function TimesheetPage() {
  const { currentUser } = useAuth();
  const [dashboard, setDashboard] = useState(null);
  const [loading, setLoading] = useState(true);
  const [actionLoading, setActionLoading] = useState(false);
  const [error, setError] = useState('');
  const [searchValue, setSearchValue] = useState('');

  const loadDashboard = async () => {
    try {
      setError('');
      const data = await getDashboard();
      setDashboard(data);
    } catch (err) {
      setError(err.message || 'Failed to load timesheet dashboard.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadDashboard();
  }, []);

  const filteredRecords = useMemo(() => {
    if (!dashboard?.records) {
      return [];
    }
    return dashboard.records.filter((record) =>
      record.employeeName.toLowerCase().includes(searchValue.toLowerCase())
    );
  }, [dashboard, searchValue]);

  const handleClockIn = async () => {
    try {
      setActionLoading(true);
      setError('');
      await clockIn();
      await loadDashboard();
    } catch (err) {
      setError(err.message || 'Clock in failed.');
    } finally {
      setActionLoading(false);
    }
  };

  const handleClockOut = async () => {
    try {
      setActionLoading(true);
      setError('');
      await clockOut();
      await loadDashboard();
    } catch (err) {
      setError(err.message || 'Clock out failed.');
    } finally {
      setActionLoading(false);
    }
  };

  const formatDateTime = (value) => {
    if (!value) {
      return '-';
    }
    return new Date(value).toLocaleString();
  };

  if (loading) {
    return <div className="container">Loading timesheet data...</div>;
  }

  if (!dashboard) {
    return <div className="container">Unable to load dashboard.</div>;
  }

  const isEmployer = dashboard.role === 'EMPLOYER';
  const isEmployee = dashboard.role === 'EMPLOYEE';

  return (
    <div className="container">
      {error && <section className="column error-banner">{error}</section>}

      {isEmployee && (
        <section className="column employee-management">
          <h2>Employee Dashboard</h2>
          <p><strong>Employee:</strong> {currentUser?.username}</p>
          <p><strong>Employer:</strong> {dashboard.employerName}</p>
          <p><strong>Accumulated Hours:</strong> {Number(dashboard.accumulatedHours || 0).toFixed(2)} hrs</p>
          <div className="button-group">
            <button type="button" onClick={handleClockIn} disabled={actionLoading || dashboard.clockedIn}>
              Clock In
            </button>
            <button type="button" onClick={handleClockOut} disabled={actionLoading || !dashboard.clockedIn}>
              Clock Out
            </button>
          </div>
          <p className="status-text">Status: {dashboard.clockedIn ? 'Currently Clocked In' : 'Currently Clocked Out'}</p>
        </section>
      )}

      {isEmployer && (
        <section className="column summary">
          <h2>Employer Dashboard</h2>
          <p><strong>Employer:</strong> {currentUser?.username}</p>
          <h3>Employee List</h3>
          <div className="summaryTable">
            {dashboard.employees.length > 0 ? (
              dashboard.employees.map((employee) => (
                <p key={employee}>{employee}</p>
              ))
            ) : (
              <p>No employees linked yet.</p>
            )}
          </div>
        </section>
      )}

      <section className="column records">
        <h2>{isEmployer ? 'Employee Clock Logs' : 'My Clock Logs'}</h2>
        {isEmployer && (
          <input
            type="text"
            value={searchValue}
            onChange={(e) => setSearchValue(e.target.value)}
            placeholder="Search Employee"
          />
        )}
        <table>
          <thead>
            <tr>
              <th>Employee</th>
              <th>Clock In</th>
              <th>Clock Out</th>
              <th>Hours Worked</th>
            </tr>
          </thead>
          <tbody>
            {filteredRecords.map((record) => (
              <tr key={record.recordId}>
                <td>{record.employeeName}</td>
                <td>{formatDateTime(record.clockInAt)}</td>
                <td>{formatDateTime(record.clockOutAt)}</td>
                <td>{record.hoursWorked == null ? '-' : `${Number(record.hoursWorked).toFixed(2)} hrs`}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </section>
    </div>
  );
}
