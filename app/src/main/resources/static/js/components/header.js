// header.js - Reusable Header Component

/**
 * Renders the header based on user role and login state
 * Dynamically changes navigation and action buttons
 */
function renderHeader() {
  const headerDiv = document.getElementById("header");
  
  // Check if on homepage - clear session data
  if (window.location.pathname.endsWith("/")) {
    localStorage.removeItem("userRole");
    localStorage.removeItem("token");
    headerDiv.innerHTML = `
      <header class="header">
        <div class="logo-section">
          <img src="./assets/images/logo/Logo.png" alt="Hospital CRM Logo" class="logo-img">
          <span class="logo-title">Hospital CMS</span>
        </div>
      </header>`;
    return;
  }
  
  // Get user role and token from localStorage
  const role = localStorage.getItem("userRole");
  const token = localStorage.getItem("token");
  
  // Initialize header content with logo
  let headerContent = `
    <header class="header">
      <div class="logo-section">
        <img src="../assets/images/logo/Logo.png" alt="Hospital CRM Logo" class="logo-img">
        <span class="logo-title">Hospital CMS</span>
      </div>
      <nav>`;
  
  // Check for invalid session - logged roles without token
  if ((role === "loggedPatient" || role === "admin" || role === "doctor") && !token) {
    localStorage.removeItem("userRole");
    alert("Session expired or invalid login. Please log in again.");
    window.location.href = "/";
    return;
  }
  
  // Add role-specific navigation elements
  if (role === "admin") {
    headerContent += `
      <button id="addDocBtn" class="adminBtn" onclick="openModal('addDoctor')">Add Doctor</button>
      <a href="#" onclick="logout()">Logout</a>`;
  } else if (role === "doctor") {
    headerContent += `
      <button class="adminBtn" onclick="selectRole('doctor')">Home</button>
      <a href="#" onclick="logout()">Logout</a>`;
  } else if (role === "patient") {
    headerContent += `
      <button id="patientLogin" class="adminBtn">Login</button>
      <button id="patientSignup" class="adminBtn">Sign Up</button>`;
  } else if (role === "loggedPatient") {
    headerContent += `
      <button id="home" class="adminBtn" onclick="window.location.href='/pages/loggedPatientDashboard.html'">Home</button>
      <button id="patientAppointments" class="adminBtn" onclick="window.location.href='/pages/patientAppointments.html'">Appointments</button>
      <a href="#" onclick="logoutPatient()">Logout</a>`;
  }
  
  // Close navigation and header
  headerContent += `
      </nav>
    </header>`;
  
  // Inject header content into DOM
  headerDiv.innerHTML = headerContent;
  
  // Attach event listeners to dynamically created buttons
  attachHeaderButtonListeners();
}

/**
 * Attaches event listeners to header buttons
 * Handles login/signup modals for patients
 */
function attachHeaderButtonListeners() {
  // Patient Login button
  const loginBtn = document.getElementById("patientLogin");
  if (loginBtn) {
    loginBtn.addEventListener("click", () => {
      openModal("patientLogin");
    });
  }
  
  // Patient Signup button
  const signupBtn = document.getElementById("patientSignup");
  if (signupBtn) {
    signupBtn.addEventListener("click", () => {
      openModal("patientSignup");
    });
  }
  
  // Add Doctor button (if admin role)
  const addDocBtn = document.getElementById("addDocBtn");
  if (addDocBtn) {
    addDocBtn.addEventListener("click", () => {
      openModal("addDoctor");
    });
  }
}

/**
 * Logout function for admin and doctor roles
 * Clears all session data and redirects to homepage
 */
function logout() {
  localStorage.removeItem("token");
  localStorage.removeItem("userRole");
  alert("You have been logged out successfully.");
  window.location.href = "/";
}

/**
 * Logout function specifically for patients
 * Keeps role as "patient" (not logged in) and redirects to patient dashboard
 */
function logoutPatient() {
  localStorage.removeItem("token");
  localStorage.setItem("userRole", "patient");
  alert("You have been logged out successfully.");
  window.location.href = "/pages/patientDashboard.html";
}

// Render header when page loads
renderHeader();

