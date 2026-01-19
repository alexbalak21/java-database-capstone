// doctorCard.js - Reusable Doctor Card Component

// Import service functions (to be implemented in next lab)
import { deleteDoctor } from '../services/doctorServices.js';
import { getPatientData } from '../services/patientServices.js';

/**
 * Creates a reusable doctor card component
 * @param {Object} doctor - Doctor object with name, specialty, email, availability
 * @returns {HTMLElement} - DOM element representing the doctor card
 */
export function createDoctorCard(doctor) {
  // Create the main card container
  const card = document.createElement("div");
  card.classList.add("doctor-card");
  
  // Get current user role from localStorage
  const role = localStorage.getItem("userRole");
  
  // Create doctor info section
  const infoDiv = document.createElement("div");
  infoDiv.classList.add("doctor-info");
  
  // Create and add doctor name
  const name = document.createElement("h3");
  name.textContent = doctor.name;
  
  // Create and add specialization
  const specialization = document.createElement("p");
  specialization.innerHTML = `<strong>Specialty:</strong> ${doctor.specialty}`;
  
  // Create and add email
  const email = document.createElement("p");
  email.innerHTML = `<strong>Email:</strong> ${doctor.email}`;
  
  // Create and add availability
  const availability = document.createElement("p");
  if (doctor.availableTimes && doctor.availableTimes.length > 0) {
    availability.innerHTML = `<strong>Available Times:</strong> ${doctor.availableTimes.join(", ")}`;
  } else {
    availability.innerHTML = `<strong>Available Times:</strong> Not specified`;
  }
  
  // Append all info elements to info container
  infoDiv.appendChild(name);
  infoDiv.appendChild(specialization);
  infoDiv.appendChild(email);
  infoDiv.appendChild(availability);
  
  // Create actions container for buttons
  const actionsDiv = document.createElement("div");
  actionsDiv.classList.add("card-actions");
  
  // Add role-specific buttons
  if (role === "admin") {
    // === ADMIN ROLE: Show Delete Button ===
    const removeBtn = document.createElement("button");
    removeBtn.textContent = "Delete";
    removeBtn.classList.add("delete-btn");
    
    // Attach delete event handler
    removeBtn.addEventListener("click", async () => {
      const confirmDelete = confirm(`Are you sure you want to delete Dr. ${doctor.name}?`);
      
      if (confirmDelete) {
        try {
          // Get admin token
          const token = localStorage.getItem("token");
          
          if (!token) {
            alert("Session expired. Please log in again.");
            window.location.href = "/";
            return;
          }
          
          // Call delete API
          const result = await deleteDoctor(doctor.id, token);
          
          if (result.success) {
            alert(`Dr. ${doctor.name} has been deleted successfully.`);
            // Remove card from DOM
            card.remove();
          } else {
            alert(`Failed to delete doctor: ${result.message || 'Unknown error'}`);
          }
        } catch (error) {
          console.error("Error deleting doctor:", error);
          alert("An error occurred while deleting the doctor.");
        }
      }
    });
    
    actionsDiv.appendChild(removeBtn);
    
  } else if (role === "patient") {
    // === PATIENT (NOT LOGGED IN): Show Book Now with Login Prompt ===
    const bookNow = document.createElement("button");
    bookNow.textContent = "Book Now";
    bookNow.classList.add("book-now-btn");
    
    bookNow.addEventListener("click", () => {
      alert("Please log in to book an appointment.");
      // Optionally trigger login modal
      if (typeof openModal === 'function') {
        openModal('patientLogin');
      }
    });
    
    actionsDiv.appendChild(bookNow);
    
  } else if (role === "loggedPatient") {
    // === LOGGED-IN PATIENT: Show Book Now with Booking Functionality ===
    const bookNow = document.createElement("button");
    bookNow.textContent = "Book Now";
    bookNow.classList.add("book-now-btn");
    
    bookNow.addEventListener("click", async (e) => {
      try {
        // Get patient token
        const token = localStorage.getItem("token");
        
        if (!token) {
          alert("Session expired. Please log in again.");
          window.location.href = "/pages/patientDashboard.html";
          return;
        }
        
        // Fetch patient data
        const patientData = await getPatientData(token);
        
        if (patientData) {
          // Show booking overlay with doctor and patient info
          // This function will be imported from modals.js
          if (typeof showBookingOverlay === 'function') {
            showBookingOverlay(e, doctor, patientData);
          } else {
            console.error("showBookingOverlay function not found");
            alert("Booking system is not available at the moment.");
          }
        } else {
          alert("Could not retrieve patient information.");
        }
      } catch (error) {
        console.error("Error during booking:", error);
        alert("An error occurred while trying to book an appointment.");
      }
    });
    
    actionsDiv.appendChild(bookNow);
  }
  
  // Append info and actions to card
  card.appendChild(infoDiv);
  card.appendChild(actionsDiv);
  
  // Return the complete doctor card
  return card;
}
