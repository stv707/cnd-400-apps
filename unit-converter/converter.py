"""Clean-room unit conversion utilities for CND-400 Module 1, Module 2, and Module 5."""

from __future__ import annotations

ABSOLUTE_ZERO_C = -273.15
KM_PER_MILE = 1.609344
LBS_PER_KG = 2.20462262185

_UNIT_ALIASES: dict[str, str] = {
    "c": "c",
    "celsius": "c",
    "f": "f",
    "fahrenheit": "f",
    "k": "k",
    "kelvin": "k",
    "km": "km",
    "kilometer": "km",
    "kilometers": "km",
    "mi": "mi",
    "mile": "mi",
    "miles": "mi",
    "kg": "kg",
    "kilogram": "kg",
    "kilograms": "kg",
    "lb": "lb",
    "lbs": "lb",
    "pound": "lb",
    "pounds": "lb",
}


def c_to_f(celsius: float) -> float:
    """Convert degrees Celsius to Fahrenheit."""
    if celsius < ABSOLUTE_ZERO_C:
        raise ValueError(f"Temperature below absolute zero: {celsius} C")
    return round((celsius * 9.0 / 5.0) + 32.0, 4)


def f_to_c(fahrenheit: float) -> float:
    """Convert degrees Fahrenheit to Celsius."""
    celsius = (fahrenheit - 32.0) * 5.0 / 9.0
    if celsius < ABSOLUTE_ZERO_C:
        raise ValueError(f"Temperature below absolute zero: {fahrenheit} F")
    return round(celsius, 4)


def c_to_k(celsius: float) -> float:
    """Convert degrees Celsius to Kelvin."""
    if celsius < ABSOLUTE_ZERO_C:
        raise ValueError(f"Temperature below absolute zero: {celsius} C")
    return round(celsius - ABSOLUTE_ZERO_C, 4)


def k_to_c(kelvin: float) -> float:
    """Convert Kelvin to degrees Celsius."""
    if kelvin < 0:
        raise ValueError(f"Kelvin cannot be negative: {kelvin} K")
    return round(kelvin + ABSOLUTE_ZERO_C, 4)


def km_to_miles(km: float) -> float:
    """Convert kilometers to statute miles."""
    if km < 0:
        raise ValueError(f"Distance cannot be negative: {km} km")
    return round(km / KM_PER_MILE, 6)


def miles_to_km(miles: float) -> float:
    """Convert statute miles to kilometers."""
    if miles < 0:
        raise ValueError(f"Distance cannot be negative: {miles} mi")
    return round(miles * KM_PER_MILE, 6)


def kg_to_lbs(kg: float) -> float:
    """Convert kilograms to avoirdupois pounds."""
    if kg < 0:
        raise ValueError(f"Mass cannot be negative: {kg} kg")
    return round(kg * LBS_PER_KG, 6)


def lbs_to_kg(lbs: float) -> float:
    """Convert avoirdupois pounds to kilograms."""
    if lbs < 0:
        raise ValueError(f"Mass cannot be negative: {lbs} lb")
    return round(lbs / LBS_PER_KG, 6)


_CONVERTERS = {
    ("c", "f"): c_to_f,
    ("f", "c"): f_to_c,
    ("c", "k"): c_to_k,
    ("k", "c"): k_to_c,
    ("f", "k"): lambda v: c_to_k(f_to_c(v)),
    ("k", "f"): lambda v: c_to_f(k_to_c(v)),
    ("km", "mi"): km_to_miles,
    ("mi", "km"): miles_to_km,
    ("kg", "lb"): kg_to_lbs,
    ("lb", "kg"): lbs_to_kg,
}


def normalize_unit(unit: str) -> str:
    """Normalize a unit name or abbreviation."""
    key = unit.strip().lower()
    if key not in _UNIT_ALIASES:
        raise ValueError(f"Unsupported unit: {unit!r}")
    return _UNIT_ALIASES[key]


def convert(value: float, from_unit: str, to_unit: str) -> float:
    """Convert a numeric value between compatible units."""
    src = normalize_unit(from_unit)
    dst = normalize_unit(to_unit)
    if src == dst:
        if src in ("km", "mi", "kg", "lb", "k") and value < 0:
            raise ValueError(f"Value cannot be negative for unit {src}: {value}")
        if src == "c" and value < ABSOLUTE_ZERO_C:
            raise ValueError(f"Temperature below absolute zero: {value} C")
        if src == "f" and ((value - 32.0) * 5.0 / 9.0) < ABSOLUTE_ZERO_C:
            raise ValueError(f"Temperature below absolute zero: {value} F")
        return round(float(value), 6)

    fn = _CONVERTERS.get((src, dst))
    if fn is None:
        raise ValueError(f"Incompatible conversion: {from_unit!r} -> {to_unit!r}")
    return fn(float(value))
