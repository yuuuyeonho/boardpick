const API_BASE = '/api/games';

async function request(path = '', options = {}) {
  const response = await fetch(`${API_BASE}${path}`, {
    headers: {
      'Content-Type': 'application/json',
      ...options.headers
    },
    ...options
  });

  if (!response.ok) {
    const message = await response.text();
    throw new Error(message || `요청에 실패했습니다. (${response.status})`);
  }

  if (response.status === 204) {
    return null;
  }

  return response.json();
}

export function getGames() {
  return request();
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

export function pickGame() {
  return request('/pick');
}
