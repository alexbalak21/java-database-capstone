## Patient User Stories

### 1. View Doctors Without Logging In
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


### 2. Patient Signup
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


### 3. Patient Login
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


### 4. Patient Logout
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


### 5. Book Appointment
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


### 6. View Upcoming Appointments
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
