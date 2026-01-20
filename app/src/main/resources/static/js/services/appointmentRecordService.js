// appointmentRecordService.js
import { API_BASE_URL } from "../config/config.js";
const APPOINTMENT_API = `${API_BASE_URL}/appointments`;


//This is for the doctor to get all the patient Appointments
export async function getAllAppointments(date, patientName, token) {
  const url = `${APPOINTMENT_API}?date=${date}&patientName=${patientName}`;
  console.log('🔵 [getAllAppointments] Fetching appointments from:', url);
  console.log('🔵 [getAllAppointments] Parameters:', { date, patientName, token: token ? 'present' : 'missing' });
  
  const response = await authorizedFetch(url);
  console.log('🔵 [getAllAppointments] Response status:', response.status, response.ok ? '✅' : '❌');
  
  if (!response.ok) {
    throw new Error("Failed to fetch appointments");
  }

  const data = await response.json();
  console.log('🔵 [getAllAppointments] Response data:', data);
  return data;
}

export async function bookAppointment(appointment, token) {
  try {
    const response = await authorizedFetch(`${APPOINTMENT_API}`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(appointment)
    });

    const data = await response.json();
    return {
      success: response.ok,
      message: data.message || "Something went wrong"
    };
  } catch (error) {
    console.error("Error while booking appointment:", error);
    return {
      success: false,
      message: "Network error. Please try again later."
    };
  }
}

export async function updateAppointment(appointment, token) {
  try {
    const response = await authorizedFetch(`${APPOINTMENT_API}`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(appointment)
    });

    const data = await response.json();
    return {
      success: response.ok,
      message: data.message || "Something went wrong"
    };
  } catch (error) {
    console.error("Error while booking appointment:", error);
    return {
      success: false,
      message: "Network error. Please try again later."
    };
  }
}

export async function cancelAppointment(id, token) {
  try {
    const response = await authorizedFetch(`${APPOINTMENT_API}/${id}`, {
      method: "DELETE"
    });

    const data = await response.json();
    return {
      success: response.ok,
      message: data.message || "Something went wrong"
    };
  } catch (error) {
    console.error("Error while canceling appointment:", error);
    return {
      success: false,
      message: "Network error. Please try again later."
    };
  }
}
