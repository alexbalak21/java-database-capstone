# Frontend Implementation

## Overview
This document lists all HTML and CSS files in the project, providing a complete reference of the frontend structure.

---

## HTML Files

### Static Pages
Located in `app/src/main/resources/static/`

1. [index.html](app/src/main/resources/static/index.html)
   - Main landing/login page

### Static Pages - Pages Directory
Located in `app/src/main/resources/static/pages/`

2. [addPrescription.html](app/src/main/resources/static/pages/addPrescription.html)
   - Page for adding prescriptions

3. [loggedPatientDashboard.html](app/src/main/resources/static/pages/loggedPatientDashboard.html)
   - Dashboard for logged-in patients

4. [patientAppointments.html](app/src/main/resources/static/pages/patientAppointments.html)
   - Patient appointments management page

5. [patientDashboard.html](app/src/main/resources/static/pages/patientDashboard.html)
   - Patient dashboard page

6. [patientRecord.html](app/src/main/resources/static/pages/patientRecord.html)
   - Patient medical records page

7. [updateAppointment.html](app/src/main/resources/static/pages/updateAppointment.html)
   - Page for updating appointments

### Template Pages
Located in `app/src/main/resources/templates/`

8. [adminDashboard.html](app/src/main/resources/templates/admin/adminDashboard.html)
   - Admin dashboard (Thymeleaf template)

9. [doctorDashboard.html](app/src/main/resources/templates/doctor/doctorDashboard.html)
   - Doctor dashboard (Thymeleaf template)

---

## CSS Files

### Stylesheets
Located in `app/src/main/resources/static/assets/css/`

1. [addPrescription.css](app/src/main/resources/static/assets/css/addPrescription.css)
   - Styles for add prescription page

2. [adminDashboard.css](app/src/main/resources/static/assets/css/adminDashboard.css)
   - Styles for admin dashboard

3. [doctorDashboard.css](app/src/main/resources/static/assets/css/doctorDashboard.css)
   - Styles for doctor dashboard

4. [index.css](app/src/main/resources/static/assets/css/index.css)
   - Styles for main landing/login page

5. [patientDashboard.css](app/src/main/resources/static/assets/css/patientDashboard.css)
   - Styles for patient dashboard

6. [style.css](app/src/main/resources/static/assets/css/style.css)
   - Global/shared styles

7. [updateAppointment.css](app/src/main/resources/static/assets/css/updateAppointment.css)
   - Styles for update appointment page

---

## Summary

- **Total HTML Files**: 9
  - Static pages: 7
  - Template pages: 2

- **Total CSS Files**: 7

## Directory Structure

```
app/src/main/resources/
├── static/
│   ├── index.html
│   ├── assets/
│   │   └── css/
│   │       ├── addPrescription.css
│   │       ├── adminDashboard.css
│   │       ├── doctorDashboard.css
│   │       ├── index.css
│   │       ├── patientDashboard.css
│   │       ├── style.css
│   │       └── updateAppointment.css
│   └── pages/
│       ├── addPrescription.html
│       ├── loggedPatientDashboard.html
│       ├── patientAppointments.html
│       ├── patientDashboard.html
│       ├── patientRecord.html
│       └── updateAppointment.html
└── templates/
    ├── admin/
    │   └── adminDashboard.html
    └── doctor/
        └── doctorDashboard.html
```
