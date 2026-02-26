import { api } from './client';

export async function getDashboard() {
  return api.get('/api/timesheets/dashboard', true);
}

export async function clockIn() {
  return api.post('/api/timesheets/clock-in', null, true);
}

export async function clockOut() {
  return api.post('/api/timesheets/clock-out', null, true);
}
