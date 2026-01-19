// doctorServices.js - Doctor Service Layer
import { API_BASE_URL } from "../config/config.js";

// Define a constant DOCTOR_API to hold the full endpoint for doctor-related actions
const DOCTOR_API = API_BASE_URL + '/doctor';

/**
 * Function: getDoctors
 * Purpose: Fetch the list of all doctors from the API
 */
export async function getDoctors() {
  try {
    console.log("[doctorServices] GET /doctor start");
    // Use authorizedFetch() to send a GET request with JWT token in Authorization header
    const response = await authorizedFetch(DOCTOR_API);
    console.log("[doctorServices] GET /doctor status", response.status);
    
    // Convert the response to JSON
    const data = await response.json();
    
    // Return the 'doctors' array from the response
    return data.doctors || [];
  } catch (error) {
    // If there's an error (e.g., network issue), log it and return an empty array
    console.error("Error fetching doctors:", error);
    return [];
  }
}

/**
 * Function: deleteDoctor
 * Purpose: Delete a specific doctor using their ID with authorization token in header
 */
export async function deleteDoctor(doctorId, token) {
  try {
    // Use authorizedFetch() with the DELETE method
    // Token is now passed in the Authorization header instead of the URL
    const response = await authorizedFetch(`${DOCTOR_API}/${doctorId}`, {
      method: "DELETE"
    });
    
    // Convert the response to JSON
    const data = await response.json();
    
    // Return an object with success status and message from the server
    return {
      success: response.ok,
      message: data.message || "Doctor deleted successfully"
    };
  } catch (error) {
    // If an error occurs, log it and return a default failure response
    console.error("Error deleting doctor:", error);
    return {
      success: false,
      message: "Network error. Please try again later."
    };
  }
}

/**
 * Function: saveDoctor
 * Purpose: Save (create) a new doctor using a POST request with authorization
 */
export async function saveDoctor(doctor, token) {
  try {
    // Use authorizedFetch() with the POST method
    // Token is passed in the Authorization header instead of the URL
    const response = await authorizedFetch(DOCTOR_API, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(doctor)
    });
    
    // Parse the JSON response and return success status and message
    const data = await response.json();
    return {
      success: response.ok,
      message: data.message || "Doctor added successfully"
    };
  } catch (error) {
    // Catch and log errors, return a failure response if an error occurs
    console.error("Error saving doctor:", error);
    return {
      success: false,
      message: "Network error. Please try again later."
    };
  }
}

/**
 * Function: filterDoctors
 * Purpose: Fetch doctors based on filtering criteria (name, time, and specialty)
 */
export async function filterDoctors(name, time, specialty) {
  try {
    // Build query parameters from the filters
    const params = new URLSearchParams();
    if (name) params.append('name', name);
    if (time) params.append('time', time);
    if (specialty) params.append('specialty', specialty);
    
    // Use authorizedFetch() with the GET method and query parameters
    const url = params.toString() ? `${DOCTOR_API}/filter?${params.toString()}` : DOCTOR_API;
    console.log("[doctorServices] filterDoctors request", url);
    const response = await authorizedFetch(url, {
      method: "GET",
      headers: {
        "Content-Type": "application/json"
      }
    });
    console.log("[doctorServices] filterDoctors status", response.status);
    
    // Check if the response is OK
    if (response.ok) {
      // If yes, parse and return the doctor data
      const data = await response.json();
      console.log("[doctorServices] filterDoctors data", data);
      return data;
    } else {
      // If no, log the error and return an object with an empty 'doctors' array
      console.error("Failed to fetch doctors:", response.statusText);
      return { doctors: [] };
    }
  } catch (error) {
    // Catch any other errors, alert the user, and return a default empty result
    console.error("[doctorServices] Error filtering doctors:", error);
    alert("Something went wrong while filtering doctors!");
    return { doctors: [] };
  }
}
