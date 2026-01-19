## Doctor User Stories

### 1. Doctor Login
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


### 2. Doctor Logout
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


### 3. View Appointment Calendar
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


### 4. Mark Unavailability
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


### 5. Update Profile
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


### 6. View Patient Details
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
