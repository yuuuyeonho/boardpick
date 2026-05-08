const API_BASE = '/api/games';

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

export function getGames(filters = {}) {
  return request(`${toQuery(filters)}`);
}

export function getCollections() {
  return request('/collections');
}

export function getCollection(slug) {
  return request(`/collections/${slug}`);
}

export function getCollectionGames(slug, filters = {}) {
  return request(`/collections/${slug}/games${toQuery(filters)}`);
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

export function pickCollectionGame(slug, filters = {}) {
  return request(`/collections/${slug}/pick${toQuery(filters)}`);
}

export function addMyCollectionGame(gameId) {
  return request(`/collections/me/games/${gameId}`, {
    method: 'POST'
  });
}

export function removeMyCollectionGame(gameId) {
  return request(`/collections/me/games/${gameId}`, {
    method: 'DELETE'
  });
}
