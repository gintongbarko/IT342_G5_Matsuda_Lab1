import { useEffect, useState } from 'react';
import { useNavigate, NavLink, Navigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { searchEmployers } from '../../api/authApi';
import { getPasswordRuleResults } from '../../utils/passwordValidation';
import './RegisterPage.css';

export default function RegisterPage() {
  const { currentUser, register } = useAuth();
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [role, setRole] = useState('EMPLOYEE');
  const [employerQuery, setEmployerQuery] = useState('');
  const [selectedEmployer, setSelectedEmployer] = useState('');
  const [employerOptions, setEmployerOptions] = useState([]);
  const [loadingEmployers, setLoadingEmployers] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const passwordRules = getPasswordRuleResults(password);
  const isPasswordValid = passwordRules.every((r) => r.passed);

  useEffect(() => {
    if (role !== 'EMPLOYEE') {
      setEmployerOptions([]);
      setEmployerQuery('');
      setSelectedEmployer('');
      return;
    }

    const normalized = employerQuery.trim();
    const timeoutId = setTimeout(async () => {
      try {
        setLoadingEmployers(true);
        const results = await searchEmployers(normalized);
        setEmployerOptions(results);
      } catch {
        setEmployerOptions([]);
      } finally {
        setLoadingEmployers(false);
      }
    }, 250);

    return () => clearTimeout(timeoutId);
  }, [employerQuery, role]);

  if (currentUser) {
    return <Navigate to="/" replace />;
  }

  const normalizeError = (msg) => {
    if (msg === 'User already exists' || msg === 'Email already exists') {
      return 'User already exists.';
    }
    return msg;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    if (password !== confirmPassword) {
      setError('Passwords do not match.');
      return;
    }

    if (!isPasswordValid) {
      setError('Password does not meet all requirements.');
      return;
    }

    if (role === 'EMPLOYEE' && !selectedEmployer) {
      setError('Please choose your employer.');
      return;
    }

    const result = await register(name.trim(), email.trim(), password, role, selectedEmployer || null);
    if (result.success) {
      navigate('/');
    } else {
      setError(normalizeError(result.error));
    }
  };

  return (
    <div className="auth-page">
      <section className="auth-card">
        <h2>Register</h2>
        <form onSubmit={handleSubmit}>
          {error && <div className="auth-error">{error}</div>}
          <label htmlFor="register-name">Username</label>
          <input
            id="register-name"
            type="text"
            value={name}
            onChange={(e) => setName(e.target.value)}
            placeholder="Enter username"
            required
          />
          <label htmlFor="register-email">Email</label>
          <input
            id="register-email"
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="Enter your email"
            required
          />
          <label htmlFor="register-password">Password</label>
          <input
            id="register-password"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="Enter your password"
            required
          />
          <ul className="password-warnings">
            {passwordRules.map((rule) => (
              <li key={rule.id} className={rule.passed ? 'passed' : ''}>
                {rule.passed ? '✓' : '○'} {rule.label}
              </li>
            ))}
          </ul>
          <label htmlFor="register-confirm">Confirm Password</label>
          <input
            id="register-confirm"
            type="password"
            value={confirmPassword}
            onChange={(e) => setConfirmPassword(e.target.value)}
            placeholder="Confirm your password"
            required
          />
          <label htmlFor="register-role">Sign up as</label>
          <select
            id="register-role"
            value={role}
            onChange={(e) => setRole(e.target.value)}
            required
          >
            <option value="EMPLOYER">Employer (Admin)</option>
            <option value="EMPLOYEE">Employee</option>
          </select>
          {role === 'EMPLOYEE' && (
            <>
              <label htmlFor="register-employer-search">Find Employer</label>
              <input
                id="register-employer-search"
                type="text"
                value={employerQuery}
                onChange={(e) => {
                  setEmployerQuery(e.target.value);
                  setSelectedEmployer('');
                }}
                placeholder="Search employer username"
                required
              />
              <label htmlFor="register-employer">Select Employer</label>
              <select
                id="register-employer"
                value={selectedEmployer}
                onChange={(e) => setSelectedEmployer(e.target.value)}
                required
              >
                <option value="" disabled>
                  {loadingEmployers ? 'Searching employers...' : 'Choose employer'}
                </option>
                {employerOptions.map((employer) => (
                  <option key={employer.userId} value={employer.username}>
                    {employer.username}
                  </option>
                ))}
              </select>
            </>
          )}
          <button type="submit">Register</button>
        </form>
        <p className="auth-footer">
          Already have an account? <NavLink to="/login">Login</NavLink>
        </p>
      </section>
    </div>
  );
}
