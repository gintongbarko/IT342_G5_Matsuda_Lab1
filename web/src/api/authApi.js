import { api } from './client';

/**
 * Consolidated auth API calls per docs.
 * POST /api/auth/register
 * POST /api/auth/login
 * POST /api/auth/logout
 * GET /api/user/me (protected)
 */

export async function register(username, email, password, role, employerUsername = null) {
  const data = await api.post('/api/auth/register', { username, email, password, role, employerUsername }, false);
  return data;
}

export async function searchEmployers(query) {
  const value = encodeURIComponent(query || '');
  const data = await api.get(`/api/auth/employers/search?q=${value}`, false);
  return data;
}

export async function login(username, password) {
  const data = await api.post('/api/auth/login', { username, password }, false);
  return data;
}

export async function logout() {
  await api.post('/api/auth/logout', null, true);
}

export async function getMe() {
  const data = await api.get('/api/user/me', true);
  return data;
}
