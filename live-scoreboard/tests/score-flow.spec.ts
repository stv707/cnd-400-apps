import { expect, test } from '@playwright/test';

test('submitting team scores updates sorted leaderboard', async ({ page }) => {
  await page.goto('/');
  await page.getByTestId('reset-board').click();

  await page.getByTestId('team-input').fill('Team Alpha');
  await page.getByTestId('points-input').fill('10');
  await page.getByTestId('submit-score').click();

  await page.getByTestId('team-input').fill('Team Beta');
  await page.getByTestId('points-input').fill('25');
  await page.getByTestId('submit-score').click();

  const rows = page.getByTestId('leaderboard-row');
  await expect(rows).toHaveCount(2);
  await expect(rows.nth(0)).toContainText('Team Beta');
  await expect(rows.nth(0)).toContainText('25');
  await expect(rows.nth(1)).toContainText('Team Alpha');
});
