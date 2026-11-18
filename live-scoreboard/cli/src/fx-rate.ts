import { USD_RATES } from './rate-table';

/**
 * Converts a monetary amount between two supported ISO currency codes.
 */
export function convertCurrency(amount: number, fromCurrency: string, toCurrency: string): number {
  if (!Number.isFinite(amount) || amount < 0) {
    throw new Error(`Amount must be a finite non-negative number: ${amount}`);
  }
  const src = fromCurrency.trim().toUpperCase();
  const dst = toCurrency.trim().toUpperCase();

  const srcRate = USD_RATES[src];
  const dstRate = USD_RATES[dst];
  if (srcRate === undefined) {
    throw new Error(`Unsupported source currency: ${fromCurrency}`);
  }
  if (dstRate === undefined) {
    throw new Error(`Unsupported target currency: ${toCurrency}`);
  }

  const usdAmount = amount / srcRate;
  return Number((usdAmount * dstRate).toFixed(4));
}

export function runCli(argv: string[]): number {
  if (argv.length < 3) {
    console.error('Usage: fx-rate <amount> <from> <to>');
    return 1;
  }
  const amount = Number(argv[0]);
  try {
    const converted = convertCurrency(amount, argv[1], argv[2]);
    console.log(`${amount} ${argv[1].toUpperCase()} = ${converted} ${argv[2].toUpperCase()}`);
    return 0;
  } catch (err) {
    console.error(`Error: ${(err as Error).message}`);
    return 1;
  }
}

if (require.main === module) {
  process.exitCode = runCli(process.argv.slice(2));
}
