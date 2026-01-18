# Schema Design — Smart Clinic Management System

## MySQL Database Design

### 1. `patients` Table
```sql
CREATE TABLE patients (
  patient_id INT AUTO_INCREMENT PRIMARY KEY,
  full_name VARCHAR(100) NOT NULL,
  email VARCHAR(100) UNIQUE NOT NULL,
  phone VARCHAR(20),
  date_of_birth DATE,
  gender ENUM('Male', 'Female', 'Other'),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 2. `doctors` Table
```sql
CREATE TABLE doctors (
  doctor_id INT AUTO_INCREMENT PRIMARY KEY,
  full_name VARCHAR(100) NOT NULL,
  specialization VARCHAR(100) NOT NULL,
  email VARCHAR(100) UNIQUE NOT NULL,
  phone VARCHAR(20),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 3. `appointments` Table
```sql
CREATE TABLE appointments (
  appointment_id INT AUTO_INCREMENT PRIMARY KEY,
  patient_id INT NOT NULL,
  doctor_id INT NOT NULL,
  appointment_date DATETIME NOT NULL,
  status ENUM('Scheduled', 'Completed', 'Cancelled') DEFAULT 'Scheduled',
  notes TEXT,
  FOREIGN KEY (patient_id) REFERENCES patients(patient_id),
  FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id)
);
```

### 4. `admin` Table
```sql
CREATE TABLE admin (
  admin_id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) UNIQUE NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  role ENUM('SuperAdmin', 'Manager') DEFAULT 'Manager',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

## MongoDB Collection Design

### `prescriptions` Collection
```json
{
  "prescription_id": "presc_001",
  "patient_id": 101,
  "doctor_id": 202,
  "date": "2026-01-18T10:30:00Z",
  "medications": [
    {
      "name": "Amoxicillin",
      "dosage": "500mg",
      "frequency": "3 times a day",
      "duration": "7 days"
    },
    {
      "name": "Ibuprofen",
      "dosage": "200mg",
      "frequency": "as needed",
      "duration": "5 days"
    }
  ],
  "notes": "Patient should avoid alcohol during medication."
}
```

### Design Justification
- **Nested `medications` array** allows flexible storage of multiple drugs per prescription.
- **ISO date format** ensures compatibility across systems and time zones.
- **Use of `patient_id` and `doctor_id`** maintains relational consistency with MySQL.
- **Document structure** supports efficient retrieval and indexing by patient or doctor.
- **No rigid schema** allows future expansion (e.g., adding pharmacy info or refill history).

---

Let me know if you want to add indexes, ER diagrams, or integrate this with your Spring Boot entities.