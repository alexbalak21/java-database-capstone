// doctorDashboard.js - Doctor Dashboard Logic
import { getAllAppointments } from "./services/appointmentRecordService.js";
import { createPatientRow } from "./components/patientRows.js";

// Get the table body where patient rows will be added
const tableBody = document.getElementById("patientTableBody");

// Initialize selectedDate with today's date in 'YYYY-MM-DD' format
let selectedDate = new Date().toISOString().split("T")[0];

// Get the saved token from localStorage (used for authenticated API calls)
const token = localStorage.getItem("token");

// Initialize patientName to null (used for filtering by name)
let patientName = null;

// Add an 'input' event listener to the search bar
document.getElementById("searchBar").addEventListener("input", (e) => {
  // On each keystroke, trim and check the input value
  const searchValue = e.target.value.trim();
  
  // If not empty, use it as the patientName for filtering
  if (searchValue.length > 0) {
    patientName = searchValue;
  } else {
    // Else, reset patientName to null (as expected by backend)
    patientName = null;
  }
  
  // Reload the appointments list with the updated filter
  loadAppointments();
});

// Add a click listener to the "Today" button
document.getElementById("todayAppointments").addEventListener("click", () => {
  // Set selectedDate to today's date
  selectedDate = new Date().toISOString().split("T")[0];
  
  // Update the date picker UI to match
  document.getElementById("dateFilter").value = selectedDate;
  
  // Reload the appointments for today
  loadAppointments();
});

// Add a change event listener to the date picker
document.getElementById("dateFilter").addEventListener("change", (e) => {
  // When the date changes, update selectedDate with the new value
  selectedDate = e.target.value;
  
  // Reload the appointments for that specific date
  loadAppointments();
});

/**
 * Function: loadAppointments
 * Purpose: Fetch and display appointments based on selected date and optional patient name
 */
async function loadAppointments() {
  try {
    // Step 1: Call getAllAppointments with selectedDate, patientName, and token
    const appointments = await getAllAppointments(selectedDate, patientName, token);
    
    // Step 2: Clear the table body content before rendering new rows
    tableBody.innerHTML = "";
    
    // Step 3: If no appointments are returned, display a message row
    if (!appointments || appointments.length === 0) {
      tableBody.innerHTML = `<tr><td colspan="5" class="noPatientRecord">No Appointments found for ${selectedDate}.</td></tr>`;
      return;
    }
    
    // Step 4: If appointments exist, loop through each appointment
    appointments.forEach(appointment => {
      // Construct a 'patient' object with id, name, phone, and email
      const patient = {
        id: appointment.patientId,
        name: appointment.patientName,
        phone: appointment.patientPhone || "N/A",
        email: appointment.patientEmail || "N/A"
      };
      
      // Call createPatientRow to generate a table row for the appointment
      const row = createPatientRow(patient, appointment.id, appointment.doctorId);
      
      // Append each row to the table body
      tableBody.appendChild(row);
    });
  } catch (error) {
    // Step 5: Catch and handle any errors during fetch
    console.error("Error loading appointments:", error);
    
    // Show a message row: "Error loading appointments. Try again later."
    tableBody.innerHTML = `<tr><td colspan="5" class="noPatientRecord">Error loading appointments. Try again later.</td></tr>`;
  }
}

// When the page is fully loaded (DOMContentLoaded)
document.addEventListener("DOMContentLoaded", () => {
  // Call renderContent() (assumes it sets up the UI layout)
  if (typeof renderContent === 'function') {
    renderContent();
  }
  
  // Call loadAppointments() to display today's appointments by default
  loadAppointments();
});
