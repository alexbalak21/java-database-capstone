// index.js - Login Service Layer
import { openModal } from "../components/modals.js";
import { API_BASE_URL } from "../config/config.js";

// Define constants for the admin and doctor login API endpoints using the base URL
const ADMIN_API = API_BASE_URL + '/admin';
const DOCTOR_API = API_BASE_URL + '/doctor/login';

// Use the window.onload event to ensure DOM elements are available after page load
window.onload = function() {
  // Select the "adminLogin" and "doctorLogin" buttons using getElementById
  const adminBtn = document.getElementById("adminLogin");
  const doctorBtn = document.getElementById("doctorLogin");
  
  // If the admin login button exists, add a click event listener
  if (adminBtn) {
    adminBtn.addEventListener("click", () => {
      // Call openModal('adminLogin') to show the admin login modal
      openModal('adminLogin');
    });
  }
  
  // If the doctor login button exists, add a click event listener
  if (doctorBtn) {
    doctorBtn.addEventListener("click", () => {
      // Call openModal('doctorLogin') to show the doctor login modal
      openModal('doctorLogin');
    });
  }
};

/**
 * Define a function named adminLoginHandler on the global window object
 * This function will be triggered when the admin submits their login credentials
 */
window.adminLoginHandler = async function() {
  try {
    // Step 1: Get the entered username and password from the input fields
    const username = document.getElementById("username").value.trim();
    const password = document.getElementById("password").value.trim();
    
    // Validate inputs
    if (!username || !password) {
      alert("Please enter both username and password.");
      return;
    }
    
    // Step 2: Create an admin object with these credentials
    const admin = {
      username,
      password
    };
    
    // Step 3: Use fetch() to send a POST request to the ADMIN_API endpoint
    const response = await fetch(ADMIN_API, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(admin)
    });
    
    // Step 4: If the response is successful
    if (response.ok) {
      // Parse the JSON response to get the token
      const result = await response.json();
      
      // Store the token in localStorage
      localStorage.setItem("token", result.token);
      
      // Call selectRole('admin') to proceed with admin-specific behavior
      selectRole('admin');
    } else {
      // Step 5: If login fails or credentials are invalid, show an alert
      alert("❌ Invalid admin credentials!");
    }
  } catch (error) {
    // Step 6: Handle network or server errors
    console.error("Admin login error:", error);
    alert("❌ An error occurred during login. Please try again.");
  }
};

/**
 * Define a function named doctorLoginHandler on the global window object
 * This function will be triggered when a doctor submits their login credentials
 */
window.doctorLoginHandler = async function() {
  try {
    // Step 1: Get the entered email and password from the input fields
    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value.trim();
    
    // Validate inputs
    if (!email || !password) {
      alert("Please enter both email and password.");
      return;
    }
    
    // Step 2: Create a doctor object with these credentials
    const doctor = {
      email,
      password
    };
    
    // Step 3: Use fetch() to send a POST request to the DOCTOR_API endpoint
    const response = await fetch(DOCTOR_API, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(doctor)
    });
    
    // Step 4: If login is successful
    if (response.ok) {
      // Parse the JSON response to get the token
      const result = await response.json();
      
      // Store the token in localStorage
      localStorage.setItem("token", result.token);
      
      // Call selectRole('doctor') to proceed with doctor-specific behavior
      selectRole('doctor');
    } else {
      // Step 5: If login fails, show an alert for invalid credentials
      alert("❌ Invalid doctor credentials!");
    }
  } catch (error) {
    // Step 6: Handle errors gracefully
    console.error("Doctor login error:", error);
    alert("❌ An error occurred during login. Please try again.");
  }
};
