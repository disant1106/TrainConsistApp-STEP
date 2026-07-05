# TrainConsistApp — Advanced Java Train Coach Management System

TrainConsistApp is a console-based Java OOP project for managing the arrangement of railway coaches in a train. It demonstrates object-oriented design, collections, validation rules, file handling, route planning, maintenance tracking, and report generation.

## Features

- Add, remove, search, move, and sort coaches
- Multiple coach types: engine, AC, sleeper, general, pantry, luggage, brake van
- Passenger capacity and approximate train weight calculation
- Maintenance status tracking with due dates
- View maintenance due/overdue coaches
- Route stop management with distance, arrival, and departure times
- Total route distance, travel duration, and average speed calculation
- Consist validation rules:
  - engine should be first
  - brake/guard/luggage coach expected at the end
  - warns for multiple pantry cars
  - warns for long trains
  - warns/errors for maintenance issues
- Safety score out of 100
- Save/load train data using local files
- Export a train consist report as a text file

## Concepts Used

- Java OOP
- Enums
- ArrayList and maps
- Date/time handling
- File handling
- Rule-based validation
- Console menu design

## How to Run

```bash
cd src
javac *.java
java Main
```

## Notes

This project is intentionally kept as a plain Java console app without GUI frameworks, databases, Maven, or external libraries. It is suitable as a stronger Java OOP college assignment.
