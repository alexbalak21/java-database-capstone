# Schema Design — Smart Clinic Management System

---

## MySQL Database Design

Structured, relational, and validated data is stored in MySQL.  
These tables represent the core operational data of the clinic.

Below are the required tables with columns, data types, constraints, and relationships.

---

### Table: patients
- **patient_id**: INT, Primary Key, AUTO_INCREMENT  
- **full_name**: VARCHAR(100), NOT NULL  
- **email**: VARCHAR(100), UNIQUE, NOT NULL  
- **phone**: VARCHAR(20), NULL  
- **date_of_birth**: DATE, NULL  
- **gender**: ENUM('Male', 'Female', 'Other'), NULL  
- **created_at**: TIMESTAMP, DEFAULT CURRENT_TIMESTAMP  

**Notes:**  
- Email is UNIQUE to prevent duplicate accounts.  
- Phone format validation will be handled in the application layer.  
- If a patient is deleted, appointments should NOT be deleted automatically (medical history must be retained).

---

### Table: doctors
- **doctor_id**: INT, Primary Key, AUTO_INCREMENT  
- **full_name**: VARCHAR(100), NOT NULL  
- **specialization**: VARCHAR(100), NOT NULL  
- **email**: VARCHAR(100), UNIQUE, NOT NULL  
- **phone**: VARCHAR(20), NULL  
- **created_at**: TIMESTAMP, DEFAULT CURRENT_TIMESTAMP  

**Notes:**  
- Doctors should not have overlapping appointments — enforced via application logic.  
- Specialization helps patients filter doctors.

---

### Table: appointments
- **appointment_id**: INT, Primary Key, AUTO_INCREMENT  
- **doctor_id**: INT, Foreign Key → doctors(doctor_id), NOT NULL  
- **patient_id**: INT, Foreign Key → patients(patient_id), NOT NULL  
- **appointment_time**: DATETIME, NOT NULL  
- **status**: INT, DEFAULT 0  
  - 0 = Scheduled  
  - 1 = Completed  
  - 2 = Cancelled  
- **notes**: TEXT, NULL  

**Notes:**  
- Appointment history should be retained forever for medical/legal reasons.  
- Preventing double-booking is handled in backend logic.  
- A prescription may be tied to an appointment, but not required.

---

### Table: admin
- **admin_id**: INT, Primary Key, AUTO_INCREMENT  
- **username**: VARCHAR(50), UNIQUE, NOT NULL  
- **password_hash**: VARCHAR(255), NOT NULL  
- **role**: ENUM('SuperAdmin', 'Manager'), DEFAULT 'Manager'  
- **created_at**: TIMESTAMP, DEFAULT CURRENT_TIMESTAMP  

**Notes:**  
- Passwords are stored as hashes.  
- Admins manage doctors, patients, and system settings.

---

### Table: clinic_locations (optional but useful)
- **location_id**: INT, Primary Key, AUTO_INCREMENT  
- **name**: VARCHAR(100), NOT NULL  
- **address**: VARCHAR(255), NOT NULL  
- **phone**: VARCHAR(20), NULL  

**Notes:**  
- Useful for multi-branch clinics.  
- Doctors may later be assigned to specific locations.

---

### Table: payments (optional)
- **payment_id**: INT, Primary Key, AUTO_INCREMENT  
- **appointment_id**: INT, Foreign Key → appointments(appointment_id), NOT NULL  
- **amount**: DECIMAL(10,2), NOT NULL  
- **payment_method**: ENUM('Cash', 'Card', 'Insurance'), NOT NULL  
- **payment_date**: DATETIME, DEFAULT CURRENT_TIMESTAMP  

**Notes:**  
- Payments are tied to appointments.  
- Insurance claims may be added later.

---

## MongoDB Collection Design

Some data is unstructured, optional, or varies heavily between patients.  
MongoDB is ideal for this flexible information.

We will store **prescriptions** in MongoDB because:

- Each prescription may contain multiple medications  
- Doctors may add free-form notes  
- Metadata varies per case  
- JSON structure allows nested objects and arrays  

---

### Collection: prescriptions

```json
{
  "_id": "ObjectId('64fabc1234567890abcd1234')",
  "appointmentId": 51,
  "patientId": 101,
  "doctorId": 202,
  "issuedAt": "2026-01-18T10:30:00Z",

  "medications": [
    {
      "name": "Amoxicillin",
      "dosage": "500mg",
      "frequency": "3 times a day",
      "duration": "7 days",
      "notes": "Take after meals"
    },
    {
      "name": "Ibuprofen",
      "dosage": "200mg",
      "frequency": "as needed",
      "duration": "5 days"
    }
  ],

  "doctorNotes": "Patient should avoid alcohol during medication.",
  "tags": ["infection", "antibiotics"],

  "pharmacy": {
    "name": "Walgreens SF",
    "location": "Market Street",
    "contact": "+1-555-123-4567"
  },

  "metadata": {
    "createdBy": "Dr. Emily Carter",
    "lastUpdated": "2026-01-18T11:00:00Z",
    "version": 2
  }
}
```

---

### Why MongoDB Works Well Here
- **Nested medications array** allows unlimited drugs per prescription.  
- **Flexible structure** supports optional fields like pharmacy info or tags.  
- **Metadata object** helps track updates without altering SQL schema.  
- **Document-oriented storage** is ideal for doctor notes and variable content.

---
