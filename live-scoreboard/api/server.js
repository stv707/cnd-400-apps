import express from 'express';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import { ScoreboardStore, createApiHandler } from './scoreboard.js';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const frontendDir = path.resolve(__dirname, '..', 'frontend');

const app = express();
const store = new ScoreboardStore();

app.use('/api', createApiHandler(store));
app.use(express.static(frontendDir));

app.get('*', (_req, res) => {
  res.sendFile(path.join(frontendDir, 'index.html'));
});

const port = Number(process.env.PORT || 3000);
app.listen(port, '0.0.0.0', () => {
  console.log(`live-scoreboard listening on http://0.0.0.0:${port}`);
});
