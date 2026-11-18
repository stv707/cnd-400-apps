import { convertCurrency, runCli } from '../src/fx-rate';

describe('fx-rate converter', () => {
  test('converts USD to EUR accurately', () => {
    expect(convertCurrency(100, 'USD', 'EUR')).toBe(92);
  });

  test('normalizes lowercase currency codes and cross-converts via USD', () => {
    const gbp = convertCurrency(134, 'sgd', 'gbp');
    expect(gbp).toBe(79);
  });

  test('throws on negative amounts or unknown currencies', () => {
    expect(() => convertCurrency(-5, 'USD', 'EUR')).toThrow(/non-negative/);
    expect(() => convertCurrency(10, 'XYZ', 'USD')).toThrow(/Unsupported source/);
    expect(() => convertCurrency(10, 'USD', 'ABC')).toThrow(/Unsupported target/);
  });

  test('CLI returns non-zero exit code for insufficient args', () => {
    const errSpy = jest.spyOn(console, 'error').mockImplementation(() => {});
    expect(runCli(['100', 'USD'])).toBe(1);
    errSpy.mockRestore();
  });
});
