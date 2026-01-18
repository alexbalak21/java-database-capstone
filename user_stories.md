# User Stories — Smart Clinic Management System

# User Story Template:

**Title:**  
_As a [user role], I want [feature/goal], so that [reason]._

**Acceptance Criteria:**  
1. [Criteria 1]  
2. [Criteria 2]  
3. [Criteria 3]  

**Priority:** [High/Medium/Low]  
**Story Points:** [Estimated Effort in Points]  

**Notes:**  
- [Additional information or edge cases]

---
<br>
<br>
<br>
<br>
<br>
<br>

# Admin User Stories

## 1. Admin Login
**Title:**  
_As an admin, I want to log into the portal with my username and password, so that I can manage the platform securely._

**Acceptance Criteria:**  
1. Admin can enter username and password.  
2. System validates credentials.  
3. Admin is redirected to the admin dashboard.  

**Priority:** High  
**Story Points:** 3  
**Notes:**  
- Incorrect credentials should show an error message.


## 2. Admin Logout
**Title:**  
_As an admin, I want to log out of the portal, so that I can protect system access._

**Acceptance Criteria:**  
1. Logout button is visible on all admin pages.  
2. Session/JWT is invalidated.  
3. Admin is redirected to login page.  

**Priority:** High  
**Story Points:** 2  
**Notes:**  
- Ensure token cannot be reused.


## 3. Add Doctor
**Title:**  
_As an admin, I want to add doctors to the portal, so that they can access the system and manage appointments._

**Acceptance Criteria:**  
1. Admin can fill out doctor details.  
2. System validates required fields.  
3. Doctor account is created and stored in the database.  

**Priority:** High  
**Story Points:** 5  
**Notes:**  
- Email must be unique.


## 4. Delete Doctor Profile
**Title:**  
_As an admin, I want to delete a doctor’s profile, so that I can remove inactive or incorrect accounts._

**Acceptance Criteria:**  
1. Admin can select a doctor from the list.  
2. System asks for confirmation.  
3. Doctor is removed from the database.  

**Priority:** Medium  
**Story Points:** 3  
**Notes:**  
- Consider soft delete vs hard delete.


## 5. Run Stored Procedure for Monthly Appointments
**Title:**  
_As an admin, I want to run a stored procedure in MySQL CLI to get the number of appointments per month, so that I can track usage statistics._

**Acceptance Criteria:**  
1. Stored procedure exists in MySQL.  
2. Admin can execute it manually.  
3. Procedure returns monthly appointment counts.  

**Priority:** Medium  
**Story Points:** 2  
**Notes:**  
- Procedure may later be automated.


# Exercise 3: Patient User Stories

## 1. View Doctors Without Logging In
**Title:**  
_As a patient, I want to view a list of doctors without logging in, so that I can explore options before registering._

**Acceptance Criteria:**  
1. Doctors list is publicly accessible.  
2. Shows specialization and availability.  
3. No personal doctor data is exposed.  

**Priority:** High  
**Story Points:** 3  
**Notes:**  
- Cache results for performance.


## 2. Patient Signup
**Title:**  
_As a patient, I want to sign up using my email and password, so that I can book appointments._

**Acceptance Criteria:**  
1. Patient can enter email, password, and details.  
2. System validates email format and uniqueness.  
3. Account is created and stored.  

**Priority:** High  
**Story Points:** 5  
**Notes:**  
- Password must be hashed.


## 3. Patient Login
**Title:**  
_As a patient, I want to log into the portal, so that I can manage my bookings._

**Acceptance Criteria:**  
1. Patient enters credentials.  
2. System validates and returns JWT.  
3. Patient is redirected to dashboard.  

**Priority:** High  
**Story Points:** 3  
**Notes:**  
- Show error for invalid login.


## 4. Patient Logout
**Title:**  
_As a patient, I want to log out of the portal, so that I can secure my account._

**Acceptance Criteria:**  
1. Logout button is visible.  
2. JWT/session is invalidated.  
3. Redirect to login page.  

**Priority:** Medium  
**Story Points:** 2  
**Notes:**  
- Token must not be reusable.


## 5. Book Appointment
**Title:**  
_As a patient, I want to book an hour-long appointment with a doctor, so that I can receive medical consultation._

**Acceptance Criteria:**  
1. Patient selects doctor and time slot.  
2. System checks availability.  
3. Appointment is saved and confirmed.  

**Priority:** High  
**Story Points:** 5  
**Notes:**  
- Prevent double-booking.


## 6. View Upcoming Appointments
**Title:**  
_As a patient, I want to view my upcoming appointments, so that I can prepare accordingly._

**Acceptance Criteria:**  
1. Patient sees a list of future appointments.  
2. Shows doctor name, date, and time.  
3. Only the patient’s own appointments are shown.  

**Priority:** Medium  
**Story Points:** 3  
**Notes:**  
- Sort by date.


# Exercise 4: Doctor User Stories

## 1. Doctor Login
**Title:**  
_As a doctor, I want to log into the portal, so that I can manage my appointments._

**Acceptance Criteria:**  
1. Doctor enters credentials.  
2. System validates and returns JWT.  
3. Redirect to doctor dashboard.  

**Priority:** High  
**Story Points:** 3  
**Notes:**  
- Use role-based access.


## 2. Doctor Logout
**Title:**  
_As a doctor, I want to log out of the portal, so that I can protect my data._

**Acceptance Criteria:**  
1. Logout button visible.  
2. JWT/session invalidated.  
3. Redirect to login page.  

**Priority:** Medium  
**Story Points:** 2  
**Notes:**  
- Token invalidation required.


## 3. View Appointment Calendar
**Title:**  
_As a doctor, I want to view my appointment calendar, so that I can stay organized._

**Acceptance Criteria:**  
1. Calendar shows all upcoming appointments.  
2. Shows patient name and time.  
3. Only doctor’s own appointments are shown.  

**Priority:** High  
**Story Points:** 5  
**Notes:**  
- Calendar UI optional for MVP.


## 4. Mark Unavailability
**Title:**  
_As a doctor, I want to mark my unavailability, so that patients only see available slots._

**Acceptance Criteria:**  
1. Doctor selects unavailable dates/times.  
2. System blocks those slots.  
3. Patients cannot book during blocked times.  

**Priority:** Medium  
**Story Points:** 5  
**Notes:**  
- Prevent conflicts with existing appointments.


## 5. Update Profile
**Title:**  
_As a doctor, I want to update my profile with specialization and contact information, so that patients have up-to-date information._

**Acceptance Criteria:**  
1. Doctor can edit profile fields.  
2. System validates required fields.  
3. Updated data is saved.  

**Priority:** Medium  
**Story Points:** 3  
**Notes:**  
- Consider profile picture later.


## 6. View Patient Details
**Title:**  
_As a doctor, I want to view patient details for upcoming appointments, so that I can be prepared._

**Acceptance Criteria:**  
1. Doctor can open appointment details.  
2. Patient information is displayed.  
3. Only authorized doctors can view patient data.  

**Priority:** High  
**Story Points:** 5  
**Notes:**  
- Must follow privacy rules.
