"""Command-line interface for unit-converter."""

from __future__ import annotations

import argparse
import sys

from converter import convert


def build_parser() -> argparse.ArgumentParser:
    parser = argparse.ArgumentParser(
        prog="unit-converter",
        description="Convert temperature, distance, and mass measurements.",
    )
    parser.add_argument("value", type=float, help="Numeric measurement to convert")
    parser.add_argument("from_unit", help="Source unit (c, f, k, km, mi, kg, lb)")
    parser.add_argument("to_unit", help="Target unit (c, f, k, km, mi, kg, lb)")
    return parser


def main(argv: list[str] | None = None) -> int:
    parser = build_parser()
    args = parser.parse_args(argv)
    try:
        result = convert(args.value, args.from_unit, args.to_unit)
    except ValueError as exc:
        print(f"Error: {exc}", file=sys.stderr)
        return 1
    print(f"{args.value:g} {args.from_unit} = {result:g} {args.to_unit}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
