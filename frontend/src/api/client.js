export class ApiError extends Error {
  constructor(message, status, payload) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
    this.payload = payload;
  }
}

export async function apiRequest(path, options = {}) {
  const response = await fetch(path, {
    headers: {
      'Content-Type': 'application/json',
      ...options.headers
    },
    credentials: 'include',
    ...options
  });

  if (response.status === 204) {
    return null;
  }

  const contentType = response.headers.get('content-type') || '';
  const payload = contentType.includes('application/json')
    ? await response.json()
    : await response.text();

  if (!response.ok) {
    const message =
      (typeof payload === 'object' && (payload.detail || payload.message || payload.error)) ||
      (typeof payload === 'string' && payload) ||
      statusMessage(response.status);
    throw new ApiError(message, response.status, payload);
  }

  return payload;
}

function statusMessage(status) {
  if (status === 400) return '입력값을 확인해주세요.';
  if (status === 401) return '로그인이 필요합니다.';
  if (status === 403) return '요청할 권한이 없습니다.';
  if (status === 404) return '요청한 대상을 찾을 수 없습니다.';
  if (status === 409) return '이미 존재하는 값입니다.';
  return `요청에 실패했습니다. (${status})`;
}
