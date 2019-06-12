from unittest import TestCase

from solution_files.task_1 import Aircraft, IntercontinentalAircraft, ShortHaulAircraft, ControlTower


class Task1Test(TestCase):

    def test_inheritance(self):
        intercontinental_flight = IntercontinentalAircraft(40, "intercontinental", 100)
        short_haul_flight = ShortHaulAircraft(90, "short")

        self.assertTrue(isinstance(intercontinental_flight, Aircraft),
                        "IntercontinentalAircraft should inherit from Aircraft")
        self.assertTrue(isinstance(short_haul_flight, Aircraft), "ShortHaulAircraft should inherit from Aircraft")

    def test_aircraft_get_name(self):
        aircraft = IntercontinentalAircraft(40, "intercontinental", 100)
        self.assertEqual(aircraft.get_name(), "intercontinental")

        aircraft = ShortHaulAircraft(90, "short")
        self.assertEqual(aircraft.get_name(), "short")

    def test_aircraft_get_number_of_passengers(self):
        aircraft = IntercontinentalAircraft(40, "intercontinental", 100)
        self.assertEqual(aircraft.get_number_of_passengers(), 40)

        aircraft = ShortHaulAircraft(90, "short")
        self.assertEqual(aircraft.get_number_of_passengers(), 90)

    def test_calculate_amount_of_fuel_intercontinental(self):
        aircraft = IntercontinentalAircraft(40, "intercontinental", 100)
        fuel = aircraft.calculate_amount_of_fuel(1000)
        self.assertEqual(fuel, 210000)

    def test_get_manifest_intercontinental(self):
        aircraft = IntercontinentalAircraft(40, "intercontinental", 100)
        self.assertEqual(aircraft.manifest,
                         f"Intercontinental flight intercontinental: passenger count 40, cargo load 100")

    def test_calculate_amount_of_fuel_short_haul(self):
        aircraft = ShortHaulAircraft(90, "short")
        fuel = aircraft.calculate_amount_of_fuel(1000)
        self.assertEqual(fuel, 9000)

    def test_get_manifest_short_haul(self):
        aircraft = ShortHaulAircraft(40, "short")
        serial_number = aircraft.get_serial_number()
        self.assertEqual(aircraft.manifest,
                         f"Short haul flight serial number {serial_number}, name short: passenger count 40")

    def test_list_flights(self):
        intercontinental_flight = IntercontinentalAircraft(500, "Boeing-747", 100)
        short_haul_flight = ShortHaulAircraft(110, "Airbus-A220")
        short_haul_flight2 = ShortHaulAircraft(85, "Airbus-A220")

        tower = ControlTower()
        tower.add_aircraft(intercontinental_flight)
        tower.add_aircraft(short_haul_flight)
        tower.add_aircraft(short_haul_flight2)

        manifests = tower.get_manifests()
        self.assertEqual(manifests, [
            "Intercontinental flight Boeing-747: passenger count 500, cargo load 100",
            f"Short haul flight serial number {short_haul_flight.get_serial_number()}, name Airbus-A220: passenger count 110",
            f"Short haul flight serial number {short_haul_flight2.get_serial_number()}, name Airbus-A220: passenger count 85"])
