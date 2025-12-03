package com.hospital.service;

import com.hospital.model.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    private static FileManager instance;

    private static final String DATA_DIR = "data/";
    private static final String PATIENTS_FILE = DATA_DIR + "patients.txt";
    private static final String DOCTORS_FILE = DATA_DIR + "doctors.txt";
    private static final String APPOINTMENTS_FILE = DATA_DIR + "appointments.txt";
    private static final String ACTIVITY_LOG_FILE = DATA_DIR + "activity_log.txt";
    private static final String USERS_FILE = DATA_DIR + "users.txt";

    private FileManager() {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
            ensureFileExists(PATIENTS_FILE);
            ensureFileExists(DOCTORS_FILE);
            ensureFileExists(APPOINTMENTS_FILE);
            ensureFileExists(ACTIVITY_LOG_FILE);
            ensureFileExists(USERS_FILE);
            initializeDefaultUsers();
        } catch (IOException e) {
            System.err.println("CRITICAL Error initializing FileManager: " + e.getMessage());
            // Consider exiting or handling more gracefully if core files can't be set up.
        }
    }

    private void ensureFileExists(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            if (file.createNewFile()) {
                 System.out.println("INFO: Created file: " + filePath);
            } else {
                 System.err.println("ERROR: Could not create file: " + filePath);
            }
        }
    }

    private void initializeDefaultUsers() {
        File usersFile = new File(USERS_FILE);
        if (usersFile.length() == 0) { // Check if file is empty
            System.out.println("INFO: No users found in " + USERS_FILE + ". Creating default users.");
            List<User> defaultUsers = new ArrayList<>();
            defaultUsers.add(new User("staff", "staff123", Role.STAFF));
            defaultUsers.add(new User("doctor1", "doc123", Role.DOCTOR, "DOC-SAMPLE1")); // For testing
            defaultUsers.add(new User("doctor2", "doc456", Role.DOCTOR, "DOC-SAMPLE2")); // For testing
            saveUsers(defaultUsers); // This uses the saveData method
            System.out.println("INFO: Default users (staff/staff123, doctor1/doc123 [DOC-SAMPLE1], doctor2/doc456 [DOC-SAMPLE2]) created.");
            System.out.println("IMPORTANT: For doctor login to manage appointments, ensure a Doctor with matching entity ID exists (e.g., DOC-SAMPLE1).");
        }
    }


    public static synchronized FileManager getInstance() {
        if (instance == null) {
            instance = new FileManager();
        }
        return instance;
    }

    private <T> void saveData(String filePath, List<T> dataList, java.util.function.Function<T, String> toStringFunction) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) { // Overwrites existing file
            for (T item : dataList) {
                writer.write(toStringFunction.apply(item));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing to file " + filePath + ": " + e.getMessage());
        }
    }

    private <T> List<T> loadData(String filePath, java.util.function.Function<String, T> fromStringFunction) {
        List<T> dataList = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists() || !file.canRead()) {
            System.err.println("Error: File " + filePath + " does not exist or cannot be read.");
            return dataList; // Return empty list
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    T item = fromStringFunction.apply(line);
                    if (item != null) {
                        dataList.add(item);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading from file " + filePath + ": " + e.getMessage());
        }
        return dataList;
    }

    public void savePatients(List<Patient> patients) { saveData(PATIENTS_FILE, patients, Patient::toFileString); }
    public List<Patient> loadPatients() { return loadData(PATIENTS_FILE, Patient::fromFileString); }
    public void saveDoctors(List<Doctor> doctors) { saveData(DOCTORS_FILE, doctors, Doctor::toFileString); }
    public List<Doctor> loadDoctors() { return loadData(DOCTORS_FILE, Doctor::fromFileString); }
    public void saveAppointments(List<Appointment> appointments) { saveData(APPOINTMENTS_FILE, appointments, Appointment::toFileString); }
    public List<Appointment> loadAppointments() { return loadData(APPOINTMENTS_FILE, Appointment::fromFileString); }
    public void saveUsers(List<User> users) { saveData(USERS_FILE, users, User::toFileString); }
    public List<User> loadUsers() { return loadData(USERS_FILE, User::fromFileString); }

    public void logActivity(String activity) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ACTIVITY_LOG_FILE, true))) { // true for append mode
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            writer.write(timestamp + " - " + activity);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error writing to activity log: " + e.getMessage());
        }
    }
}