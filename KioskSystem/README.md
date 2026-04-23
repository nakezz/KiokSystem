# MUST Kiosk Registration System

A CLIPS-based Expert System integrated with a Java Swing Graphical User Interface to simulate and manage student registrations at the Mongolian University of Science and Technology (MUST).

## Overview

The MUST Kiosk Registration System is designed to provide an intelligent, automated solution for managing university course schedules and student enrollments. By leveraging the CLIPS expert system engine, the application dynamically resolves course congestion, processes special student requests ("R" for Repeat, "E" for Excused), and maintains a persistent application state across sessions.

## Features

- **Intelligent Course Scheduling**: Automatically resolves course congestion by adding new schedule slots based on dynamic rules.
- **Student Request Management**: Processes and tracks student enrollment statuses, including standard registrations, repeating students, and excused absences.
- **Persistent State**: Utilizes CLIPS fact-saving to preserve application data (`state.dat`) between runs, so the system remembers registrations and schedule modifications.
- **Java Swing GUI**: Provides an intuitive graphical interface for interacting with the underlying expert system logic.
- **Pre-populated Knowledge Base**: Comes with an initial dataset of 50 students (freshmen and seniors), 10 teachers, and 10 courses (`initial_data.clp`).

## Architecture

This project employs a hybrid architecture:
- **Frontend**: Java Swing is used to build the desktop interface.
- **Backend/Logic**: The CLIPS Expert System evaluates rules and facts.
- **Integration**: The Java application interacts with CLIPS by dynamically generating and executing batch scripts via `ProcessBuilder`, executing the local `CLIPSDOS.exe` binary. Output and application state are passed back to Java via flat files (`out.dat` and `state.dat`).

## Prerequisites

To run this application, ensure you have the following installed and configured on your system:

1. **Java Development Kit (JDK)** (JDK 17 used in `run.bat`)
   - The provided `run.bat` expects the Eclipse Adoptium JDK at `C:\Program Files\Eclipse Adoptium\jdk-17.0.14.7-hotspot\`. 
   - *Adjust the paths in `run.bat` if your Java installation differs.*
2. **CLIPS 6.4.2**
   - The system expects the CLIPS DOS executable to be located at:
     `C:\Program Files\SSS\CLIPS 6.4.2\CLIPSDOS.exe`
   - *If your CLIPS installation is located elsewhere, you must update the `CLIPS_EXE` path in `src/ClipsEngine.java`.*

## Running the Application

1. Open your terminal or command prompt.
2. Navigate to the `KioskSystem` directory.
3. Run the provided batch file:
   ```cmd
   run.bat
   ```
   This script will automatically compile the Java source files into the `out/production/KioskSystem` directory and launch the application.

## File Structure

- `src/` - Contains the Java source code (`KioskMain.java`, `ClipsEngine.java`).
- `kiosk_rules.clp` - The core CLIPS rule base defining the expert system logic.
- `initial_data.clp` - Initial facts representing the university's base state (students, teachers, courses).
- `state.dat` - Automatically generated file storing the persistent state of the system between runs.
- `out.dat` - Temporary file used to pass specific query outputs from CLIPS back to Java.
- `run.bat` - Compilation and execution script for Windows.
- `generator.py` - A utility script for generating initial data.

## Resetting the System

If you want to reset the system to its original state, simply delete the `state.dat` file. The application will automatically reload the base data from `initial_data.clp` on its next run.
