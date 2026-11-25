/* URL configurável para o cliente web, Android e desktop. */
const API_OVERRIDE = window.API_BASE_URL || new URLSearchParams(location.search).get('api');
const localHost = ['localhost','127.0.0.1'].includes(location.hostname);
const API_BASE = API_OVERRIDE || (localHost
  ? 'http://localhost:8081/api'
  : new URL('/api', document.baseURI).toString());
const API_TOKEN_KEY = 'fastFantasy.apiToken';

function apiUrl(path, query = {}) {
  const url = new URL(API_BASE, document.baseURI);
  url.searchParams.set('path', path);
  Object.entries(query).forEach(([key,value])=>url.searchParams.set(key,value));
  return url.toString();
}

function apiGetToken() {
  return localStorage.getItem(API_TOKEN_KEY);
}

function apiSetToken(token) {
  if (token) localStorage.setItem(API_TOKEN_KEY, token);
  else localStorage.removeItem(API_TOKEN_KEY);
}

async function apiRequest(path, options = {}) {
  const headers = { Accept: 'application/json' };
  const token = options.token ?? apiGetToken();
  if (token) headers.Authorization = `Bearer ${token}`;

  let body;
  if (options.json !== undefined) {
    headers['Content-Type'] = 'application/json;charset=UTF-8';
    body = JSON.stringify(options.json);
  } else if (options.body) {
    headers['Content-Type'] = 'application/x-www-form-urlencoded;charset=UTF-8';
    body = new URLSearchParams(options.body).toString();
  }

  const controller = new AbortController();
  const timeout = setTimeout(() => controller.abort(), 8000);
  let response;
  try {
      response = await fetch(apiUrl(path,options.query), {
      method: options.method || 'GET',
      headers,
      body,
      credentials: 'same-origin',
      signal: controller.signal
    });
  } catch (cause) {
    const error = new Error(cause.name === 'AbortError'
      ? 'O backend demorou para responder.'
      : 'Não foi possível conectar ao servidor do Fast Fantasy RPG.');
    error.status = 0;
    throw error;
  } finally {
    clearTimeout(timeout);
  }

  let payload = {};
  try {
    payload = await response.json();
  } catch (_) {
    payload = {};
  }
  if (!response.ok) {
    const error = new Error(payload.message || `Falha na API (${response.status}).`);
    error.status = response.status;
    throw error;
  }
  return payload;
}

function apiForgetSession() {
  const token = apiGetToken();
  apiSetToken(null);
  if (!token) return;
  fetch(apiUrl('auth/logout'), {
    method: 'POST',
    headers: { Authorization: `Bearer ${token}` },
    credentials: 'same-origin'
  }).catch(() => {});
}
