package com.hospital.core;

import com.hospital.model.*;
import com.hospital.observer.ActivityLogger;
import com.hospital.observer.EventManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class HospitalManagementSystem {
    private static HospitalManagementFacade facade;
    private static Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static void main(String[] args) {
        EventManager eventManager = new EventManager();
        ActivityLogger activityLogger = new ActivityLogger();
        eventManager.registerObserver(activityLogger);

        facade = new HospitalManagementFacade(eventManager);

        System.out.println("Welcome to the Hospital Management System!");
        System.out.println("IMPORTANT: For simplicity, passwords are plain text. DO NOT use real passwords.");
        System.out.println("Default users exist: e.g., staff/staff123, doctor1/doc123 (for DOC-SAMPLE1)");

        boolean running = true;
        while (running) {
            if (facade.getCurrentUser() == null) {
                showLoginMenu();
            } else {
                showRoleBasedMenu();
            }
            // Exit condition is handled within menus
            if (facade.getCurrentUser() == null && !promptForLoginRetry()) { // If logout and no retry
                 System.out.println("Exiting Hospital Management System. Goodbye!");
                 running = false;
            }
        }
        scanner.close();
    }
    
    private static boolean promptForLoginRetry() {
        System.out.print("Do you want to try logging in again? (yes/no): ");
        String choice = scanner.nextLine().trim().toLowerCase();
        return choice.equals("yes");
    }


    private static void showLoginMenu() {
        System.out.println("\n--- LOGIN ---");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine(); // In real app, use char[] and clear

        if (facade.login(username, password)) {
            System.out.println("Login successful. Welcome, " + facade.getCurrentUser().getUsername() + " (" + facade.getCurrentUserRole() + ")!");
        } else {
            System.out.println("Login failed. Invalid username or password.");
            // Option to exit or retry is handled in main loop
        }
    }

    private static void showRoleBasedMenu() {
        Role role = facade.getCurrentUserRole();
        if (role == null) {
            System.out.println("Error: No user logged in.");
            facade.logout(); // Ensure clean state
            return;
        }

        switch (role) {
            case STAFF:
                showStaffMenu();
                break;
            case DOCTOR:
                showDoctorMenu();
                break;
            default:
                System.out.println("Unknown role. Logging out.");
                facade.logout();
        }
    }

    // --- STAFF MENU ---
    private static void showStaffMenu() {
        boolean staffLoggedIn = true;
        while (staffLoggedIn) {
            System.out.println("\n--- STAFF Menu (" + facade.getCurrentUser().getUsername() + ") ---");
            System.out.println("1. Register New Patient");
            System.out.println("2. Add New Doctor (& create basic user for them)");
            System.out.println("3. Schedule New Appointment (Pending Approval)");
            System.out.println("4. View All Patients");
            System.out.println("5. View All Doctors");
            System.out.println("6. View All Appointments (All Statuses)");
            System.out.println("7. View Appointments for a Patient");
            System.out.println("8. View Appointments for a Doctor (Any Status)");
            System.out.println("9. Cancel an Appointment");
            System.out.println("0. Logout");
            System.out.print("Enter your choice: ");

            int choice = -1;
             try {
                String line = scanner.nextLine();
                if (!line.trim().isEmpty()) choice = Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            switch (choice) {
                case 1: registerPatient(); break;
                case 2: addDoctor(); break;
                case 3: scheduleAppointment(); break;
                case 4: viewAllPatients(); break;
                case 5: viewAllDoctors(); break;
                case 6: viewAllAppointmentsByStaff(); break;
                case 7: viewAppointmentsByPatient(); break;
                case 8: viewAppointmentsByDoctorForStaff(); break;
                case 9: cancelAppointmentByStaff(); break;
                case 0:
                    facade.logout();
                    staffLoggedIn = false;
                    System.out.println("Logged out.");
                    break;
                default: System.out.println("Invalid choice. Please try again.");
            }
            System.out.println();
        }
    }
    
    private static void cancelAppointmentByStaff() {
        System.out.println("\n--- Cancel Appointment (Staff) ---");
        System.out.print("Enter Appointment ID to cancel: ");
        String appointmentId = scanner.nextLine();
        if (facade.cancelAppointmentByStaff(appointmentId)) {
            System.out.println("Attempted to cancel appointment " + appointmentId + ".");
        } else {
            System.out.println("Failed to process cancellation for appointment " + appointmentId + ".");
        }
    }

    // --- DOCTOR MENU ---
    private static void showDoctorMenu() {
        boolean doctorLoggedIn = true;
        String doctorEntityId = facade.getCurrentUser().getEntityId();
        if (doctorEntityId == null) {
            System.out.println("Error: Doctor user is not linked to a Doctor entity. Logging out.");
            facade.logout();
            return;
        }
        System.out.println("Linked to Doctor ID: " + doctorEntityId);


        while (doctorLoggedIn) {
            System.out.println("\n--- DOCTOR Menu (" + facade.getCurrentUser().getUsername() + " - Dr. ID: " + doctorEntityId + ") ---");
            System.out.println("1. View My PENDING Appointments");
            System.out.println("2. View My ACCEPTED Appointments");
            System.out.println("3. View My REJECTED/CANCELLED Appointments");
            System.out.println("4. ACCEPT an Appointment");
            System.out.println("5. REJECT an Appointment");
            System.out.println("6. View All Patients (Read-only)");
            System.out.println("7. View All Doctors (Read-only)");
            System.out.println("0. Logout");
            System.out.print("Enter your choice: ");

            int choice = -1;
             try {
                String line = scanner.nextLine();
                if (!line.trim().isEmpty()) choice = Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            switch (choice) {
                case 1: viewMyAppointmentsByStatus(doctorEntityId, AppointmentStatus.PENDING_APPROVAL); break;
                case 2: viewMyAppointmentsByStatus(doctorEntityId, AppointmentStatus.ACCEPTED); break;
                case 3: viewMyProblemAppointments(doctorEntityId); break;
                case 4: processDoctorAppointmentAction("ACCEPT"); break;
                case 5: processDoctorAppointmentAction("REJECT"); break;
                case 6: viewAllPatients(); break;
                case 7: viewAllDoctors(); break;
                case 0:
                    facade.logout();
                    doctorLoggedIn = false;
                    System.out.println("Logged out.");
                    break;
                default: System.out.println("Invalid choice. Please try again.");
            }
             System.out.println();
        }
    }

    private static void viewMyAppointmentsByStatus(String doctorId, AppointmentStatus status) {
        System.out.println("\n--- My " + status + " Appointments ---");
        List<Appointment> apps = facade.getAppointmentsForDoctor(doctorId, status);
        if (apps.isEmpty()) {
            System.out.println("No " + status + " appointments found for you.");
        } else {
            apps.forEach(System.out::println);
        }
    }
    
    private static void viewMyProblemAppointments(String doctorId) {
        System.out.println("\n--- My REJECTED/CANCELLED Appointments ---");
        List<Appointment> rejected = facade.getAppointmentsForDoctor(doctorId, AppointmentStatus.REJECTED);
        List<Appointment> cancelled = facade.getAppointmentsForDoctor(doctorId, AppointmentStatus.CANCELLED_BY_STAFF); // Or more generic CANCELLED
        
        if (rejected.isEmpty() && cancelled.isEmpty()) {
            System.out.println("No rejected or cancelled appointments found for you.");
        } else {
            if(!rejected.isEmpty()) {
                System.out.println("REJECTED:");
                rejected.forEach(System.out::println);
            }
            if(!cancelled.isEmpty()) {
                System.out.println("CANCELLED:");
                cancelled.forEach(System.out::println);
            }
        }
    }

    private static void processDoctorAppointmentAction(String action) {
        System.out.println("\n--- " + action + " Appointment ---");
        System.out.print("Enter Appointment ID to " + action.toLowerCase() + ": ");
        String appointmentId = scanner.nextLine();
        if (facade.processAppointmentAction(appointmentId, action)) {
            // Message is printed within Facade/State
        } else {
            // Error message printed within Facade
        }
    }

    // --- Shared/Modified Menu Actions ---
    private static void registerPatient() { // Staff only
        System.out.println("\n--- Register New Patient ---");
        System.out.print("Enter Patient Name: "); String name = scanner.nextLine();
        System.out.print("Enter Contact Number: "); String contact = scanner.nextLine();
        System.out.print("Enter Age: "); int age = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter Gender: "); String gender = scanner.nextLine();
        System.out.print("Enter Address: "); String address = scanner.nextLine();
        Patient p = facade.registerNewPatient(name, contact, age, gender, address);
        if (p != null) System.out.println("Patient registered successfully: " + p.getId());
    }

    private static void addDoctor() { // Staff only
        System.out.println("\n--- Add New Doctor ---");
        System.out.print("Enter Doctor Name: "); String name = scanner.nextLine();
        System.out.print("Enter Contact Number: "); String contact = scanner.nextLine();
        System.out.print("Enter Specialization: "); String spec = scanner.nextLine();
        System.out.print("Enter Department: "); String dept = scanner.nextLine();
        Doctor d = facade.addNewDoctor(name, contact, spec, dept);
        if (d != null) {
            System.out.println("Doctor added successfully: " + d.getId());
            System.out.println("A basic user account may have been created for this doctor.");
            System.out.println("Please check activity_log.txt for username/password if a new user was created.");
        }
    }

    private static void scheduleAppointment() { // Staff only
        System.out.println("\n--- Schedule New Appointment (will be PENDING_APPROVAL) ---");
        System.out.print("Enter Patient ID: "); String patId = scanner.nextLine();
        System.out.print("Enter Doctor ID: "); String docId = scanner.nextLine();
        System.out.print("Enter Appointment Date and Time (YYYY-MM-DD HH:MM): "); String dtStr = scanner.nextLine();
        LocalDateTime dt;
        try {
            dt = LocalDateTime.parse(dtStr, dateTimeFormatter);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date/time format. Use YYYY-MM-DD HH:MM."); return;
        }
        System.out.print("Enter Description: "); String desc = scanner.nextLine();
        Appointment app = facade.scheduleNewAppointment(patId, docId, dt, desc);
        if (app != null) System.out.println("Appointment scheduled (pending approval): " + app.getAppointmentId());
    }

    private static void viewAllPatients() {
        System.out.println("\n--- All Patients ---");
        List<Patient> items = facade.getAllPatients();
        if (items.isEmpty()) System.out.println("No patients found.");
        else items.forEach(System.out::println);
    }

    private static void viewAllDoctors() {
        System.out.println("\n--- All Doctors ---");
        List<Doctor> items = facade.getAllDoctors();
        if (items.isEmpty()) System.out.println("No doctors found.");
        else items.forEach(System.out::println);
    }

    private static void viewAllAppointmentsByStaff() { // Staff view
        System.out.println("\n--- All Appointments (Staff View) ---");
        List<Appointment> items = facade.getAllAppointments();
        if (items.isEmpty()) System.out.println("No appointments found.");
        else items.forEach(System.out::println);
    }
    
    private static void viewAppointmentsByPatient() {
        System.out.println("\n--- Appointments by Patient ---");
        System.out.print("Enter Patient ID: ");
        String patientId = scanner.nextLine();
        List<Appointment> appointments = facade.getAppointmentsForPatient(patientId);
        if (appointments.isEmpty()) {
            System.out.println("No appointments found for patient ID: " + patientId);
        } else {
            appointments.forEach(System.out::println);
        }
    }

    private static void viewAppointmentsByDoctorForStaff() { // Staff viewing any doctor's appointments
        System.out.println("\n--- Appointments by Doctor (Staff View) ---");
        System.out.print("Enter Doctor ID: ");
        String doctorId = scanner.nextLine();
        // Staff can view all statuses for any doctor
        List<Appointment> appointments = facade.getAppointmentsForDoctor(doctorId, null); // null for status means all
        if (appointments.isEmpty()) {
            System.out.println("No appointments found for doctor ID: " + doctorId);
        } else {
            appointments.forEach(System.out::println);
        }
    }
}