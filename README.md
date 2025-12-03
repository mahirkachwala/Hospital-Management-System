# Hospital Management System (Role-Based)

This is a simple command-line Hospital Management System developed in Java.
It demonstrates role-based authentication, core Java programming, and several software engineering design patterns for managing patients, doctors, and appointments using file handling for data persistence.

**Current Date:** Dec 03, 2025
**author :** Mahir Kachwala

## Features

* **Role-Based Authentication:**
    * **STAFF Role:** Can register patients, add doctors (which also creates a basic doctor user account), schedule appointments (initial status: PENDING_APPROVAL), view all system data, and cancel appointments.
    * **DOCTOR Role:** Can view appointments assigned to them, and ACCEPT or REJECT these appointments. Can also view patient/doctor lists.
* Patient, Doctor, and Appointment management (CRUD-like operations based on role).
* Appointment lifecycle management with statuses (PENDING_APPROVAL, ACCEPTED, REJECTED, CANCELLED).
* Activity logging for major events, including logins and appointment status changes.
* Data persisted in text files in a `data/` directory.
* **Security Note:** User passwords are stored in plain text in `data/users.txt` for simplicity. **This is highly insecure and should NOT be done in a real-world application.** Always use strong hashing algorithms (e.g., bcrypt, Argon2) for password storage.

## Design Patterns Implemented

This project implements the following five design patterns:

1.  **Factory Method Pattern:**
    * **Purpose:** Used for creating objects (`Patient`, `Doctor`, `Appointment`, `User`) without exposing the instantiation logic to the client.
    * **Implementation:** `com.hospital.factory.EntityFactory`, `com.hospital.factory.HospitalEntityFactory`.

2.  **Singleton Pattern:**
    * **Purpose:** Ensures `FileManager` has only one instance for managing all file operations.
    * **Implementation:** `com.hospital.service.FileManager`.

3.  **Facade Pattern:** 
    * **Purpose:** Provides a simplified interface (`HospitalManagementFacade`) to the system, also managing user sessions, authentication, and role-based access control.
    * **Implementation:** `com.hospital.core.HospitalManagementFacade`.

4.  **Observer Pattern:** 
    * **Purpose:** `EventManager` notifies `ActivityLogger` of events like patient registration, appointment scheduling/status changes, and user logins (success/failure).
    * **Implementation:** `com.hospital.observer.*` classes.

5.  **State Pattern:**
    * **Purpose:** Manages the state and behavior of `Appointment` objects (e.g., `PendingApprovalState`, `AcceptedState`, `RejectedState`). An appointment transitions between these states, and its behavior (e.g., whether it can be accepted or rejected) depends on its current state.
    * **Implementation:**
        * `com.hospital.state.AppointmentState` (interface)
        * Concrete state classes: `PendingApprovalState`, `AcceptedState`, `RejectedState`, `CancelledState`.
        * `com.hospital.model.Appointment` now holds a reference to its current state object and delegates actions to it.

## Class Diagram
[![](https://img.plantuml.biz/plantuml/svg/lLTTQzim57qFv3_uqLth_e0GGWjfja3Q3BgtCOOYtxeQicGa9IDTzzztOycE97-qZB4dE_Avqhb7HyVEZJP4cNt9fnFAYTR99wahPWY_9u8KK88mjrML_9fEakIo94oaZqOnKNpzTf4S9Ckdaz_JILox8fJaO8koPpVuUDG6obaBih_hUSEwzA8UL50mHAZFn31auTQzIfWmLqdpmrulPkwcBYp0k9eb6CAuJblKjZ9C2iBd3h2e2pYkSykv6zCf0kz9DNTh152Ivqj9ZLHfo62GYKKF4x7F0nvsAARX0pLzXdpF0NT6eXobBqzptKXA-98O-C9A6AJdpJ3CqGC5HBtnc6sdH9C381jombdVM3Mj2xOx3Ue0AcrkMbIFkso9SnVQrp2Afi0RGQ7FNbdbDZPWcFZxckLMyn3XIPuHhzSG5RUQXGoScnrFVHJqMKd1NY1FKF-rutsIfUTWe42D_TqLSJifEH0HUVqjav-TQo_TYG9Z3tcqpthUVNVMhaV7hIL9tl79a7c6RLFdHoaFC0eHqh2dO_D4jsTek_i1rFHvy4uOPewOKoZ8qQMYsrgg04_R-zBAeRhbRuMBVTwj-viay6g27XsvP3v_1CK8Pozans6nS2VEdQPrxcT99aW9mG2-F6QdxkiV1OWSLF2SIc5GneTzkGkVNCOIZaaP7O9rddOYxW6PHQD6sX_-i2U05NceVniLRxMlAw2szkJqTtO-klkTdfUEoBzesVF2KDzU0aRDzpkYLoTlaiQHiz2WjXOqLQoAfRiSi_cutNlYgAFSe4PO0Eg9K4YQh717Vr_bkD0xSiFMdVHqQiecTB1zV9lEcudRiFx_sNPB3JjWkcraKM27hAnu_VsdewwppVRZzzLwSnU8Dou5LlrK3FT4hXY7yul08z7_uPbbY-Ypyenqdva-f7btza22w3uZtDV4XonhWs-Mb3K7tOjowJ0Eyi-JXmnV7zVN2szD6-Ize7R94KouOWkCk6NPwy8putJY6NF-UY8Uc60wkS7andy7Vm00)](https://editor.plantuml.com/uml/lLTTQzim57qFv3_uqLth_e0GGWjfja3Q3BgtCOOYtxeQicGa9IDTzzztOycE97-qZB4dE_Avqhb7HyVEZJP4cNt9fnFAYTR99wahPWY_9u8KK88mjrML_9fEakIo94oaZqOnKNpzTf4S9Ckdaz_JILox8fJaO8koPpVuUDG6obaBih_hUSEwzA8UL50mHAZFn31auTQzIfWmLqdpmrulPkwcBYp0k9eb6CAuJblKjZ9C2iBd3h2e2pYkSykv6zCf0kz9DNTh152Ivqj9ZLHfo62GYKKF4x7F0nvsAARX0pLzXdpF0NT6eXobBqzptKXA-98O-C9A6AJdpJ3CqGC5HBtnc6sdH9C381jombdVM3Mj2xOx3Ue0AcrkMbIFkso9SnVQrp2Afi0RGQ7FNbdbDZPWcFZxckLMyn3XIPuHhzSG5RUQXGoScnrFVHJqMKd1NY1FKF-rutsIfUTWe42D_TqLSJifEH0HUVqjav-TQo_TYG9Z3tcqpthUVNVMhaV7hIL9tl79a7c6RLFdHoaFC0eHqh2dO_D4jsTek_i1rFHvy4uOPewOKoZ8qQMYsrgg04_R-zBAeRhbRuMBVTwj-viay6g27XsvP3v_1CK8Pozans6nS2VEdQPrxcT99aW9mG2-F6QdxkiV1OWSLF2SIc5GneTzkGkVNCOIZaaP7O9rddOYxW6PHQD6sX_-i2U05NceVniLRxMlAw2szkJqTtO-klkTdfUEoBzesVF2KDzU0aRDzpkYLoTlaiQHiz2WjXOqLQoAfRiSi_cutNlYgAFSe4PO0Eg9K4YQh717Vr_bkD0xSiFMdVHqQiecTB1zV9lEcudRiFx_sNPB3JjWkcraKM27hAnu_VsdewwppVRZzzLwSnU8Dou5LlrK3FT4hXY7yul08z7_uPbbY-Ypyenqdva-f7btza22w3uZtDV4XonhWs-Mb3K7tOjowJ0Eyi-JXmnV7zVN2szD6-Ize7R94KouOWkCk6NPwy8putJY6NF-UY8Uc60wkS7andy7Vm00)

## Project Structure

```
HospitalManagementSystem/
├── src/
│   └── com/hospital/
│       ├── core/         # Main application, Facade
│       ├── model/        # Data entities (Patient, Doctor, Appointment, User, Role, AppointmentStatus)
│       ├── factory/      # Factory pattern
│       ├── service/      # File management (Singleton)
│       ├── observer/     # Observer pattern
│       └── state/        # State pattern for Appointment
├── data/                 # Data files (created automatically if not present)
│   ├── users.txt         # Stores user credentials
│   ├── patients.txt
│   ├── doctors.txt
│   ├── appointments.txt
│   └── activity_log.txt
└── README.md
```
## How to Compile and Run

1.  **Prerequisites:**
    * Java Development Kit (JDK) version 8 or higher installed.

2.  **Compilation:**
    Navigate to the root `HospitalManagementSystem` directory in your terminal.
    Compile all Java files, placing the output into an `out` directory (create `out` if it doesn't exist):
    ```bash
    mkdir out
    javac -d out src/com/hospital/core/*.java src/com/hospital/model/*.java src/com/hospital/factory/*.java src/com/hospital/service/*.java src/com/hospital/observer/*.java src/com/hospital/state/*.java
    ```

3.  **Running:**
    From the root `HospitalManagementSystem` directory:
    ```bash
    java -cp out com.hospital.core.HospitalManagementSystem
    ```
    A `data/` directory will be created (if it doesn't exist) inside `HospitalManagementSystem`. This is where `users.txt`, `patients.txt`, `doctors.txt`, `appointments.txt`, and `activity_log.txt` will be stored.

## Default Users

Upon first run (if `data/users.txt` is empty or not found), the system will create default users:
* **Staff:** `staff` / `staff123`
* **Doctor 1 (Sample):** `doctor1` / `doc123` (intended for a doctor with entity ID `DOC-SAMPLE1`)
* **Doctor 2 (Sample):** `doctor2` / `doc456` (intended for a doctor with entity ID `DOC-SAMPLE2`)

**Note for Doctor Login:** For a doctor user to manage appointments effectively, a corresponding Doctor entity with the `entityId` specified in `users.txt` (e.g., `DOC-SAMPLE1`) should exist in `doctors.txt`. When adding new doctors via the staff interface, a basic user account will be attempted to be created for them; check `activity_log.txt` for credentials.

## File Format

* **users.txt:** `username,password,ROLE_NAME,entityId_if_doctor_or_null`
    * Example Staff: `staff,staff123,STAFF,null`
    * Example Doctor: `doc1,pass123,DOCTOR,DOC-XYZ123`
* **patients.txt:** `id,name,contactNumber,age,gender,address`
* **doctors.txt:** `id,name,contactNumber,specialization,department`
* **appointments.txt (status added):** `appointmentId,patientId,doctorId,dateTimeISOString,description,STATUS_NAME`
* **activity_log.txt:** `timestamp - Event: EVENT_TYPE | Data: entity_toString_details`