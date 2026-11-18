#!/usr/bin/env python3
"""Availability poller for CND-400 zero-downtime deployment verification (Ex 6.3 / Lab 5)."""

from __future__ import annotations

import argparse
import sys
import time
import urllib.error
import urllib.request


def poll_once(url: str, timeout: float) -> tuple[int, float]:
    """Perform a single HTTP GET request and return (status_code, elapsed_ms)."""
    start = time.monotonic()
    req = urllib.request.Request(url, method="GET")
    try:
        with urllib.request.urlopen(req, timeout=timeout) as resp:
            resp.read()
            elapsed = (time.monotonic() - start) * 1000.0
            return resp.status, elapsed
    except urllib.error.HTTPError as exc:
        elapsed = (time.monotonic() - start) * 1000.0
        return exc.code, elapsed
    except Exception:
        elapsed = (time.monotonic() - start) * 1000.0
        return 0, elapsed


def main(argv: list[str] | None = None) -> int:
    parser = argparse.ArgumentParser(description="Poll HTTP availability during Kubernetes rollouts.")
    parser.add_argument("--url", required=True, help="Target health endpoint URL")
    parser.add_argument("--count", type=int, default=30, help="Total requests (0 = infinite)")
    parser.add_argument("--interval", type=float, default=1.0, help="Seconds between polls")
    parser.add_argument("--timeout", type=float, default=3.0, help="HTTP timeout in seconds")
    args = parser.parse_args(argv)

    total = 0
    success = 0
    try:
        while args.count == 0 or total < args.count:
            total += 1
            code, latency_ms = poll_once(args.url, args.timeout)
            ok = 200 <= code < 300
            if ok:
                success += 1
            status_label = str(code) if code > 0 else "CONN_ERR"
            print(f"[{total:03d}] {time.strftime('%H:%M:%S')} HTTP {status_label} ({latency_ms:.1f} ms)")
            sys.stdout.flush()
            if args.count == 0 or total < args.count:
                time.sleep(args.interval)
    except KeyboardInterrupt:
        pass

    availability = (success / total * 100.0) if total else 0.0
    print(f"Summary: {success}/{total} OK ({availability:.1f}% availability)")
    return 0 if success == total else 1


if __name__ == "__main__":
    raise SystemExit(main())
