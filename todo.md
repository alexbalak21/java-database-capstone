# Smart Clinic Management System — TODO

## Module 1 – Overview & Requirements

- [ ] **Write Architecture Overview**
  - [ ] Describe frontend, backend, security, databases
  - [ ] Add flow of data and control
  - [ ] Add sequence + component diagrams (Mermaid/ASCII)

- [ ] **Define Roles & User Stories**
  - [ ] Roles: Admin, Doctor, Patient
  - [ ] Create user stories for each role
  - [ ] Create GitHub issues for main stories

---

## Module 2 – Database Design & JPA Models

- [ ] **Design Relational Schema (MySQL)**
  - [ ] Tables: users, doctors, patients, appointments
  - [ ] Define relationships (FKs, 1–1, 1–many)
  - [ ] Add constraints (NOT NULL, unique, etc.)

- [ ] **Design Document Schema (MongoDB)**
  - [ ] Collection: prescriptions
  - [ ] Define document structure (medications, notes, etc.)

- [ ] **Create JPA Entities**
  - [ ] `User` with role enum (ADMIN/DOCTOR/PATIENT)
  - [ ] `Doctor`, `Patient`, `Appointment`
  - [ ] Add JPA mappings and validation annotations

- [ ] **Create MongoDB Document Classes**
  - [ ] `Prescription` document with patientId, doctorId, appointmentId

---

## Module 3 – Sample Data & Stored Procedures

- [ ] **Initialize Databases**
  - [ ] Create MySQL schema and tables
  - [ ] Create MongoDB database and collection

- [ ] **Insert Sample Data**
  - [ ] Admin, doctors, patients
  - [ ] Appointments
  - [ ] Prescriptions in MongoDB

- [ ] **Create Stored Procedures (MySQL)**
  - [ ] Get appointments by doctor
  - [ ] Get appointments by patient
  - [ ] Any reporting query required by course

---

## Module 4 – Backend Auth, Frontend & MVC

- [ ] **Implement Spring Security + JWT**
  - [ ] Configure `SecurityConfig`
  - [ ] Create `UserDetailsService` and password encoder
  - [ ] Implement `/api/auth/login` endpoint
  - [ ] Implement JWT utility (generate/validate)
  - [ ] Implement JWT filter and register in filter chain

- [ ] **Test Auth via Postman**
  - [ ] Login with sample user
  - [ ] Receive JWT
  - [ ] Call protected endpoint with `Authorization: Bearer <token>`

- [ ] **Create Frontend Pages**
  - [ ] `login.html`
  - [ ] `admin-dashboard.html`
  - [ ] `doctor-dashboard.html`
  - [ ] `patient-dashboard.html`

- [ ] **Wire MVC + REST**
  - [ ] MVC controllers for dashboards
  - [ ] REST controllers for doctors, patients, appointments, prescriptions
  - [ ] Use JWT in frontend requests (JS fetch)

---

## Module 5 – REST, Docker & CI

- [ ] **Finalize REST Endpoints**
  - [ ] CRUD for doctors, patients, appointments
  - [ ] Endpoints for prescriptions (MongoDB)
  - [ ] Apply role-based access with annotations

- [ ] **Containerize Application**
  - [ ] Dockerfile for Spring Boot backend
  - [ ] Dockerfile or static server for frontend
  - [ ] `docker-compose.yml` for backend, frontend, MySQL, MongoDB

- [ ] **Set Up GitHub Actions (CI)**
  - [ ] Workflow to build and test (Maven)
  - [ ] Optional: run basic security/lint checks

---

## Module 6 – Final Submission

- [ ] **Clean Up & Document**
  - [ ] Write `README.md` with setup and usage
  - [ ] Add architecture and diagrams
  - [ ] Link all labs and modules

- [ ] **Verify Requirements**
  - [ ] JWT auth + role-based access
  - [ ] Functional frontend + backend
  - [ ] MySQL + MongoDB integration
  - [ ] Stored procedures used
  - [ ] Docker + CI working

- [ ] **Push Final Code to GitHub**
  - [ ] Ensure repo is clean and builds
  - [ ] Submit GitHub link as final deliverable
