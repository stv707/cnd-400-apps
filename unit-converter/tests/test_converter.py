"""Unit tests for unit-converter."""

import pytest

from converter import (
    c_to_f,
    convert,
    f_to_c,
    kg_to_lbs,
    km_to_miles,
    lbs_to_kg,
    miles_to_km,
)


def test_celsius_to_fahrenheit() -> None:
    assert c_to_f(0.0) == 32.0
    assert c_to_f(100.0) == 212.0
    assert c_to_f(-40.0) == -40.0


def test_fahrenheit_to_celsius() -> None:
    assert f_to_c(32.0) == 0.0
    assert f_to_c(212.0) == 100.0
    assert f_to_c(-40.0) == -40.0


def test_distance_round_trip() -> None:
    miles = km_to_miles(10.0)
    assert miles == pytest.approx(6.213712, rel=1e-5)
    assert miles_to_km(miles) == pytest.approx(10.0, rel=1e-5)


def test_weight_round_trip() -> None:
    lbs = kg_to_lbs(5.0)
    assert lbs == pytest.approx(11.023113, rel=1e-5)
    assert lbs_to_kg(lbs) == pytest.approx(5.0, rel=1e-5)


def test_generic_convert_aliases() -> None:
    assert convert(25, "Celsius", "Fahrenheit") == 77.0
    assert convert(0, "c", "k") == 273.15
    assert convert(42, "km", "km") == 42.0


def test_invalid_inputs_raise_value_error() -> None:
    with pytest.raises(ValueError):
        c_to_f(-300.0)
    with pytest.raises(ValueError):
        km_to_miles(-1.0)
    with pytest.raises(ValueError):
        convert(10, "km", "kg")
    with pytest.raises(ValueError):
        convert(10, "parsecs", "km")
