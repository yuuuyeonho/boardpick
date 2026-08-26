import { apiRequest } from './client';

const API_BASE = '/api/games';
const LIST_API_BASE = '/api/lists';

function toQuery(params = {}) {
  const query = new URLSearchParams();
  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== '') {
      query.set(key, value);
    }
  });

  const value = query.toString();
  return value ? `?${value}` : '';
}

const request = (path = '', options = {}) => apiRequest(`${API_BASE}${path}`, options);
const listRequest = (path = '', options = {}) => apiRequest(`${LIST_API_BASE}${path}`, options);

export function getGames(filters = {}) {
  return request(`${toQuery(filters)}`);
}

export function getLists() {
  return listRequest('');
}

export function getList(listId) {
  return listRequest(`/${listId}`);
}

export function getListGames(listId, filters = {}) {
  return listRequest(`/${listId}/games${toQuery(filters)}`);
}

export function createGame(game) {
  return request('', {
    method: 'POST',
    body: JSON.stringify(game)
  });
}

export function updateGame(id, game) {
  return request(`/${id}`, {
    method: 'PUT',
    body: JSON.stringify(game)
  });
}

export function deleteGame(id) {
  return request(`/${id}`, {
    method: 'DELETE'
  });
}

export function pickGame(filters = {}) {
  return request(`/pick${toQuery(filters)}`);
}

export function pickListGame(listId, filters = {}) {
  return listRequest(`/${listId}/pick${toQuery(filters)}`);
}

export function addMyListGame(gameId) {
  return listRequest(`/me/games/${gameId}`, {
    method: 'POST'
  });
}

export function removeMyListGame(gameId) {
  return listRequest(`/me/games/${gameId}`, {
    method: 'DELETE'
  });
}
