import { describe, it } from 'node:test';
import assert from 'node:assert/strict';
import http from 'node:http';
import { ScoreboardStore, createApiHandler } from './scoreboard.js';

async function withServer(fn) {
  const store = new ScoreboardStore();
  const server = http.createServer(createApiHandler(store));
  await new Promise((resolve) => server.listen(0, '127.0.0.1', resolve));
  const { port } = server.address();
  const baseUrl = `http://127.0.0.1:${port}`;
  try {
    await fn(baseUrl, store);
  } finally {
    await new Promise((resolve) => server.close(resolve));
  }
}

describe('ScoreboardStore', () => {
  it('accumulates scores and orders descending with alphabetical tie-breaks', () => {
    const store = new ScoreboardStore();
    store.recordScore('Zebra', 10);
    store.recordScore('Alpha', 10);
    store.recordScore('Beta', 25);
    store.recordScore('Alpha', 15);

    assert.deepEqual(store.getLeaderboard(), [
      { team: 'Alpha', points: 25 },
      { team: 'Beta', points: 25 },
      { team: 'Zebra', points: 10 },
    ]);
  });

  it('rejects blank team names and non-integer scores', () => {
    const store = new ScoreboardStore();
    assert.throws(() => store.recordScore('   ', 10), /non-empty/);
    assert.throws(() => store.recordScore('Alpha', 1.5), /finite integer/);
  });
});

describe('Scoreboard HTTP API', () => {
  it('supports health check, score recording, retrieval, reset, and 400 errors', async () => {
    await withServer(async (baseUrl) => {
      const healthRes = await fetch(`${baseUrl}/api/health`);
      assert.equal(healthRes.status, 200);
      assert.deepEqual(await healthRes.json(), { status: 'ok' });

      const createRes = await fetch(`${baseUrl}/api/scores`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ team: 'Team Nova', points: 40 }),
      });
      assert.equal(createRes.status, 201);
      const created = await createRes.json();
      assert.deepEqual(created.teams, [{ team: 'Team Nova', points: 40 }]);

      const invalidRes = await fetch(`${baseUrl}/api/scores`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ team: '', points: 10 }),
      });
      assert.equal(invalidRes.status, 400);

      const listRes = await fetch(`${baseUrl}/api/scores`);
      assert.equal(listRes.status, 200);
      assert.equal((await listRes.json()).teams.length, 1);

      const resetRes = await fetch(`${baseUrl}/api/scores/reset`, { method: 'POST' });
      assert.equal(resetRes.status, 200);
      assert.deepEqual(await resetRes.json(), { teams: [] });
    });
  });
});
