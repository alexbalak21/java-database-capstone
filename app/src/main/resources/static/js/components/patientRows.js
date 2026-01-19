// patientRows.js
export function createPatientRow(patient, appointmentId, doctorId) {
  const tr = document.createElement("tr");
  console.log("CreatePatientRow :: ", doctorId);
  
  // Determine status class for styling
  const statusClass = (patient.status || "pending").toLowerCase() === "consulted" ? "status-consulted" : "status-pending";
  const statusDisplay = (patient.status || "pending").charAt(0).toUpperCase() + (patient.status || "pending").slice(1);
  
  tr.innerHTML = `
      <td class="patient-id" data-label="Patient ID">${patient.id}</td>
      <td data-label="Name">${patient.name}</td>
      <td data-label="Phone">${patient.phone}</td>
      <td data-label="Email">${patient.email}</td>
      <td data-label="Status"><span class="status-badge ${statusClass}">${statusDisplay}</span></td>
      <td data-label="Prescription"><img src="../assets/images/addPrescriptionIcon/addPrescription.png" alt="addPrescriptionIcon" class="prescription-btn" data-id="${patient.id}"></td>
    `;

  // Attach event listeners
  tr.querySelector(".patient-id").addEventListener("click", () => {
    try {
      window.location.href = `/pages/patientRecord.html?id=${patient.id}&doctorId=${doctorId}`;
    } catch (error) {
      console.error("Error navigating to patient record:", error);
      alert("Unable to open patient record. Please try again.");
    }
  });

  tr.querySelector(".prescription-btn").addEventListener("click", () => {
    try {
      window.location.href = `/pages/addPrescription.html?appointmentId=${appointmentId}&patientName=${patient.name}`;
    } catch (error) {
      console.error("Error opening prescription form:", error);
      alert("Unable to open prescription form. Please try again.");
    }
  });

  return tr;
}
