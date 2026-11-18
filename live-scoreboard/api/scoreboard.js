/**
 * Pure Node.js 20 in-memory scoreboard store and HTTP request handler.
 * Zero external runtime dependencies so unit tests execute fast via `node --test`.
 */

export class ScoreboardStore {
  constructor() {
    this.scores = new Map();
  }

  /**
   * Adds (or subtracts) integer points for a named team.
   * @param {string} rawTeam
   * @param {number} points
   * @returns {Array<{team: string, points: number}>}
   */
  recordScore(rawTeam, points) {
    if (typeof rawTeam !== 'string' || rawTeam.trim().length === 0) {
      throw new Error('team must be a non-empty string');
    }
    if (typeof points !== 'number' || !Number.isFinite(points) || !Number.isInteger(points)) {
      throw new Error('points must be a finite integer');
    }

    const team = rawTeam.trim();
    const current = this.scores.get(team) ?? 0;
    this.scores.set(team, current + points);
    return this.getLeaderboard();
  }

  /**
   * Returns teams sorted descending by score, then ascending alphabetically.
   * @returns {Array<{team: string, points: number}>}
   */
  getLeaderboard() {
    return Array.from(this.scores.entries())
      .map(([team, points]) => ({ team, points }))
      .sort((a, b) => (b.points - a.points) || a.team.localeCompare(b.team));
  }

  reset() {
    this.scores.clear();
  }
}

function sendJson(res, statusCode, payload) {
  res.statusCode = statusCode;
  res.setHeader('Content-Type', 'application/json; charset=utf-8');
  res.end(JSON.stringify(payload));
}

function readJsonBody(req) {
  if (req.body && typeof req.body === 'object') {
    return Promise.resolve(req.body);
  }
  return new Promise((resolve, reject) => {
    const chunks = [];
    req.on('data', (chunk) => chunks.push(chunk));
    req.on('end', () => {
      if (chunks.length === 0) {
        resolve({});
        return;
      }
      try {
        resolve(JSON.parse(Buffer.concat(chunks).toString('utf8')));
      } catch {
        reject(new Error('Invalid JSON payload'));
      }
    });
    req.on('error', reject);
  });
}

/**
 * Creates a standard Node HTTP / Express middleware handler for `/api/*` routes.
 * @param {ScoreboardStore} store
 */
export function createApiHandler(store) {
  return async (req, res) => {
    const rawPath = (req.url || '/').split('?')[0];
    const path = rawPath.startsWith('/api/')
      ? rawPath.slice(4)
      : rawPath === '/api'
        ? '/'
        : rawPath;
    const method = (req.method || 'GET').toUpperCase();

    if (method === 'GET' && path === '/health') {
      sendJson(res, 200, { status: 'ok' });
      return;
    }

    if (method === 'GET' && path === '/scores') {
      sendJson(res, 200, { teams: store.getLeaderboard() });
      return;
    }

    if (method === 'POST' && path === '/scores/reset') {
      store.reset();
      sendJson(res, 200, { teams: [] });
      return;
    }

    if (method === 'POST' && path === '/scores') {
      try {
        const body = await readJsonBody(req);
        const teams = store.recordScore(body.team, body.points);
        sendJson(res, 201, { teams });
      } catch (err) {
        sendJson(res, 400, { error: err.message });
      }
      return;
    }

    sendJson(res, 404, { error: `Route not found: ${method} ${path}` });
  };
}
