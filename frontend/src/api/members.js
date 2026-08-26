import { apiRequest } from './client';

const API_BASE = '/api/members';

const request = (path = '', options = {}) => apiRequest(`${API_BASE}${path}`, options);

export const getMe = () => request('/me');
export const login = (credentials) => request('/login', { method: 'POST', body: JSON.stringify(credentials) });
export const logout = () => request('/login', { method: 'DELETE' });
export const signup = (member) => request('', { method: 'POST', body: JSON.stringify(member) });
