# ESGP - Encost Smart Graph Project

A software system that enables the visualisation of Encost smart home devices
using a graph data structure, developed by SoTech Engineering.

## Project Overview

ESGP allows users to:
- Select a user type (Community or Encost User)
- Log in as an Encost User to access additional features
- Visualise the smart home device network as an interactive graph
- Load a custom dataset (Encost Verified users only)
- View summary statistics about the device network (Encost Verified users only)

## User Types

| User Type         | Graph Visualisation | Load Custom Dataset | Summary Statistics |
|-------------------|:-------------------:|:-------------------:|:-----------------:|
| Community         | ✓                   |                     |                   |
| Encost Unverified | ✓                   |                     |                   |
| Encost Verified   | ✓                   | ✓                   | ✓                 |

## Dataset Format

The default dataset is the Encost Smart Homes Dataset bundled with the project.
Custom datasets must be a CSV file with the following columns in order:

```
deviceID, dateConnected, deviceName, deviceType, householdID, routerConnection, sends, receives
```

Example:
```
EWR-1234,01/04/22,Encost Router 360,Router,WKO-1234,-,Yes,Yes
ELB-4567,01/04/22,Encost Smart Bulb B22,Light Bulb,WKO-1234,EWR-1234,No,Yes
```

- `routerConnection` should be `-` for root devices (routers)
- `sends` and `receives` should be `Yes` or `No`

## Device Categories

| Category          | Device Types                                        |
|-------------------|-----------------------------------------------------|
| Wifi Routers      | Router, Extender                                    |
| Hubs/Controllers  | Hub, Controller                                     |
| Smart Lighting    | Light Bulb, Other Lighting                          |
| Smart Appliances  | Kettle, Toaster, Coffee Maker, Refrigerator/Freezer |
| Smart Whiteware   | Washing Machine/Dryer, Dishwasher                   |

## Graph Visualisation

Devices are visually distinguished in the graph by:
- **Colour** — based on device category
- **Shape** — based on send/receive capability:
  - Square — sends only
  - Circle — sends and receives
  - Triangle — receives only

## Project Structure

```
COMPX341_A3/
├── src/
│   ├── main/java/esgp/
│   │   ├── Backend.java          # Singleton: coordinates all user interaction
│   │   ├── Dataset.java          # Loads CSV and calculates summary statistics
│   │   ├── Device.java           # Parses one CSV row into a device object
│   │   ├── GraphDataType.java    # Builds and displays the GraphStream graph
│   │   ├── LoginService.java     # Handles Encost user authentication
│   │   ├── User.java             # Stores username and user type for a session
│   │   └── UserType.java         # String constants for the three user types
│   └── test/
│       ├── java/
│       │   ├── DatasetTest.java
│       │   ├── DeviceTest.java
│       │   ├── LoginServiceTest.java
│       │   └── UserTest.java
│       └── resources/
│           ├── validDataset.csv
│           └── corruptedDataset.csv
├── lib/                          # External JARs (GraphStream, JUnit) - local only
├── Encost Smart Homes Dataset (bigger).txt
├── users.txt
├── README.md
└── .gitignore
```

## Prerequisites

- Java 1.8.0 or higher
- GraphStream (`gs-core`, `gs-ui-swing`) — place JARs in `lib/`
- JUnit Platform Console Standalone — place JAR in `lib/` for running tests

Download GraphStream: https://graphstream-project.org

## How to Compile

```bash
mkdir -p out/production

javac -cp "lib/*" -d out/production src/main/java/esgp/*.java
```

On Windows:
```powershell
mkdir out/production

javac -cp "lib/*" -d out/production src/main/java/esgp/*.java
```

## How to Run

```powershell
java -cp "lib/*;out/production" esgp.Backend
```

The application will start with the welcome prompt:
```
Welcome to the Encost Smart Graph Project
What type of user are you?
(a) An Encost User
(b) A Community User
Please input a or b:
```

## How to Run Tests

```powershell
# Compile tests
mkdir out/test

javac -cp "lib/*;out/production" -d out/test src/test/java/*.java

# Run tests
java -cp "lib/*;out/production;out/test" org.junit.platform.console.ConsoleLauncher --scan-classpath --classpath=out/test
```

## Data Files

The following files must be present in the project root directory when running:

- `users.txt` — comma-separated username and password credentials
- `Encost Smart Homes Dataset (bigger).txt` — the default Encost Smart Homes Dataset

## Authors

SoTech Engineering — COMPX341-26A Assignment 3, 2026
