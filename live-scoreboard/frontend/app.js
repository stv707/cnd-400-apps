const form = document.getElementById('score-form');
const teamInput = document.getElementById('team-name');
const pointsInput = document.getElementById('points-input');
const resetButton = document.getElementById('reset-board');
const errorBanner = document.getElementById('error-banner');
const leaderboardBody = document.getElementById('leaderboard-body');

function renderLeaderboard(teams) {
  leaderboardBody.replaceChildren();
  for (const [idx, entry] of teams.entries()) {
    const tr = document.createElement('tr');
    tr.dataset.testid = 'leaderboard-row';

    const rankTd = document.createElement('td');
    rankTd.textContent = String(idx + 1);

    const teamTd = document.createElement('td');
    teamTd.textContent = entry.team;

    const pointsTd = document.createElement('td');
    pointsTd.textContent = String(entry.points);

    tr.append(rankTd, teamTd, pointsTd);
    leaderboardBody.appendChild(tr);
  }
}

async function fetchLeaderboard() {
  const res = await fetch('/api/scores');
  const data = await res.json();
  renderLeaderboard(data.teams || []);
}

form.addEventListener('submit', async (event) => {
  event.preventDefault();
  errorBanner.textContent = '';

  const team = teamInput.value.trim();
  const points = Number.parseInt(pointsInput.value, 10);

  const res = await fetch('/api/scores', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ team, points }),
  });

  const payload = await res.json();
  if (!res.ok) {
    errorBanner.textContent = payload.error || 'Failed to record score';
    return;
  }
  renderLeaderboard(payload.teams || []);
  form.reset();
});

resetButton.addEventListener('click', async () => {
  errorBanner.textContent = '';
  const res = await fetch('/api/scores/reset', { method: 'POST' });
  const payload = await res.json();
  renderLeaderboard(payload.teams || []);
});

fetchLeaderboard();
