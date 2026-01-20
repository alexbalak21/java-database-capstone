// doctorDashboard.js - Doctor Dashboard Logic with Enhanced Filtering & Pagination
import { getAllAppointments } from "./services/appointmentRecordService.js";
import { createPatientRow } from "./components/patientRows.js";

// Get the table body where patient rows will be added
const tableBody = document.getElementById("patientTableBody");

// Pagination configuration
const itemsPerPage = 10;
let currentPage = 1;
let allAppointments = [];

// Initialize selectedDate with today's date in 'YYYY-MM-DD' format
let selectedDate = new Date().toISOString().split("T")[0];

// Get the saved token from localStorage (used for authenticated API calls)
const token = localStorage.getItem("token");

// Initialize filters
let patientName = null;
let statusFilter = "";

/**
 * Display error message to user
 * @param {string} message - Error message to display
 */
function showError(message) {
  const errorContainer = document.getElementById("errorContainer");
  const errorText = document.getElementById("errorText");
  errorText.textContent = message;
  errorContainer.style.display = "block";
  
  // Auto-hide error after 5 seconds
  setTimeout(() => {
    errorContainer.style.display = "none";
  }, 5000);
}

/**
 * Show loading indicator
 */
function showLoading(show = true) {
  const loadingIndicator = document.getElementById("loadingIndicator");
  if (show) {
    loadingIndicator.style.display = "flex";
  } else {
    loadingIndicator.style.display = "none";
  }
}

/**
 * Display paginated appointments
 */
function displayPaginatedAppointments() {
  try {
    console.log('🔴 [displayPaginatedAppointments] Starting display...', { allAppointments, currentPage, itemsPerPage });
    tableBody.innerHTML = "";
    
    if (!allAppointments || allAppointments.length === 0) {
      console.log('🔴 [displayPaginatedAppointments] No appointments to display');
      tableBody.innerHTML = `<tr><td colspan="6" class="noPatientRecord">No Appointments found for ${selectedDate}.</td></tr>`;
      document.getElementById("paginationContainer").style.display = "none";
      return;
    }
    
    console.log('🔴 [displayPaginatedAppointments] Total appointments:', allAppointments.length);
    
    // Calculate pagination
    const totalPages = Math.ceil(allAppointments.length / itemsPerPage);
    const startIndex = (currentPage - 1) * itemsPerPage;
    const endIndex = startIndex + itemsPerPage;
    const paginatedAppointments = allAppointments.slice(startIndex, endIndex);
    
    // Display appointments for current page
    console.log('🔴 [displayPaginatedAppointments] Paginated appointments:', paginatedAppointments);
    paginatedAppointments.forEach(appointment => {
      console.log('🔴 [displayPaginatedAppointments] Processing appointment:', appointment);
      const patient = {
        id: appointment.patientId,
        name: appointment.patientName,
        phone: appointment.patientPhone || "N/A",
        email: appointment.patientEmail || "N/A",
        status: appointment.status || "pending"
      };
      console.log('🔴 [displayPaginatedAppointments] Created patient object:', patient);
      
      const row = createPatientRow(patient, appointment.id, appointment.doctorId);
      tableBody.appendChild(row);
    });
    console.log('🔴 [displayPaginatedAppointments] All rows added to table');
    
    // Show pagination controls if there are multiple pages
    if (totalPages > 1) {
      document.getElementById("paginationContainer").style.display = "flex";
      document.getElementById("pageInfo").textContent = `Page ${currentPage} of ${totalPages}`;
      
      // Enable/disable navigation buttons
      document.getElementById("prevBtn").disabled = currentPage === 1;
      document.getElementById("nextBtn").disabled = currentPage === totalPages;
    } else {
      document.getElementById("paginationContainer").style.display = "none";
    }
  } catch (error) {
    console.error("Error displaying paginated appointments:", error);
    showError("Error displaying appointments. Please try again.");
  }
}

/**
 * Navigate to previous page
 */
window.previousPage = function() {
  if (currentPage > 1) {
    currentPage--;
    displayPaginatedAppointments();
    // Scroll to top of table
    document.querySelector(".table-container").scrollIntoView({ behavior: "smooth" });
  }
};

/**
 * Navigate to next page
 */
window.nextPage = function() {
  const totalPages = Math.ceil(allAppointments.length / itemsPerPage);
  if (currentPage < totalPages) {
    currentPage++;
    displayPaginatedAppointments();
    // Scroll to top of table
    document.querySelector(".table-container").scrollIntoView({ behavior: "smooth" });
  }
};

/**
 * Apply status filter to appointments
 */
function applyStatusFilter(appointments) {
  console.log('🟡 [applyStatusFilter] Input appointments:', appointments);
  console.log('🟡 [applyStatusFilter] Current status filter:', statusFilter);
  
  if (!statusFilter) {
    console.log('🟡 [applyStatusFilter] No filter applied, returning all appointments');
    return appointments;
  }
  
  const filtered = appointments.filter(apt => {
    const appointmentStatus = (apt.status || "pending").toLowerCase();
    const match = appointmentStatus === statusFilter.toLowerCase();
    console.log('🟡 [applyStatusFilter] Appointment:', apt.id, 'Status:', appointmentStatus, 'Match:', match);
    return match;
  });
  
  console.log('🟡 [applyStatusFilter] Filtered result:', filtered);
  return filtered;
}

// Add an 'input' event listener to the search bar
document.getElementById("searchBar").addEventListener("input", (e) => {
  const searchValue = e.target.value.trim();
  patientName = searchValue.length > 0 ? searchValue : null;
  currentPage = 1; // Reset to first page on search
  loadAppointments();
});

// Add status filter event listener
document.getElementById("statusFilter").addEventListener("change", (e) => {
  statusFilter = e.target.value;
  currentPage = 1; // Reset to first page on filter change
  loadAppointments();
});

// Add a click listener to the "Today" button
document.getElementById("todayAppointments").addEventListener("click", () => {
  selectedDate = new Date().toISOString().split("T")[0];
  document.getElementById("dateFilter").value = selectedDate;
  currentPage = 1; // Reset to first page
  loadAppointments();
});

// Add a change event listener to the date picker
document.getElementById("dateFilter").addEventListener("change", (e) => {
  selectedDate = e.target.value;
  currentPage = 1; // Reset to first page on date change
  loadAppointments();
});

/**
 * Function: loadAppointments
 * Purpose: Fetch and display appointments based on selected date, patient name, and status
 */
async function loadAppointments() {
  try {
    console.log('🟢 [loadAppointments] Starting...', { selectedDate, patientName, statusFilter, token: token ? 'present' : 'missing' });
    showLoading(true);
    
    // Fetch appointments from API
    const response = await getAllAppointments(selectedDate, patientName, token);
    console.log('🟢 [loadAppointments] Received response:', response);
    
    if (!response || !response.appointments) {
      console.error('❌ [loadAppointments] Invalid response structure:', response);
      throw new Error("Failed to fetch appointments. Please try again.");
    }
    
    console.log('🟢 [loadAppointments] Appointments array:', response.appointments);
    console.log('🟢 [loadAppointments] Appointments count:', response.appointments.length);
    
    // Apply status filter
    allAppointments = applyStatusFilter(response.appointments);
    console.log('🟢 [loadAppointments] After status filter:', allAppointments);
    console.log('🟢 [loadAppointments] Filtered count:', allAppointments.length);
    
    // Reset to first page and display
    currentPage = 1;
    displayPaginatedAppointments();
    showLoading(false);
    
  } catch (error) {
    console.error("Error loading appointments:", error);
    showLoading(false);
    
    // User-friendly error message
    const errorMessage = error.message || "Unable to load appointments. Please check your connection and try again.";
    showError(errorMessage);
    
    tableBody.innerHTML = `<tr><td colspan="6" class="noPatientRecord">Error loading appointments.</td></tr>`;
  }
}

// When the page is fully loaded (DOMContentLoaded)
document.addEventListener("DOMContentLoaded", () => {
  // Call renderContent() (assumes it sets up the UI layout)
  if (typeof renderContent === 'function') {
    renderContent();
  }
  
  // Set today's date as default in date picker
  document.getElementById("dateFilter").value = selectedDate;
  
  // Load today's appointments by default
  loadAppointments();
});
