// adminDashboard.js - Admin Dashboard Logic
import { getDoctors, saveDoctor, filterDoctors } from './services/doctorServices.js';
import { createDoctorCard } from './components/doctorCard.js';

// Attach click listener to "Add Doctor" button (handled by header.js)
// The header.js calls openModal('addDoctor') when clicked

// When DOM is loaded, load all doctor cards
document.addEventListener("DOMContentLoaded", () => {
  loadDoctorCards();
});

/**
 * Function: loadDoctorCards
 * Purpose: Fetch all doctors and display them as cards
 */
async function loadDoctorCards() {
  try {
    // Call getDoctors() from the service layer
    const doctors = await getDoctors();
    
    // Clear the current content area
    const contentDiv = document.getElementById("content");
    contentDiv.innerHTML = "";
    
    // For each doctor returned, create and append a doctor card
    doctors.forEach(doctor => {
      const card = createDoctorCard(doctor);
      contentDiv.appendChild(card);
    });
  } catch (error) {
    // Handle any fetch errors by logging them
    console.error("Failed to load doctors:", error);
    alert("❌ Failed to load doctors. Please try again.");
  }
}

// Attach 'input' and 'change' event listeners to search bar and filter dropdowns
document.getElementById("searchBar").addEventListener("input", filterDoctorsOnChange);
document.getElementById("filterTime").addEventListener("change", filterDoctorsOnChange);
document.getElementById("filterSpecialty").addEventListener("change", filterDoctorsOnChange);

/**
 * Function: filterDoctorsOnChange
 * Purpose: Filter doctors based on name, available time, and specialty
 */
async function filterDoctorsOnChange() {
  // Read values from the search bar and filters
  const searchBar = document.getElementById("searchBar").value.trim();
  const filterTime = document.getElementById("filterTime").value;
  const filterSpecialty = document.getElementById("filterSpecialty").value;
  
  // Normalize empty values to null
  const name = searchBar.length > 0 ? searchBar : null;
  const time = filterTime.length > 0 ? filterTime : null;
  const specialty = filterSpecialty.length > 0 ? filterSpecialty : null;
  
  try {
    // Call filterDoctors(name, time, specialty) from the service
    const response = await filterDoctors(name, time, specialty);
    const doctors = response.doctors;
    
    const contentDiv = document.getElementById("content");
    contentDiv.innerHTML = "";
    
    // If doctors are found, render them using createDoctorCard()
    if (doctors.length > 0) {
      doctors.forEach(doctor => {
        const card = createDoctorCard(doctor);
        contentDiv.appendChild(card);
      });
    } else {
      // If no doctors match the filter, show a message
      contentDiv.innerHTML = "<p>No doctors found with the given filters.</p>";
    }
  } catch (error) {
    // Catch and display any errors with an alert
    console.error("Failed to filter doctors:", error);
    alert("❌ An error occurred while filtering doctors.");
  }
}

/**
 * Function: renderDoctorCards
 * Purpose: A helper function to render a list of doctors passed to it
 */
function renderDoctorCards(doctors) {
  // Clear the content area
  const contentDiv = document.getElementById("content");
  contentDiv.innerHTML = "";
  
  // Loop through the doctors and append each card to the content area
  doctors.forEach(doctor => {
    const card = createDoctorCard(doctor);
    contentDiv.appendChild(card);
  });
}

/**
 * Function: adminAddDoctor
 * Purpose: Collect form data and add a new doctor to the system
 * This function is called from modals.js when the save button is clicked
 */
window.adminAddDoctor = async function() {
  // Collect input values from the modal form
  const name = document.getElementById("doctorName").value.trim();
  const email = document.getElementById("doctorEmail").value.trim();
  const phone = document.getElementById("doctorPhone").value.trim();
  const password = document.getElementById("doctorPassword").value.trim();
  const specialty = document.getElementById("specialization").value;
  
  // Get selected available times from checkboxes
  const availabilityCheckboxes = document.querySelectorAll('input[name="availability"]:checked');
  const availableTimes = Array.from(availabilityCheckboxes).map(cb => cb.value);
  
  // Validate form inputs
  if (!name || !email || !phone || !password || !specialty) {
    alert("Please fill in all required fields.");
    return;
  }
  
  if (availableTimes.length === 0) {
    alert("Please select at least one available time slot.");
    return;
  }
  
  // Retrieve the authentication token from localStorage
  const token = localStorage.getItem("token");
  
  // If no token is found, show an alert and stop execution
  if (!token) {
    alert("Session expired. Please log in again.");
    window.location.href = "/";
    return;
  }
  
  // Build a doctor object with the form values
  const doctor = {
    name,
    email,
    phone,
    password,
    specialty,
    availableTimes
  };
  
  try {
    // Call saveDoctor(doctor, token) from the service
    const result = await saveDoctor(doctor, token);
    
    // If save is successful
    if (result.success) {
      alert("✅ Doctor added successfully!");
      // Close the modal and reload the page
      document.getElementById("modal").style.display = "none";
      window.location.reload();
    } else {
      // If saving fails, show an error message
      alert(`❌ Failed to add doctor: ${result.message}`);
    }
  } catch (error) {
    console.error("Error adding doctor:", error);
    alert("❌ An error occurred while adding the doctor.");
  }
};
