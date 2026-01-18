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
